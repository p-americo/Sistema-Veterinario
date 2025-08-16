package br.com.clinicavet.clinica_api.controller;

import br.com.clinicavet.clinica_api.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.dto.ClienteResponseDTO;
import br.com.clinicavet.clinica_api.service.ClienteServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteServiceImplement clienteServiceImplement;

    @Autowired
    public ClienteController(ClienteServiceImplement clienteServiceImplement) {
        this.clienteServiceImplement = clienteServiceImplement;
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criarCliente(@RequestBody ClienteRequestDTO clienteRequestDTO, UriComponentsBuilder uriBuilder) {
        ClienteResponseDTO responseDTO = clienteServiceImplement.criarCliente(clienteRequestDTO);
        URI uri = uriBuilder.path("/api/clientes/{id}").buildAndExpand(responseDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodosClientes() {
        List<ClienteResponseDTO> listaClientes = clienteServiceImplement.listarTodos();
        return ResponseEntity.ok(listaClientes);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<ClienteResponseDTO>> buscarClientePorNome(@PathVariable String nome) {
        List<ClienteResponseDTO> listaClientes = clienteServiceImplement.buscarPorNome(nome);
        return ResponseEntity.ok(listaClientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarClientePorId(@PathVariable Long id) {
        ClienteResponseDTO responseDTO = clienteServiceImplement.buscarPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable Long id, @RequestBody ClienteRequestDTO UpdateRequestDTO) {
        ClienteResponseDTO responseDTO = clienteServiceImplement.atualizarCliente(id, UpdateRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteServiceImplement.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }
}