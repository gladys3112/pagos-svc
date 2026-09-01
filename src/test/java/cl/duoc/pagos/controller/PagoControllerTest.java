package cl.duoc.pagos.controller;

import cl.duoc.pagos.exception.ApiExceptionHandler;
import cl.duoc.pagos.exception.ResourceNotFoundException;
import cl.duoc.pagos.model.EstadoPago;
import cl.duoc.pagos.model.Pago;
import cl.duoc.pagos.service.PagoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PagoController.class)
@Import(ApiExceptionHandler.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagoService pagoService;

    @Test
    void debeListarLosPagos() throws Exception {
        Pago pago = new Pago("Entrada VIP", "ORD-1001", new BigDecimal("45000"),
                EstadoPago.APROBADO);
        pago.setId(1L);
        when(pagoService.listar()).thenReturn(List.of(pago));

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].ordenCompra").value("ORD-1001"));
    }

    @Test
    void debeCrearUnPagoValido() throws Exception {
        Pago pago = new Pago("Entrada VIP", "ORD-1001", new BigDecimal("45000"),
                EstadoPago.APROBADO);
        pago.setId(1L);
        when(pagoService.crear(any())).thenReturn(pago);

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Entrada VIP",
                                  "orden": "ORD-1001",
                                  "monto": 45000,
                                  "estado": "APROBADO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }

    @Test
    void debeRechazarUnPagoInvalido() throws Exception {
        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"", "orden":"", "monto":0, "estado":null}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeEliminarUnPago() throws Exception {
        doNothing().when(pagoService).eliminar(1L);

        mockMvc.perform(delete("/api/pagos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void debeResponderNotFoundAlEliminarUnPagoInexistente() throws Exception {
        doThrow(new ResourceNotFoundException("No existe un pago con id 99"))
                .when(pagoService).eliminar(99L);

        mockMvc.perform(delete("/api/pagos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe un pago con id 99"));
    }
}
