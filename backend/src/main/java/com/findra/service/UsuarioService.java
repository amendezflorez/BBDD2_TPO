package com.findra.service;

import com.findra.dto.UsuarioRequest;
import com.findra.model.OrganismoFuente;
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
        String organismoNorm = request.organismo().trim().toUpperCase();
        try {
            OrganismoFuente.valueOf(organismoNorm);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Organismo no reconocido: " + organismoNorm);
        }
        usuario.setOrganismo(organismoNorm);
        return usuarioRepository.save(usuario);
    }
}
