package com.alertavecinal.admin_service.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final RestTemplate restTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Acceso denegado. Se requiere Token JWT.");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            // El admin-service le pregunta al auth-service si el token vale
            Map<?, ?> authResponse = restTemplate.getForObject("http://localhost:8081/api/auth/validate?token=" + token, Map.class);
            
            if (authResponse != null && Boolean.TRUE.equals(authResponse.get("valid"))) {
                String rol = (String) authResponse.get("rol");
                if ("ROLE_ADMIN".equals(rol)) {
                    return true; // Pasa, es el jefe
                } else {
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Acceso denegado. Solo ROLE_ADMIN.");
                    return false;
                }
            }
        } catch (Exception e) {}

        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token inválido o expirado.");
        return false;
    }
}
