package puzzles;

import utils.Debug;
import utils.SeqUtil;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static puzzles.Sudoku.PrintSettings.*;
import static utils.ConsoleColor.*;
import static utils.Debug.DT_DEBUG_LOW;
import static utils.Debug.DT_WARNING;

/**
 * Class to solve and generate sudoku's
 *
 * @author Dirk Koelewijn
 * @version 3.0
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Sudoku {
    //Init debug
    private static final Debug d = new Debug(Sudoku.class);

    /**
     * Create string representation of Sudoku grid
     *
     * @param grid Sudoku grid
     * @return String representation of Sudoku
     */
    public static String toString(int[][] grid) {
        return toString(grid, new int[9][9]);
    }

    /**
     * Create string representation of Sudoku grid
     *
     * @param grid     Sudoku grid
     * @param original Original sudoku
     * @return String representation of Sudoku
     */
    public static String toString(final int[][] grid, int[][] original) {
        String BBFullLine = colored("+-----+-----+-----+-----+-----+-----+-----+-----+-----+\n", BIG_BORDER_FG, BIG_BORDER_BG);
        String BBPlus = colored("+", BIG_BORDER_FG, BIG_BORDER_BG);
        String B3Lines = colored("-----+-----+-----", BORDER_FG, BORDER_BG);
        String BBVLine = colored("|", BIG_BORDER_FG, BIG_BORDER_BG);
        String BVLine = colored("|", BORDER_FG, BORDER_BG);
        String normalLine = BBPlus + B3Lines + BBPlus + B3Lines + BBPlus + B3Lines + BBPlus + "\n";
        String res = BBFullLine;
        for (int r = 0; r < 9; r++) {
            res += BBVLine;
            for (int c = 0; c < 9; c++) {
                int val = grid[r][c];
                res += "  " + (val != 0 ? (original[r][c] != 0 ? colored("" + val, ORIGINAL_FG, ORIGINAL_BG) : colored("" + val, DEFAULT_FG, DEFAULT_BG)) : " ") + "  " + ((c + 1) % 3 == 0 ? BBVLine : BVLine);
            }
            res += "\n" + ((r + 1) % 3 == 0 ? BBFullLine : normalLine);
        }
        return res;
    }

    /**
     * Hard copy of sudoku representatoin
     *
     * @param source Source
     * @return Hard copy of source
     */
    public static int[][] copy(final int[][] source) {
        final int[][] res = new int[source.length][source[0].length];
        for (int x = 0; x < source.length; x++) {
            System.arraycopy(source[x], 0, res[x], 0, source[x].length);
        }
        return res;
    }

    /**
     * Finds first solution of Sudoku
     *
     * @param original Original sudoku
     * @return Solved sudoku
     */
    public static int[][] getSolution(int[][] original) {
        //Init vars
        final int[][] grid = copy(original);
        int pos = 0, r = 0, c = 0;

        //Check if sudoku is valid
        if (!isValid(original)) return null;

        //Continue until returned to first position
        while (pos >= 0) {
            //Check if current square isn't defined in original
            if (original[r][c] == 0) {
                //Check if increased value still in range
                if (++grid[r][c] < 10) {
                    //Check if value is valid. If not, just increase again at current position
                    if (isValidValue(grid, r, c)) {
                        //Check if end of sudoku (solution found!), else continue to next position
                        if (pos >= 80) return grid;
                        else {
                            r = ++pos / 9;
                            c = pos % 9;
                        }
                    }
                } else {
                    //Reset current square
                    grid[r][c] = 0;
                    do {
                        r = --pos / 9;
                        c = pos % 9;
                    } while (pos >= 0 && original[r][c] != 0);
                }
            } else {
                //Check if end reached
                if (++pos >= 80) {
                    return grid;
                } else {
                    r = pos / 9;
                    c = pos % 9;
                }
            }
        }
        return null;
    }

    /**
     * Checks if value at row, col in grid is a valid value for that position
     *
     * @param grid Sudoku grid
     * @param row  Cell's row
     * @param col  Cell's column
     * @return Whether the value is valid.
     */
    public static boolean isValidValue(final int[][] grid, int row, int col) {
        final int value = grid[row][col];
        // if v present square.row, return false
        for (int c = 0; c < 9; c++) {
            if (c != col && grid[row][c] == value)
                return false;
        }

        // if v present in square.col, return false
        for (int r = 0; r < 9; r++) {
            if (r != row && grid[r][col] == value)
                return false;
        }

        // if v present in grid, return false

        // to get the grid we should calculate (x1,y1) (x2,y2)
        final int x1 = 3 * (row / 3);
        final int y1 = 3 * (col / 3);
        final int x2 = x1 + 2;
        final int y2 = y1 + 2;

        for (int x = x1; x <= x2; x++)
            for (int y = y1; y <= y2; y++)
                if (!(x == row && y == col) && grid[x][y] == value)
                    return false;

        // if value not present in row, col and bounding box, return true
        return true;
    }

    /**
     * Checks if a Sudoku is valid.
     *
     * @param grid Sudoku grid
     * @return Whether the Sudoku is valid
     */
    public static boolean isValid(int[][] grid) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (grid[r][c] != 0 && !Sudoku.isValidValue(grid, r, c)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Generates a random solution
     *
     * @param grid Sudoku grid
     * @return Random solution
     */
    public static int[][] getRandomSolution(int[][] grid) {
        //Init random sequence variables
        int[][][] randSequences = new int[9][9][9];
        int[][] seqIndex = new int[9][9];

        //Get sequence and random
        int[] seq = SeqUtil.getSequence(1, 9);
        Random random = new SecureRandom();

        //Init sequence table
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                randSequences[r][c] = SeqUtil.randOrder(seq, random);
            }
        }

        //Copy grid
        int[][] original = copy(grid);
        //Init position
        int pos = 0, r = 0, c = 0;

        //Check if sudoku is valid
        if (!isValid(original)) return null;

        //Continue until returned to first position
        while (pos >= 0) {
            //Check if current square isn't defined in original
            if (original[r][c] == 0) {
                //Get sequence index for position and increase it afterwards
                int curIndex = seqIndex[r][c]++;
                //Check if sequence index still in range
                if (curIndex < 9) {
                    //Get next random number
                    grid[r][c] = randSequences[r][c][curIndex];
                    //Check if value is valid and increase position
                    if (isValidValue(grid, r, c)) {
                        //If solution found
                        if (pos > 79) return grid;
                        else {
                            r = ++pos / 9;
                            c = pos % 9;
                        }
                    }
                } else {
                    //Reset current square & random number index
                    grid[r][c] = 0;
                    seqIndex[r][c] = 0;
                    //Go to previous position which is empty in original
                    do {
                        r = --pos / 9;
                        c = pos % 9;
                    } while (original[r][c] != 0);
                }
            } else {
                //Check if end reached
                if (++pos >= 80) return grid;
                else {
                    r = pos / 9;
                    c = pos % 9;
                }
            }
        }
        return null;
    }

    /**
     * Checks whether a Sudoku is solved (full and valid)
     *
     * @param grid Sudoku grid
     * @return Whether the sudoku is solved
     */
    public static boolean isSolved(int[][] grid) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (grid[r][c] == 0 || !Sudoku.isValidValue(grid, r, c)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks whether a Sudoku has a solution.
     *
     * @param grid Sudoku grid
     * @return Solution
     */
    public static boolean hasSolution(int[][] grid) {
        return getSolution(grid) != null;
    }

    /**
     * Compares number of solutions to 1.
     *
     * @param grid Sudoku grid
     * @return Returns -1 if no solutions, 0 if one solution and 1 if more than 1 solution
     */
    public static int compHasSingleSolution(int[][] grid) {
        return getSolutions(grid, -1, 2).size() - 1;
    }

    /**
     * Returns all solutions. WARNING: Might take ages if a too empty Sudoku is given.
     *
     * @param grid Sudoku grid
     * @return List of all found solutions
     */
    public static ArrayList<int[][]> getSolutions(int[][] grid) {
        return getSolutions(grid, -1, -1);
    }

    /**
     * Returns all solutions which could be found in the given time
     *
     * @param grid    Sudoku grid
     * @param maxTime Time in ms (-1 = no time restriction)
     * @return List of all found solutions
     */
    public static ArrayList<int[][]> getSolutions(int[][] grid, int maxTime) {
        return getSolutions(grid, maxTime, -1);
    }

    /**
     * Returns a maximal amount solutions which could be found in the given time
     *
     * @param grid         Sudoku grid
     * @param maxTime      Time in ms (-1 = no restriction)
     * @param maxSolutions Solutions (-1 = no restriction)
     * @return List of all found solutions
     */
    public static ArrayList<int[][]> getSolutions(int[][] grid, int maxTime, int maxSolutions) {
        if (maxTime == -1 && maxSolutions == -1)
            d.print(DT_WARNING, "getSolutions(grid, -1, -1)", "Looking for all solutions without time or solution count restrictions");

        //Init vars
        int[][] original = copy(grid);
        grid = copy(original);
        ArrayList<int[][]> solutions = new ArrayList<>();

        //Check if sudoku is valid
        if (!isValid(grid)) {
            return solutions;
        }

        int pos = 0, r = 0, c = 0;

        long sT = System.currentTimeMillis();

        //Continue until returned to first position
        while (pos >= 0 && (maxTime == -1 || System.currentTimeMillis() - sT > maxTime)) {
            //Check if current square isn't defined in original
            if (original[r][c] == 0) {
                //Check if increased value still in range
                if (++grid[r][c] < 10) {
                    //Check if value is valid. If not, just increase again at current position
                    if (isValidValue(grid, r, c)) {
                        //Check if end of sudoku (solution found!), else continue to next position
                        if (pos >= 80) {
                            solutions.add(grid);
                            if (maxSolutions != -1 && solutions.size() >= maxSolutions) {
                                return solutions;
                            }
                        } else {
                            r = ++pos / 9;
                            c = pos % 9;
                        }
                    }
                } else {
                    //Reset current square
                    grid[r][c] = 0;
                    do {
                        r = --pos / 9;
                        c = pos % 9;
                    } while (pos >= 0 && original[r][c] != 0);
                }
            } else {
                //Check if end reached
                if (++pos >= 80) {
                    solutions.add(grid);
                    if (maxSolutions != -1 && solutions.size() == maxSolutions) {
                        return solutions;
                    }
                    do {
                        r = --pos / 9;
                        c = pos % 9;
                    } while (pos >= 0 && original[r][c] != 0);
                } else {
                    r = pos / 9;
                    c = pos % 9;
                }
            }
        }
        return solutions;
    }

    /**
     * Generates a random Sudoku in the given time
     *
     * @param maxTime Maximal execution time in ms.
     * @return A random sudoku
     */
    public static int[][] getRandom(int maxTime) {
        //Create vars for best result
        int max_empty = 0;
        int[][] best_sudoku = new int[9][9];

        //Randomize position order
        int[] positionOrder = SeqUtil.randOrder(SeqUtil.getSequence(0, 81), new SecureRandom());
        int i = 0;

        //Get start time
        long start = System.currentTimeMillis();
        int r = 0, c = 0;

        //Generate random solution
        int[][] originalSolution = Sudoku.getRandomSolution(new int[9][9]);
        if (originalSolution == null) {
            d.print(DT_WARNING, "getRandom()", "'originalSolution' == null! Retrying...");
            return getRandom(maxTime);
        }

        //Initiate variables
        int[][] current = copy(originalSolution);
        int curEmpty = 0;

        //Start making squares empty
        while (i >= 0 && (System.currentTimeMillis() - start < maxTime)) {
            if (Sudoku.compHasSingleSolution(copy(current)) == 0) {
                if (curEmpty > max_empty) {
                    max_empty = curEmpty;
                    best_sudoku = copy(current);
                }

                i++;
                r = positionOrder[i] / 9;
                c = positionOrder[i] % 9;

                current[r][c] = 0;
                curEmpty++;
            } else {
                current[r][c] = originalSolution[r][c];
                curEmpty--;
                do {
                    i--;
                    r = positionOrder[i] / 9;
                    c = positionOrder[i] % 9;
                } while (current[r][c] != 0);

                current[r][c] = originalSolution[r][c];
                curEmpty--;
            }
        }

        d.print(DT_DEBUG_LOW, "getRandom()", "Generated Sudoku with " + max_empty + " empty squares.");
        return best_sudoku;
    }

    /**
     * Counts the amount of empty squares in a Sudoku.
     *
     * @param grid Sudoku grid
     * @return Amount of empty squares
     */
    public static int getEmptySquares(int[][] grid) {
        int res = 0;
        for (int[] row : grid) for (int cell : row) if (cell == 0) res++;
        return res;
    }

    /**
     * Returns a near unique hash of a Sudoku
     *
     * @param grid Sudoku grid
     * @return Hash
     */
    public static int getHash(int[][] grid) {
        return Arrays.deepHashCode(grid);
    }

    public static class PrintSettings {
        public final static String BIG_BORDER_FG = Foreground.BLUE;
        public final static String BIG_BORDER_BG = NONE;
        public final static String BORDER_FG = NONE;
        public final static String BORDER_BG = NONE;
        public final static String ORIGINAL_FG = Foreground.CYAN;
        public final static String ORIGINAL_BG = NONE;
        public final static String DEFAULT_FG = NONE;
        public final static String DEFAULT_BG = NONE;
    }
}
