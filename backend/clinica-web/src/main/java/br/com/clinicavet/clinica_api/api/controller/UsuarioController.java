package br.com.clinicavet.clinica_api.api.controller;

import br.com.clinicavet.clinica_api.application.dto.SenhaUpdateDTO;
import br.com.clinicavet.clinica_api.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import br.com.clinicavet.clinica_api.domain.model.Usuario;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento e listagem de usuários do sistema")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar todos os usuários de forma paginada", description = "Retorna uma página de usuários cadastrados no sistema.")
    public ResponseEntity<Page<Usuario>> listarTodosUsuarios(@PageableDefault(size = 10) Pageable pageable) {
        Page<Usuario> usuarios = usuarioService.listarTodosUsuarios(pageable);
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("{id}/atualizar-senha")
    @Operation(summary = "Atualizar a senha de um usuário", description = "Permite alterar a senha de acesso de um usuário existente, validando a senha atual.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Nova senha e confirmação não conferem, ou senha atual incorreta.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
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
