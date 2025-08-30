package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.SenhaUpdateDTO;
import br.com.clinicavet.clinica_api.service.Interface.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import br.com.clinicavet.clinica_api.model.Usuario;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("{id}/atualizar-senha")
    public ResponseEntity<?> atualizarSenha(@PathVariable Long id, @RequestBody SenhaUpdateDTO dto){
        if (!dto.getNovaSenha().equals(dto.getConfirmarNovaSenha())) {
            return ResponseEntity.badRequest().body("Nova senha e confirmação não conferem.");
        }
        try {
            usuarioService.atualizarSenha(id, dto.getSenhaAtual(), dto.getNovaSenha());
            return ResponseEntity.ok("Senha atualizada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
