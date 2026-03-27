package com.Day18.Sudoku;

public class SudokuGame {

    private SudokuBoard board;
    private SudokuGenerator generator;
    private SudokuValidator validator;
    private InputHandler input;
    private GamePrinter printer;

    public SudokuGame() {

        board = new SudokuBoard();
        generator = new SudokuGenerator();
        validator = new SudokuValidator();
        input = new InputHandler();
        printer = new GamePrinter();

    }

    public void startGame() {

        generator.generateBoard(board);

        printer.printBoard(board);

        while (!board.isBoardComplete()) {

            playTurn();

        }

        printer.printMessage("\n🎉 Congratulations! You completed the Sudoku!\n");

    }

    private void playTurn() {

        try {

            int row = input.readRow();
            int col = input.readColumn();

            // Check if cell already filled
            if (!board.isCellEmpty(row, col)) {

                printer.printMessage("\n⚠ This cell is already filled. Choose another position.\n");
                return;

            }

            int number = input.readNumber();

            // Validate Sudoku rules
            if (!validator.isValidMove(board, row, col, number)) {

                throw new InvalidMoveException("\n❌ Invalid move! Number violates Sudoku rules.\n");

            }

            // Place number
            board.setValue(row, col, number);

            printer.printMessage("\n✅ Number placed successfully!\n");

            printer.printBoard(board);

        }

        catch (InvalidMoveException e) {

            printer.printMessage(e.getMessage());

        }

        catch (Exception e) {

            printer.printMessage("\n⚠ Invalid input! Please try again.\n");

        }

    }

}