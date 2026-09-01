package cl.duoc.pagos.dto;

import cl.duoc.pagos.model.EstadoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PagoRequest(
        @NotBlank String nombre,
        @NotBlank String orden,
        @NotNull @DecimalMin("0.01") BigDecimal monto,
        @NotNull EstadoPago estado
) {
}
