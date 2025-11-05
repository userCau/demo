package com.projeto.gestock.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.projeto.gestock.model.Produto;
import com.projeto.gestock.model.Usuario;
import com.projeto.gestock.model.Categoria;
import com.projeto.gestock.repository.ProdutoRepository;
import com.projeto.gestock.repository.UsuarioRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaService categoriaService;

    public ProdutoService(ProdutoRepository produtoRepository,
            UsuarioRepository usuarioRepository,
            CategoriaService categoriaService) {
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaService = categoriaService;
    }

    // =====================================================
    // CRUD BÁSICO
    // =====================================================
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));
    }

    public Produto salvar(Produto produto) {
        // ✅ Verifica e salva categoria corretamente
        if (produto.getCategoria() != null && produto.getCategoria().getNome() != null) {
            Categoria categoria = categoriaService.salvarNovaCategoria(produto.getCategoria().getNome());
            produto.setCategoria(categoria);
        }

        // ✅ Garante que o usuário associado existe
        if (produto.getCriadoPor() != null && produto.getCriadoPor().getId() != null) {
            Usuario usuario = usuarioRepository.findById(produto.getCriadoPor().getId())
                    .orElse(produto.getCriadoPor());
            produto.setCriadoPor(usuario);
        }

        return produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // =====================================================
    // RELATÓRIOS E CONTAGENS
    // =====================================================
    public Map<String, Long> contarPorCategoria() {
        return produtoRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        produto -> produto.getCategoria() != null ? produto.getCategoria().getNome() : "Sem categoria",
                        Collectors.counting()));
    }

    public Map<String, Long> contarPorStatus() {
        return produtoRepository.findAll().stream()
                .collect(Collectors.groupingBy(produto -> {
                    if (produto.getValidade() == null)
                        return "Sem validade";
                    if (produto.getValidade().isBefore(LocalDate.now()))
                        return "Vencido";
                    if (produto.getValidade().isBefore(LocalDate.now().plusDays(7)))
                        return "Próx. vencimento";
                    return "Válido";
                }, Collectors.counting()));
    }

    // =====================================================
    // CONSULTAS DE VALIDADE
    // =====================================================
    public List<Produto> buscarPorValidadeAntesDe(LocalDate data) {
        return produtoRepository.findByValidadeBefore(data);
    }

    public List<Produto> buscarPorValidadeEntre(LocalDate inicio, LocalDate fim) {
        return produtoRepository.findByValidadeBetween(inicio, fim);
    }

    public List<Produto> buscarPorValidadeDepoisDe(LocalDate data) {
        return produtoRepository.findByValidadeAfter(data);
    }

    // =====================================================
    // ESTOQUE
    // =====================================================
    public List<Produto> buscarComEstoqueBaixo() {
        return produtoRepository.findAll().stream()
                .filter(p -> p.getEstoqueMinimo() != null && p.getQuantidade() != null)
                .filter(p -> p.getQuantidade() <= p.getEstoqueMinimo())
                .collect(Collectors.toList());
    }
}
