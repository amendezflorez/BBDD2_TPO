package com.findra.repository;

import com.findra.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    boolean existsByNombre(String nombre);
}
