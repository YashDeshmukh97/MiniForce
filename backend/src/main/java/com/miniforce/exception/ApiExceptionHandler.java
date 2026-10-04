package com.miniforce.exception;
import jakarta.servlet.http.HttpServletRequest; import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.time.*; import java.util.*;
@RestControllerAdvice public class ApiExceptionHandler {
 record ErrorBody(Instant timestamp,int status,String error,String message,String path,Map<String,String> fields){}
 @ExceptionHandler(NotFoundException.class) ResponseEntity<ErrorBody> notFound(NotFoundException e,HttpServletRequest r){return response(HttpStatus.NOT_FOUND,e.getMessage(),r,Map.of());}
 @ExceptionHandler(UnauthorizedException.class) ResponseEntity<ErrorBody> unauthorized(UnauthorizedException e,HttpServletRequest r){return response(HttpStatus.UNAUTHORIZED,e.getMessage(),r,Map.of());}
 @ExceptionHandler(ConflictException.class) ResponseEntity<ErrorBody> conflict(ConflictException e,HttpServletRequest r){return response(HttpStatus.CONFLICT,e.getMessage(),r,Map.of());}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException e,HttpServletRequest r){Map<String,String> errors=new LinkedHashMap<>(); e.getBindingResult().getFieldErrors().forEach(x->errors.put(x.getField(),x.getDefaultMessage())); return response(HttpStatus.BAD_REQUEST,"Validation failed",r,errors);}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<ErrorBody> bad(IllegalArgumentException e,HttpServletRequest r){return response(HttpStatus.BAD_REQUEST,e.getMessage(),r,Map.of());}
 @ExceptionHandler(Exception.class) ResponseEntity<ErrorBody> other(Exception e,HttpServletRequest r){return response(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected server error",r,Map.of());}
 private ResponseEntity<ErrorBody> response(HttpStatus s,String m,HttpServletRequest r,Map<String,String> f){return ResponseEntity.status(s).body(new ErrorBody(Instant.now(),s.value(),s.getReasonPhrase(),m,r.getRequestURI(),f));}
}
