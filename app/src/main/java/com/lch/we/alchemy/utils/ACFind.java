package com.lch.we.alchemy.utils;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * created by jiayaoguang at 2017年9月28日 敏感词过滤
 */
public class ACFind {

    private final TrieNode root;

//    public static void main(String[] args) {
//
//        String[] keywords = new String[] {
//            "的红包",
//            "该红包已过期",
//            "红包排行榜",
//            "再领红包",
//        };
//        ACFind find = new ACFind(keywords);
//        int index = find.find("红包");
//        System.out.println(index);
//
//    }

    public ACFind(String[] keywords) {
        root = buildTree(keywords);
        // printTree(root);
        addFailNode(root);
    }

    /**
     * 查找是否包含目标字符串
     */
    public int find(String text) {
        int len = text.length();
        TrieNode node = root;

        for (int index = 0; index < len; index++) {
            char c = text.charAt(index);

            while (node != null && node.getSonNode(c) == null) {
                node = node.getFailNode();
            }

            node = (node == null ? root : node.getSonNode(c));

            TrieNode temp = node;

            while (temp != null) {
                if (temp.isWordEnd()) {
                    return index;
                }
                temp = temp.getFailNode();
            }
        }
        return -1;
    }

    /**
     * 初始化字典树
     */
    private TrieNode buildTree(String[] keywords) {
        final TrieNode root = new TrieNode(' ');
        for (String word : keywords) {
            TrieNode temp = root;
            for (char ch : word.toCharArray()) {
                if (temp.containsSonNode(ch)) {
                    temp = temp.getSonNode(ch);
                } else {
                    TrieNode newNode = new TrieNode(ch);
                    temp.addSonNode(newNode);
                    temp = newNode;
                }
            }
            temp.setWordEnd(true);
        }
        return root;
    }

    /**
     * 添加节点
     */
    private void addWordToTree(TrieNode rootNode, String word) {
        if (word == null || word.length() == 0) {
            return;
        }
        for (char c : word.toCharArray()) {
            if (rootNode.containsSonNode(c)) {
                rootNode = rootNode.getSonNode(c);
            } else {
                TrieNode newNode = new TrieNode(c);
                rootNode.addSonNode(newNode);
                rootNode = newNode;
            }
        }
        rootNode.setWordEnd(true);
    }

    public void printTree(TrieNode root) {
        Queue<TrieNode> queue = new LinkedList<>();
        queue.offer(root);
        TrieNode enterNode = new TrieNode('\n');
        queue.add(enterNode);
        while (!queue.isEmpty()) {
            TrieNode parent = queue.poll();
            System.out.print(parent.value + ";");
            if (parent == enterNode && queue.size() > 1) {
                queue.offer(enterNode);
                continue;
            }
            queue.addAll(parent.getSonsNode());
        }

    }

    /**
     * BFS遍历树，给每一个节点建立FailNode
     */
    private void addFailNode(final TrieNode root) {
        root.setFailNode(null);
        Queue<TrieNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TrieNode parent = queue.poll();
            TrieNode temp;
            for (TrieNode child : parent.getSonsNode()) {
                if (parent == root) {
                    child.setFailNode(root);
                } else {
                    temp = parent.getFailNode();
                    while (temp != null) {

                        TrieNode node = temp.getSonNode(child.getValue());
                        if (node != null) {
                            child.setFailNode(node);
                            break;
                        }
                        temp = temp.getFailNode();
                    }
                    if (temp == null) {
                        child.setFailNode(root);
                    }
                }
                queue.add(child);
            }
        }
    }

    class TrieNode {

        private TrieNode failNode;

        private char value;

        private boolean isWordEnd = false;

        private Map<Character, TrieNode> sons;

        public TrieNode(char value) {

            this.value = value;
            sons = new HashMap<Character, TrieNode>();
        }

        // 添加子节点
        public void addSonNode(TrieNode node) {
            sons.put(node.value, node);
        }

        // 获取子节点中指定字符节点
        public TrieNode getSonNode(char ch) {
            return sons.get(ch);
        }

        // 判断子节点中是否存在该字符
        public boolean containsSonNode(char ch) {
            return sons.containsKey(ch);
        }

        // 获取字符
        public char getValue() {
            return value;
        }

        // 设置失败指针并且返回
        public void setFailNode(TrieNode failNode) {
            this.failNode = failNode;
        }

        public TrieNode getFailNode() {
            return failNode;
        }

        // 获取所有的孩子节点
        public Collection<TrieNode> getSonsNode() {
            return sons.values();
        }

        public boolean isWordEnd() {
            return isWordEnd;
        }

        public void setWordEnd(boolean isWordEnd) {
            this.isWordEnd = isWordEnd;
        }

    }
}