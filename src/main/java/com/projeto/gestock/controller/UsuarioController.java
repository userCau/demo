package com.projeto.gestock.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.projeto.gestock.model.Usuario;
import com.projeto.gestock.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.buscarTodosUsuarios());
        return "usuarios";
    }

    @GetMapping("/novo")
    public String novoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarioCadastro";
    }

    @PostMapping("/salvar")
        public String salvarUsuario(@ModelAttribute Usuario usuario) {
    usuarioService.salvarUsuario(usuario); 
    return "redirect:/usuarios";
}

@GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        return "usuarioCadastro";
    }


    @GetMapping("/deletar/{id}")
    public String deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return "redirect:/usuarios";
    }

    
}
