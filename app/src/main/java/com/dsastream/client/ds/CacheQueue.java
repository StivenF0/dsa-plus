package com.dsastream.client.ds;

import com.dsastream.model.QueueNode;

public class CacheQueue {
    private QueueNode front;
    private QueueNode rear;
    private int size;

    public CacheQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    // Adds a new ID to the rear of the queue
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

    // Removes and returns the ID at the front of the queue. Returns -1 if the queue is empty.
    public int dequeue() {
        // Empty queue case
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