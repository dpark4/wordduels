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
                line = line.trim().toUpperCase();
                if (line.length() >= 3 && line.length() <= 15) { // Filter for length
                    dictionary.add(line);
                }
            }
        }
        return dictionary;
    }
}
