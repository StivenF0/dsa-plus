package com.dsastream.client.ds;

import com.dsastream.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LRUCache {

    private static class Node {
        int id;
        Movie movie;
        Node prev;
        Node next;

        Node(int id, Movie movie) {
            this.id = id;
            this.movie = movie;
        }
    }

    // Capacidade máxima do cache
    private final int capacity;
    // HashMap para lookup O(1) — mapeia ID do filme ao nó correspondente na lista
    private final HashMap<Integer, Node> map;
    // Cabeça e cauda da lista duplamente ligada (head = mais recente, tail = LRU)
    private Node head;
    private Node tail;
    // Histórico de IDs removidos pela política LRU
    private final List<Integer> evictionHistory;
    private int comparisons;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.evictionHistory = new ArrayList<>();
    }

    // Busca um filme pelo ID. Se encontrado, move o nó para o início (mais recente)
    public Movie get(int id) {
        comparisons++;
        Node node = map.get(id);

        if (node == null) {
            System.out.println("[CACHE MISS] (ID " + id + " não está no cache). Comparações: " + comparisons);
            comparisons = 0;
            return null;
        }

        moveToFront(node);
        System.out.println("[CACHE HIT] (ID " + id + " encontrado no cache). Comparações: " + comparisons);
        comparisons = 0;
        return node.movie;
    }

    // Insere ou atualiza um filme no cache. Se atingir a capacidade, remove o LRU (cauda)
    public void put(int id, Movie movie) {
        // Se o filme já existe no cache, apenas atualiza e move ao início
        if (map.containsKey(id)) {
            Node node = map.get(id);
            node.movie = movie;
            moveToFront(node);
            return;
        }

        // Cache cheio: remove o menos recentemente utilizado (cauda da lista)
        if (map.size() >= capacity) {
            evictTail();
        }

        Node newNode = new Node(id, movie);
        addToFront(newNode);
        map.put(id, newNode);
    }

    // Remove um filme específico do cache
    public void remove(int id) {
        Node node = map.remove(id);
        if (node == null) return;

        // Ajusta os ponteiros dos vizinhos para desencadear o nó da lista
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    // Move um nó para o início da lista (marcando-o como mais recentemente usado)
    private void moveToFront(Node node) {
        if (node == head) return;

        // Desconecta o nó da posição atual
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        if (node == tail) {
            tail = node.prev;
        }

        addToFront(node);
    }

    // Adiciona um novo nó ao início da lista
    private void addToFront(Node node) {
        node.prev = null;
        node.next = head;
        if (head != null) {
            head.prev = node;
        }
        head = node;
        if (tail == null) {
            tail = node;
        }
    }

    // Remove o nó da cauda (LRU) — o menos recentemente utilizado
    private void evictTail() {
        if (tail == null) return;

        Node evicted = tail;

        evictionHistory.add(evicted.id);
        System.out.println("[CACHE EVICT] Removendo ID " + evicted.id + " (" + evicted.movie.getTitle() + ")");

        map.remove(evicted.id);

        if (evicted.prev != null) {
            evicted.prev.next = null;
        } else {
            head = null;
        }
        tail = evicted.prev;

        // Libera referências do nó removido para o GC
        evicted.prev = null;
        evicted.next = null;
        evicted.movie = null;
    }

    // Verifica se o ID está no cache sem mover o nó (para consultas internas)
    public Movie getIfPresent(int id) {
        Node node = map.get(id);
        return node != null ? node.movie : null;
    }

    // Retorna os n filmes mais recentemente acessados (do início da lista)
    public List<Movie> getRecentMovies(int n) {
        List<Movie> recent = new ArrayList<>();
        Node current = head;
        int count = 0;
        while (current != null && count < n) {
            recent.add(current.movie);
            current = current.next;
            count++;
        }
        return recent;
    }

    // Retorna o histórico de IDs que foram removidos pela política LRU
    public List<Integer> getEvictionHistory() {
        return new ArrayList<>(evictionHistory);
    }

    public boolean contains(int id) {
        return map.containsKey(id);
    }

    public int size() {
        return map.size();
    }
}
