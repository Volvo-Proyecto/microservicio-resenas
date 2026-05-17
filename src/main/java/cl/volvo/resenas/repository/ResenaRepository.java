package cl.volvo.resenas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.volvo.resenas.model.Resena;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long>{

    // Esto por si se quieren ver todas las reseñas del usuario X
    List<Resena> findByUsuarioId(Long usuarioId);
}
