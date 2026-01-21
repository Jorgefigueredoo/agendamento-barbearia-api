package br.com.jorgefigueredoo.agendamento_barbearia_api.controller;

import br.com.jorgefigueredoo.agendamento_barbearia_api.dto.LoginRequest;
import br.com.jorgefigueredoo.agendamento_barbearia_api.dto.LoginResponse;
import br.com.jorgefigueredoo.agendamento_barbearia_api.model.Usuario;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.UsuarioRepository;
import br.com.jorgefigueredoo.agendamento_barbearia_api.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest req) {
        Usuario u = usuarioRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        if (u.getActive() == null || !u.getActive()) {
            throw new RuntimeException("Usuário inativo");
        }

        if (!passwordEncoder.matches(req.getSenha(), u.getSenhaHash())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        // role deve ser "ADMIN" ou "BARBEIRO"
        String token = jwtService.gerarToken(u.getEmail(), u.getRole());
        return new LoginResponse(token);
    }
}
