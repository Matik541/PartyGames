package com.example.partygames;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {


	Random random = new Random();
	SharedPreferences sharedPreferences;

	static ArrayList<String> questsList;
	static ArrayList<String> playersList;

	TextView currentPlayer;
	TextView playersQueue;
	TextView questContent;

	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		sharedPreferences = this.getSharedPreferences("com.example.partygames", Context.MODE_PRIVATE);

		questsList = new ArrayList<>(sharedPreferences.getStringSet("quests", new HashSet<>()));
		playersList = new ArrayList<>(sharedPreferences.getStringSet("players", new HashSet<>()));

		Collections.shuffle(questsList);
		Collections.shuffle(playersList);

		currentPlayer = findViewById(R.id.currentPlayer);
		playersQueue = findViewById(R.id.playersQueue);
		questContent = findViewById(R.id.questContent);

		update();

		findViewById(R.id.playersListBtn).setOnClickListener(view -> {
			StringBuilder playersQueueMessage = new StringBuilder();

			int i = 0;
			for (String player : playersList) {
				if (i++ < 10)
					playersQueueMessage.append(player).append("\n");
			}
			if (playersList.size() > 10)
				playersQueueMessage.append(
					String.format("%s %d %s...",
						getString(R.string.and),
						(playersList.size() - 10),
						getString(R.string.more))
				);

			new AlertDialog.Builder(this)
				.setTitle(getString(R.string.players_queue) + ": ")
				.setMessage(
					playersQueueMessage.toString()
				)

				.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel())

				.setIcon(R.drawable.round_warning_32)
				.show();
		});

		findViewById(R.id.doneBtn).setOnClickListener(view -> {
			playersList.add(playersList.get(0));
			playersList.remove(0);

			questsList.add(questsList.get(0));
			questsList.remove(0);

			update();
		});

		findViewById(R.id.rerollBtn).setOnClickListener(view -> {
			questsList.add(playersList.get(0));
			questsList.remove(0);

			update();
		});
	}

	private void update() {
		currentPlayer.setText(playersList.get(0));


		StringBuilder playersQueueContent = new StringBuilder();
		playersQueueContent
			.append(getString(R.string.next))
			.append(": ")
			.append(playersList.get(1));

		for (int i = 2; i < playersList.size(); i++)
			playersQueueContent
				.append(", ")
				.append(playersList.get(i));

		playersQueue.setText(playersQueueContent.toString());


		String quest = questsList.get(0);
		while (quest.contains("_")) {
			quest = quest.replaceFirst("_",
				playersList.get(
					random.nextInt(playersList.size() - 1) + 1
				)
			);
		}

		questContent.setText(quest);
	}
}