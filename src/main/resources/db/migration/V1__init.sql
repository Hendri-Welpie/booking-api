-- ================================================
-- Booking schema setup (production ready)
-- ================================================

CREATE SCHEMA IF NOT EXISTS booking;

CREATE EXTENSION IF NOT EXISTS btree_gist;

-- =====================
-- ROOMS
-- =====================
CREATE TABLE IF NOT EXISTS booking.rooms (
    id UUID PRIMARY KEY,
    room_type VARCHAR(50),
    room_number BIGINT
    );

-- =====================
-- USERS
-- =====================
CREATE TABLE IF NOT EXISTS booking.users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- =====================
-- RESERVATIONS
-- =====================
CREATE TABLE IF NOT EXISTS booking.reservation (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    user_id UUID NOT NULL,
    room_number INTEGER,
    room_id UUID NOT NULL,
    checkin_date DATE NOT NULL,
    checkout_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    version BIGINT,
    created_date TIMESTAMP,
    update_date TIMESTAMP
    );

-- Indexes
CREATE INDEX IF NOT EXISTS idx_reservation_roomid ON booking.reservation (room_id);
CREATE INDEX IF NOT EXISTS idx_reservation_dates ON booking.reservation (checkin_date, checkout_date);
CREATE INDEX IF NOT EXISTS idx_reservation_status ON booking.reservation (status);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- Foreign keys
ALTER TABLE booking.reservation
    ADD CONSTRAINT fk_reservation_user
        FOREIGN KEY (user_id)
            REFERENCES booking.users(id)
            ON DELETE CASCADE;

ALTER TABLE booking.reservation
    ADD CONSTRAINT fk_reservation_room
        FOREIGN KEY (room_id)
            REFERENCES booking.rooms(id)
            ON DELETE CASCADE;

-- Exclusion constraint to prevent overlapping active bookings
ALTER TABLE booking.reservation
    ADD COLUMN period daterange
        GENERATED ALWAYS AS (daterange(checkin_date, checkout_date, '[)')) STORED;

ALTER TABLE booking.reservation
    ADD CONSTRAINT reservation_no_overlap
    EXCLUDE USING GIST (
        room_id WITH =,
        period WITH &&
    )
    WHERE (status IS DISTINCT FROM 'CANCELLED');

-- ================================================
-- Mock data for booking.rooms
-- ================================================
INSERT INTO booking.rooms (id, room_type, room_number) VALUES
                                                           ('11111111-1111-1111-1111-111111111111', 'SINGLE', 101),
                                                           ('22222222-2222-2222-2222-222222222222', 'DOUBLE', 102),
                                                           ('33333333-3333-3333-3333-333333333333', 'SUITE', 201),
                                                           ('44444444-4444-4444-4444-444444444444', 'DELUXE', 202),
                                                           ('55555555-5555-5555-5555-555555555555', 'PRESIDENTIAL', 301);

-- ================================================
-- Mock data for booking.users
-- ================================================
INSERT INTO booking.users (id, username, email, password, first_name, last_name) VALUES
                                                                                   ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'john_doe', 'john.doe@example.com', 'hashedpassword1', 'John', 'Doe'),
                                                                                   ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'jane_smith', 'jane.smith@example.com', 'hashedpassword2', 'Jane', 'Smith'),
                                                                                   ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'alice_wong', 'alice.wong@example.com', 'hashedpassword3', 'Alice', 'Wong'),
                                                                                   ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'bob_johnson', 'bob.johnson@example.com', 'hashedpassword4', 'Bob', 'Johnson'),
                                                                                   ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'charlie_brown', 'charlie.brown@example.com', 'hashedpassword5', 'Charlie', 'Brown');

-- ================================================
-- Mock data for booking.reservation
-- ================================================
INSERT INTO booking.reservation (id, first_name, last_name, user_id, room_number, room_id, checkin_date, checkout_date, status, version, created_date, update_date) VALUES
                                                                                                                                                                        ('11111111-aaaa-1111-aaaa-111111111111', 'John', 'Doe', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 101, '11111111-1111-1111-1111-111111111111', '2025-10-20', '2025-10-25', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                                        ('22222222-bbbb-2222-bbbb-222222222222', 'Jane', 'Smith', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 102, '22222222-2222-2222-2222-222222222222', '2025-10-21', '2025-10-23', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                                        ('33333333-cccc-3333-cccc-333333333333', 'Alice', 'Wong', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 201, '33333333-3333-3333-3333-333333333333', '2025-11-01', '2025-11-05', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                                        ('44444444-dddd-4444-dddd-444444444444', 'Bob', 'Johnson', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 202, '44444444-4444-4444-4444-444444444444', '2025-11-10', '2025-11-15', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                                        ('55555555-eeee-5555-eeee-555555555555', 'Charlie', 'Brown', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 301, '55555555-5555-5555-5555-555555555555', '2025-12-01', '2025-12-05', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


