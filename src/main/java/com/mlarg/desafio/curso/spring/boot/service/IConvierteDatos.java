package com.mlarg.desafio.curso.spring.boot.service;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
