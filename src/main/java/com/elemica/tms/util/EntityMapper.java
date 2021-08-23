package com.elemica.tms.util;

import org.modelmapper.ModelMapper;

public class EntityMapper<T extends Object, G extends Object> {

    private final Class<T> object;
    private final Class<G> objectDTO;

    public EntityMapper(Class<T> object, Class<G> objectDTO) {

        super();
        this.object = object;
        this.objectDTO = objectDTO;
    }

    public G convertToDTO(T object) {

        return new ModelMapper().map(object, objectDTO);
    }

    public T mapToDBObject(G objectDTO) {

        return new ModelMapper().map(objectDTO, object);
    }
}
