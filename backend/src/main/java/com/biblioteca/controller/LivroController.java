package com.biblioteca.controller;

import com.biblioteca.model.Livro;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LivroRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = "*")
public class LivroController {

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public LivroController(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    @PostMapping
    public ResponseEntity<Livro> criar(@Valid @RequestBody Livro livro) {
        // O JSON traz só "autor": {"id": 1}; buscamos o autor completo no banco
        if (livro.getAutor().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return autorRepository.findById(livro.getAutor().getId())
                .map(autor -> {
                    livro.setAutor(autor);
                    Livro salvo = livroRepository.save(livro);
                    return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping
    public List<Livro> listar() {
        return livroRepository.findAllByOrderByTituloAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscar(@PathVariable Long id) {
        return livroRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/autor/{autorId}")
    public List<Livro> listarPorAutor(@PathVariable Long autorId) {
        return livroRepository.findByAutorId(autorId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Long id, @Valid @RequestBody Livro dados) {
        return livroRepository.findById(id)
                .map(livro -> {
                    livro.setTitulo(dados.getTitulo());
                    livro.setIsbn(dados.getIsbn());
                    livro.setAnoPublicacao(dados.getAnoPublicacao());
                    livro.setGenero(dados.getGenero());
                    // Atualiza também o autor (como o setContato no compromisso)
                    if (dados.getAutor().getId() != null) {
                        autorRepository.findById(dados.getAutor().getId()).ifPresent(livro::setAutor);
                    }
                    return ResponseEntity.ok(livroRepository.save(livro));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        livroRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
