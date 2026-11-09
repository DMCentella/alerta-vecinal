package pe.edu.cibertec.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.authservice.dto.AuthResponse;
import pe.edu.cibertec.authservice.dto.ErrorMessage;
import pe.edu.cibertec.authservice.dto.GenericResponseDto;
import pe.edu.cibertec.authservice.dto.LoginRequest;
import pe.edu.cibertec.authservice.dto.RegisterRequest;
import pe.edu.cibertec.authservice.model.Usuario;
import pe.edu.cibertec.authservice.repository.UsuarioRepository;
import pe.edu.cibertec.authservice.security.JwtUtils;
import pe.edu.cibertec.authservice.security.UserDetailsImpl;
import pe.edu.cibertec.authservice.service.AuthService;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<GenericResponseDto<AuthResponse>> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(GenericResponseDto.<AuthResponse>builder()
                    .response(response)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(GenericResponseDto.<AuthResponse>builder()
                            .error(ErrorMessage.builder()
                                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                                    .dateError(java.time.LocalDate.now())
                                    .message("Credenciales incorrectas")
                                    .build())
                            .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponseDto<AuthResponse>> registerUser(@RequestBody RegisterRequest signUpRequest) {
        try {
            AuthResponse response = authService.registerUser(signUpRequest);
            return ResponseEntity.ok(GenericResponseDto.<AuthResponse>builder()
                    .response(response)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(GenericResponseDto.<AuthResponse>builder()
                            .error(ErrorMessage.builder()
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .dateError(java.time.LocalDate.now())
                                    .message(e.getMessage())
                                    .build())
                            .build());
        }
    }

    // GET: http://localhost:8081/api/auth/profile (Requiere JWT en el header Authorization)
    @GetMapping("/profile")
    public ResponseEntity<GenericResponseDto<Map<String, Object>>> getProfile(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<String, Object> perfil = Map.of(
                "id", usuario.getId(),
                "username", usuario.getUsername(),
                "rol", usuario.getRol(),
                "nombres", usuario.getNombres() != null ? usuario.getNombres() : "",
                "apellidos", usuario.getApellidos() != null ? usuario.getApellidos() : "",
                "email", usuario.getEmail() != null ? usuario.getEmail() : "",
                "telefono", usuario.getTelefono() != null ? usuario.getTelefono() : "",
                "dni", usuario.getDni() != null ? usuario.getDni() : "",
                "fechaRegistro", usuario.getFechaRegistro() != null ? usuario.getFechaRegistro().toString() : ""
        );

        return ResponseEntity.ok(GenericResponseDto.<Map<String, Object>>builder()
                .response(perfil)
                .build());
    }

    // GET: http://localhost:8081/api/auth/validate?token=xxx (Endpoint publico para validacion inter-servicio)
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam String token) {
        try {
            boolean valid = jwtUtils.validateJwtToken(token);
            if (valid) {
                String username = jwtUtils.getUserNameFromJwtToken(token);
                Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
                if (usuario != null) {
                    return ResponseEntity.ok(Map.of("valid", true, "username", username, "rol", usuario.getRol()));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Token invalido"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Token invalido o expirado"));
        }
    }
}

