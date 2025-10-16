package org.project.bookingapi.exception;

public class RoomAlreadyBookedException extends RuntimeException {
    public RoomAlreadyBookedException(String message) {
        super(message);
    }
}