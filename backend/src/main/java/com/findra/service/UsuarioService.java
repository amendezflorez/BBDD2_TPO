package com.findra.service;

import com.findra.dto.UsuarioRequest;
import com.findra.model.Usuario;
import com.findra.repository.UsuarioRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
    }

    public Usuario crear(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre().trim());
        usuario.setRol(request.rol().trim().toUpperCase());
        usuario.setOrganismo(request.organismo().trim().toUpperCase());
        return usuarioRepository.save(usuario);
    }
}
