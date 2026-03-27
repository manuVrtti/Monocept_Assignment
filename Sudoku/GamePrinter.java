package com.Day18.Sudoku;

public class GamePrinter {

    public void printBoard(SudokuBoard board) {

        int[][] grid = board.getGrid();

        System.out.println("\n====================================");
        System.out.println("            SUDOKU GAME");
        System.out.println("====================================\n");

        for (int i = 0; i < 9; i++) {

            if (i % 3 == 0 && i != 0) {
                System.out.println("-------------------------");
            }

            for (int j = 0; j < 9; j++) {

                if (j % 3 == 0 && j != 0) {
                    System.out.print("| ");
                }

                if (grid[i][j] == 0) {
                    System.out.print(". ");
                }
                else {
                    System.out.print(grid[i][j] + " ");
                }

            }

            System.out.println();

        }

        System.out.println("\n------------------------------------");
        System.out.println("	Enter your move");
        System.out.println("------------------------------------\n");

    }

    public void printMessage(String message) {

        System.out.println(message);

    }

}