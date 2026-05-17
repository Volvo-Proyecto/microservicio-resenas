package cl.volvo.resenas.controller;

import cl.volvo.resenas.dto.ResenaRequestDTO;
import cl.volvo.resenas.dto.ResenaResponseDTO;
import cl.volvo.resenas.service.ResenaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v0/resenas") // Ruta principal: localhost:8082/resenas
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    // Endpoint para crear una reseña (POST)
    @PostMapping
    public ResponseEntity<ResenaResponseDTO> crearResena(@Valid @RequestBody ResenaRequestDTO requestDTO) {
        log.info("Petición REST recibida para crear reseña del usuario ID: {}", requestDTO.getUsuarioId());
        
        ResenaResponseDTO nuevaResena = resenaService.crearResena(requestDTO);
        
        // Retornamos 201 (CREATED)
        return new ResponseEntity<>(nuevaResena, HttpStatus.CREATED);
    }

    // Endpoint para obtener todas las reseñas (GET)
    @GetMapping
    public ResponseEntity<List<ResenaResponseDTO>> obtenerTodas() {
        log.info("Petición REST recibida para listar todas las reseñas");
        
        List<ResenaResponseDTO> resenas = resenaService.obtenerTodas();
        return ResponseEntity.ok(resenas);
    }

    // Endpoint para buscar todas las reseñas de un usuario específico (GET)
    // Ejemplo: localhost:8082/resenas/usuario/1
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaResponseDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        log.info("Petición REST recibida para listar reseñas del usuario ID: {}", usuarioId);
        
        List<ResenaResponseDTO> resenas = resenaService.obtenerPorUsuarioId(usuarioId);
        return ResponseEntity.ok(resenas);
    }

    // Endpoint para actualizar una reseña (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<ResenaResponseDTO> actualizarResena(
            @PathVariable Long id, 
            @Valid @RequestBody ResenaRequestDTO requestDTO) {
        
        log.info("Petición REST recibida para actualizar reseña ID: {}", id);
        ResenaResponseDTO resenaActualizada = resenaService.actualizarResena(id, requestDTO);
        
        return ResponseEntity.ok(resenaActualizada);
    }

    // Endpoint para eliminar una reseña (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarResena(@PathVariable Long id) {
        log.info("Petición REST recibida para eliminar reseña ID: {}", id);
        resenaService.eliminarResena(id);
        
        return ResponseEntity.noContent().build();
    }
}