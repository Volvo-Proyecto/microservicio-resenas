package cl.volvo.resenas.service;

import cl.volvo.resenas.dto.ResenaRequestDTO;
import cl.volvo.resenas.dto.ResenaResponseDTO;
import cl.volvo.resenas.model.Resena;
import cl.volvo.resenas.repository.ResenaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    // 1. Crear Reseña (CREATE)
    public ResenaResponseDTO crearResena(ResenaRequestDTO requestDTO) {
        log.info("Intentando crear reseña para el usuario ID: {}", requestDTO.getUsuarioId());

        // PASO A: Llamar al microservicio de Usuarios
        try {
            webClientBuilder.build()
                    .get() // Se usa el método GET
                    .uri("http://localhost:8085/api/v0/usuarios/" + requestDTO.getUsuarioId()) // La ruta del microservicio de Usuarios para obtener un usuario por ID
                    .retrieve() // Ejecutamos la solicitud y esperamos la respuesta
                    .bodyToMono(Object.class) // Convertimos la respuesta a un Mono de tipo Object (no nos importa el contenido, solo si existe o no)
                    .block(); // block() hace que la llamada sea síncrona, esperando la respuesta antes de continuar
            
            log.info("Usuario ID {} validado correctamente en el microservicio de Usuarios", requestDTO.getUsuarioId());

        } catch (WebClientResponseException.NotFound e) {
            // Si el otro microservicio responde con un Error 404 (No Encontrado)
            log.error("El usuario con ID {} no existe.", requestDTO.getUsuarioId());
            throw new RuntimeException("No se puede crear la reseña: El usuario indicado no existe en el sistema.");
        } catch (Exception e) {
            // Si el otro microservicio está apagado o hay un error de red
            log.error("Error de conexión con el microservicio de Usuarios: {}", e.getMessage());
            throw new RuntimeException("Error interno: No se pudo verificar la existencia del usuario.");
        }

        // PASO B: Llamar al microservicio de Juegos
        try {
            webClientBuilder.build()
                    .get() 
                    .uri("http://localhost:8083/api/v1/juegos/" + requestDTO.getJuegoId()) 
                    .retrieve() 
                    .bodyToMono(Object.class) 
                    .block(); 
            
            log.info("Juego ID {} validado correctamente en el microservicio de Juegos", requestDTO.getJuegoId());

        } catch (WebClientResponseException.NotFound e) {
            log.error("El juego con ID {} no existe.", requestDTO.getJuegoId());
            throw new RuntimeException("No se puede crear la reseña: El juego indicado no existe en el sistema.");
        } catch (Exception e) {
            log.error("Error de conexión con el microservicio de Juegos: {}", e.getMessage());
            throw new RuntimeException("Error interno: No se pudo verificar la existencia del juego.");
        }

        // PASO C: Si no hubo errores arriba, ambos existen. Procedemos a guardar la reseña.
        Resena resena = new Resena();
        resena.setComentario(requestDTO.getComentario());
        resena.setCalificacion(requestDTO.getCalificacion());
        resena.setUsuarioId(requestDTO.getUsuarioId());
        resena.setJuegoId(requestDTO.getJuegoId()); // Agregamos el ID del juego a la base de datos

        Resena guardada = resenaRepository.save(resena);
        log.info("Reseña creada y guardada con éxito con ID: {}", guardada.getId());

        return mapearADTO(guardada);
    }

    // 2. Obtener todas las reseñas
    public List<ResenaResponseDTO> obtenerTodas() {
        log.info("Obteniendo todas las reseñas");
        List<Resena> resenas = resenaRepository.findAll();
        List<ResenaResponseDTO> lista = new ArrayList<>();
        for (Resena r : resenas) {
            lista.add(mapearADTO(r));
        }
        return lista;
    }

    // 3. Obtener reseñas de un usuario en específico
    public List<ResenaResponseDTO> obtenerPorUsuarioId(Long usuarioId) {
        log.info("Buscando reseñas del usuario ID: {}", usuarioId);
        List<Resena> resenas = resenaRepository.findByUsuarioId(usuarioId);
        List<ResenaResponseDTO> lista = new ArrayList<>();
        for (Resena r : resenas) {
            lista.add(mapearADTO(r));
        }
        return lista;
    }

    // 4. Actualizar una reseña existente (UPDATE)
    public ResenaResponseDTO actualizarResena(Long id, ResenaRequestDTO requestDTO) {
        log.info("Actualizando reseña con ID: {}", id);

        // Buscamos si la reseña existe
        Resena resenaExistente = resenaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No se pudo actualizar. La reseña con ID {} no existe.", id);
                    return new RuntimeException("Reseña no encontrada para actualizar");
                });

        // Solo actualizamos el comentario y la calificación (no tiene sentido cambiar al autor)
        resenaExistente.setComentario(requestDTO.getComentario());
        resenaExistente.setCalificacion(requestDTO.getCalificacion());

        // Guardamos los cambios
        Resena guardada = resenaRepository.save(resenaExistente);
        log.info("Reseña ID: {} actualizada correctamente", id);

        return mapearADTO(guardada);
    }

    // 5. Eliminar una reseña (DELETE)
    public void eliminarResena(Long id) {
        log.info("Iniciando eliminación de reseña con ID: {}", id);

        // Verificamos si existe
        if (!resenaRepository.existsById(id)) {
            log.error("No se puede eliminar. La reseña con ID {} no existe.", id);
            throw new RuntimeException("Reseña no encontrada para eliminar");
        }

        resenaRepository.deleteById(id);
        log.info("Reseña con ID: {} eliminada de la base de datos", id);
    }

    // Método auxiliar para mapear de entidad a DTO
    private ResenaResponseDTO mapearADTO(Resena resena) {
        ResenaResponseDTO dto = new ResenaResponseDTO();
        dto.setId(resena.getId());
        dto.setComentario(resena.getComentario());
        dto.setCalificacion(resena.getCalificacion());
        dto.setUsuarioId(resena.getUsuarioId());
        dto.setJuegoId(resena.getJuegoId()); // <-- ESTA LÍNEA ES LA NUEVA
        dto.setFechaCreacion(resena.getFechaCreacion());
        return dto;
    }
}
