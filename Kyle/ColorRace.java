package com.example.playwareexercise2;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.GameType;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoSound;

public class ColorRace extends Game {

    MotoConnection connection = MotoConnection.getInstance();
    MotoSound sound = MotoSound.getInstance();
    ColorRace() {
        setName("Color Race");
        setDescription("What a wonderful game!");

        GameType gt = new GameType(1, GameType.GAME_TYPE_TIME, 30, "1 Player 30 sec", 1);
        GameType gt2 = new GameType(2, GameType.GAME_TYPE_TIME, 60, "1 Player 60 sec", 1);
        GameType gt3 = new GameType(3, GameType.GAME_TYPE_TIME, 120, "1 Player 120 sec", 1);
        addGameType(gt);
        addGameType(gt2);
        addGameType(gt3);

    }

    @Override
    public void onGameStart() {
        super.onGameStart();
        connection.setAllTilesIdle(AntData.LED_COLOR_OFF);

        int randomTile = connection.randomIdleTile();
        connection.setTileColor(AntData.LED_COLOR_RED, randomTile);
    }

    @Override
    public void onGameUpdate(byte[] message) {
        super.onGameUpdate(message);

        int event = AntData.getCommand(message);
        int tileId = AntData.getId(message);
        if(event == AntData.EVENT_PRESS) {
            incrementPlayerScore(1,0);
            sound.playMatched();
            int randomTile = connection.randomIdleTile();
            connection.setTileIdle(AntData.LED_COLOR_OFF, tileId);
            connection.setTileColor(AntData.LED_COLOR_RED, randomTile);

        }

        //To get tile id that was pressed
    }

    @Override
    public void onGameEnd() {
        super.onGameEnd();

        connection.setAllTilesBlink(4, AntData.LED_COLOR_RED);
        sound.playStop();
    }
}
