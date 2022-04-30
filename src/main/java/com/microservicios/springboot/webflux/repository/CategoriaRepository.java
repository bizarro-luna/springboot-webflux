package com.microservicios.springboot.webflux.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.microservicios.springboot.webflux.documentos.Categoria;



public interface CategoriaRepository extends ReactiveMongoRepository<Categoria, String> {

}
