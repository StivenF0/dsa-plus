package com.dsaplus.util;

public class MinHeap<T extends Comparable<T>> {
    private T[] heap;
    private int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public MinHeap(int capacity) {
        this.capacity = capacity;
        this.heap = (T[]) new Comparable[capacity];
        this.size = 0;
    }

    // Retorna a quantidade atual de elementos no heap
    public int size() {
        return size;
    }

    // Insere um elemento no final e o faz subir até sua posição correta
    public void insert(T element) {
        if (size == capacity) {
            Logger.warn("MinHeap", "Heap cheio. Não foi possível inserir.");
            return;
        }
        heap[size] = element;
        size++;
        siftUp(size - 1);
    }

    // Remove e retorna o menor elemento (raiz). Em seguida, recoloca o último
    // elemento na raiz e o faz descer até restaurar a propriedade do heap
    public T removeMin() {
        if (size == 0) return null;
        T root = heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        siftDown(0);
        return root;
    }

    // Constrói o heap a partir de um array desordenado.
    // Aplica siftDown nos nós não-folha, do último ao primeiro
    public void build() {
        for (int i = (size - 1) / 2; i >= 0; i--) {
            siftDown(i);
        }
    }

    // Sobe o elemento na posição i enquanto for menor que seu pai
    private void siftUp(int i) {
        int parent = (i - 1) / 2;
        if (parent >= 0 && heap[i].compareTo(heap[parent]) < 0) {
            T temp = heap[i];
            heap[i] = heap[parent];
            heap[parent] = temp;
            siftUp(parent);
        }
    }

    // Desce o elemento na posição i trocando com o menor filho,
    // até que a propriedade de heap mínimo seja restaurada
    private void siftDown(int i) {
        int child = 2 * i + 1;
        if (child >= size) return;
        // Escolhe o menor entre os dois filhos
        if (child < size - 1 && heap[child + 1].compareTo(heap[child]) < 0) {
            child++;
        }
        if (heap[child].compareTo(heap[i]) < 0) {
            T temp = heap[i];
            heap[i] = heap[child];
            heap[child] = temp;
            siftDown(child);
        }
    }
}
