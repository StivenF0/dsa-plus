package com.dsaplus.server.ds;

import com.dsaplus.util.Logger;

public class HashTable {
    private static class HashEntry {
        int key;
        ListNode listNode;
        HashEntry next;

        public HashEntry(int key, ListNode listNode) {
            this.key = key;
            this.listNode = listNode;
            this.next = null;
        }
    }

    private final HashEntry[] table;
    private final int size;

    public HashTable(int size) {
        this.size = size;
        this.table = new HashEntry[size];
    }

    public int hashFunction(int key) {
        // Utilizando o método da multiplicação para calcular o índice
        // h(k) = floor(m * ((k*A) % 1))
        double A = (Math.sqrt(5) - 1) / 2; // Constante de Knuth

        // Cálculo por partes
        double val = key * A;
        double fracPart = val % 1;
        return (int) Math.floor(size * fracPart);
    }

    // Insere o ID do filme e o nó correspondente na tabela hash
    public void put(int key, ListNode listNode) {
        int index = hashFunction(key);
        HashEntry newEntry = new HashEntry(key, listNode);

        if (table[index] == null) {
            table[index] = newEntry;
        } else {
            HashEntry current = table[index];
            while (current.next != null) {
                current = current.next;
            }
            current.next = newEntry;
        }
    }

    // Busca o nó correspondente ao ID do filme usando a tabela hash
    public ListNode searchIndexed(int key) {
        if (key <= 0) {
            Logger.warn("HashTable", "Busca rejeitada. ID inválido: " + key);
            return null;
        }

        int comparisions = 0;
        int index = hashFunction(key);
        HashEntry current = table[index];

        while (current != null) {
            comparisions++;
            if (current.key == key) {
                Logger.debug("HashTable", "Busca indexada ID " + key + ". Comparações = " + comparisions);
                return current.listNode;
            }
            current = current.next;
        }

        Logger.debug("HashTable", "ID " + key + " não encontrado. Comparações = " + comparisions);
        return null;
    }
}
