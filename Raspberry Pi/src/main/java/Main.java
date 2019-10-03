/*
 * Copyright (c) 2016 Dirk Koelewijn. All Rights Reserved.
 */

import puzzles.Sudoku;
import utils.Debug;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static utils.Debug.DT_INFO;

/**
 * Class for trying and measuring Sudoku related things
 *
 * @author Dirk Koelewijn
 * @version 1.0
 */

public class Main {
    private static Debug d = new Debug(Main.class);

    public static void main(String[] args) {
        if (args.length > 0) {
            String arguments = String.join(" ", (CharSequence[]) args);
            if (arguments.toLowerCase().contains("-cmd")) Debug.PRINT_COLORS = false;
        }

        Map<Integer, Set<int[][]>> generated = new HashMap<>();

        int N = 50;
        for (int i = 0; i < N; i++) {
            int[][] randomSolution = Sudoku.getRandom(500);
            int empty = Sudoku.getEmptySquares(randomSolution);
            if (!generated.containsKey(empty)) {
                generated.put(empty, new HashSet<>());
            }
            generated.get(empty).add(randomSolution);

            if ((i + 1) % 50 == 0) {
                int unique = 0;
                for (int e :
                        generated.keySet()) {
                    unique += generated.get(e).size();
                }

                d.print(DT_INFO, "main()", "\n\tSudoku's generated: " + (i + 1)
                        + "\n\tOf which unique: " + unique + " (" + (unique * 100 / (i + 1)) + "%)\n\t"
                );
            }
        }
        String res = "";
        for (int e : generated.keySet()) {
            res += "\n\t" + e + " empty squares: " + generated.get(e).size() + " sudoku's";
        }

        d.print(DT_INFO, "main()", res);

    }
}
