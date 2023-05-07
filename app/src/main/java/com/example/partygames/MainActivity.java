package com.example.partygames;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> questsList;
    static ArrayList<String> playersList;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.example.partygames", Context.MODE_PRIVATE);

        questsList = new ArrayList<>(sharedPreferences.getStringSet("quests", new HashSet<>()));
        playersList = new ArrayList<>(sharedPreferences.getStringSet("players", new HashSet<>()));

        findViewById(R.id.questsNavBtn).setOnClickListener(view -> openIntent(QuestsActivity.class));
        findViewById(R.id.playNavBtn).setOnClickListener(view -> {
            if (questsList.size() >= 5 && playersList.size() >= 3) {
                openIntent(PlayActivity.class);
            }
            else {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.start_game))
                        .setMessage(
                                getString(R.string.not_enough_alert) +
                                "\n" +
                                getString(R.string.questions) + ": " + questsList.size() +
                                        " (" + getString(R.string.required) + " 3)\n" +
                                getString(R.string.questions) + ": " + questsList.size() +
                                        " (" + getString(R.string.required) + " 5)\n"
                        )

                        .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel())

                        .setIcon(R.drawable.round_warning_32)
                        .show();
            }
        });
        findViewById(R.id.playersNavBtn).setOnClickListener(view -> openIntent(PlayersActivity.class));
    }

    private void openIntent(Class<?> tClass) {
        Intent intent = new Intent(this, tClass);
        sharedPreferences.edit().putStringSet("quests", new HashSet<>(questsList)).apply();
        sharedPreferences.edit().putStringSet("players", new HashSet<>(playersList)).apply();
        startActivity(intent);
    }
}