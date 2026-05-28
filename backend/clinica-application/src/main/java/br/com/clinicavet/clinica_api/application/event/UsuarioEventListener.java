package br.com.clinicavet.clinica_api.application.event;

import br.com.clinicavet.clinica_api.application.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsuarioEventListener {

    private final UsuarioService usuarioService;

    public UsuarioEventListener(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @EventListener
    public void handleClienteCriado(ClienteCriadoEvent event) {
        log.info("Recebido evento ClienteCriadoEvent para o cliente: {} (CPF: {})", event.cliente().getNome(), event.cliente().getCpf());
        usuarioService.criarUsuarioCliente(event.clienteDTO(), event.cliente());
        log.info("Usuário associado criado com sucesso para o cliente: {}", event.cliente().getNome());
    }

    @EventListener
    public void handleFuncionarioCriado(FuncionarioCriadoEvent event) {
        log.info("Recebido evento FuncionarioCriadoEvent para o funcionário: {} (CPF: {})", event.funcionario().getNome(), event.funcionario().getCpf());
        usuarioService.criarUsuarioFuncionario(event.funcionarioDTO(), event.funcionario());
        log.info("Usuário associado criado com sucesso para o funcionário: {}", event.funcionario().getNome());
    }
}
