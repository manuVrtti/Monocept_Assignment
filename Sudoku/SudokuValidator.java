package com.Day18.Sudoku;

public class SudokuValidator {

    public boolean isValidMove(SudokuBoard board, int row, int col, int num) {

        return checkRow(board, row, num)
                && checkColumn(board, col, num)
                && checkBox(board, row, col, num);

    }

    private boolean checkRow(SudokuBoard board, int row, int num) {

        for (int i = 0; i < 9; i++) {

            if (board.getValue(row, i) == num)
                return false;

        }

        return true;

    }

    private boolean checkColumn(SudokuBoard board, int col, int num) {

        for (int i = 0; i < 9; i++) {

            if (board.getValue(i, col) == num)
                return false;

        }

        return true;

    }

    private boolean checkBox(SudokuBoard board, int row, int col, int num) {

        int startRow = row - row % 3;
        int startCol = col - col % 3;

        for (int i = 0; i < 3; i++) {

            for (int j = 0; j < 3; j++) {

                if (board.getValue(startRow + i, startCol + j) == num)
                    return false;

            }

        }

        return true;

    }
}