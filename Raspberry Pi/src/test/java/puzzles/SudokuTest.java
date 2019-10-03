package puzzles;

import org.junit.Before;
import org.junit.Test;
import utils.Debug;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;
import static puzzles.Examples.Sudokus.*;

/**
 * Test for Sudoku class
 *
 * @author Dirk (created on 19-10-2016)
 * @version 1.0
 */
public class SudokuTest {

    private static Debug d = new Debug(SudokuTest.class);

    @Before
    public void setUp() throws Exception {
        Debug.DEBUG_LEVEL = Debug.DT_INFO.getPriority();
    }

    @Test
    public void copy() throws Exception {
        int[][] testPuzzle = PUZZLE_1_TWO_SOLUTIONS;
        assertTrue("Hashes of copied sudoku's don't match", Sudoku.getHash(testPuzzle) == Sudoku.getHash(Sudoku.copy(testPuzzle)));
        assertFalse("Hard copy of sudoku is the same object", testPuzzle == Sudoku.copy(testPuzzle));
    }

    @Test
    public void getSolution() throws Exception {
        //Test valid puzzle
        int[][] testPuzzle = PUZZLE_1_TWO_SOLUTIONS, solution = Sudoku.getSolution(testPuzzle);
        assertTrue("Puzzle should have a solution", solution != null);
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (testPuzzle[r][c] != 0)
                    assertTrue("getSolution changes the original values", testPuzzle[r][c] == solution[r][c]);
            }
        }
        assertTrue("Sudoku is not solved. [isSolved() returns false]", Sudoku.isSolved(solution));

        //Test invalid puzzle
        testPuzzle = PUZZLE_3_INVALID;
        assertTrue("Puzzle 3 should have no solution [invalid sudoku]", Sudoku.getSolution(testPuzzle) == null);

        //Test puzzle without solution
        testPuzzle = PUZZLE_4_NOSOLUTION;
        assertTrue("Puzzle 4 should have no solution [no solutions possible]", Sudoku.getSolution(testPuzzle) == null);
    }

    @Test
    public void isValidValue() throws Exception {
        //Fault is at 3rd row, 6th column
        int[][] testPuzzle = {
                {0, 0, 0, 5, 0, 8, 4, 0, 0},
                {5, 2, 0, 0, 0, 0, 0, 0, 0},
                {0, 8, 7, 0, 0, 5, 0, 3, 1},
                {0, 0, 3, 0, 1, 0, 0, 8, 0},
                {9, 0, 0, 8, 6, 3, 0, 0, 5},
                {0, 5, 0, 0, 9, 0, 6, 0, 0},
                {1, 3, 0, 0, 0, 0, 2, 5, 0},
                {0, 0, 0, 0, 0, 0, 0, 7, 4},
                {0, 0, 5, 2, 0, 6, 3, 0, 0}};

        assertTrue("Value of 2nd row, 2nd column is correct", Sudoku.isValidValue(testPuzzle, 1, 1));
        assertFalse("Value at 3rd row, 5th column is incorrect", Sudoku.isValidValue(testPuzzle, 2, 5));
    }

    @Test
    public void isValid() throws Exception {
        assertTrue("Puzzle1 is valid", Sudoku.isValid(PUZZLE_1_TWO_SOLUTIONS));
        assertTrue("Puzzle2 is valid", Sudoku.isValid(PUZZLE_2_SINGLE_SOLUTION));
        assertTrue("Puzzle4 is valid", Sudoku.isValid(PUZZLE_4_NOSOLUTION));
        assertFalse("Puzzle3 is invalid", Sudoku.isValid(PUZZLE_3_INVALID));
    }

    @Test
    public void getRandomSolution() throws Exception {

    }

    @Test
    public void isSolved() throws Exception {
        assertFalse(Sudoku.isSolved(PUZZLE_1_TWO_SOLUTIONS));
        assertFalse(Sudoku.isSolved(PUZZLE_2_SINGLE_SOLUTION));
        assertTrue(Sudoku.isSolved(Sudoku.getSolution(PUZZLE_1_TWO_SOLUTIONS)));
        assertTrue(Sudoku.isSolved(Sudoku.getSolution(PUZZLE_2_SINGLE_SOLUTION)));
    }

    @Test
    public void hasSolution() throws Exception {
        assertTrue(Sudoku.hasSolution(PUZZLE_1_TWO_SOLUTIONS));
        assertTrue(Sudoku.hasSolution(PUZZLE_2_SINGLE_SOLUTION));
        assertFalse(Sudoku.hasSolution(PUZZLE_3_INVALID));
        assertFalse(Sudoku.hasSolution(PUZZLE_4_NOSOLUTION));
    }

    @Test
    public void compHasSingleSolution() throws Exception {
        assertEquals(1, Sudoku.compHasSingleSolution(PUZZLE_1_TWO_SOLUTIONS));
        assertEquals(0, Sudoku.compHasSingleSolution(PUZZLE_2_SINGLE_SOLUTION));
        assertEquals(-1, Sudoku.compHasSingleSolution(PUZZLE_3_INVALID));
    }

    @Test
    public void getSolutions() throws Exception {
        assertEquals(2, Sudoku.getSolutions(PUZZLE_1_TWO_SOLUTIONS).size());
        assertEquals(1, Sudoku.getSolutions(PUZZLE_2_SINGLE_SOLUTION).size());
        assertEquals(0, Sudoku.getSolutions(PUZZLE_3_INVALID).size());
    }

    @Test
    public void getSolutions1() throws Exception {
        for (int i = 0; i < 10; i++) {
            int timeGiven = new Random().nextInt(500);
            long sT = System.currentTimeMillis();
            Sudoku.getSolutions(PUZZLE_2_SINGLE_SOLUTION, timeGiven);
            long eT = System.currentTimeMillis();
            assertTrue(eT - sT < timeGiven + 5);
        }
    }

    @Test
    public void getSolutions2() throws Exception {
        assertEquals(1, Sudoku.getSolutions(PUZZLE_1_TWO_SOLUTIONS, -1, 1).size());
        assertEquals(1, Sudoku.getSolutions(PUZZLE_2_SINGLE_SOLUTION, -1, 1).size());
        assertEquals(0, Sudoku.getSolutions(PUZZLE_3_INVALID, -1, 1).size());
    }

    @Test
    public void getRandom() throws Exception {
        //Test randomization
        int N = 10;
        int maxTime = 250;
        d.print(Debug.DT_TEST_WARNING, "getRandom()", "This test might take long . Estimated time to finish: " + ((double) N * maxTime) / 1000 + " seconds");
        Set<int[][]> results = new HashSet<>();
        for (int i = 0; i < N; i++) {
            results.add(Sudoku.getRandom(maxTime));
        }
        assertEquals(N, results.size());
    }

    @Test
    public void getEmptySquares() throws Exception {
        assertEquals(81, Sudoku.getEmptySquares(new int[9][9]));
        assertEquals(0, Sudoku.getEmptySquares(Sudoku.getRandomSolution(new int[9][9])));
    }

    @Test
    public void getHash() throws Exception {
        assertNotEquals(Sudoku.getHash(PUZZLE_1_TWO_SOLUTIONS), Sudoku.getHash(PUZZLE_2_SINGLE_SOLUTION));
        assertEquals(Sudoku.getHash(PUZZLE_1_TWO_SOLUTIONS), Sudoku.getHash(Sudoku.copy(PUZZLE_1_TWO_SOLUTIONS)));
    }

}