package cl.duoc.usuario_service.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ClobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity> handleDuplicateKey(DataIntegrityViolationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "El RUT o el Email ya se encuentran registrados.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}