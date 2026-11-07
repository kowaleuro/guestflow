package pl.mkowalik.guestflow.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.mkowalik.guestflow.globalExceptions.ConflictException;
import pl.mkowalik.guestflow.globalExceptions.NotFoundException;

@RestControllerAdvice 
public class GlobalExceptionHandler{
    
    @ExceptionHandler(NotFoundException.class)
    ProblemDetail NotFound(NotFoundException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    ProblemDetail Conflict(ConflictException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }
    
}