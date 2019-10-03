/*
 * Copyright (c) 2016 Dirk Koelewijn. All Rights Reserved.
 */

package utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class to randomize sequences
 *
 * @author Dirk (created on 14-10-2016)
 * @version 1.0
 */
public class SeqUtil {
    /**
     * Randomize order of an array
     *
     * @param sequence The array which will be ordered randomly
     * @param r        A {@link Random} instance to use
     * @return The randomly ordered array
     */
    public static int[] randOrder(int[] sequence, Random r) {
        int length = sequence.length;
        int[] res = new int[length];

        //Create list of sequence items
        ArrayList<Integer> items = new ArrayList<>();
        for (int value : sequence) {
            items.add(value);
        }

        for (int i = 0; i < length; i++) {
            int randomIndex = r.nextInt(items.size());
            res[i] = items.get(randomIndex);
            items.remove(randomIndex);
        }

        return res;
    }

    /**
     * Creates a consecutive sequence
     *
     * @param start  Start value
     * @param length Sequence length
     * @return The new sequence
     */
    public static int[] getSequence(int start, int length) {
        int[] res = new int[length];

        for (int i = 0; i < length; i++) {
            res[i] = start + i;
        }

        return res;
    }
}
