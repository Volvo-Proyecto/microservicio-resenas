package cl.volvo.resenas.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "resenas")
@Data
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 500)
    private String comentario;
    @Column(nullable = false)
    private Integer calificacion; //1 a 5 estrellas
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "juego_id", nullable = false)
    private Long juegoId;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    // Se llena la fecha automáticamente cuando se crea una reseña
    @PrePersist
    public void prePersist(){
        this.fechaCreacion = LocalDate.now();
    }

}