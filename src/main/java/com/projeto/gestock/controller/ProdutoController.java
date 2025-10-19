package com.projeto.gestock.controller;

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
public String listarProdutos(@RequestParam(required = false) String nome, Model model) {
    if (nome != null && !nome.isBlank()) {
        model.addAttribute("produtos", produtoService.buscarPorNome(nome));
    } else {
        model.addAttribute("produtos", produtoService.listarTodos());
    }

    model.addAttribute("nome", nome); // mantém o texto digitado na barra
    return "produtos"; // o nome da view que mostra a lista
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
