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

// IMPORTACIONES DE SWAGGER / OPENAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@Slf4j
@RestController
@RequestMapping("/api/v1/resenas") // Ruta principal: localhost:8086/api/v1/resenas
@Tag(name = "Controlador de Reseñas", description = "Endpoints para crear, listar, modificar y eliminar las reseñas de videojuegos")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    // POST http://localhost:8086/api/v1/resenas
    @Operation(summary = "Crear una nueva reseña", description = "Permite a un usuario registrar una nueva reseña o calificación para un juego específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ResenaResponseDTO.class),
            examples = @ExampleObject(value = "{\n  \"id\": 10,\n  \"comentario\": \"¡Excelente juego!\",\n  \"calificacion\": 5,\n  \"usuarioId\": 1,\n  \"fechaCreacion\": \"2026-06-23\",\n  \"juegoId\": 42\n}"))),
        @ApiResponse(responseCode = "400", description = "Errores de validación en los datos enviados", content = @Content)
    })
    @PostMapping()
    public ResponseEntity<ResenaResponseDTO> crearResena(@Valid @RequestBody ResenaRequestDTO requestDTO) {
        log.info("Petición REST recibida para crear reseña del usuario ID: {}", requestDTO.getUsuarioId());
        
        ResenaResponseDTO nuevaResena = resenaService.crearResena(requestDTO);
        
        // Retornamos 201 (CREATED)
        return new ResponseEntity<>(nuevaResena, HttpStatus.CREATED);
    }

    // GET http://localhost:8086/api/v1/resenas
    @Operation(summary = "Obtener todas las reseñas", description = "Retorna una lista general con todas las reseñas publicadas en la plataforma.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida con éxito",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ResenaResponseDTO.class)))),
        @ApiResponse(responseCode = "204", description = "No hay reseñas registradas aún", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<List<ResenaResponseDTO>> obtenerTodas() {
        log.info("Petición REST recibida para listar todas las reseñas");
        
        List<ResenaResponseDTO> resenas = resenaService.obtenerTodas();
        return ResponseEntity.ok(resenas);
    }

    // GET http://localhost:8086/api/v1/resenas/{usuarioId}
    @Operation(summary = "Obtener reseñas por ID de usuario", description = "Busca y retorna el listado de reseñas realizadas por un usuario en particular.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseñas del usuario encontradas exitosamente",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ResenaResponseDTO.class)))),
        @ApiResponse(responseCode = "404", description = "El usuario no existe o no tiene reseñas", content = @Content)
    })
    @GetMapping("{usuarioId}")
    public ResponseEntity<List<ResenaResponseDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        log.info("Petición REST recibida para listar reseñas del usuario ID: {}", usuarioId);
        
        List<ResenaResponseDTO> resenas = resenaService.obtenerPorUsuarioId(usuarioId);
        if (resenas.isEmpty()) {
            throw new RuntimeException("No se encontraron reseñas para el usuario con ID: " + usuarioId);
        }
        return ResponseEntity.ok(resenas); // Retornamos código 200 (OK)
    }


    // PUT http://localhost:8086/api/v1/resenas/{id}
    @Operation(summary = "Actualizar una reseña", description = "Permite editar el contenido o la calificación de una reseña ya existente buscando por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ResenaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "La reseña solicitada no existe", content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos", content = @Content)
    })
    @PutMapping("{id}")
    public ResponseEntity<ResenaResponseDTO> actualizarResena(
            @PathVariable Long id, 
            @Valid @RequestBody ResenaRequestDTO requestDTO) {
        
        log.info("Petición REST recibida para actualizar reseña ID: {}", id);
        ResenaResponseDTO resenaActualizada = resenaService.actualizarResena(id, requestDTO);
        
        return ResponseEntity.ok(resenaActualizada);
    }

    // DELETE http://localhost:8086/api/v1/resenas/{id}
    @Operation(summary = "Eliminar una reseña", description = "Elimina permanentemente una reseña de la plataforma usando su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reseña eliminada correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "La reseña no existe", content = @Content)
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Void> eliminarResena(@PathVariable Long id) {
        log.info("Petición REST recibida para eliminar reseña ID: {}", id);
        resenaService.eliminarResena(id);
        
        return ResponseEntity.noContent().build();
    }
}