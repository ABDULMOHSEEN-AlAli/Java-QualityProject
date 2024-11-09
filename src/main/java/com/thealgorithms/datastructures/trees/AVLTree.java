package com.thealgorithms.datastructures.trees;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an AVL Tree, a self-balancing binary search tree.
 * In an AVL tree, the heights of the two child subtrees of any node
 * differ by at most one. If they differ by more than one at any time,
 * rebalancing is performed to restore this property.
 */
public class AVLTree {

    private Node root;

    private static class Node {
        private int key;
        private int balance;
        private int height;
        private Node left;
        private Node right;
        private Node parent;

        Node(int k, Node p) {
            key = k;
            parent = p;
        }

        public Integer getBalance() {
            return balance;
        }
    }

    /**
     * Inserts a new key into the AVL tree.
     *
     * @param key the key to be inserted
     * @return {@code true} if the key was inserted, {@code false} if the key already exists
     */
    public boolean insert(int key) {
        if (root == null) {
            root = new Node(key, null);
        } else {
            Node n = root;
            Node parent;
            while (true) {
                if (n.key == key) {
                    return false;
                }

                parent = n;
                boolean goLeft = n.key > key;
                n = goLeft ? n.left : n.right;

                if (n == null) {
                    if (goLeft) {
                        parent.left = new Node(key, parent);
                    } else {
                        parent.right = new Node(key, parent);
                    }
                    rebalance(parent);
                    break;
                }
            }
        }
        return true;
    }

    /**
     * Deletes a key from the AVL tree.
     *
     * @param delKey the key to be deleted
     */
    public void delete(int delKey) {
        if (root == null) {
            return;
        }

        // Find the node to be deleted
        Node node = root;
        Node child = root;
        while (child != null) {
            node = child;
            child = delKey >= node.key ? node.right : node.left;
            if (delKey == node.key) {
                delete(node);
                return;
            }
        }
    }

    private void delete(Node node) {
        if (node.left == null && node.right == null) {
            // Leaf node
            if (node.parent == null) {
                root = null;
            } else {
                Node parent = node.parent;
                if (parent.left == node) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
                rebalance(parent);
            }
            return;
        }

        // Node has one or two children
        Node child;
        if (node.left != null) {
            child = node.left;
            while (child.right != null) {
                child = child.right;
            }
        } else {
            child = node.right;
            while (child.left != null) {
                child = child.left;
            }
        }
        node.key = child.key;
        delete(child);
    }

    /**
     * Returns a list of balance factors for each node in the tree.
     *
     * @return a list of integers representing the balance factors of the nodes
     */
    public List<Integer> returnBalance() {
        List<Integer> balances = new ArrayList<>();
        returnBalance(root, balances);
        return balances;
    }

    private void returnBalance(Node n, List<Integer> balances) {
        if (n != null) {
            returnBalance(n.left, balances);
            balances.add(n.getBalance());
            returnBalance(n.right, balances);
        }
    }

    /**
     * Searches for a key in the AVL tree.
     *
     * @param key the key to be searched
     * @return true if the key is found, false otherwise
     */
    public boolean search(int key) {
        Node result = searchHelper(this.root, key);
        return result != null;
    }

    private Node searchHelper(Node root, int key) {
        if (root == null || root.key == key) {
            return root;
        }

        if (root.key > key) {
            return searchHelper(root.left, key);
        }
        return searchHelper(root.right, key);
    }

    private void rebalance(Node n) {
        setBalance(n);
        if (n.balance == -2) {
            if (height(n.left.left) >= height(n.left.right)) {
                n = rotateRight(n);
            } else {
                n = rotateLeftThenRight(n);
            }
        } else if (n.balance == 2) {
            if (height(n.right.right) >= height(n.right.left)) {
                n = rotateLeft(n);
            } else {
                n = rotateRightThenLeft(n);
            }
        }

        if (n.parent != null) {
            rebalance(n.parent);
        } else {
            root = n;
        }
    }
    private void updateParent(Node node, Node child) {
        if (node.parent != null) {
            if (node.parent.right == node) {
                node.parent.right = child;
            } else {
                node.parent.left = child;
            }
        }
    }
    private Node rotate(Node a, boolean isLeftRotation) {
        Node b = isLeftRotation ? a.right : a.left;
        Node child = isLeftRotation ? b.left : b.right;

        b.parent = a.parent;
        a.parent = b;
        child.parent = a;

        if (isLeftRotation) {
            a.right = child;
            b.left = a;
        } else {
            a.left = child;
            b.right = a;
        }

        updateParent(a, b);
        setBalance(a, b);

        return b;
    }
    private Node rotateLeft(Node a) {
        return rotate(a, true);

    }

    private Node rotateRight(Node a) {
        return rotate(a, true);
    }

    private Node rotateLeftThenRight(Node n) {
        n.left = rotateLeft(n.left);
        return rotateRight(n);
    }

    private Node rotateRightThenLeft(Node n) {
        n.right = rotateRight(n.right);
        return rotateLeft(n);
    }

    private int height(Node n) {
        if (n == null) {
            return -1;
        }
        return n.height;
    }

    private void setBalance(Node... nodes) {
        for (Node n : nodes) {
            reheight(n);
            n.balance = height(n.right) - height(n.left);
        }
    }

    private void reheight(Node node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }
}