package com.tictactoe.facade;

import java.util.Scanner;

public class GameService {

    private BoardService board = new BoardService();
    private ComputerService computer = new ComputerService();
    private Scanner sc = new Scanner(System.in);
    private InputService input = new InputService(sc);

    private int humanScore = 0;
    private int computerScore = 0;
    private int draws = 0;

    public void play() {

        boolean playAgain = true;

        System.out.println("===== TIC TAC TOE (Human vs Computer) =====");

        while(playAgain) {

            board.reset();
            boolean gameOver = false;

            while(!gameOver) {

                board.display();

                // HUMAN MOVE
                int humanMove = input.getMove(board);
                board.place(humanMove, 'X');

                if(board.checkWin('X')) {
                    board.display();
                    System.out.println("🎉 You Win!");
                    humanScore++;
                    break;
                }

                if(board.isDraw()) {
                    board.display();
                    System.out.println("🤝 Match Draw!");
                    draws++;
                    break;
                }

                // COMPUTER MOVE
                System.out.println("Computer is thinking...");

                int compMove = computer.getMove(board);
                board.place(compMove, 'O');

                System.out.println("Computer chose position: " + compMove);

                if(board.checkWin('O')) {
                    board.display();
                    System.out.println("💻 Computer Wins!");
                    computerScore++;
                    break;
                }

                if(board.isDraw()) {
                    board.display();
                    System.out.println("🤝 Match Draw!");
                    draws++;
                    break;
                }
            }

            showScore();
            playAgain = askReplay();
        }

        System.out.println("Thanks for playing 😎");
        sc.close();
    }

    private void showScore() {

        System.out.println("\n===== SCOREBOARD =====");
        System.out.println("You Wins      : " + humanScore);
        System.out.println("Computer Wins : " + computerScore);
        System.out.println("Draws         : " + draws);
        System.out.println("======================\n");
    }

    private boolean askReplay() {

        while(true) {

            System.out.print("Play again? (Y/N): ");
            String choice = sc.next().toUpperCase();

            if(choice.equals("Y")) return true;
            if(choice.equals("N")) return false;

            System.out.println("❌ Invalid choice!");
        }
    }
}