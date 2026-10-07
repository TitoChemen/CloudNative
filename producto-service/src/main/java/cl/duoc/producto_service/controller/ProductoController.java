package cl.duoc.producto_service.controller;

import cl.duoc.producto_service.dto.ProductoDTO;
import cl.duoc.producto_service.model.Producto;
import cl.duoc.producto_service.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Catálogo Omnicanal (Productos)", description = "Núcleo de gestión de inventario y matriz de productos.")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @Operation(summary = "Recuperar catálogo maestro")
    @ApiResponse(responseCode = "200", description = "Lista de productos desplegada exitosamente.")
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarTodos() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Consultar producto específico por código o ID")
    @ApiResponse(responseCode = "200", description = "Producto localizado exitosamente.")
    @ApiResponse(responseCode = "404", description = "El producto no existe.")
    @GetMapping("/{codigo}")
    public ResponseEntity<ProductoDTO> obtener(@PathVariable String codigo) {
        ProductoDTO p = service.buscarPorCodigo(codigo);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }

    @Operation(summary = "Provisionar nuevo producto")
    @ApiResponse(responseCode = "201", description = "Entidad creada y persistida.")
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        return new ResponseEntity<>(service.save(producto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar producto existente por código o ID")
    @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente.")
    @ApiResponse(responseCode = "404", description = "El producto no existe.")
    @PutMapping("/{codigo}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable String codigo, @Valid @RequestBody Producto producto) {
        ProductoDTO p = service.actualizar(codigo, producto);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }

    @Operation(summary = "Eliminar producto por código o ID")
    @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente.")
    @ApiResponse(responseCode = "404", description = "El producto no existe.")
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminar(@PathVariable String codigo) {
        boolean eliminado = service.deleteByCodigo(codigo);
        if (!eliminado) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}