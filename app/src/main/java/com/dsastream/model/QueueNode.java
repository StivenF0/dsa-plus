package com.dsastream.model;

public class QueueNode {
    private int id;
    private QueueNode next;

    public QueueNode(int id) {
        this.id = id;
        this.next = null;
    }

    // --- Getters and Setters ---

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
