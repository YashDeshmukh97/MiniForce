package com.miniforce.judge.pilot;

import java.util.ArrayList;
import java.util.List;

public class WordLadderInputCodec implements InputCodec {
    @Override
    public Object[] parse(String rawInput) {
        try {
            String[] parts = rawInput.split(";", 3);
            String beginWord = parts[0].trim();
            String endWord = parts[1].trim();
            String[] listParts = parts[2].trim().split(",");
            List<String> wordList = new ArrayList<>();
            for (String s : listParts) {
                if (!s.trim().isEmpty()) {
                    wordList.add(s.trim());
                }
            }
            return new Object[]{beginWord, endWord, wordList};
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed input: " + rawInput, e);
        }
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] args = parse(rawInput);
        String beginWord = (String) args[0];
        String endWord = (String) args[1];
        @SuppressWarnings("unchecked")
        List<String> wordList = (List<String>) args[2];
        StringBuilder sb = new StringBuilder();
        sb.append(beginWord).append("\n");
        sb.append(endWord).append("\n");
        sb.append(wordList.size()).append("\n");
        for (String w : wordList) sb.append(w).append("\n");
        return sb.toString();
    }
}
