package com.example.playwareexercise2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoSound;
import com.livelife.motolibrary.OnAntEventListener;

public class MainActivity extends AppCompatActivity implements OnAntEventListener {

    MotoConnection connection = MotoConnection.getInstance();
    MotoSound sound = MotoSound.getInstance();

    Button paringButton;
    Button actionButton;
    Button startColorRace;
    Button startHitTheTargetTwoPlayer;
    boolean isParing = false;
    boolean isAction = false;

    TextView statusTextView;
    TextView counter;
    int tilesConnected;
    int numPresses = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sound.initializeSounds(this);

        connection.startMotoConnection(MainActivity.this);
        connection.saveRfFrequency(26); //(Group No.)*10+6
        connection.setDeviceId(2); //Your group number
        connection.registerListener(MainActivity.this);

        statusTextView = findViewById(R.id.statusTextView);
        counter = findViewById(R.id.counter);
        startColorRace = findViewById(R.id.startColorRace);
        startHitTheTargetTwoPlayer = findViewById(R.id.startHitTheTargetTwoPlayer);

        paringButton = findViewById(R.id.paringButton);
        paringButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!isParing){
                    connection.pairTilesStart();
                    paringButton.setText("Stop Paring");
                } else {
                    connection.pairTilesStop();
                    paringButton.setText("Start Paring");
                }
                isParing = !isParing;
            }
        });

        actionButton = findViewById(R.id.actionButton);

        actionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!isAction){
                    connection.setAllTilesColor(AntData.LED_COLOR_WHITE); //TODO Redo this
                    actionButton.setText("Don't Something");
                } else {
                    connection.setAllTilesBlink(2, AntData.LED_COLOR_WHITE);
                    actionButton.setText("Do Something");
                }
                isAction = !isAction;
            }
        });

        //Use this format for click listeners
        startColorRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection.unregisterListener(MainActivity.this);
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        startHitTheTargetTwoPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connection.unregisterListener(MainActivity.this);
                    Intent i = new Intent(MainActivity.this, HitTheTargetTwoPlayerActivity.class);
                    startActivity(i);
                }
        });



    }

    @Override
    public void onMessageReceived(byte[] bytes, long l) {
        if (AntData.getCommand(bytes) == AntData.EVENT_PRESS)
        {
            numPresses++;
            //Use this when updating the text
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    counter.setText("Number of taps:g " + numPresses);
                }
            });

            if(AntData.getId(bytes) % 4 == 1) {
                sound.playPianoSound(1);
                sound.playPianoSound(3);
                sound.playPianoSound(5);
            } else if(AntData.getId(bytes) % 4 == 2) {
                sound.playPianoSound(1);
                sound.playPianoSound(4);
                sound.playPianoSound(6);
            } else if (AntData.getId(bytes) % 4 == 3){
                sound.playPianoSound(2);
                sound.playPianoSound(5);
                sound.playPianoSound(7);
            }
            else if (AntData.getId(bytes) % 4 == 0){
                sound.playPianoSound(2);
                sound.playPianoSound(4);
                sound.playPianoSound(6);
            }

//            for (int i = 0; i < 3; i++){ //TODO change back to 4 whn have enough tiles
//                temp = (byte) (rand.nextInt(7) + 1);
//                colors[i] = temp;
//            }
//            connection.setAllTilesColors(colors);

        }
    }

    @Override
    public void onAntServiceConnected() {
        connection.setAllTilesToInit();
    }

    @Override
    public void onNumbersOfTilesConnected(final int i) {
        statusTextView.setText(i + "connected tile(s)");
        tilesConnected = i;
    }

    @Override
    protected void onResume() {
        super.onResume();
        connection.registerListener(MainActivity.this);
    }
//****THESE DELETED TO ALLOW FOR MULTIPLE ACTIVITIES****
//    @Override
//    protected void onPause() {
//        super.onPause();
//        connection.stopMotoConnection();
//        connection.unregisterListener(MainActivity.this);
//    }
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        connection.startMotoConnection(MainActivity.this);
//        connection.registerListener(MainActivity.this);
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.stopMotoConnection();
        connection.unregisterListener(MainActivity.this);
    }
}
