package cl.volvo.resenas.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ResenaResponseDTO {
    private Long id;
    private String comentario;
    private Integer calificacion;
    private Long usuarioId;
    private LocalDate fechaCreacion;
    private Long juegoId;
}
