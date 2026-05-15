package com.dsastream.server.ds;

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
        return key % size;
    }

    // Inserts the ID and the Node reference into the hash table
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

    public ListNode searchIndexed(int key) {
        if (key <= 0) {
            System.out.println("Busca rejeitada. ID inválido: " + key);
            return null;
        }

        int comparisions = 0;
        int index = hashFunction(key);
        HashEntry current = table[index];

        while (current != null) {
            comparisions++;
            if (current.key == key) {
                System.out.println("Busca indexada finalizada. Comparações = " + comparisions);
                return current.listNode;
            }
            current = current.next;
        }

        System.out.println("Filme não encontrado na tabela hash. Comparações = " + comparisions);
        return null;
    }
}
