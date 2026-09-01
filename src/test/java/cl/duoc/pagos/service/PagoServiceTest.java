package cl.duoc.pagos.service;

import cl.duoc.pagos.dto.PagoRequest;
import cl.duoc.pagos.exception.ResourceNotFoundException;
import cl.duoc.pagos.model.EstadoPago;
import cl.duoc.pagos.model.Pago;
import cl.duoc.pagos.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void debeListarLosPagos() {
        List<Pago> pagos = List.of(new Pago("Entrada VIP", "ORD-1001",
                new BigDecimal("45000"), EstadoPago.APROBADO));
        when(pagoRepository.findAll()).thenReturn(pagos);

        assertSame(pagos, pagoService.listar());
    }

    @Test
    void debeCrearUnPago() {
        PagoRequest request = new PagoRequest("Entrada VIP", "ORD-1001",
                new BigDecimal("45000"), EstadoPago.APROBADO);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago pago = invocation.getArgument(0);
            pago.setId(1L);
            return pago;
        });

        Pago creado = pagoService.crear(request);

        assertEquals(1L, creado.getId());
        assertEquals("Entrada VIP", creado.getNombre());
        assertEquals("ORD-1001", creado.getOrdenCompra());
        assertEquals(new BigDecimal("45000"), creado.getMonto());
        assertEquals(EstadoPago.APROBADO, creado.getEstado());
    }

    @Test
    void debeEliminarUnPagoExistente() {
        when(pagoRepository.existsById(1L)).thenReturn(true);

        pagoService.eliminar(1L);

        verify(pagoRepository).deleteById(1L);
    }

    @Test
    void debeRechazarLaEliminacionDeUnPagoInexistente() {
        when(pagoRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pagoService.eliminar(99L));

        assertEquals("No existe un pago con id 99", exception.getMessage());
        verify(pagoRepository, never()).deleteById(99L);
    }
}
