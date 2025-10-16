package org.project.bookingapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.bookingapi.model.RoomsDto;
import org.project.bookingapi.model.request.ReservationRequest;
import org.project.bookingapi.model.response.ReservationResponse;
import org.project.bookingapi.service.BookingService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody final ReservationRequest reservationRequest) {
        return ResponseEntity.status(201).body(bookingService.createReservation(reservationRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(@PathVariable("id") final UUID id,
                                                                 @Valid @RequestBody final ReservationRequest reservationRequest) {
        return ResponseEntity.ok(bookingService.updateReservation(id, reservationRequest));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable("id") final UUID id) {
        bookingService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<ReservationResponse> getReservations(@RequestParam(defaultValue = "0") final int page,
                                                     @RequestParam(defaultValue = "20") final int size) {
        return bookingService.getAllReservations(page, size);
    }

    @GetMapping("/user/{id}")
    public List<ReservationResponse> getReservations(@PathVariable("id") final UUID id,
                                                     @RequestParam(defaultValue = "0") final int page,
                                                     @RequestParam(defaultValue = "20") final int size) {
        return bookingService.getAllReservationsByUser(id, page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getById(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok(bookingService.getReservationById(id));
    }

    @GetMapping(value = "/available-rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RoomsDto> getAvailableRooms(@RequestParam("checkin") final String checkin,
                                            @RequestParam("checkout") final String checkout) {
        return bookingService.getAvailableRooms(LocalDate.parse(checkin), LocalDate.parse(checkout));
    }
}