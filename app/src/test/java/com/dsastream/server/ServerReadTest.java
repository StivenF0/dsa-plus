package com.dsastream.server; // Ajusta o pacote conforme a tua estrutura exata

import com.dsastream.model.Movie; // Importa o teu modelo (ajusta o pacote se necessário)
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Importações estáticas do JUnit para facilitar as asserções (verificações)
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ServerReadTest {

    private Server server;

    // O JUnit executa este método ANTES de cada @Test
    // Isto garante que cada teste tem um servidor "limpo" e pronto a usar
    @BeforeEach
    public void setUp() {
        server = new Server();
        server.loadInitialData(); // Carrega os 1000 filmes fictícios
    }

    // 1. Testar se a busca SEQUENCIAL encontra um filme que EXISTE
    @Test
    public void testReadExistingMovieWithoutIndex() {
        // Arrange (Preparar): Vamos buscar o ID 500, que sabemos que foi gerado
        int targetId = 500;

        // Act (Agir): Executamos o método de leitura
        Movie foundMovie = server.requestMovieWithoutIndex(targetId);

        // Assert (Verificar): Validamos se o resultado é o esperado
        assertNotNull(foundMovie, "O filme com ID 500 não deveria ser null.");
        assertEquals(500, foundMovie.getId(), "O ID do filme encontrado deve ser 500.");
        assertEquals("Movie Title 500", foundMovie.getTitle(), "O título deve corresponder ao ID.");
    }

    // 2. Testar se a busca INDEXADA (Hash) encontra um filme que EXISTE
    @Test
    public void testReadExistingMovieWithIndex() {
        int targetId = 250;

        Movie foundMovie = server.requestMovieWithIndex(targetId);

        assertNotNull(foundMovie, "O filme com ID 250 não deveria ser null usando o índice.");
        assertEquals(250, foundMovie.getId(), "O ID do filme encontrado deve ser 250.");
        assertEquals("Movie Title 250", foundMovie.getTitle(), "O título deve corresponder ao ID.");
    }

    // 3. Testar se as buscas lidam corretamente com IDs que NÃO EXISTEM (Miss)
    @Test
    public void testReadNonExistingMovie() {
        int invalidId = 9999; // Sabemos que só existem IDs de 1 a 1000

        Movie movieWithoutIndex = server.requestMovieWithoutIndex(invalidId);
        Movie movieWithIndex = server.requestMovieWithIndex(invalidId);

        // Ambas as buscas devem retornar null e não devem causar erros (Exceptions)
        assertNull(movieWithoutIndex, "Busca sequencial de um ID inválido deve retornar null.");
        assertNull(movieWithIndex, "Busca indexada de um ID inválido deve retornar null.");
    }

    // 4. Testar os limites (Boundary Testing) - O primeiro e o último registro
    @Test
    public void testReadBoundaryMovies() {
        Movie firstMovie = server.requestMovieWithIndex(1);
        Movie lastMovie = server.requestMovieWithIndex(1000);

        assertNotNull(firstMovie, "O primeiro filme (ID 1) deve ser encontrado.");
        assertNotNull(lastMovie, "O último filme (ID 1000) deve ser encontrado.");
    }
}