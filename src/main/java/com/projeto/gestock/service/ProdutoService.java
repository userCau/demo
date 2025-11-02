package com.projeto.gestock.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.projeto.gestock.model.Produto;
import com.projeto.gestock.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // 🔹 Novo: contar produtos por categoria
    public Map<String, Long> contarPorCategoria() {
        return produtoRepository.findAll().stream()
                .collect(Collectors.groupingBy(Produto::getCategoria, Collectors.counting()));
    }

    // 🔹 Novo: contar produtos por status (validade)
    public Map<String, Long> contarPorStatus() {
        return produtoRepository.findAll().stream()
                .collect(Collectors.groupingBy(produto -> {
                    if (produto.getValidade() == null) return "Sem validade";
                    if (produto.getValidade().isBefore(LocalDate.now())) return "Vencido";
                    if (produto.getValidade().isBefore(LocalDate.now().plusDays(7))) return "Próx. vencimento";
                    return "Em estoque";
                }, Collectors.counting()));
    }

    public List<Produto> buscarPorValidadeAntesDe(LocalDate data) {
        return produtoRepository.findByValidadeBefore(data);
    }

    public List<Produto> buscarPorValidadeEntre(LocalDate inicio, LocalDate fim) {
        return produtoRepository.findByValidadeBetween(inicio, fim);
    }

    public List<Produto> buscarPorValidadeDepoisDe(LocalDate data) {
        return produtoRepository.findByValidadeAfter(data);
    }
}
