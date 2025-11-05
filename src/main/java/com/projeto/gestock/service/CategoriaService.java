package com.projeto.gestock.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.projeto.gestock.model.Categoria;
import com.projeto.gestock.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // =====================================================
    // CRUD BÁSICO
    // =====================================================

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida: " + id));
    }

    public Categoria buscarPorNome(String nome) {
        return categoriaRepository.findByNomeIgnoreCase(nome);
    }

    public Categoria salvar(Categoria categoria) {
        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da categoria não pode estar vazio.");
        }

        // Evita duplicação
        Categoria existente = categoriaRepository.findByNomeIgnoreCase(categoria.getNome());
        if (existente != null) {
            return existente;
        }

        return categoriaRepository.save(categoria);
    }

    // =====================================================
    // CRIAR NOVA CATEGORIA (usado no ProdutoController)
    // =====================================================
    public Categoria salvarNovaCategoria(String nomeCategoria) {
        if (nomeCategoria == null || nomeCategoria.isBlank()) {
            throw new IllegalArgumentException("O nome da categoria não pode estar vazio.");
        }

        Categoria existente = categoriaRepository.findByNomeIgnoreCase(nomeCategoria);
        if (existente != null) {
            return existente;
        }

        Categoria nova = new Categoria();
        nova.setNome(nomeCategoria);
        return categoriaRepository.save(nova);
    }

    // =====================================================
    // OPCIONAL: EXCLUIR CATEGORIA
    // =====================================================
    public void deletar(Long id) {
        categoriaRepository.deleteById(id);
    }
}
