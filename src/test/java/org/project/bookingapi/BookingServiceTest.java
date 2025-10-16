package org.project.bookingapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.bookingapi.entity.Reservation;
import org.project.bookingapi.entity.Rooms;
import org.project.bookingapi.enums.BookingStatusType;
import org.project.bookingapi.enums.RoomType;
import org.project.bookingapi.exception.BookingConflictException;
import org.project.bookingapi.exception.ResourceNotFoundException;
import org.project.bookingapi.model.RoomsDto;
import org.project.bookingapi.model.request.ReservationRequest;
import org.project.bookingapi.model.response.ReservationResponse;
import org.project.bookingapi.repository.BookingRepository;
import org.project.bookingapi.repository.RoomsRepository;
import org.project.bookingapi.service.BookingService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    RoomsRepository roomsRepository;

    @InjectMocks
    BookingService bookingService;

    private Reservation buildReservation(UUID id, UUID roomId, UUID userId, LocalDate ci, LocalDate co) {
        Reservation r = new Reservation();
        r.setId(id);
        r.setRoomId(roomId);
        r.setUserId(userId);
        r.setFirstName("First");
        r.setLastName("Last");
        r.setCheckinDate(ci);
        r.setCheckoutDate(co);
        r.setRoomNumber(1);
        r.setStatus(BookingStatusType.ACTIVE);
        r.setVersion(1L);
        r.setCreatedDate(LocalDateTime.now());
        r.setLastModifiedDate(LocalDateTime.now());
        return r;
    }

    private Rooms buildRoom(UUID id, Long roomNumber, RoomType type) {
        Rooms rooms = new Rooms();
        rooms.setId(id);
        rooms.setRoomNumber(roomNumber);
        rooms.setType(type);
        return rooms;
    }

    @Test
    void createReservation_success() {
        UUID roomId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReservationRequest req = ReservationRequest.builder()
                .roomId(roomId)
                .userId(userId)
                .firstname("John")
                .surname("Doe")
                .roomNum(101)
                .checkinDate(LocalDate.of(2025, 1, 1))
                .checkoutDate(LocalDate.of(2025, 1, 3))
                .build();

        Rooms room = buildRoom(roomId, 101L, RoomType.SINGLE);
        given(roomsRepository.findById(eq(roomId))).willReturn(Optional.of(room));
        given(bookingRepository.findOverlappingReservations(eq(roomId), any(), any()))
                .willReturn(Collections.emptyList());

        given(bookingRepository.save(any(Reservation.class))).willAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        ReservationResponse resp = bookingService.createReservation(req);

        assertThat(resp).isNotNull();
        assertThat(resp.firstname()).isEqualTo("John");
        assertThat(resp.surname()).isEqualTo("Doe");
        assertThat(resp.roomId()).isEqualTo(roomId);

        then(roomsRepository).should().findById(roomId);
        then(bookingRepository).should().save(any(Reservation.class));
    }

    @Test
    void createReservation_roomNotFound_throwsResourceNotFound() {
        UUID roomId = UUID.randomUUID();
        ReservationRequest req = ReservationRequest.builder()
                .roomId(roomId)
                .userId(UUID.randomUUID())
                .firstname("a").surname("b")
                .roomNum(1)
                .checkinDate(LocalDate.now()).checkoutDate(LocalDate.now().plusDays(1))
                .build();

        given(roomsRepository.findById(eq(roomId))).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createReservation(req));

        then(roomsRepository).should().findById(roomId);
    }

    @Test
    void createReservation_overlapping_throwsBookingConflict() {
        UUID roomId = UUID.randomUUID();
        ReservationRequest req = ReservationRequest.builder()
                .roomId(roomId)
                .userId(UUID.randomUUID())
                .firstname("a").surname("b")
                .roomNum(1)
                .checkinDate(LocalDate.now()).checkoutDate(LocalDate.now().plusDays(1))
                .build();

        Rooms room = buildRoom(roomId, 1L, RoomType.SINGLE);
        Reservation existing = buildReservation(UUID.randomUUID(), roomId, req.userId(), req.checkinDate(), req.checkoutDate());

        given(roomsRepository.findById(eq(roomId))).willReturn(Optional.of(room));
        given(bookingRepository.findOverlappingReservations(eq(roomId), any(), any()))
                .willReturn(List.of(existing));

        assertThrows(BookingConflictException.class, () -> bookingService.createReservation(req));

        then(roomsRepository).should().findById(roomId);
        then(bookingRepository).should().findOverlappingReservations(eq(roomId), any(), any());
    }

    @Test
    void updateReservation_success() {
        UUID resId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        Reservation existing = buildReservation(resId, roomId, UUID.randomUUID(), LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 3));

        given(bookingRepository.findById(eq(resId))).willReturn(Optional.of(existing));
        given(bookingRepository.findOverlappingReservations(eq(roomId), any(), any()))
                .willReturn(Collections.emptyList());
        given(bookingRepository.save(any(Reservation.class))).willAnswer(invocation -> invocation.<Reservation>getArgument(0));

        ReservationRequest req = ReservationRequest.builder()
                .roomId(roomId)
                .userId(existing.getUserId())
                .firstname("x").surname("y")
                .roomNum(1)
                .checkinDate(LocalDate.of(2025, 2, 2))
                .checkoutDate(LocalDate.of(2025, 2, 4))
                .build();

        ReservationResponse resp = bookingService.updateReservation(resId, req);

        assertThat(resp.checkinDate()).isEqualTo(LocalDate.of(2025, 2, 2));
        assertThat(resp.checkoutDate()).isEqualTo(LocalDate.of(2025, 2, 4));

        then(bookingRepository).should().findById(resId);
        then(bookingRepository).should().findOverlappingReservations(eq(roomId), any(), any());
        then(bookingRepository).should().save(any(Reservation.class));
    }

    @Test
    void updateReservation_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        given(bookingRepository.findById(eq(id))).willReturn(Optional.empty());

        ReservationRequest req = ReservationRequest.builder()
                .roomId(UUID.randomUUID()).userId(UUID.randomUUID())
                .roomNum(1).checkinDate(LocalDate.now()).checkoutDate(LocalDate.now().plusDays(1))
                .build();

        assertThrows(ResourceNotFoundException.class, () -> bookingService.updateReservation(id, req));

        then(bookingRepository).should().findById(id);
    }

    @Test
    void updateReservation_conflict_throwsBookingConflict() {
        UUID existingResId = UUID.randomUUID();
        UUID conflictingResId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        LocalDate checkin = LocalDate.now();
        LocalDate checkout = LocalDate.now().plusDays(1);

        Reservation existing = buildReservation(existingResId, roomId, UUID.randomUUID(), checkin, checkout);
        Reservation other = buildReservation(conflictingResId, roomId, UUID.randomUUID(), checkin, checkout);

        ReservationRequest req = ReservationRequest.builder()
                .roomId(roomId).userId(existing.getUserId())
                .roomNum(1).checkinDate(checkin).checkoutDate(checkout)
                .build();

        given(bookingRepository.findById(eq(existingResId))).willReturn(Optional.of(existing));
        given(bookingRepository.findOverlappingReservations(eq(roomId), any(), any()))
                .willReturn(List.of(other));

        assertThrows(BookingConflictException.class, () -> bookingService.updateReservation(existingResId, req));

        then(bookingRepository).should().findById(existingResId);
        then(bookingRepository).should().findOverlappingReservations(eq(roomId), any(), any());
    }

    @Test
    void updateReservation_optimisticLock_throwsBookingConflict() {
        UUID id = UUID.randomUUID();
        Reservation existing = buildReservation(id, UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(1));

        given(bookingRepository.findById(eq(id))).willReturn(Optional.of(existing));
        given(bookingRepository.findOverlappingReservations(any(), any(), any())).willReturn(Collections.emptyList());

        // Simulate save throwing an optimistic lock exception
        willThrow(new ObjectOptimisticLockingFailureException("obj", "can't")).given(bookingRepository).save(any());

        ReservationRequest req = ReservationRequest.builder()
                .roomId(existing.getRoomId()).userId(existing.getUserId())
                .roomNum(1).checkinDate(existing.getCheckinDate()).checkoutDate(existing.getCheckoutDate())
                .build();

        assertThrows(BookingConflictException.class, () -> bookingService.updateReservation(id, req));

        then(bookingRepository).should().findById(id);
        then(bookingRepository).should().findOverlappingReservations(any(), any(), any());
        then(bookingRepository).should().save(any());
    }

    @Test
    void cancelReservation_success() {
        UUID id = UUID.randomUUID();
        Reservation existing = buildReservation(id, UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(1));
        given(bookingRepository.findById(eq(id))).willReturn(Optional.of(existing));
        willAnswer(invocation -> invocation.getArgument(0)).given(bookingRepository).save(any(Reservation.class));

        bookingService.cancelReservation(id);

        assertThat(existing.getStatus()).isEqualTo(BookingStatusType.CANCELLED);
        then(bookingRepository).should().save(existing);
    }

    @Test
    void cancelReservation_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        given(bookingRepository.findById(eq(id))).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.cancelReservation(id));
    }

    @Test
    void getAllReservations_mapsPages() {
        Reservation r1 = buildReservation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(1));
        given(bookingRepository.findAll(PageRequest.of(0, 20))).willReturn(new PageImpl<>(List.of(r1)));

        List<ReservationResponse> result = bookingService.getAllReservations(0, 20);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isNotNull();
        assertThat(result.get(0).firstname()).isNotNull();

        then(bookingRepository).should().findAll(PageRequest.of(0, 20));
    }

    @Test
    void getReservationById_success_and_notFound() {
        UUID id = UUID.randomUUID();
        Reservation r = buildReservation(id, UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(1));
        given(bookingRepository.findById(eq(id))).willReturn(Optional.of(r));

        ReservationResponse resp = bookingService.getReservationById(id);
        assertThat(resp.id()).isEqualTo(id);

        UUID missing = UUID.randomUUID();
        given(bookingRepository.findById(eq(missing))).willReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.getReservationById(missing));
    }

    @Test
    void getAvailableRooms_filters_rooms_with_overlaps() {
        UUID room1Id = UUID.randomUUID();
        UUID room2Id = UUID.randomUUID();

        Rooms room1 = buildRoom(room1Id, 1L, RoomType.SINGLE);
        Rooms room2 = buildRoom(room2Id, 2L, RoomType.DOUBLE);

        // room1 has overlapping reservation; room2 none
        Reservation overlap = buildReservation(UUID.randomUUID(), room1Id, UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(1));
        given(roomsRepository.findAll()).willReturn(List.of(room1, room2));
        given(bookingRepository.findOverlappingReservations(eq(room1Id), any(), any())).willReturn(List.of(overlap));
        given(bookingRepository.findOverlappingReservations(eq(room2Id), any(), any())).willReturn(Collections.emptyList());

        List<RoomsDto> available = bookingService.getAvailableRooms(LocalDate.now(), LocalDate.now().plusDays(1));

        assertThat(available).hasSize(1);
        assertThat(available.get(0).id()).isEqualTo(room2Id);
        assertThat(available.get(0).roomNumber()).isEqualTo(2L);
    }
}