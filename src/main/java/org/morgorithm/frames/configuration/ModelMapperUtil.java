package org.morgorithm.frames.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

public class ModelMapperUtil {
    private static ModelMapper modelMapper;
    public static ModelMapper getModelMapper(){
        modelMapper=new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        return modelMapper;
    }
}