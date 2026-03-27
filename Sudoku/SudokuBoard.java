package com.Day18.Sudoku;

public class SudokuBoard {

    private int[][] grid = new int[9][9];

    public int getValue(int row, int col) {

        return grid[row][col];

    }

    public void setValue(int row, int col, int value) {

        grid[row][col] = value;

    }

    public boolean isCellEmpty(int row, int col) {

        return grid[row][col] == 0;

    }

    public boolean isBoardComplete() {

        for (int i = 0; i < 9; i++) {

            for (int j = 0; j < 9; j++) {

                if (grid[i][j] == 0)
                    return false;

            }

        }

        return true;

    }

    public int[][] getGrid() {

        return grid;

    }
}