package com.dsastream.client.ds;

public class CacheQueue {
    private QueueNode front;
    private QueueNode rear;
    private int size;

    public CacheQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    // Adiciona um novo ID ao final da fila
    public void enqueue(int id) {
        QueueNode newNode = new QueueNode(id);
        if (rear == null) {
            front = newNode;
            rear = newNode;
        } else {
            rear.setNext(newNode);
            rear = newNode;
        }
        size++;
    }

    // Remove e retorna o ID da frente da fila. Retorna -1 se a fila estiver vazia.
    public int dequeue() {
        // Caso de fila vazia
        if (front == null) {
            return -1;
        }
        int removedId = front.getId();
        front = front.getNext();

        if (front == null) {
            rear = null;
        }
        size--;
        return removedId;
    }

    public int getSize() {
        return size;
    }
}