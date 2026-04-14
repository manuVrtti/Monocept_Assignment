package com.tictactoe.facade;

public class TicTacToeFacade {

    private GameService gameService;

    public TicTacToeFacade() {
        gameService = new GameService();
    }

    public void startGame() {
        gameService.play();
    }
}