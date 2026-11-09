package com.alertavecinal.admin_service.exception;

import com.alertavecinal.admin_service.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorMessage handleServiceUnavailable(
            ResourceAccessException e, WebRequest webRequest) {
        return ErrorMessage.builder()
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("No se pudo conectar con el incident-service. Verifique que este activo en el puerto 8082.")
                .dateError(LocalDate.now())
                .build();
    }
}
