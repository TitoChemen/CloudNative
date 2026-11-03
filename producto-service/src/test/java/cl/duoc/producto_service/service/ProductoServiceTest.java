package cl.duoc.producto_service.service;

import cl.duoc.producto_service.model.Categoria;
import cl.duoc.producto_service.model.Producto;
import cl.duoc.producto_service.repository.CategoriaRepository;
import cl.duoc.producto_service.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void testSaveProducto_WithValidCategory_Success() {
        // Given
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("Electrónica");

        Producto p = new Producto();
        p.setNombre("Laptop");
        p.setCategoria(cat);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(productoRepository.save(any(Producto.class))).thenReturn(p);

        // When
        Producto resultado = productoService.save(p);

        // Then
        assertNotNull(resultado);
        assertEquals("Electrónica", resultado.getCategoria().getNombre());
        verify(productoRepository, times(1)).save(p);
    }

    @Test
    void testSaveProducto_WithInvalidCategory_ThrowsException() {
        // Given
        Categoria catInexistente = new Categoria();
        catInexistente.setId(999L);

        Producto p = new Producto();
        p.setCategoria(catInexistente);

        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> productoService.save(p));
        verify(productoRepository, never()).save(any());
    }
}