package com.example.wordhunt;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;

public class WordNetHelper {
    private static Dictionary dictionary;

    public static void initialize() {
        try {
            InputStream inputStream = WordNetHelper.class.getResourceAsStream("/jwnl_properties.xml");
            JWNL.initialize(inputStream);
            dictionary = Dictionary.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Dictionary getDictionary() {
        return dictionary;
    }

        public static Set<String> getRelatedWords(String word) {
        Set<String> relatedWords = new HashSet<>();
        try {
            // Initialize dictionary if not already done
            if (dictionary == null) {
                initialize();
            }

            // Lookup the word in different parts of speech
            POS[] posArray = {POS.NOUN, POS.VERB, POS.ADJECTIVE, POS.ADVERB};

            for (POS pos : posArray) {
                IndexWord indexWord = dictionary.lookupIndexWord(pos, word);
                if (indexWord != null) {
                    for (Synset synset : indexWord.getSenses()) {
                        for (Word synsetWord : synset.getWords()) {
                            String lemma = synsetWord.getLemma().replace('_', ' ');
                            relatedWords.add(lemma);
                        }
                    }
                }
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return relatedWords;
    }
}
