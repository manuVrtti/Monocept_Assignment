package com.tictactoe.facade;

import java.util.Scanner;

public class InputService {

    private Scanner sc;

    public InputService(Scanner sc) {
        this.sc = sc;
    }

    public int getMove(BoardService board) {

        int pos;

        while(true) {

            System.out.print("Enter position (1-9): ");

            if(!sc.hasNextInt()) {
                System.out.println("❌ Invalid input! Enter numbers only.");
                sc.next();
                continue;
            }

            pos = sc.nextInt();

            if(pos < 1 || pos > 9) {
                System.out.println("❌ Enter number between 1-9.");
                continue;
            }

            if(!board.isAvailable(pos)) {
                System.out.println("❌ Position already taken!");
                continue;
            }

            break;
        }

        return pos;
    }
}