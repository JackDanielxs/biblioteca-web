package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.repository.AutorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutorController.class)
class AutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AutorRepository autorRepository;

    @Test
    @DisplayName("Deve criar autor com sucesso (201)")
    void deveCriarAutorComSucesso() throws Exception {
        Autor novo = new Autor(null, "Machado de Assis", "Brasileira", LocalDate.of(1839, 6, 21), null);
        Autor salvo = new Autor(1L, "Machado de Assis", "Brasileira", LocalDate.of(1839, 6, 21), null);
        when(autorRepository.save(any(Autor.class))).thenReturn(salvo);

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Machado de Assis"));
    }

    @Test
    @DisplayName("Deve retornar 404 para autor inexistente")
    void deveRetornar404ParaAutorInexistente() throws Exception {
        when(autorRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/autores/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 400 quando o nome está vazio")
    void deveRetornar400SemNome() throws Exception {
        Autor invalido = new Autor(null, "", null, null, null);

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("(extra) Deve listar autores ordenados por nome")
    void deveListarAutoresOrdenados() throws Exception {
        when(autorRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(
                new Autor(2L, "Clarice Lispector", "Brasileira", null, null),
                new Autor(1L, "Machado de Assis", "Brasileira", null, null)));

        mockMvc.perform(get("/api/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Clarice Lispector"))
                .andExpect(jsonPath("$[1].nome").value("Machado de Assis"));
    }
}
