package cl.volvo.resenas.service;

import cl.volvo.resenas.dto.ResenaRequestDTO;
import cl.volvo.resenas.dto.ResenaResponseDTO;
import cl.volvo.resenas.model.Resena;
import cl.volvo.resenas.repository.ResenaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    // Utilizamos RETURNS_DEEP_STUBS para poder mockear la cadena fluida de WebClient
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient usuariowebClient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient juegowebClient;

    @InjectMocks
    private ResenaService resenaService;

    private Resena resenaMock;
    private ResenaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        resenaMock = new Resena();
        resenaMock.setId(1L);
        resenaMock.setComentario("¡Excelente juego!");
        resenaMock.setCalificacion(5);
        resenaMock.setUsuarioId(1L);
        resenaMock.setJuegoId(101L);
        resenaMock.setFechaCreacion(LocalDate.now());

        requestDTO = new ResenaRequestDTO();
        requestDTO.setComentario("¡Excelente juego!");
        requestDTO.setCalificacion(5);
        requestDTO.setUsuarioId(1L);
        requestDTO.setJuegoId(101L);
    }

    @Test
    void crearResena_Exitoso() {
        // Given (Dado)
        // Simulamos que el WebClient responde OK para Usuario y Juego
        when(usuariowebClient.get().uri(anyString()).retrieve().bodyToMono(Object.class).block())
                .thenReturn(new Object());
        when(juegowebClient.get().uri(anyString()).retrieve().bodyToMono(Object.class).block())
                .thenReturn(new Object());
        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaMock);

        // When (Cuando)
        ResenaResponseDTO response = resenaService.crearResena(requestDTO);

        // Then (Entonces)
        assertNotNull(response);
        assertEquals("¡Excelente juego!", response.getComentario());
        assertEquals(5, response.getCalificacion());
        verify(resenaRepository, times(1)).save(any(Resena.class));
    }

    @Test
    void crearResena_LanzaExcepcion_SiUsuarioNoExiste() {
        // Given (Dado)
        // Simulamos que el WebClient lanza NotFound (404)
        WebClientResponseException.NotFound notFoundException = mock(WebClientResponseException.NotFound.class);
        when(usuariowebClient.get().uri(anyString()).retrieve().bodyToMono(Object.class).block())
                .thenThrow(notFoundException);

        // When & Then (Cuando y Entonces)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            resenaService.crearResena(requestDTO);
        });

        assertTrue(exception.getMessage().contains("El usuario indicado no existe"));
        verify(resenaRepository, never()).save(any(Resena.class)); // Verificamos que NO se guardó nada
    }

    @Test
    void obtenerPorUsuarioId_Exitoso() {
        // Given
        when(resenaRepository.findByUsuarioId(1L)).thenReturn(List.of(resenaMock));

        // When
        List<ResenaResponseDTO> response = resenaService.obtenerPorUsuarioId(1L);

        // Then
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals(101L, response.get(0).getJuegoId());
    }

    @Test
    void eliminarResena_Exitoso() {
        // Given
        when(resenaRepository.existsById(1L)).thenReturn(true);

        // When
        resenaService.eliminarResena(1L);

        // Then
        verify(resenaRepository, times(1)).deleteById(1L);
    }
}