package com.projeto.gestock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.gestock.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
