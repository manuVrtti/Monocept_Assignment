package com.Day18.Sudoku;

import java.util.Scanner;

public class InputHandler {

    private Scanner scanner = new Scanner(System.in);

    public int readRow() {

        return readInput( "Enter Row (1-9): ");

    }

    public int readColumn() {

        return readInput("Enter Column (1-9): ");

    }

    public int readNumber() {

        return readInput("Enter Number (1-9): ");

    }

    private int readInput(String message) {

        while (true) {

            System.out.print(message);

            String input = scanner.nextLine();

            if (!input.matches("[1-9]")) {

                System.out.println("Invalid input! Enter 1-9.");
                continue;

            }

            return Integer.parseInt(input) - 1;

        }

    }
}