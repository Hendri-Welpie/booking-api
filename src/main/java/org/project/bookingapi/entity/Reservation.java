package org.project.bookingapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.project.bookingapi.enums.BookingStatusType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "booking")
@Entity(name = "reservation")
@EntityListeners(AuditingEntityListener.class)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "room_number")
    private Integer roomNumber;

    @Column(name = "room_id")
    private UUID roomId;

    @Column(name = "checkin_date")
    private LocalDate checkinDate;

    @Column(name = "checkout_date")
    private LocalDate checkoutDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatusType status;

    @Version
    private Long version;

    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime lastModifiedDate;
}
