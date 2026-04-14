package com.tictactoe.facade;

public class BoardService {

    private char[] board = new char[9];

    public void reset() {

        for(int i = 0; i < 9; i++) {
            board[i] = (char)('1' + i);
        }
    }

    public void display() {

        System.out.println();
        System.out.println("-------------");
        System.out.println("| " + board[0] + " | " + board[1] + " | " + board[2] + " |");
        System.out.println("-------------");
        System.out.println("| " + board[3] + " | " + board[4] + " | " + board[5] + " |");
        System.out.println("-------------");
        System.out.println("| " + board[6] + " | " + board[7] + " | " + board[8] + " |");
        System.out.println("-------------");
    }

    public boolean isAvailable(int pos) {
        return board[pos - 1] != 'X' && board[pos - 1] != 'O';
    }

    public void place(int pos, char player) {
        board[pos - 1] = player;
    }

    public boolean checkWin(char p) {

        int[][] w = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
        };

        for(int i = 0; i < w.length; i++) {

            if(board[w[i][0]] == p &&
               board[w[i][1]] == p &&
               board[w[i][2]] == p) {
                return true;
            }
        }

        return false;
    }

    public boolean isDraw() {

        for(int i = 0; i < 9; i++) {
            if(board[i] != 'X' && board[i] != 'O')
                return false;
        }

        return true;
    }
}