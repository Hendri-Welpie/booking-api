package org.project.bookingapi.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.postgresql.util.PSQLException;
import org.project.bookingapi.entity.Reservation;
import org.project.bookingapi.entity.Rooms;
import org.project.bookingapi.enums.BookingStatusType;
import org.project.bookingapi.exception.BookingConflictException;
import org.project.bookingapi.exception.ResourceNotFoundException;
import org.project.bookingapi.exception.RoomAlreadyBookedException;
import org.project.bookingapi.mapper.BookingRequestMapper;
import org.project.bookingapi.mapper.BookingResponseMapper;
import org.project.bookingapi.model.RoomsDto;
import org.project.bookingapi.model.request.ReservationRequest;
import org.project.bookingapi.model.response.ReservationResponse;
import org.project.bookingapi.repository.BookingRepository;
import org.project.bookingapi.repository.RoomsRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomsRepository roomsRepository;

    @Transactional
    public ReservationResponse createReservation(final ReservationRequest request) {

        Rooms room = roomsRepository.findById(request.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + request.roomId()));

        List<Reservation> overlapping = bookingRepository.findOverlappingReservations(
                request.roomId(), request.checkinDate(), request.checkoutDate());
        if (!overlapping.isEmpty()) {
            throw new BookingConflictException("Room already booked for the selected dates");
        }

        Reservation reservation = BookingRequestMapper.INSTANCE.map(request);
        reservation.setRoomNumber(room.getRoomNumber().intValue());
        try {
            Reservation saved = bookingRepository.save(reservation);

            return BookingResponseMapper.INSTANCE.map(saved);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            if (isOverlapViolation(dataIntegrityViolationException)) {
                throw new RoomAlreadyBookedException("Room is already booked for the selected dates.");
            }
            throw dataIntegrityViolationException;
        }
    }

    @Transactional
    public ReservationResponse updateReservation(final UUID id, final ReservationRequest request) {

        Reservation existing = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));

        LocalDate newCheckin = ObjectUtils.isEmpty(request.checkinDate()) ? existing.getCheckinDate() : request.checkinDate();
        LocalDate newCheckout = ObjectUtils.isEmpty(request.checkoutDate()) ? existing.getCheckoutDate() : request.checkoutDate();

        List<Reservation> overlapping = new ArrayList<>(bookingRepository.findOverlappingReservations(
                existing.getRoomId(), newCheckin, newCheckout));
        overlapping.removeIf(reservation -> reservation.getId().equals(existing.getId()));

        if (!overlapping.isEmpty())
            throw new BookingConflictException("Updated dates conflict with existing reservation");

        existing.setCheckinDate(newCheckin);
        existing.setCheckoutDate(newCheckout);
        try {
            Reservation saved = bookingRepository.save(existing);
            return BookingResponseMapper.INSTANCE.map(saved);
        } catch (ObjectOptimisticLockingFailureException ole) {
            throw new BookingConflictException("Reservation was updated concurrently. Please retry.");
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            if (isOverlapViolation(dataIntegrityViolationException)) {
                throw new RoomAlreadyBookedException("Room is already booked for the selected dates.");
            }
            throw dataIntegrityViolationException;
        }
    }

    @Transactional
    public void cancelReservation(UUID reservationId) {

        Reservation existing = bookingRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + reservationId));

        existing.setStatus(BookingStatusType.CANCELLED);
        try {
            bookingRepository.save(existing);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            if (isOverlapViolation(dataIntegrityViolationException)) {
                throw new RoomAlreadyBookedException("Room is already booked for the selected dates.");
            }
            throw dataIntegrityViolationException;
        }
    }

    public List<ReservationResponse> getAllReservations(final int page, final int size) {
        return bookingRepository.findAll(PageRequest.of(page, size)).getContent().stream()
                .map(BookingResponseMapper.INSTANCE::map)
                .toList();
    }

    public List<ReservationResponse> getAllReservationsByUser(final UUID id, final int page, final int size) {
        return bookingRepository.findAllByUserId(id, PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(BookingResponseMapper.INSTANCE::map)
                .toList();
    }

    public ReservationResponse getReservationById(final UUID id) {
        return BookingResponseMapper.INSTANCE.map(bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id)));
    }

    public List<RoomsDto> getAvailableRooms(final LocalDate checkin, final LocalDate checkout) {
        return roomsRepository.findAll()
                .stream()
                .filter(room -> bookingRepository
                        .findOverlappingReservations(room.getId(), checkin, checkout)
                        .isEmpty())
                .map(roomsEntity -> RoomsDto.builder()
                        .id(roomsEntity.getId())
                        .roomNumber(roomsEntity.getRoomNumber())
                        .type(roomsEntity.getType())
                        .build())
                .toList();
    }

    private boolean isOverlapViolation(Throwable throwable) {
        var root = ExceptionUtils.getRootCause(throwable);
        return root instanceof PSQLException &&
                ((PSQLException) root)
                        .getSQLState().equals("23P01");
    }
}