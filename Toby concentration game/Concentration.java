package com.example.myapplication;

import android.content.Context;
import android.os.Handler;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.GameType;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoSound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


import static com.livelife.motolibrary.AntData.*;
import static com.livelife.motolibrary.AntData.CMD_SET_IDLE;
import static com.livelife.motolibrary.AntData.LED_COLOR_RED;

public class Concentration extends Game {

    MotoConnection connection = MotoConnection.getInstance();
    MotoSound sound = MotoSound.getInstance();
    HashMap<Integer, Integer> hiddenColors = new HashMap<>();
    ArrayList<Integer> selectedTiles = new ArrayList<>();
    Handler showColorHandler;

    public Concentration() {
        setName("Concentration Color");
        setMaxPlayers(1);
        setGameId(3);
        GameType gt1 = new GameType(1, GameType.GAME_TYPE_SPEED, 1, "Default", 1);
        addGameType(gt1);
    }

    boolean isOdd(int num) {
        if (num % 2 == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onGameStart() {
        super.onGameStart();

        showColorHandler = new Handler();

        selectedTiles.clear();
        ArrayList<Integer> shuffeled = new ArrayList<>(connection.connectedTiles);
        Collections.shuffle(shuffeled);

        int col = 0;
        int tile = 1;

        for (Integer t : shuffeled) {
            connection.setTileColor(LED_COLOR_WHITE, t);
            hiddenColors.put(t, allColors().get(col));
            tile++;
            if (isOdd(tile)) {
                col++;
            }
        }

        if (isOdd(shuffeled.size())) {
            Integer last = shuffeled.get(shuffeled.size() - 1);
            connection.setTileColor(LED_COLOR_OFF, last);
        }


    }

    @Override
    public void onGameUpdate(byte[] message) {
        super.onGameUpdate(message);
        int event = getCommand(message);
        int tileId = getId(message);
        if (event == EVENT_PRESS) {
            if (selectedTiles.size() == 0) {
                selectedTiles.add(tileId);
                int currColor = hiddenColors.get(tileId);
                connection.setTileColor(currColor, tileId);
                sound.playPress1();
            } else if (selectedTiles.size() == 1) {
                int prevTile = selectedTiles.get(0);
                int prevColor = hiddenColors.get(prevTile);
                if (prevTile == tileId) {
                    return;
                }

                int currColor = hiddenColors.get(tileId);
                selectedTiles.add(tileId);
                connection.setTileColor(currColor, tileId);
                if (prevColor == currColor) {
                    incrementPlayerScore(1, 0);
                    sound.playMatched();
                    showColorHandler.postDelayed(showColorMatchRunnable, 1000);
                } else {
                    sound.playError();
                    showColorHandler.postDelayed(showColorErrorRunnable, 1000);
                }
            }
        }
    }

    Runnable showColorMatchRunnable = new Runnable() {
        @Override
        public void run() {
            if (selectedTiles.size() == 2) {
                for (int t : selectedTiles) {
                    connection.setTileColor(LED_COLOR_OFF, t);
                }
                selectedTiles.clear();
            }
            int matched = 0;
            for (int t : connection.connectedTiles) {

                AntData data = connection.getCurrentDataForTile(t);
                int status = getCommand(data.getPayload());
                if (status == CMD_SET_IDLE) {
                    matched++;
                }
            }
            if (matched == connection.connectedTiles.size()) {
                stopGame();
            }
        }


    };

    Runnable showColorErrorRunnable = new Runnable() {
        @Override
        public void run() {
            if (selectedTiles.size() == 2) {
                for (int t : selectedTiles) {
                    connection.setTileColor(LED_COLOR_OFF, t);
                }
                selectedTiles.clear();
            }
        }
    };

    @Override
    public void onGameEnd() {
        super.onGameEnd();
        connection.setAllTilesBlink(4, LED_COLOR_RED);
        sound.playStop();

    }
}


