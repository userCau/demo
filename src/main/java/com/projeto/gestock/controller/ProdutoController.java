package com.projeto.gestock.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.projeto.gestock.model.Produto;
import com.projeto.gestock.model.Usuario;
import com.projeto.gestock.model.Categoria;
import com.projeto.gestock.service.ProdutoService;
import com.projeto.gestock.service.UsuarioService;
import com.projeto.gestock.service.CategoriaService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;

    public ProdutoController(ProdutoService produtoService, UsuarioService usuarioService,
            CategoriaService categoriaService) {
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
    }

    // =====================================================
    // LISTAGEM E FILTROS
    // =====================================================
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
                default -> produtos.clear();
            }
        }

        // 🔹 Filtro por nome
        if (nome != null && !nome.isBlank()) {
            String nomeLower = nome.toLowerCase();
            produtos = produtos.stream()
                    .filter(p -> p.getNome() != null && p.getNome().toLowerCase().contains(nomeLower))
                    .collect(Collectors.toList());
        }

        // 🔹 Filtro por categoria (agora Categoria é um objeto)
        if (categoria != null && !categoria.isBlank()) {
            produtos = produtos.stream()
                    .filter(p -> p.getCategoria() != null &&
                            p.getCategoria().getNome() != null &&
                            p.getCategoria().getNome().equalsIgnoreCase(categoria))
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

        // ✅ Estoque baixo
        List<Produto> produtosBaixoEstoque = produtoService.buscarComEstoqueBaixo();
        boolean temEstoqueBaixo = !produtosBaixoEstoque.isEmpty();

        // ✅ Validade vencida
        List<Produto> produtosVencidos = produtos.stream()
                .filter(p -> p.getValidade() != null && p.getValidade().isBefore(hoje))
                .collect(Collectors.toList());
        boolean temVencidos = !produtosVencidos.isEmpty();

        // ✅ Validade próxima (7 dias)
        List<Produto> produtosProximoVencimento = produtos.stream()
                .filter(p -> p.getValidade() != null &&
                        !p.getValidade().isBefore(hoje) &&
                        !p.getValidade().isAfter(hoje.plusDays(7)))
                .collect(Collectors.toList());
        boolean temProximoVencimento = !produtosProximoVencimento.isEmpty();

        // ✅ Adiciona ao modelo
        model.addAttribute("produtos", produtos);
        model.addAttribute("produtosBaixoEstoque", produtosBaixoEstoque);
        model.addAttribute("temEstoqueBaixo", temEstoqueBaixo);
        model.addAttribute("produtosVencidos", produtosVencidos);
        model.addAttribute("temVencidos", temVencidos);
        model.addAttribute("produtosProximoVencimento", produtosProximoVencimento);
        model.addAttribute("temProximoVencimento", temProximoVencimento);

        // 🔹 Adiciona categorias para o filtro
        model.addAttribute("categorias", categoriaService.listarTodas());

        // Mantém filtros selecionados
        model.addAttribute("nome", nome);
        model.addAttribute("categoria", categoria);
        model.addAttribute("precoMin", precoMin);
        model.addAttribute("precoMax", precoMax);
        model.addAttribute("status", status);

        return "produtos";
    }

    // =====================================================
    // FORMULÁRIOS
    // =====================================================

    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "produtoCadastro";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userLogado,
            @RequestParam(required = false) String novaCategoria) {

        // ✅ Se o usuário digitou uma nova categoria
        if (novaCategoria != null && !novaCategoria.isBlank()) {
            Categoria categoria = categoriaService.salvarNovaCategoria(novaCategoria);
            produto.setCategoria(categoria); // agora recebe um objeto Categoria
        } else if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            // apenas garantir que a categoria selecionada existe no banco
            Categoria existente = categoriaService.buscarPorId(produto.getCategoria().getId());
            produto.setCategoria(existente);
        }

        // ✅ Associa o criador do produto (se o campo existir)
        if (userLogado != null) {
            Usuario usuario = usuarioService.buscarPorEmail(userLogado.getUsername());
            try {
                produto.setCriadoPor(usuario);
            } catch (NoSuchMethodError e) {
                // ignora se a entidade Produto não tiver o campo criadoPor
            }
        }

        produtoService.salvar(produto);
        return "redirect:/produtos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id);
        model.addAttribute("produto", produto);
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "produtoCadastro";
    }

    @GetMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable Long id) {
        produtoService.deletar(id);
        return "redirect:/produtos";
    }
}
