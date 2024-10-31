package com.example.wordhunt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class DictionaryLoader {

    public static Set<String> loadDictionary(String filePath) throws Exception {
        Set<String> dictionary = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                dictionary.add(line.trim().toUpperCase());
            }
        }
        return dictionary;
    }
}
