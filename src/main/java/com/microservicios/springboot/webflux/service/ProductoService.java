package com.microservicios.springboot.webflux.service;

import com.microservicios.springboot.webflux.documentos.Categoria;
import com.microservicios.springboot.webflux.documentos.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interfaz para el negocio de prodcutos
 * @author Hector
 *
 */
public interface ProductoService {
	
	/**
	 * Buscar todos los productos
	 * @return
	 */
	Flux<Producto> findAll();
	
	/**
	 * Buscar todos los productos con nombre en mayusculas
	 * @return
	 */
	Flux<Producto> findAllConNombreUpperCase();
	
	/**
	 * Buscar todos,con nombre en mayusculas y repeticion
	 * @param repeticiones
	 * @return
	 */
	Flux<Producto> findAllConNombreUpperCaseRepeat(Long repeticiones);
	
	/**
	 * Buscar producto por ID
	 * @param id
	 * @return
	 */
	Mono<Producto> findById(String id);
	
	
	/**
	 * Guardar Producto
	 * @param producto
	 * @return
	 */
	Mono<Producto> save(Producto producto);
	
	/**
	 * eliminar producto
	 * @param producto
	 * @return
	 */
	Mono<Void> delete(Producto producto);
	
	
	/**
	 * Buscar todas las categorias
	 * @return
	 */
	Flux<Categoria> findAllCategoria();
	
	
	/**
	 * Obtener un Mono de categoria
	 * @param id
	 * @return
	 */
	Mono<Categoria> findByIdCategoria(String id);
	
	
	/**
	 * Guardar la categoria
	 * @param categoria
	 * @return
	 */
	Mono<Categoria> saveCategoria(Categoria categoria);
	

}
