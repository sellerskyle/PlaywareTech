
package com.example.myapplication;

        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.Button;
        import android.widget.LinearLayout;

        import com.livelife.motolibrary.Game;
        import com.livelife.motolibrary.GameType;
        import com.livelife.motolibrary.MotoConnection;
        import com.livelife.motolibrary.MotoSound;
        import com.livelife.motolibrary.OnAntEventListener;

public class ConcentrationActivity extends AppCompatActivity implements OnAntEventListener {

    MotoConnection connection = MotoConnection.getInstance();
    MotoSound sound = MotoSound.getInstance();

    LinearLayout gameTypeContainer;
    Concentration concentration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.concentration);

        concentration = new Concentration();
        connection.registerListener(this);
        gameTypeContainer = findViewById(R.id.gameTypeContainer);

        for (final GameType gt: concentration.getGameTypes()) {
            Button b = new Button(this);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    concentration.selectedGameType = gt;
                    sound.playStart();
                    concentration.startGame();
                }
            });
            b.setText(gt.getName());
            gameTypeContainer.addView(b);
        }

        concentration.setOnGameEventListener(new Game.OnGameEventListener() {
            @Override
            public void onGameTimerEvent(int i) {

            }

            @Override
            public void onGameScoreEvent(int score, int playerIndex) {

            }

            @Override
            public void onGameStopEvent() {

            }

            @Override
            public void onSetupMessage(String s) {

            }

            @Override
            public void onGameMessage(String s) {

            }

            @Override
            public void onSetupEnd() {

            }
        });
    }

    @Override
    public void onMessageReceived(byte[] bytes, long l) {
        concentration.addEvent(bytes);
    }

    @Override
    public void onAntServiceConnected() {

    }

    @Override
    public void onNumbersOfTilesConnected(int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.unregisterListener(this);
    }
}