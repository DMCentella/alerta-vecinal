package pe.edu.cibertec.authservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
    private String rol;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private String dni;
}
