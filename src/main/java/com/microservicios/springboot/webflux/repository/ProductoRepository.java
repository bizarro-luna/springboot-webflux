package com.microservicios.springboot.webflux.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.microservicios.springboot.webflux.documentos.Producto;

/*
 * Repositorio reactivo para mongo
 */
public interface ProductoRepository extends ReactiveMongoRepository<Producto,String> {

}
