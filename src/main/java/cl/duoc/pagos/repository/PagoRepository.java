package cl.duoc.pagos.repository;

import cl.duoc.pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}
