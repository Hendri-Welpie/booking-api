package org.project.bookingapi.exception;


import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import static org.project.bookingapi.exception.ProblemDetailExt.forStatusDetailAndErrors;

public class ValidationException extends ErrorResponseException {

    public ValidationException(final HttpStatus status, final Map<String, String> errors) {
        super(status, forStatusDetailAndErrors(status, "Request validation failed", errors), null);
    }

}