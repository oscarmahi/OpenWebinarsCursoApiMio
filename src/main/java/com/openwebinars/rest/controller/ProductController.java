package com.openwebinars.rest.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.openwebinars.rest.dto.CreateProductDTO;
import com.openwebinars.rest.dto.ProductDTO;
import com.openwebinars.rest.dto.converter.ProductDTOConverter;
import com.openwebinars.rest.error.ApiError;
import com.openwebinars.rest.error.ProductNotFoundException;
import com.openwebinars.rest.model.Category;
import com.openwebinars.rest.repository.ICategoryRepository;
import com.openwebinars.rest.service.CategoryService;
import com.openwebinars.rest.service.ProductService;
import com.openwebinars.rest.upload.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.openwebinars.rest.model.Product;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoriaService;

	@Autowired
	private ProductDTOConverter productDTOConverter;

	@Autowired
	private StorageService storageService;

	/**
	 * Obtenemos todos los productos
	 * 
	 * @return
	 */
	@GetMapping("/product")
	//@CrossOrigin(origins = "http://localhos:8080", methods = {RequestMethod.GET, RequestMethod.POST})        //esto es para habilitar CORS a nivel de método o a nivel de clase
	public ResponseEntity<?> obtenerTodos() {
		List<Product> result = productService.findAllService();
		if (result.isEmpty()){
			return ResponseEntity.notFound().build();
		}else{
			return ResponseEntity.ok(result);
		}
	}

	/**
	 * Obtenemos todos los productos con el productoDTOConverter
	 *
	 * @return
	 */
	@GetMapping("/product_converter")
	public ResponseEntity<?> obtenerTodosConverter() {
		List<Product> result = productService.findAllService();
		if (result.isEmpty()){
			return ResponseEntity.notFound().build();
		}else{
			List<ProductDTO> dtoList = result.stream()
					.map(productDTOConverter::convertToDto)
					.collect(Collectors.toList());
			return ResponseEntity.ok(dtoList);
		}
	}

	/**
	 * Obtenemos todos los productos con la categoria
	 *
	 * @return
	 */
	@GetMapping("/product_categoria")
	public ResponseEntity<?> obtenerTodosConCategoria() {
		List<Product> result = productService.findAllService();
		if (result.isEmpty()){
			return ResponseEntity.notFound().build();
		}else{
			return ResponseEntity.ok(result);
		}
	}

	/**
	 * Obtenemos un producto en base a su ID
	 * 
	 * @param id
	 * @return Null si no encuentra el producto
	 */
	@GetMapping("/product/{id}")
//	public ResponseEntity<?> showOne(@PathVariable Long id) {
//		Product result = productService.findByIdService(id).orElse(null);
//		if (result == null){
//			return ResponseEntity.notFound().build();
//		}else{
//			return ResponseEntity.ok(result);
//		}
	public Product showOne(@PathVariable Long id) {
		return productService.findByIdService(id).orElseThrow(() -> new ProductNotFoundException(id));
	}

	/**
	 * Insertamos un nuevo producto
	 *
	 * @param nuevo
	 * @return 201 y el producto insertado
	 */
	@PostMapping(value = "/product", consumes= MediaType.MULTIPART_FORM_DATA_VALUE) //Aunque no es obligatorio, podemos indicar que se consume multipart/form-data
	public ResponseEntity<?> nuevoProducto(@RequestPart("nuevo") CreateProductDTO nuevo,
										   @RequestPart("file") MultipartFile file) {

		// Almacenamos el fichero y obtenemos su URL
		String urlImagen = null;

		if (!file.isEmpty()) {
			String imagen = storageService.store(file);
			urlImagen = MvcUriComponentsBuilder
					// El segundo argumento es necesario solo cuando queremos obtener la imagen
					// En este caso tan solo necesitamos obtener la URL
					.fromMethodName(FileController.class, "serveFile", imagen, null)
					.build().toUriString();
		}

		// Construimos nuestro nuevo Producto a partir del DTO
		// Como decíamos en ejemplos anteriores, esto podría ser más bien código
		// de un servicio, pero lo dejamos aquí para no hacer más complejo el código.
		Product nuevoProducto = new Product();
		nuevoProducto.setName(nuevo.getName());
		nuevoProducto.setPrice(nuevo.getPrice());
		nuevoProducto.setImage(urlImagen);
		Category category = categoriaService.findByIdService(nuevo.getCategoryId()).orElse(null);
		nuevoProducto.setCategory(category);
		return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveService(nuevoProducto));
	}

	/**
	 * Insertamos un nuevo producto, sin el campo de la imagen
	 * 
	 * @param newP
	 * @return producto insertado
	 */
//	@PostMapping("/product")
//	public ResponseEntity<Product> newProduct(@RequestBody Product newP) {
//		Product result = productService.saveService(newP);
//		return ResponseEntity.status(HttpStatus.CREATED).body(result);
//	}

	/**
	 * Insertamos un nuevo producto a traves del converter DTO
	 *
	 * @param newProductDTO
	 * @return producto insertado
	 */
	@PostMapping("/product_converter")
	public ResponseEntity<Product> newProductConDto(@RequestBody CreateProductDTO newProductDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveServiceConDTO(newProductDTO));
	}

	/**
	 *
	 * @param editP
	 * @param id
	 * @return
	 */
	@PutMapping("/product/{id}")
//	public ResponseEntity<?> editProduct(@RequestBody Product editP, @PathVariable Long id) {	//va cuando pongo lo comentado abajo
	public Product editProduct(@RequestBody Product editP, @PathVariable Long id) {
		return productService.findByIdService(id).map(p -> {
					p.setName(editP.getName());
					p.setPrice(editP.getPrice());
					return productService.saveService(p);
				}).orElseThrow(()->new ProductNotFoundException(id));	//es otra forma de hacerlo, como la de abajo
//		}).orElseGet(() -> {
//			return ResponseEntity.notFound().build();
//		});
	}

	/**
	 * Borra un producto del catálogo en base a su id
	 * @param id
	 * @return
	 */
	@DeleteMapping("/product/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
//		productService.deleteByIdService(id);
//		return ResponseEntity.noContent().build();

		Product product = productService.findByIdService(id)
				.orElseThrow(()-> new ProductNotFoundException(id));
		productService.deleteService(product);
		return ResponseEntity.noContent().build();
	}

//	@ExceptionHandler(ProductNotFoundException.class)
//	public ResponseEntity<ApiError> handleProductNotFound(ProductNotFoundException ex){
//		ApiError apiError = new ApiError();
//		apiError.setEstado(HttpStatus.NOT_FOUND);
//		apiError.setFecha(LocalDateTime.now());
//		apiError.setMensaje(ex.getMessage());
//
//		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
//	}
//
//	@ExceptionHandler(JsonMappingException.class)
//	public ResponseEntity<ApiError> handleJsonMappingException(JsonMappingException ex){
//		ApiError apiError = new ApiError();
//		apiError.setEstado(HttpStatus.BAD_REQUEST);
//		apiError.setFecha(LocalDateTime.now());
//		apiError.setMensaje(ex.getMessage());
//
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
//	}


}
