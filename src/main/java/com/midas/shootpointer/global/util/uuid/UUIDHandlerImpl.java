package com.midas.shootpointer.global.util.uuid;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDHandlerImpl implements UUIDHandler{

    @Override
    public UUID generate() {
        return UUID.randomUUID();
    }
}
