package com.miniforce.judge.pilot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class NestedUnorderedListIntegerComparator implements OutputComparator {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean compare(String expected, String actual) {
        try {
            List<List<Integer>> exp = mapper.readValue(expected, new TypeReference<List<List<Integer>>>() {});
            List<List<Integer>> act = mapper.readValue(actual, new TypeReference<List<List<Integer>>>() {});
            
            if (exp.size() != act.size()) return false;

            List<List<Integer>> expSorted = new ArrayList<>();
            for (List<Integer> list : exp) {
                List<Integer> sortedList = new ArrayList<>(list);
                Collections.sort(sortedList);
                expSorted.add(sortedList);
            }
            
            List<List<Integer>> actSorted = new ArrayList<>();
            for (List<Integer> list : act) {
                List<Integer> sortedList = new ArrayList<>(list);
                Collections.sort(sortedList);
                actSorted.add(sortedList);
            }
            
            expSorted.sort((l1, l2) -> {
                int lenCmp = Integer.compare(l1.size(), l2.size());
                if (lenCmp != 0) return lenCmp;
                for (int i = 0; i < l1.size(); i++) {
                    int c = l1.get(i).compareTo(l2.get(i));
                    if (c != 0) return c;
                }
                return 0;
            });

            actSorted.sort((l1, l2) -> {
                int lenCmp = Integer.compare(l1.size(), l2.size());
                if (lenCmp != 0) return lenCmp;
                for (int i = 0; i < l1.size(); i++) {
                    int c = l1.get(i).compareTo(l2.get(i));
                    if (c != 0) return c;
                }
                return 0;
            });

            return expSorted.equals(actSorted);
        } catch (Exception e) {
            return false;
        }
    }
}
