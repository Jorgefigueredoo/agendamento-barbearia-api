package br.com.jorgefigueredoo.agendamento_barbearia_api.security;

import br.com.jorgefigueredoo.agendamento_barbearia_api.model.Usuario;
import br.com.jorgefigueredoo.agendamento_barbearia_api.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (u.getActive() == null || !u.getActive()) {
            throw new UsernameNotFoundException("Usuário inativo");
        }

        String role = u.getRole();
        if (!role.startsWith("ROLE_")) role = "ROLE_" + role;

        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getSenhaHash(),
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
