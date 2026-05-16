package com.dsastream.client.ds;

public class QueueNode {
    private int id;
    private QueueNode next;

    public QueueNode(int id) {
        this.id = id;
        this.next = null;
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public QueueNode getNext() {
        return next;
    }

    public void setNext(QueueNode next) {
        this.next = next;
    }
}
