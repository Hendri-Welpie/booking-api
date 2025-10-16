package org.project.bookingapi;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.bookingapi.controller.BookingController;
import org.project.bookingapi.enums.BookingStatusType;
import org.project.bookingapi.enums.RoomType;
import org.project.bookingapi.model.RoomsDto;
import org.project.bookingapi.model.request.ReservationRequest;
import org.project.bookingapi.model.response.ReservationResponse;
import org.project.bookingapi.service.BookingService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    BookingService bookingService;

    BookingController controller;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setup() {

        controller = new BookingController(bookingService);
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createReservation_endpoint_returns201() throws Exception {
        UUID id = UUID.randomUUID();
        ReservationResponse resp = ReservationResponse.builder()
                .id(id)
                .userId(UUID.randomUUID())
                .roomId(UUID.randomUUID())
                .firstname("John").surname("Doe")
                .roomNumber(101L)
                .checkinDate(LocalDate.of(2025, 1, 1))
                .checkoutDate(LocalDate.of(2025, 1, 2))
                .status(BookingStatusType.ACTIVE)
                .build();

        ReservationRequest req = ReservationRequest.builder()
                .roomId(resp.roomId()).userId(resp.userId())
                .firstname("John").surname("Doe")
                .roomNum(101).checkinDate(resp.checkinDate()).checkoutDate(resp.checkoutDate())
                .build();

        given(bookingService.createReservation(any())).willReturn(resp);

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstname").value("John"));

        then(bookingService).should().createReservation(any());
    }

    @Test
    void getById_returns200_andBody() throws Exception {
        UUID id = UUID.randomUUID();
        ReservationResponse resp = ReservationResponse.builder()
                .id(id).userId(UUID.randomUUID()).roomId(UUID.randomUUID())
                .firstname("A").surname("B").roomNumber(1L)
                .checkinDate(LocalDate.now()).checkoutDate(LocalDate.now().plusDays(1))
                .status(BookingStatusType.ACTIVE).build();

        given(bookingService.getReservationById(eq(id))).willReturn(resp);

        mockMvc.perform(get("/api/v1/reservations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstname").value("A"));
    }

    @Test
    void getReservations_returns_flux() throws Exception {
        ReservationResponse r1 = ReservationResponse.builder()
                .id(UUID.randomUUID()).userId(UUID.randomUUID()).roomId(UUID.randomUUID())
                .firstname("X").surname("Y").roomNumber(1L).checkinDate(LocalDate.now()).checkoutDate(LocalDate.now().plusDays(1))
                .status(BookingStatusType.ACTIVE)
                .build();

        given(bookingService.getAllReservations(0, 20)).willReturn(List.of(r1));

        mockMvc.perform(get("/api/v1/reservations")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstname").value("X"));
    }

    @Test
    void getAvailableRooms_parses_dates() throws Exception {
        RoomsDto dto = RoomsDto.builder().id(UUID.randomUUID()).roomNumber(5L).type(RoomType.SUITE).build();
        given(bookingService.getAvailableRooms(any(), any())).willReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/reservations/available-rooms")
                        .param("checkin", "2025-01-01")
                        .param("checkout", "2025-01-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomNumber").value(5));
    }

    @Test
    void cancelReservation_returnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(bookingService).cancelReservation(eq(id));

        mockMvc.perform(post("/api/v1/reservations/{id}/cancel", id))
                .andExpect(status().isNoContent());

        then(bookingService).should().cancelReservation(id);
    }
}