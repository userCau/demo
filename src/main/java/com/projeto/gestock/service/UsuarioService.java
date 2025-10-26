package com.projeto.gestock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.projeto.gestock.model.Usuario;
import com.projeto.gestock.repository.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario salvarUsuario(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha())); // criptografa senha
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> buscarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário inválido: " + id));
    }

    public void deletarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public boolean atualizarSenha(String email, String senhaAtual, String novaSenha) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null && passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            usuario.setSenha(passwordEncoder.encode(novaSenha));
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    // 🔍 Filtro de pesquisa
    public List<Usuario> buscarPorNomeOuEmail(String termo) {
        if (termo == null || termo.isEmpty()) {
            return usuarioRepository.findAll();
        }
        return usuarioRepository.buscarPorNomeOuEmail(termo);
    }
}
