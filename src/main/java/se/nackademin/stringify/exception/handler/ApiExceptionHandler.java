package se.nackademin.stringify.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import se.nackademin.stringify.exception.ChatSessionNotFoundException;
import se.nackademin.stringify.exception.ConnectionLimitException;
import se.nackademin.stringify.exception.InvalidKeyException;
import se.nackademin.stringify.exception.ProfileNotFoundException;
import se.nackademin.stringify.exception.response.ErrorResponse;
import se.nackademin.stringify.util.DateUtil;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles Api exceptions.
 */
@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles all javax validation errors and sends a {@code ResponseEntity} with appropriate error messages.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status);


        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);
    }

    /**
     * Handles all exceptions related to status code 404 (NOT FOUND)
     *
     * @param ex The exception that occurs
     * @return {@code ResponseEntity.class, ErrorResponse.class}
     */
    @ExceptionHandler({ChatSessionNotFoundException.class, ProfileNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception ex) {
        return getAndLogApiException(HttpStatus.NOT_FOUND, ex);
    }

    /**
     * Handles all exceptions related to status code 503 (SERVICE UNAVAILABLE)
     *
     * @param ex The exception that occurs
     * @return {@code ResponseEntity.class, ErrorResponse.class}
     */
    @ExceptionHandler(ConnectionLimitException.class)
    public ResponseEntity<ErrorResponse> handleConnectionLimitException(Exception ex) {
        return getAndLogApiException(HttpStatus.SERVICE_UNAVAILABLE, ex);
    }

    /**
     * Handles all exceptions related to status code 400 (BAD REQUEST)
     *
     * @param ex The exception that occurs
     * @return {@code ResponseEntity.class, ErrorResponse.class}
     */
    @ExceptionHandler(InvalidKeyException.class)
    public ResponseEntity<ErrorResponse> handleInvalidKeyException(Exception ex) {
        return getAndLogApiException(HttpStatus.BAD_REQUEST, ex);
    }

    private ResponseEntity<ErrorResponse> getAndLogApiException(HttpStatus status, Exception ex) {
        log.warn(ex.getMessage());

        return new ResponseEntity<>(ErrorResponse.builder()
                .exceptionType(ex.getClass().getSimpleName())
                .message(ex.getMessage())
                .timestamp(DateUtil.stringValueOfNow())
                .status(status)
                .build(), status);
    }
}
