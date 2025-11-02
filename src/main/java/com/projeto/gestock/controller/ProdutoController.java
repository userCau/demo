package com.projeto.gestock.controller;

import java.time.LocalDate;
import java.util.List;

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
            @RequestParam(required = false) String status,
            Model model) {

        List<Produto> produtos;
        LocalDate hoje = LocalDate.now();

        // ✅ Filtro por status (validos, vencidos, a vencer)
        if ("vencidos".equalsIgnoreCase(status)) {
            produtos = produtoService.buscarPorValidadeAntesDe(hoje);
        } else if ("a_vencer".equalsIgnoreCase(status)) {
            produtos = produtoService.buscarPorValidadeEntre(hoje, hoje.plusDays(7));
        } else if ("validos".equalsIgnoreCase(status)) {
            produtos = produtoService.buscarPorValidadeDepoisDe(hoje.plusDays(7));
        } else {
            produtos = produtoService.listarTodos();
        }

        // ✅ Filtro por nome (mantém compatibilidade com busca existente)
        if (nome != null && !nome.isBlank()) {
            String nomeLower = nome.toLowerCase();
            produtos = produtos.stream()
                    .filter(p -> p.getNome().toLowerCase().contains(nomeLower))
                    .toList();
        }

        model.addAttribute("produtos", produtos);
        model.addAttribute("status", status);
        model.addAttribute("nome", nome);
        return "produtos"; // mantém sua view atual
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
