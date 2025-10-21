package com.projeto.gestock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projeto.gestock.model.Produto;
import com.projeto.gestock.service.ProdutoService;
import com.projeto.gestock.service.UsuarioService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String dashboard(Model model) {
        List<Produto> todosProdutos = produtoService.listarTodos();
        long totalProdutos = todosProdutos.size();
        long totalUsuarios = usuarioService.buscarTodosUsuarios().size();

        List<Produto> produtosVencidos = todosProdutos.stream()
                .filter(p -> p.getValidade() != null && p.getValidade().isBefore(LocalDate.now()))
                .collect(Collectors.toList());

        List<Produto> produtosProximoVencimento = todosProdutos.stream()
                .filter(p -> p.getValidade() != null
                        && !p.getValidade().isBefore(LocalDate.now())
                        && !p.getValidade().isAfter(LocalDate.now().plusDays(7)))
                .collect(Collectors.toList());

        // 🔹 Adiciona dados para os gráficos
        model.addAttribute("categorias", produtoService.contarPorCategoria());
        model.addAttribute("status", produtoService.contarPorStatus());

        model.addAttribute("totalProdutos", totalProdutos);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("produtosVencidos", produtosVencidos);
        model.addAttribute("produtosProximoVencimento", produtosProximoVencimento);

        return "dashboard";
    }
}
