package cl.volvo.resenas.controller;

import cl.volvo.resenas.dto.ResenaRequestDTO;
import cl.volvo.resenas.dto.ResenaResponseDTO;
import cl.volvo.resenas.service.ResenaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResenaController.class)
public class ResenaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResenaService resenaService;

    private ResenaRequestDTO requestDTO;
    private ResenaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Preparamos los datos válidos para las pruebas
        requestDTO = new ResenaRequestDTO();
        requestDTO.setComentario("¡Excelente juego!");
        requestDTO.setCalificacion(5);
        requestDTO.setUsuarioId(1L);
        requestDTO.setJuegoId(101L);

        responseDTO = new ResenaResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setComentario("¡Excelente juego!");
        responseDTO.setCalificacion(5);
        responseDTO.setUsuarioId(1L);
        responseDTO.setJuegoId(101L);
        responseDTO.setFechaCreacion(LocalDate.now());
    }

    @Test
    void crearResena_DebeRetornar201_CuandoDatosSonValidos() throws Exception {
        // Given
        when(resenaService.crearResena(any(ResenaRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.calificacion").value(5));
    }

    @Test
    void crearResena_DebeRetornar400_CuandoFaltanDatosObligatorios() throws Exception {
        // Given (Un DTO inválido, por ejemplo, sin calificación)
        ResenaRequestDTO requestInvalido = new ResenaRequestDTO();
        requestInvalido.setComentario("Juego incompleto");

        // When & Then (Debe fallar la validación @Valid del controlador)
        mockMvc.perform(post("/api/v1/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerTodas_DebeRetornar200YLista() throws Exception {
        // Given
        when(resenaService.obtenerTodas()).thenReturn(List.of(responseDTO));

        // When & Then
        mockMvc.perform(get("/api/v1/resenas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].comentario").value("¡Excelente juego!"));
    }

    @Test
    void obtenerPorUsuario_DebeRetornar200_CuandoHayResenas() throws Exception {
        // Given
        when(resenaService.obtenerPorUsuarioId(1L)).thenReturn(List.of(responseDTO));

        // When & Then
        mockMvc.perform(get("/api/v1/resenas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value(1L));
    }

    @Test
    void actualizarResena_DebeRetornar200_CuandoDatosSonValidos() throws Exception {
        // Given
        when(resenaService.actualizarResena(eq(10L), any(ResenaRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/resenas/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void eliminarResena_DebeRetornar204() throws Exception {
        // Given
        doNothing().when(resenaService).eliminarResena(10L);

        // When & Then
        mockMvc.perform(delete("/api/v1/resenas/10"))
                .andExpect(status().isNoContent());
    }
}