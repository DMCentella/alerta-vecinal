package pe.edu.cibertec.authservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String username;
    private String rol;
    private String message;

    public AuthResponse(String token, String username, String rol, String message) {
        this.token = token;
        this.username = username;
        this.rol = rol;
        this.message = message;
    }
}
