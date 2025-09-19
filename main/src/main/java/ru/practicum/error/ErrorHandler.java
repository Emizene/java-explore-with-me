package ru.practicum.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@SuppressWarnings("unused")
public class ErrorHandler {
    //    @ExceptionHandler(ValidationException.class)
    //    @ResponseStatus(HttpStatus.BAD_REQUEST)
    //    public ErrorResponse handleIncorrectParameter(final ValidationException e) {
    //        log.error("Ошибка валидации.", e);
    //        return new ErrorResponse("Ошибка валидации.", e.getMessage());
    //    }
    //
    //    @ExceptionHandler(NotFoundException.class)
    //    @ResponseStatus(HttpStatus.NOT_FOUND)
    //    public ErrorResponse handleNotFoundException(final NotFoundException e) {
    //        log.error("Ошибка с входным параметром.", e);
    //        return new ErrorResponse("Ошибка с входным параметром.", e.getMessage());
    //    }
}
