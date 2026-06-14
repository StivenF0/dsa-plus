package com.dsaplus.common.ds;

import com.dsaplus.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class SplayTree {

    private SplayNode root;

    // Right rotation: y becomes the new root of this subtree
    private SplayNode rotateRight(SplayNode x) {
        SplayNode y = x.getLeft();
        x.setLeft(y.getRight());
        y.setRight(x);
        return y;
    }

    // Left rotation: y becomes the new root of this subtree
    private SplayNode rotateLeft(SplayNode x) {
        SplayNode y = x.getRight();
        x.setRight(y.getLeft());
        y.setLeft(x);
        return y;
    }

    /*
     * Splay operation (top-down)
     *
     * Recursively searches for the node with the given key
     * and brings it to the root through rotations during unwinding.
     * If the key is not found, the closest node is brought to the root.
     */
    private SplayNode splay(SplayNode node, int key) {
        if (node == null || node.getKey() == key) {
            return node;
        }

        // Key is in the left subtree
        if (key < node.getKey()) {
            if (node.getLeft() == null) {
                return node;
            }

            // Zig-Zig (left-left)
            if (key < node.getLeft().getKey()) {
                node.getLeft().setLeft(splay(node.getLeft().getLeft(), key));
                node = rotateRight(node);
            }
            // Zig-Zag (left-right)
            else if (key > node.getLeft().getKey()) {
                node.getLeft().setRight(splay(node.getLeft().getRight(), key));
                if (node.getLeft().getRight() != null) {
                    node.setLeft(rotateLeft(node.getLeft()));
                }
            }

            return (node.getLeft() == null) ? node : rotateRight(node);
        }
        // Key is in the right subtree
        else {
            if (node.getRight() == null) {
                return node;
            }

            // Zig-Zag (right-left)
            if (key < node.getRight().getKey()) {
                node.getRight().setLeft(splay(node.getRight().getLeft(), key));
                if (node.getRight().getLeft() != null) {
                    node.setRight(rotateRight(node.getRight()));
                }
            }
            // Zig-Zig (right-right)
            else if (key > node.getRight().getKey()) {
                node.getRight().setRight(splay(node.getRight().getRight(), key));
                node = rotateLeft(node);
            }

            return (node.getRight() == null) ? node : rotateLeft(node);
        }
    }

    /*
     * Search with splaying.
     * If found, the node is brought to the root.
     * If not found, the closest node is brought to the root.
     * Returns true if the key exists.
     */
    public boolean find(int key) {
        root = splay(root, key);
        return root != null && root.getKey() == key;
    }

    /*
     * Returns the movie associated with the key, or null if not found.
     * The accessed node is splayed to the root.
     */
    public Movie findByKey(int key) {
        root = splay(root, key);
        return (root != null && root.getKey() == key) ? root.getValue() : null;
    }

    /*
     * Insert a key-value pair with splaying.
     * The new node (or existing node if key already exists) is brought to the root.
     */
    public void insert(int key, Movie value) {
        if (root == null) {
            root = new SplayNode(key, value);
            return;
        }

        root = splay(root, key);

        if (root.getKey() == key) {
            root.setValue(value);
            return;
        }

        SplayNode newNode = new SplayNode(key, value);

        if (root.getKey() > key) {
            newNode.setRight(root);
            newNode.setLeft(root.getLeft());
            root.setLeft(null);
        } else {
            newNode.setLeft(root);
            newNode.setRight(root.getRight());
            root.setRight(null);
        }

        root = newNode;
    }

    // --- Utilities ---

    // Returns the n nodes closest to the root (pre-order traversal)
    public List<SplayNode> getTop(int n) {
        List<SplayNode> result = new ArrayList<>();
        collectPreOrder(root, result, n);
        return result;
    }

    private void collectPreOrder(SplayNode node, List<SplayNode> result, int limit) {
        if (node == null || result.size() >= limit) return;
        result.add(node);
        collectPreOrder(node.getLeft(), result, limit);
        collectPreOrder(node.getRight(), result, limit);
    }

    public SplayNode getRoot() {
        return root;
    }

    public boolean isEmpty() {
        return root == null;
    }
}
