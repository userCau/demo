package com.projeto.gestock.config;

import com.projeto.gestock.model.Usuario;
import com.projeto.gestock.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UsuarioService usuarioService;

    @ModelAttribute("usuarioNome")
    public String usuarioNome() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String email = auth.getName(); // login é o email
            Usuario usuario = usuarioService.buscarPorEmail(email);

            if (usuario != null) {
                return usuario.getNome(); // retorna o nome do usuário
            }
        }

        return null;
    }
}
