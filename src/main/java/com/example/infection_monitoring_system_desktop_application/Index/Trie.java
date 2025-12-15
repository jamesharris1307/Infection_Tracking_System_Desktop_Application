package com.example.infection_monitoring_system_desktop_application.Index;

import java.util.*;

public class Trie<T> {

    private final TrieNode<T> root = new TrieNode<>();

    private static class TrieNode<T> {
        Map<Character, TrieNode<T>> children = new HashMap<>();
        List<T> values = new ArrayList<>();
    }

    public void insert(String key, T value) {
        TrieNode<T> node = root;
        for (char c : key.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode<>());
        }
        node.values.add(value);
    }

    public void remove(String key, T value) {
        remove(root, key, 0, value);
    }

    private boolean remove(TrieNode<T> node, String key, int index, T value) {
        if (index == key.length()) {
            node.values.remove(value);
            return node.children.isEmpty() && node.values.isEmpty();
        }
        char c = key.charAt(index);
        TrieNode<T> child = node.children.get(c);
        if (child == null) return false;

        boolean shouldDeleteChild = remove(child, key, index + 1, value);
        if (shouldDeleteChild) node.children.remove(c);
        return node.children.isEmpty() && node.values.isEmpty();
    }

    public List<T> search(String prefix) {
        TrieNode<T> node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return Collections.emptyList();
        }
        return collectAllValues(node);
    }

    private List<T> collectAllValues(TrieNode<T> node) {
        List<T> results = new ArrayList<>(node.values);
        for (TrieNode<T> child : node.children.values()) {
            results.addAll(collectAllValues(child));
        }
        return results;
    }

    public void clear() {
        root.children.clear();
        root.values.clear();
    }
}