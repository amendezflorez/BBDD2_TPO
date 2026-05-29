package com.findra.repository;

import com.findra.model.Caso;
import com.findra.model.EstadoCaso;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CasoRepository extends MongoRepository<Caso, String> {

    Optional<Caso> findByCasoId(String casoId);

    long countByEstado(EstadoCaso estado);

    boolean existsByCasoId(String casoId);
}
