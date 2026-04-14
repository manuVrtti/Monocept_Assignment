package com.tictactoe.facade;


import java.util.Random;

public class ComputerService {

    private Random rand = new Random();

    public int getMove(BoardService board) {

        int move;

        while(true) {

            move = rand.nextInt(9) + 1;

            if(board.isAvailable(move))
                return move;
        }
    }
}