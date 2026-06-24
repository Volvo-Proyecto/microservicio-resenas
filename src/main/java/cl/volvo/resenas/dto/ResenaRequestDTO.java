package cl.volvo.resenas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Objeto que representa los datos requeridos para publicar o actualizar una reseña")
public class ResenaRequestDTO {

    @Schema(description = "Texto de la opinión o crítica del videojuego", example = "¡Excelente juego, gráficos increíbles y buena historia!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El comentario no puede estar vacío")
    private String comentario;

    @Schema(description = "Calificación numérica otorgada (del 1 al 5)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1 estrella")
    @Max(value = 5, message = "La calificación máxima es 5 estrellas")
    private Integer calificacion;

    @Schema(description = "Identificador único del usuario que escribe la reseña", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @Schema(description = "Identificador único del videojuego reseñado", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del juego es obligatorio")
    private Long juegoId;
}