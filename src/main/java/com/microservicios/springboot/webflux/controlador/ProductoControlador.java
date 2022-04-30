package com.microservicios.springboot.webflux.controlador;


import java.time.Duration;
import java.time.LocalDate;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.microservicios.springboot.webflux.documentos.Categoria;
import com.microservicios.springboot.webflux.documentos.Producto;
import com.microservicios.springboot.webflux.service.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Controlador para el producto
 * @author Hector
 *
 */
@SessionAttributes("producto")
@Controller
public class ProductoControlador {
	
	/**
	 * Servicio
	 */
	@Autowired
	private ProductoService servicio;
	
	/*
	 * Variable para los logs
	 */
	private static final Logger log= LoggerFactory.getLogger(ProductoControlador.class);
	
	
	
	/**
	 * Atributo para todas las vistas del controlador Producto
	 * @return
	 */
	@ModelAttribute("categorias")
	public Flux<Categoria> categorias(){
		return servicio.findAllCategoria();
	}
	
	/**
	 * Metodo para ver la foto
	 * @param id
	 * @return
	 */
	@GetMapping("/uploads/img/{id}")
	public Mono<ResponseEntity<Resource>> verFoto(@PathVariable String id){

		return servicio.findById(id)
				.defaultIfEmpty(new Producto())
				.flatMap(p-> {
			    	if(p.getId()==null) {
			    		//return Mono.error(new InterruptedException("No existe el producto a eliminar!"));
			    		return Mono.just(ResponseEntity.notFound().build());
			    	}
			    	Resource imagen= new ByteArrayResource(p.getFoto());
			    	return Mono.just(ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imagen));
			    });
	}
	
	/**
	 * Metodo para ver el detalle 
	 * @param modelo
	 * @param id
	 * @return
	 */
	@GetMapping("ver/{id}")
	public Mono<String> ver(Model modelo,@PathVariable String id){
		
		return servicio.findById(id)
				.doOnNext(p->{
					modelo.addAttribute("producto", p);
					modelo.addAttribute("titulo", "Detalle del producto");
				})
				.switchIfEmpty(Mono.just(new Producto()))
				.flatMap(p->{
					if(p.getId()==null) {
			    		return Mono.error(new InterruptedException("No existe el producto"));
			    	}
			    	return Mono.just(p);
				}).then(Mono.just("ver"))
				.onErrorResume(ex->Mono.just("redirect:/listar?error=no+existe+el+producto"));
		
	}
	
	/**
	 * Metodo para ir redireccionar al formulario de crear
	 * @param modelo
	 * @return
	 */
	@GetMapping("/form")
	public Mono<String> crear(Model modelo){
		
		modelo.addAttribute("producto",new Producto());
		modelo.addAttribute("titulo","Crear Producto");
		modelo.addAttribute("boton","Crear");
		return Mono.just("form");
	}
	
	/**
	 * Metodo para ir al formulario de editar
	 * @param id
	 * @param modelo
	 * @return
	 */
	@GetMapping("/form/{id}")
	public Mono<String> editar(@PathVariable String id, Model modelo){
		
		Mono<Producto> productoMono=servicio.findById(id).doOnNext(p->{
			log.info("Producto: "+p.getNombre());
		});
		
		
		modelo.addAttribute("titulo", "Editar Producto");
		modelo.addAttribute("producto", productoMono);
		modelo.addAttribute("boton","Editar");
		return Mono.just("form");
	}
	
	/**
	 * Metodo para ir al formulario de ediar 2
	 * @param id
	 * @param modelo
	 * @return
	 */
	@GetMapping("/form-v2/{id}")
	public Mono<String> editarV2(@PathVariable String id, Model modelo){
		
		return servicio.findById(id).doOnNext(p->{
			log.info("Producto: "+p.getNombre());
			modelo.addAttribute("titulo", "Editar Producto");
			modelo.addAttribute("producto", p);	
			modelo.addAttribute("boton","Editar");
		}).defaultIfEmpty(new Producto())
	    .flatMap(p-> {
	    	if(p.getId()==null) {
	    		return Mono.error(new InterruptedException("No existe el producto"));
	    	}
	    	return Mono.just(p);
	    })
		.then(Mono.just("form"))
		.onErrorResume(ex->Mono.just("redirect:/listar?error=no+existe+el+producto"));
	}
	
	/**
	 * Metodo para guardar el producto
	 * @param producto
	 * @param result
	 * @param status
	 * @param modelo
	 * @return
	 */
	@PostMapping("/form")
	public Mono<String> guardar(@Valid Producto producto,BindingResult result  ,@RequestPart FilePart archivo,   SessionStatus status,Model modelo){
		
		
		if(result.hasErrors()) {
			modelo.addAttribute("titulo", "Errores en Producto");
			modelo.addAttribute("boton","Guardar");
			return Mono.just("form");
		}
		
		Mono<Categoria> categoria= servicio.findByIdCategoria(producto.getCategoria().getId());
		status.setComplete();
		
		return categoria.flatMap(c->{
			
			if(producto.getFecha()==null) {
				producto.setFecha(LocalDate.now());
			}
			if(archivo!=null) {
				Mono<byte[]> monoBytes= mergeDataBuffers(archivo.content());
				//Mono.block cuando el Mono es de tipo primitivo  Mono<byte[]>
				producto.setFoto(monoBytes.block());
			}
			
			producto.setCategoria(c);
			return servicio.save(producto);
			
		})
		.doOnNext(p->{ 
			log.info("La categoria seleccionada "+p.getCategoria().getNombre()+" ID: "+p.getCategoria().getId());
			log.info("El producto guardado "+p.getNombre()+" ID: "+p.getId());
			})
		.thenReturn("redirect:/listar?success=Producto+guardardo+con+éxito");
	}
	
	/**
	 * Metodo para obtener los byte[] del archivo 
	 * @param dataBufferFlux
	 * @return
	 */
	private Mono<byte[]> mergeDataBuffers(Flux<DataBuffer> dataBufferFlux) {
        return DataBufferUtils.join(dataBufferFlux)
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    
                    return bytes;
                });
    }
	
	/**
	 * Metodo para eliminar el producto
	 * @param id
	 * @return
	 */
	@GetMapping("/eliminar/{id}")
	public Mono<String> eliminar(@PathVariable String id){
		
		return servicio.findById(id)
				.defaultIfEmpty(new Producto())
				.flatMap(p-> {
			    	if(p.getId()==null) {
			    		return Mono.error(new InterruptedException("No existe el producto a eliminar!"));
			    	}
			    	return Mono.just(p);
			    })
				.flatMap(p->{
					log.info("Producto a eliminar: "+p.getNombre());
					log.info("Producto a eliminar: "+p.getId());
			return servicio.delete(p);
		}).then(Mono.just("redirect:/listar?success=El+producto+fue+eliminado+con+éxito"))
		  .onErrorResume(ex->Mono.just("redirect:/listar?error=No+existe+el+producto+a+eliminar"));
	}
	
	
	/**
	 * Redirigir a la pagiuna de listar los productos
	 * @param modelo
	 * @return
	 */
	@GetMapping({"/listar","/"})
	public String listar(Model modelo) {
		Flux<Producto> productos= servicio.findAllConNombreUpperCase();
		
		productos.subscribe(p->log.info(p.getNombre()));
		
		
		modelo.addAttribute("lista", productos);
		modelo.addAttribute("titulo", "Listado de productos");
		return "listar";
	}
	
	/**
	 * Redirigir a listar-full
	 * @param modelo
	 * @return
	 */
	@GetMapping("/listar-full")
	public String listarFull(Model modelo) {
		Flux<Producto> productos= servicio.findAllConNombreUpperCaseRepeat(5000L);
		
		
		
		modelo.addAttribute("lista", productos);
		modelo.addAttribute("titulo", "Listado de productos");
		return "listar";
	}
	
	/**
	 * Redirigir a listar-chuncked
	 * @param modelo
	 * @return
	 */
	@GetMapping("/listar-chuncked")
	public String listarChuncked(Model modelo) {
		Flux<Producto> productos= servicio.findAllConNombreUpperCaseRepeat(5000L);
		
		
		
		modelo.addAttribute("lista", productos);
		modelo.addAttribute("titulo", "Listado de productos");
		return "listar-chunked";
	}
	
	
	/**
	 * Redirigir a listar-datadriver
	 * @param modelo
	 * @return
	 */
	@GetMapping("/listar-datadriver")
	public String listarDataDriver(Model modelo) {
		Flux<Producto> productos= servicio.findAllConNombreUpperCase().delayElements(Duration.ofSeconds(1));
		
		productos.subscribe(p->log.info(p.getNombre()));
		
		                                 //Para mostrar ir mostrando elemento por elemento
		modelo.addAttribute("lista", new ReactiveDataDriverContextVariable(productos,1) );
		modelo.addAttribute("titulo", "Listado de productos");
		return "listar";
	}
	
	
	

}
