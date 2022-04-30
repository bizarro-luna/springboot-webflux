package com.microservicios.springboot.webflux.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservicios.springboot.webflux.documentos.Categoria;
import com.microservicios.springboot.webflux.documentos.Producto;
import com.microservicios.springboot.webflux.repository.CategoriaRepository;
import com.microservicios.springboot.webflux.repository.ProductoRepository;
import com.microservicios.springboot.webflux.service.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Servicio para implementar negocio de producto
 * @author Hector
 *
 */
@Service
public class ProductoServiceImpl implements ProductoService  {
	
	
//	private static final Logger log= LoggerFactory.getLogger(ProductoServiceImpl.class);
	
	/*
	 * Repositorio de producto
	 */
	@Autowired
	private ProductoRepository productoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;


	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#findAll()
	 */
	@Override
	public Flux<Producto> findAll() {
		// TODO Auto-generated method stub
		return productoRepository.findAll();
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#findById(java.lang.String)
	 */
	@Override
	public Mono<Producto> findById(String id) {
		return productoRepository.findById(id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#save(com.microservicios.springboot.webflux.documentos.Producto)
	 */
	@Override
	public Mono<Producto> save(Producto producto) {
		return productoRepository.save(producto);
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#delete(com.microservicios.springboot.webflux.documentos.Producto)
	 */
	@Override
	public Mono<Void> delete(Producto producto) {
		return productoRepository.delete(producto);
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#findAllConNombreUpperCase()
	 */
	@Override
	public Flux<Producto> findAllConNombreUpperCase() {
		return productoRepository.findAll().map(producto->{
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#findAllConNombreUpperCaseRepeat(java.lang.Long)
	 */
	@Override
	public Flux<Producto> findAllConNombreUpperCaseRepeat(Long repeticiones) {
		return findAllConNombreUpperCase().repeat(repeticiones);
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#findAllCategoria()
	 */
	@Override
	public Flux<Categoria> findAllCategoria() {
		return categoriaRepository.findAll();
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#findByIdCategoria(java.lang.String)
	 */
	@Override
	public Mono<Categoria> findByIdCategoria(String id) {
		return categoriaRepository.findById(id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.microservicios.springboot.webflux.service.ProductoService#saveCategoria(com.microservicios.springboot.webflux.documentos.Categoria)
	 */
	@Override
	public Mono<Categoria> saveCategoria(Categoria categoria) {
		return categoriaRepository.save(categoria);
	}
	
	

}
