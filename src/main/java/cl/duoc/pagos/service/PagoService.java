package cl.duoc.pagos.service;

import cl.duoc.pagos.dto.PagoRequest;
import cl.duoc.pagos.exception.ResourceNotFoundException;
import cl.duoc.pagos.model.Pago;
import cl.duoc.pagos.repository.PagoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public List<Pago> listar() {
        return pagoRepository.findAll();
    }

    public Pago crear(PagoRequest request) {
        Pago pago = new Pago(request.nombre(), request.orden(), request.monto(), request.estado());
        return pagoRepository.save(pago);
    }

    public void eliminar(Long id) {
        if (!pagoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe un pago con id " + id);
        }
        pagoRepository.deleteById(id);
    }
}
