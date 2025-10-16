package org.project.bookingapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.project.bookingapi.enums.RoomType;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "booking")
@Entity(name = "rooms")
public class Rooms {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type")
    private RoomType type;

    @Column(name = "room_number")
    private Long roomNumber;
}
