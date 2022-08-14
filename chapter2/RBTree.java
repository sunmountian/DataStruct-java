package com.period3.week12.chapter2;


import com.period2.week8.chapter2.BSTMap;

/**
 * @version 1.0
 * @authoe 李祥基
 */
public class RBTree<K extends Comparable<K>, V> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    private Node root;
    private int size;

    public RBTree() {
        root = null;
        size = 0;
    }


    //判断节点node的颜色
    private boolean isRed(Node node) {
        if (node == null)
            return BLACK;
        return node.color;
    }

    // RR
    private Node leftRotate(Node node) {
        Node x = node.right;
        //左旋转
        node.right = x.left;
        x.left = node;
        //更新节点颜色
        x.color = node.color;
        node.color = RED;

        return x;
    }

    // LL
    private Node rightRotate(Node node) {
        Node x = node.left;
        //右旋转
        node.left = x.right;
        x.right = node;
        //更新节点颜色
        x.color = node.color;
        node.color = RED;

        return x;
    }

    //颜色翻转
    private void flipColors(Node node) {
        node.color = RED;
        node.left.color = BLACK;
        node.right.color = BLACK;
    }

    //向红黑树中添加新的元素(key, value)
    public void add(K key, V value) {
        root = add(root, key, value);
        root.color = BLACK;//最终根节点为黑色节点
    }

    //向以node为根的红黑树中插入元素(key, value)，递归算法
    //返回插入新节点后红黑树的根
    private Node add(Node node, K key, V value) {
        if (node == null) {
            size++;
            return new Node(key, value);//默认插入红色节点
        }
        if (key.compareTo(node.key) < 0)
            node.left = add(node.left, key, value);
        else if (key.compareTo(node.key) > 0)
            node.right = add(node.right, key, value);
        if (isRed(node.right) && !isRed(node.left))
            node = leftRotate(node);
        if (isRed(node.left) && isRed(node.left.left))
            node = rightRotate(node);
        if (isRed(node.left) && isRed(node.right))
            flipColors(node);
        return node;
    }

    //返回以node为根节点的二分搜索树中，key所在的节点
    private Node getNode(Node node, K key) {
        if (node == null)
            return null;

        if (key.compareTo(node.key) == 0)
            return node;
        else if (key.compareTo(node.key) < 0)
            return getNode(node.left, key);
        else
            return getNode(node.right, key);
    }


    public V remove(K key) {
        return null;
    }


    public boolean contains(K key) {
        return getNode(root, key) != null;
    }


    public V get(K key) {
        Node node = getNode(root, key);
        return node == null ? null : node.value;
    }


    public void set(K key, V newValue) {
        Node node = getNode(root, key);
        if (node == null)
            throw new IllegalArgumentException(key + " does not exist!");
        node.value = newValue;
    }

    //返回以node为根的二分搜索树的最小值所在的节点
    private Node mininum(Node node) {
        if (node.left == null)
            return node;
        return mininum(node.left);
    }

    //删除掉以node为根的二分搜索树中的最小节点
    //返回删除节点后新的二分搜索树的根
    private Node removeMin(Node node) {
        if (node.left == null) {
            Node rightNode = node.right;
            node.right = node;
            size--;
            return rightNode;
        }

        node.left = removeMin(node.left);
        return node;
    }


    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }


    private class Node {
        public K key;
        public V value;
        public Node left, right;
        public boolean color;


        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            left = null;
            right = null;
            color = RED;
        }
    }

    ;
}
