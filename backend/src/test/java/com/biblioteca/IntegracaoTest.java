package com.biblioteca;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long criarAutor(String nome) throws Exception {
        String resposta = mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"" + nome + "\",\"nacionalidade\":\"Brasileira\",\"dataNascimento\":\"1839-06-21\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(resposta);
        return json.get("id").asLong();
    }

    @Test
    @DisplayName("Deve vincular livro a autor")
    void deveVincularLivroAAutor() throws Exception {
        Long autorId = criarAutor("Machado de Assis");

        String livroJson = "{\"titulo\":\"Dom Casmurro\",\"isbn\":\"978-8535910667\","
                + "\"anoPublicacao\":1899,\"genero\":\"Romance\",\"autor\":{\"id\":" + autorId + "}}";

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(livroJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor.id").value(autorId))
                .andExpect(jsonPath("$.autor.nome").value("Machado de Assis"));

        mockMvc.perform(get("/api/livros/autor/" + autorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("(extra) Deve executar fluxo completo de autor: criar, buscar, atualizar, deletar")
    void deveExecutarFluxoCompletoAutor() throws Exception {
        Long id = criarAutor("Clarice");

        mockMvc.perform(get("/api/autores/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Clarice"));

        mockMvc.perform(put("/api/autores/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Clarice Lispector\",\"nacionalidade\":\"Brasileira\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Clarice Lispector"));

        mockMvc.perform(delete("/api/autores/" + id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/autores/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("(extra) Excluir autor com livros deve retornar 409")
    void deveRetornar409AoExcluirAutorComLivros() throws Exception {
        Long autorId = criarAutor("José de Alencar");
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Iracema\",\"autor\":{\"id\":" + autorId + "}}"))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/autores/" + autorId))
                .andExpect(status().isConflict());
    }
}
