// cl.duoc.producto_service.repository.ProductoRepository.java
package cl.duoc.producto_service.repository;

import cl.duoc.producto_service.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Producto findByCodigo(String codigo);
}