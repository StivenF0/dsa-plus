package com.dsastream.client.ds;

import com.dsastream.model.Movie;

public class AVLTree {

    private AVLNode root;

    public AVLTree() {
        this.root = null;
    }

    // --- Métodos de busca e contagem ---

    public Movie search(int id) {
        // Usando a referência do array para contar as comparações durante a busca
        int[] comparisons = {0};
        AVLNode result = searchRecursive(this.root, id, comparisons);

        if (result == null) {
            System.out.println("[CACHE MISS] (Filme \"" + id + "\" não está na AVL). Comparações na árvore: " + comparisons[0]);
            return null;
        } else {
            System.out.println("[CACHE HIT] (Filme \"" + id + "\" encontrado na AVL). Comparações na árvore: " + comparisons[0]);
            return result.getMovie();
        }
    }

    private AVLNode searchRecursive(AVLNode node, int id, int[] comparisons) {
        if (node == null) {
            return null;
        }

        comparisons[0]++;

        if (node.getId() > id) {
            return searchRecursive(node.getLeft(), id, comparisons);
        } else if (node.getId() < id) {
            return searchRecursive(node.getRight(), id, comparisons);
        } else {
            return node;
        }
    }

    // --- Métodos de Inserção e Remoção ---

    public void insert(int id, Movie movie) {
        this.root = insertRecursive(this.root, id, movie);
    }

    private AVLNode insertRecursive(AVLNode node, int id, Movie movie) {
        if (node == null) {
            return new AVLNode(id, movie);
        } else if (id < node.getId()) {
            node.setLeft(insertRecursive(node.getLeft(), id, movie));
        } else if (id > node.getId()) {
            node.setRight(insertRecursive(node.getRight(), id, movie));
        } else {
            return node;
        }

        return checkBalancing(node);
    }

    public void remove(int id) {
        root = removeRecursive(this.root, id);
    }

    private AVLNode removeRecursive(AVLNode node, int id) {
        if (node == null) return null;

        if (id < node.getId()) {
            node.setLeft(removeRecursive(node.getLeft(), id));
        } else if (id > node.getId()) {
            node.setRight(removeRecursive(node.getRight(), id));
        } else {
            // Encontrou o nó a ser deletado
            if (node.getLeft() == null) {
                return node.getRight();
            } else if (node.getRight() == null) {
                return node.getLeft();
            } else {
                // Caso 3: Nó com dois filhos
                AVLNode temp = nodeWithMinId(node.getRight());
                node.setId(temp.getId());
                node.setMovie(temp.getMovie());
                node.setRight(removeRecursive(node.getRight(), temp.getId()));
            }
        }

        if (node == null) return null;

        return checkBalancing(node);
    }

    // --- Métodos auxiliares de balanceamento ---

    private AVLNode checkBalancing(AVLNode node) {
        node.setHeight(1 + Math.max(getHeight(node.getLeft()), getHeight(node.getRight())));

        int fb = getBalanceFactor(node);
        int fbLeft = getBalanceFactor(node.getLeft());
        int fbRight = getBalanceFactor(node.getRight());

        // Rotação simples à direita
        if (fb > 1 && fbLeft >= 0)
            return rotateRight(node);

        // Rotação simples à esquerda
        if (fb < -1 && fbRight <= 0)
            return rotateLeft(node);

        // Rotação dupla à direita
        if (fb > 1 && fbLeft < 0) {
            node.setLeft(rotateLeft(node.getLeft()));
            return rotateRight(node);
        }

        // Rotação dupla à esquerda
        if (fb < -1 && fbRight > 0) {
            node.setRight(rotateRight(node.getRight()));
            return rotateLeft(node);
        }

        return node;
    }

    private int getHeight(AVLNode node) {
        if (node == null) return -1;
        return node.getHeight();
    }

    private int getBalanceFactor(AVLNode node) {
        if (node == null) return 0;
        return getHeight(node.getLeft()) - getHeight(node.getRight());
    }

    private AVLNode nodeWithMinId(AVLNode node) {
        AVLNode temp = node;
        while (temp.getLeft() != null) {
            temp = temp.getLeft();
        }
        return temp;
    }

    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.getRight();
        AVLNode z = y.getLeft();

        y.setLeft(x);
        x.setRight(z);

        x.setHeight(1 + Math.max(getHeight(x.getLeft()), getHeight(x.getRight())));
        y.setHeight(1 + Math.max(getHeight(y.getLeft()), getHeight(y.getRight())));

        return y;
    }

    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.getLeft();
        AVLNode z = x.getRight();

        x.setRight(y);
        y.setLeft(z);

        y.setHeight(1 + Math.max(getHeight(y.getLeft()), getHeight(y.getRight())));
        x.setHeight(1 + Math.max(getHeight(x.getLeft()), getHeight(x.getRight())));

        return x;
    }
}