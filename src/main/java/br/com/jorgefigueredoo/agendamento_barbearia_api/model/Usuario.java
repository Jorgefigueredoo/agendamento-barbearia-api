package br.com.jorgefigueredoo.agendamento_barbearia_api.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
        }
)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String email;

    // guardar senha sempre como HASH (BCrypt), nunca senha pura
    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    // exemplo: "BARBEIRO" ou "ADMIN"
    @Column(nullable = false, length = 30)
    private String role;

    @Column(nullable = false)
    private Boolean active = true;

    public Usuario() {
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
