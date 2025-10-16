package com.projeto.gestock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErroController {

    @GetMapping("/acessoNegado")
    public String acessoNegado() {
        return "acessoNegado"; // nome do template (src/main/resources/templates/acesso-negado.html)
    }
}
