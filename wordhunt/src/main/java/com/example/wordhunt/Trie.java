package com.example.wordhunt;

public class Trie {
    private TrieNode root = new TrieNode();  

    public void insert(String word) {
      TrieNode node = root;
      for (char c : word.toUpperCase().toCharArray()) {
        node = node.getChildren().computeIfAbsent(c, k -> new TrieNode());
      }
      node.setWord(true);
    }

    public boolean search(String word) {
      TrieNode node = root;
      for (char c : word.toUpperCase().toCharArray()) {
        node = node.getChildren().get(c);
        if (node == null) return false;
      }
      return node.isWord();
    }

    public boolean startsWith(String prefix) {
      TrieNode node = root;
      for (char c : prefix.toUpperCase().toCharArray()) {
        node = node.getChildren().get(c);
        if (node == null) return false;
      }
      return true;
    }
}
