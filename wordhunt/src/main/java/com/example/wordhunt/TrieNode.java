package com.example.wordhunt;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    private Map<Character, TrieNode> children = new HashMap<>();  
    private boolean isWord;
    
    public Map<Character, TrieNode> getChildren() {
      return children;
    }

    public boolean isWord() {
      return isWord;
    }

    public void setWord(boolean word) {
      isWord = word;
    }
}
