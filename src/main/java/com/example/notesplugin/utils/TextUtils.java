package com.example.notesplugin.utils;


import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {

    @NotNull
    public static Integer findClosestOffset(String data , String textToSearch, Integer initialOffset) {
        if (StringUtils.isEmpty(textToSearch)) {
            return -1;
        }
        List<Integer> offsets = findOffsets(data, textToSearch);

        return findClosestOffset(offsets, initialOffset);
    }

    @NotNull
    public static Integer findClosestOffset(List<Integer> offsets,  Integer initialOffset) {
        Integer minDeviation = Integer.MAX_VALUE;
        Integer leastDeviatedOffset = Integer.MAX_VALUE;
        for (Integer offset : offsets) {
            int deviation = Math.abs(initialOffset - offset);
            if (deviation < minDeviation) {
                minDeviation = deviation;
                leastDeviatedOffset = offset;
            }
        }
        return leastDeviatedOffset == Integer.MAX_VALUE ? -1 : leastDeviatedOffset;
    }

    public static List<Integer> findOffsets(String input, String search) {
        List<Integer> offsets = new ArrayList<>();
        int offset;
        int nextOffset = 0;
        do {
            offset = input.indexOf(search, nextOffset);
            if (offset != -1) {
                offsets.add(offset);
                nextOffset = offset + search.length();
            }
        } while (offset != -1);
        return offsets;
    }

}
