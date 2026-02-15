package com.zipgame.android;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity implements GameView.OnLevelCompleteListener {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);
        gameView.setOnLevelCompleteListener(this);

        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(v -> finish());

        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> gameView.resetLevel());
    }

    @Override
    public void onLevelComplete(long completionTime) {
        showLevelCompleteDialog(completionTime);
    }

    private void showLevelCompleteDialog(long completionTime) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_level_complete);

        TextView tvCompletionTime = dialog.findViewById(R.id.tv_completion_time);
        tvCompletionTime.setText(String.format(Locale.getDefault(), "Completion Time: %s", formatTime(completionTime)));

        Button btnNextGame = dialog.findViewById(R.id.btn_next_game);
        btnNextGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewLevel();
                dialog.dismiss();
            }
        });

        Button btnExitGame = dialog.findViewById(R.id.btn_exit_game);
        btnExitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.show();
    }

    private void startNewLevel() {
        gameView.startNewLevel();
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
