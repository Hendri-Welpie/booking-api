package org.project.bookingapi.exception;

public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) { super(message); }
}