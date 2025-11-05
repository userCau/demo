package com.projeto.gestock.repository;

import com.projeto.gestock.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Categoria findByNomeIgnoreCase(String nome);
}
