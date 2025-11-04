package com.projeto.gestock.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.projeto.gestock.model.Produto;
import com.projeto.gestock.service.ProdutoService;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public String listarProdutos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Double precoMin,
            @RequestParam(required = false) Double precoMax,
            @RequestParam(required = false) String status,
            Model model) {

        List<Produto> produtos = produtoService.listarTodos();
        LocalDate hoje = LocalDate.now();

        // 🔹 Filtro por status
        if (status != null && !status.isBlank()) {
            switch (status.toLowerCase()) {
                case "vencidos" -> produtos = produtoService.buscarPorValidadeAntesDe(hoje);
                case "a_vencer" -> produtos = produtoService.buscarPorValidadeEntre(hoje, hoje.plusDays(7));
                case "validos" -> produtos = produtoService.buscarPorValidadeDepoisDe(hoje.plusDays(7));
                default -> produtos.clear(); // status inválido → lista vazia
            }
        }

        // 🔹 Filtro por nome
        if (nome != null && !nome.isBlank()) {
            String nomeLower = nome.toLowerCase();
            produtos = produtos.stream()
                    .filter(p -> p.getNome() != null && p.getNome().toLowerCase().contains(nomeLower))
                    .collect(Collectors.toList());
        }

        // 🔹 Filtro por categoria
        if (categoria != null && !categoria.isBlank()) {
            produtos = produtos.stream()
                    .filter(p -> p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria))
                    .collect(Collectors.toList());
        }

        // 🔹 Filtro por preço mínimo
        if (precoMin != null) {
            produtos = produtos.stream()
                    .filter(p -> p.getPreco() != null && p.getPreco() >= precoMin)
                    .collect(Collectors.toList());
        }

        // 🔹 Filtro por preço máximo
        if (precoMax != null) {
            produtos = produtos.stream()
                    .filter(p -> p.getPreco() != null && p.getPreco() <= precoMax)
                    .collect(Collectors.toList());
        }

        // ✅ Verifica estoque baixo
        List<Produto> produtosBaixoEstoque = produtoService.buscarComEstoqueBaixo();
        boolean temEstoqueBaixo = !produtosBaixoEstoque.isEmpty();

        // ✅ Adiciona ao modelo
        model.addAttribute("produtos", produtos);
        model.addAttribute("produtosBaixoEstoque", produtosBaixoEstoque);
        model.addAttribute("temEstoqueBaixo", temEstoqueBaixo);

        // Mantém filtros selecionados
        model.addAttribute("nome", nome);
        model.addAttribute("categoria", categoria);
        model.addAttribute("precoMin", precoMin);
        model.addAttribute("precoMax", precoMax);
        model.addAttribute("status", status);

        return "produtos";
    }

    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("produto", new Produto());
        return "produtoCadastro";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto) {
        produtoService.salvar(produto);
        return "redirect:/produtos";
    }

    @GetMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable Long id) {
        produtoService.deletar(id);
        return "redirect:/produtos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id);
        model.addAttribute("produto", produto);
        return "produtoCadastro";
    }
}
