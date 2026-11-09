package pe.edu.cibertec.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.authservice.dto.AuthResponse;
import pe.edu.cibertec.authservice.dto.LoginRequest;
import pe.edu.cibertec.authservice.dto.RegisterRequest;
import pe.edu.cibertec.authservice.model.Usuario;
import pe.edu.cibertec.authservice.repository.UsuarioRepository;
import pe.edu.cibertec.authservice.security.JwtUtils;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new AuthResponse(jwt, loginRequest.getUsername(), usuario.getRol(), "Autenticación exitosa");
    }

    public AuthResponse registerUser(RegisterRequest signUpRequest) {
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: El nombre de usuario ya está en uso!");
        }

        if (signUpRequest.getEmail() != null && usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: El email ya está registrado!");
        }

        Usuario user = new Usuario();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setRol(signUpRequest.getRol() != null ? signUpRequest.getRol() : "ROLE_VECINO");
        user.setNombres(signUpRequest.getNombres());
        user.setApellidos(signUpRequest.getApellidos());
        user.setEmail(signUpRequest.getEmail());
        user.setTelefono(signUpRequest.getTelefono());
        user.setDni(signUpRequest.getDni());

        usuarioRepository.save(user);

        return new AuthResponse(null, user.getUsername(), user.getRol(), "Usuario registrado exitosamente!");
    }
}

