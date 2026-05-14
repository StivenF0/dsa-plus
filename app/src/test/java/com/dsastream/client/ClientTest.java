package com.dsastream.client;

import com.dsastream.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ClientTest {

    private Client client;

    @BeforeEach
    public void setUp() {
        // Inicializa um cliente novo com o cache vazio antes de cada teste
        client = new Client();
    }

    // 1. Testar se um filme novo é adicionado corretamente ao cache
    @Test
    public void testAddMovieToCache() {
        Movie movie1 = new Movie(100, "Matrix", "Ficção");

        // Simula o cliente a receber o filme do servidor e a guardá-lo
        client.addToCache(movie1);

        // Verifica se o filme agora existe na Árvore AVL do cliente
        Movie cachedMovie = client.getCacheTree().search(100);

        assertNotNull(cachedMovie, "O filme deveria estar no cache após ser adicionado.");
        assertEquals("Matrix", cachedMovie.getTitle(), "O título no cache deve ser 'Matrix'.");
    }

    // 2. Testar o limite do Cache e a política de Eviction (FIFO)
    @Test
    public void testCacheEvictionFIFO() {
        // Enchemos o cache até ao limite (50 filmes)
        for (int i = 1; i <= 50; i++) {
            client.addToCache(new Movie(i, "Filme " + i, "Categoria X"));
        }

        // Neste momento, o ID 1 foi o primeiro a entrar. O ID 50 foi o último.
        // Vamos garantir que o ID 1 ainda lá está.
        assertNotNull(client.getCacheTree().search(1), "O ID 1 ainda deve estar no cache (limite é 50).");

        // Agora, o momento da verdade: adicionamos o 51º filme!
        Movie movie51 = new Movie(51, "Filme 51", "Categoria Y");
        client.addToCache(movie51);

        // O cache deve ter ativado o Eviction FIFO. O ID 1 (mais antigo) DEVE ter sido apagado.
        assertNull(client.getCacheTree().search(1), "O ID 1 DEVE ter sido removido pelo Cache Eviction (FIFO).");

        // O ID 2 passou a ser o mais antigo, deve continuar lá.
        assertNotNull(client.getCacheTree().search(2), "O ID 2 não deve ter sido removido.");

        // O ID 51 (o mais novo) deve ter sido inserido com sucesso.
        assertNotNull(client.getCacheTree().search(51), "O ID 51 deve estar no cache.");
    }

    // 3. Testar se adicionar um filme que já está no cache não causa problemas
    @Test
    public void testCacheHitDoesNotDuplicate() {
        Movie movie1 = new Movie(10, "Inception", "Ficção");

        client.addToCache(movie1); // Primeira vez (Miss -> Adiciona)
        client.addToCache(movie1); // Segunda vez (Hit -> Ignora)

        Movie cachedMovie = client.getCacheTree().search(10);
        assertNotNull(cachedMovie, "O filme deve continuar no cache.");
    }
}