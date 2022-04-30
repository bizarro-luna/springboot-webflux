package com.microservicios.springboot.webflux.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicios.springboot.webflux.documentos.Producto;
import com.microservicios.springboot.webflux.service.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Rest controller para productos
 * @author Hector
 *
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {
	
	/**
	 * Servicio
	 */
	@Autowired
	private ProductoService productoService;
	
	private static final Logger log= LoggerFactory.getLogger(ProductoControlador.class);
	
	@GetMapping()
	public Flux<Producto> index(){
		
		Flux<Producto> productos= productoService.findAll()
				.map(producto->{
					
					producto.setNombre(producto.getNombre().toUpperCase());
					return producto;
				}).doOnNext(p->log.info(p.getNombre()));
		
		return productos;
		
	}
	
	@GetMapping("/{id}")
	public Mono<Producto> obternerPorId(@PathVariable String id){
		
		//Mono<Producto> producto= productoRepositorio.findById(id);
		
		Flux<Producto> productos= productoService.findAll();
		
		
	   Mono<Producto> producto=productos.filter(p-> p.getId().equals(id)).next().doOnNext(p->log.info(p.getNombre()));
				
		return producto;
		
	}
	

}
