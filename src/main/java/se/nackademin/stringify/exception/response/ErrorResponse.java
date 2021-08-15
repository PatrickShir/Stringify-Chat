package se.nackademin.stringify.exception.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
@Builder
public class ErrorResponse {

    private final String exceptionType;
    private final String message;
    private final HttpStatus status;
    private final String timestamp;
}
