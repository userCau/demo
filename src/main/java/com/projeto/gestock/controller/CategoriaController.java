package com.projeto.gestock.controller;

import com.projeto.gestock.model.Categoria;
import com.projeto.gestock.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("novaCategoria", new Categoria());
        return "categorias";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Categoria categoria) {
        Categoria existente = categoriaRepository.findByNomeIgnoreCase(categoria.getNome());
        if (existente == null) {
            categoriaRepository.save(categoria);
        }
        return "redirect:/categorias";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        categoriaRepository.deleteById(id);
        return "redirect:/categorias";
    }
}
