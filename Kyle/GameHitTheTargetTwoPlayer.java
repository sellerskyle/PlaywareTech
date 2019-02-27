package com.example.playwareexercise2;


import android.os.Handler;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.GameType;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoSound;

import java.util.Random;


public class GameHitTheTargetTwoPlayer extends Game {

    MotoConnection connection = MotoConnection.getInstance();
    MotoSound sound = MotoSound.getInstance();
    int currentTilePlayer1;
    int currentTilePlayer2;

    int timeIntervalPlayer1 = 1000;
    int timeIntervalPlayer2 = 1000;

    int colorPlayer1 = AntData.LED_COLOR_RED;
    int colorPlayer2 = AntData.LED_COLOR_BLUE;


    int timeStep = 100;
    GameHitTheTargetTwoPlayer() {
        setName("Hit The Target");

        GameType gt1 = new GameType(1,GameType.GAME_TYPE_TIME, 30, "1 Player 30 Sec",2);
        addGameType(gt1);
    }
    Handler handler = new Handler();
    Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {

            int tilePlayer1 = getRandomTile(1);
            int tilePlayer2 = getRandomTile(2);

            while (tilePlayer1 == tilePlayer2)
            {
                tilePlayer2 = getRandomTile(2);
            }

            for(int t: connection.connectedTiles) {
                if(tilePlayer1 == t) {
                    connection.setTileColor(colorPlayer1, tilePlayer1);
                } else if(tilePlayer2 == t) {
                    connection.setTileColor(colorPlayer2, tilePlayer2);
                } else {
                    connection.setTileColor(AntData.LED_COLOR_OFF, t);
                }
            }

            currentTilePlayer1 = tilePlayer1;
            currentTilePlayer2 = tilePlayer2;
            handler.postDelayed(this, timeIntervalPlayer1);

        }
    };

    public void onGameStart() {
        super.onGameStart();

        connection.setAllTilesIdle(AntData.LED_COLOR_OFF);
        currentTilePlayer1 = connection.randomIdleTile();
        currentTilePlayer2 = connection.randomIdleTile();
        while(currentTilePlayer1 == currentTilePlayer2) {
            currentTilePlayer2 = connection.randomIdleTile();
        }

        connection.setTileColor(colorPlayer1,currentTilePlayer1);
        connection.setTileColor(colorPlayer2,currentTilePlayer2);

        handler.postDelayed(gameRunnable, timeIntervalPlayer1);
    }

    public void onGameUpdate(byte[] message) {
        super.onGameUpdate(message);

        int event = AntData.getCommand(message);

        if(event == AntData.EVENT_PRESS) {
            int color = AntData.getColorFromPress(message);
            int tileId = AntData.getId(message);

            if(color == colorPlayer1) {
                timeIntervalPlayer1 -= timeStep;
            } else if(color == colorPlayer2) {
                timeIntervalPlayer2 -= timeStep;
            } else {
                timeIntervalPlayer1 += timeStep;
                timeIntervalPlayer2 += timeStep;
            }

            if(timeIntervalPlayer1 <= timeStep) {
                timeIntervalPlayer1 = timeStep;
            }
            if(timeIntervalPlayer2 <= timeStep) {
                timeIntervalPlayer2 = timeStep;
            }
        }
    }

    public void onGameEnd() {
        super.onGameEnd();
        handler.removeCallbacksAndMessages(null);

        connection.setAllTilesBlink(4, AntData.LED_COLOR_RED);
        sound.playStop();
    }

    int getRandomTile(int player) {
        Random random = new Random();
        int randomTile;

        while(true) {
            randomTile = random.nextInt(connection.connectedTiles.size() + 1);
            if (randomTile != currentTilePlayer1 && randomTile != currentTilePlayer2) {
                if(player == 1) {
                    currentTilePlayer1 = randomTile;
                } else {
                    currentTilePlayer2 = randomTile;
                }

                return randomTile;
            }
        }
    }

}
