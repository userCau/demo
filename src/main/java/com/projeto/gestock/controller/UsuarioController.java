package com.projeto.gestock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.projeto.gestock.model.Usuario;
import com.projeto.gestock.service.UsuarioService;

import java.security.Principal;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Listar todos os usuários
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.buscarTodosUsuarios());
        return "usuarios";
    }

    // Formulário para novo usuário
    @GetMapping("/novo")
    public String novoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarioCadastro";
    }

    // Salvar usuário (novo ou edição)
    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.salvarUsuario(usuario);
        return "redirect:/usuarios";
    }

    // Editar usuário existente
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        return "usuarioCadastro";
    }

    // Deletar usuário
    @GetMapping("/deletar/{id}")
    public String deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return "redirect:/usuarios";
    }

    // ✅ Página de configurações do próprio usuário logado
    @GetMapping("/config")
    public String usuarioConfig(Model model, Principal principal) {
        Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
        model.addAttribute("usuario", usuario);
        return "usuarioConfig";
    }

    // ✅ Atualizar senha
    @PostMapping("/atualizarSenha")
    public String atualizarSenha(@RequestParam String senhaAtual,
                                 @RequestParam String novaSenha,
                                 Principal principal,
                                 Model model) {
        boolean sucesso = usuarioService.atualizarSenha(principal.getName(), senhaAtual, novaSenha);
        model.addAttribute("mensagem", sucesso ? "Senha atualizada com sucesso!" : "Senha atual incorreta.");
        model.addAttribute("usuario", usuarioService.buscarPorEmail(principal.getName()));
        return "usuarioConfig";
    }
}
