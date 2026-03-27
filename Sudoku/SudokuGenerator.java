package com.Day18.Sudoku;

import java.util.Random;

public class SudokuGenerator {

    public void generateBoard(SudokuBoard board) {

        fillPattern(board);
        removeCells(board, 31);

    }

    private void fillPattern(SudokuBoard board) {

        for (int row = 0; row < 9; row++) {

            for (int col = 0; col < 9; col++) {

                int value = (row * 3 + row / 3 + col) % 9 + 1;

                board.setValue(row, col, value);

            }

        }

    }

    private void removeCells(SudokuBoard board, int cellsToRemove) {

        Random random = new Random();

        int removed = 0;

        while (removed < cellsToRemove) {

            int row = random.nextInt(9);
            int col = random.nextInt(9);

            if (!board.isCellEmpty(row, col)) {

                board.setValue(row, col, 0);
                removed++;

            }

        }

    }
}