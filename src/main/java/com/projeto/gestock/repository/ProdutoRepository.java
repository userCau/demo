package com.projeto.gestock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projeto.gestock.model.Produto;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByValidadeBefore(LocalDate data);

    List<Produto> findByValidadeAfter(LocalDate data);

    List<Produto> findByValidadeBetween(LocalDate inicio, LocalDate fim);
}
