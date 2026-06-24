package cl.volvo.resenas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

@Data
@Schema(description = "Objeto de respuesta con la información detallada de una reseña publicada")
public class ResenaResponseDTO {

    @Schema(description = "Identificador único de la reseña en el sistema", example = "10")
    private Long id;

    @Schema(description = "Texto de la opinión o crítica", example = "¡Excelente juego, gráficos increíbles y buena historia!")
    private String comentario;

    @Schema(description = "Calificación otorgada (del 1 al 5)", example = "5")
    private Integer calificacion;

    @Schema(description = "ID del usuario creador de la reseña", example = "1")
    private Long usuarioId;

    @Schema(description = "Fecha de publicación de la reseña", example = "2026-06-23")
    private LocalDate fechaCreacion;

    @Schema(description = "ID del juego evaluado", example = "42")
    private Long juegoId;
}