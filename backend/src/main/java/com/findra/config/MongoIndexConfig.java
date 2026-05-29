package com.findra.config;

import com.findra.model.Caso;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;
import org.bson.Document;

@Component
public class MongoIndexConfig {

    private final MongoTemplate mongoTemplate;

    public MongoIndexConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void ensureIndexes() {
        var indexes = mongoTemplate.indexOps(Caso.class);
        indexes.ensureIndex(new Index().on("caso_id", Direction.ASC).unique());
        indexes.ensureIndex(new Index().on("estado", Direction.ASC)
                .on("fecha_activacion", Direction.DESC));
        indexes.ensureIndex(new Index().on("menor.edad", Direction.ASC)
                .on("menor.sexo", Direction.ASC));
        indexes.ensureIndex(new Index().on("historial_acciones.usuario", Direction.ASC));
        indexes.ensureIndex(new CompoundIndexDefinition(
                new Document("menor.ultima_ubicacion", "2dsphere")));
        indexes.ensureIndex(new CompoundIndexDefinition(
                new Document("reportes_ciudadanos.ubicacion", "2dsphere")));
    }
}
