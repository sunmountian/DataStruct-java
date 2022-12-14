package com.period3.week12.chapter1;

import com.period2.week8.chapter2.Map;

import java.util.ArrayList;

/**
 * @version 1.0
 * @authoe 李祥基
 * AVL树
 */
public class AVLTree<K extends Comparable<K>, V> implements Map<K, V> {
    private Node root;
    private int size;

    public AVLTree() {
        root = null;
        size = 0;
    }

    @Override
    public void add(K key, V value) {
        root = add(root, key, value);
    }

    //向以node为根的二分搜索树中插入元素(key, value)，递归算法
    //返回插入新节点后二分搜索树的根
    private Node add(Node node, K key, V value) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }
        if (key.compareTo(node.key) < 0)
            node.left = add(node.left, key, value);
        else if (key.compareTo(node.key) > 0)
            node.right = add(node.right, key, value);

        //更新height
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        //计算平衡因子
        int balanceFactor = getBalanceFactor(node);
        if (Math.abs(balanceFactor) > 1)
            System.out.println("unbalanced：" + balanceFactor);

        //平衡维护
        //右旋转，LL 节点node的左子树的高度大于右子树
        if (balanceFactor > 1 && getBalanceFactor(node.left) >= 0)
            return rightRotate(node);
        //左旋转，RR 节点node的右子树的高度大于左子树
        if (balanceFactor < -1 && getBalanceFactor(node.right) <= 0)
            return leftRotate(node);
        //LR
        if (balanceFactor > 1 && getBalanceFactor(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        //RL
        if (balanceFactor < -1 && getBalanceFactor(node.left) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
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

    //删除二分搜索树中的键为key的节点
    public V remove(K key) {
        Node node = getNode(root, key);
        if (node != null) {
            root = remove(root, key);
            return node.value;
        }
        return null;
    }

    //删除以node为根的二分搜索树中值为e的节点，递归算法
    //返回删除节点后新的二分搜索树的根
    private Node remove(Node node, K key) {
        if (node == null)
            return null;
        Node retNode;
        if (key.compareTo(node.key) < 0) {
            node.left = remove(node.left, key);
            retNode = node;
        } else if (key.compareTo(node.key) > 0) {
            node.right = remove(node.right, key);
            retNode = node;
        } else {//e == node.e
            //如果待删除节点的只有左子树或者右子树，直接删除节点,返回子树即可
            //待删除节点的左子树为空,
            if (node.left == null) {
                Node rightNode = node.right;
                node.right = null;
                size--;
                retNode = rightNode;
            }
            //待删除节点的右子树为空,
            if (node.right == null) {
                Node leftNode = node.left;
                node.left = null;
                size--;
                retNode = leftNode;
            }
            /*待删除节点左右子树均不为空
            关键在于找到待删除节点的前驱或后继，这里采用寻找后继的方法
            第一步，找到比待删除节点大的最小节点，即待删除节点右子树的最小节点
            第二步，用该节点顶替待删除结点的位置*/

            //第一步
            assert node.right != null;
            Node successor = mininum(node.right);
            //第二步，更新后继节点的左右子树
            successor.right = removeMin(node.right);
            successor.left = node.left;

            node.left = node.right = null;
            retNode = successor;
        }
        //更新height
        retNode.height = 1 + Math.max(getHeight(retNode.left), getHeight(retNode.right));
        //计算平衡因子
        int balanceFactor = getBalanceFactor(retNode);
        if (Math.abs(balanceFactor) > 1)
            System.out.println("unbalanced：" + balanceFactor);

        //平衡维护
        //右旋转，LL 节点node的左子树的高度大于右子树
        if (balanceFactor > 1 && getBalanceFactor(retNode.left) >= 0)
            return rightRotate(retNode);
        //左旋转，RR 节点node的右子树的高度大于左子树
        if (balanceFactor < -1 && getBalanceFactor(retNode.right) <= 0)
            return leftRotate(retNode);
        //LR
        if (balanceFactor > 1 && getBalanceFactor(retNode.left) < 0) {
            retNode.left = leftRotate(retNode.left);
            return rightRotate(retNode);
        }
        //RL
        if (balanceFactor < -1 && getBalanceFactor(retNode.left) > 0) {
            retNode.right = rightRotate(retNode.right);
            return leftRotate(retNode);
        }
        return retNode;

    }


    @Override
    public boolean contains(K key) {
        return getNode(root, key) != null;
    }

    @Override
    public V get(K key) {
        Node node = getNode(root, key);
        return node == null ? null : node.value;
    }

    @Override
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

    private int getHeight(Node node) {
        if (node == null)
            return 0;
        return node.height;
    }

    //获得节点的平衡因子
    private int getBalanceFactor(Node node) {
        if (node == null)
            return 0;
        return getHeight(node.left) - getHeight(node.right);
    }

    //对节点y进行右旋转操作，返回旋转后新的根节点x LL
    //        y                            x
    //       / \                         /   \
    //      x   T4    向右旋转(y)        z      y
    //     /  \       ------------>   /  \   /  \
    //    z    T3                    T1  T2 T3  T4
    //   / \
    // T1   T2
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T3 = x.right;
        //向右旋转
        x.right = y;
        y.left = T3;
        //更新height
        y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;
        x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
        return x;
    }

    //对节点y进行左旋转操作，返回旋转后新的根节点x RR
    //        y                            x
    //       / \                         /   \
    //      T1   x    向左旋转(y)        y      z
    //          /  \  ------------>   /  \   /  \
    //        T2   z                T1  T2 T3  T4
    //            / \
    //          T3   T4
    private Node leftRotate(Node y) {
        Node x = y.right;
        Node T2 = x.left;
        //左旋转
        x.left = y;
        y.right = T2;
        //更新height
        y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;
        x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
        return x;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    //判断该二叉树是否是一颗二分搜索树
    public boolean isBST() {
        ArrayList<K> keys = new ArrayList<>();
        inOrder(root, keys);
        for (int i = 1; i < keys.size(); i++) {
            if (keys.get(i - 1).compareTo(keys.get(i)) > 0)
                return false;
        }
        return true;
    }

    private void inOrder(Node node, ArrayList<K> keys) {
        if (node == null)
            return;
        inOrder(node.left, keys);
        keys.add(node.key);
        inOrder(node.right, keys);
    }

    //判断该二叉树是否是一颗平衡二叉树
    public boolean isBalanced() {
        return isBalanced(root);
    }

    //判断以node二叉树是否是一颗平衡二叉树，递归算法
    private boolean isBalanced(Node node) {
        if (node == null)
            return true;
        int balanceFactor = getBalanceFactor(node);
        if (Math.abs(balanceFactor) > 1)
            return false;
        return isBalanced(node.left) && isBalanced(node.right);
    }


    private class Node {
        public K key;
        public V value;
        public Node left, right;
        public int height;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            left = null;
            right = null;
            height = 1;
        }
    }
}