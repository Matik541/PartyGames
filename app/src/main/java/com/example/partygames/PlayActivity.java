package com.example.partygames;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayActivity extends AppCompatActivity {

	Random random = new Random();
	CountDownTimer timer;
	SharedPreferences sharedPreferences;

	static ArrayList<String> questsList;
	static ArrayList<String> playersList;

	TextView currentPlayer;
	TextView playersQueue;
	TextView questContent;
	TextView timerView;

	String mode;
	int seconds;
	int choosen;

	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		sharedPreferences = this.getSharedPreferences("com.example.partygames", Context.MODE_PRIVATE);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mode = extras.getString("mode");
		}

		questsList = new ArrayList<>(sharedPreferences.getStringSet("quests", new HashSet<>()));
		playersList = new ArrayList<>(sharedPreferences.getStringSet("players", new HashSet<>()));

		Collections.shuffle(questsList);
		Collections.shuffle(playersList);

		currentPlayer = findViewById(R.id.currentPlayer);
		playersQueue = findViewById(R.id.playersQueue);
		questContent = findViewById(R.id.questContent);
		timerView = findViewById(R.id.timer);


		if (mode.equals("quick")) {
			refreshTimer();
			timerView.setVisibility(View.VISIBLE);
		}

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

				.show();
		});

		findViewById(R.id.doneBtn).setOnClickListener(view -> {
			if (mode.equals("choose")) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
				ViewGroup viewGroup = findViewById(android.R.id.content);

				View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.choose_player, viewGroup, false);

				builder.setView(dialogView);

				ListView choosePlayerList = dialogView.findViewById(R.id.choosePlayersList);

				final AlertDialog alertDialog = builder.create();
				alertDialog.show();

				ArrayAdapter<String> choosePlayerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playersList);
				choosePlayerList.setAdapter(choosePlayerAdapter);
				choosePlayerList.setOnItemClickListener((adapterView, view1, i, l) -> {
					choosen = i;
					alertDialog.dismiss();

					questsList.add(questsList.get(0));
					questsList.remove(0);

					String curPlayer = playersList.get(0);
					playersList.set(0, playersList.get(i));
					playersList.set(i, curPlayer);

					update();
				});
			}
			else {
				playersList.add(playersList.get(0));
				playersList.remove(0);

				questsList.add(questsList.get(0));
				questsList.remove(0);

				update();
			}
		});

		findViewById(R.id.rerollBtn).setOnClickListener(view -> {
			questsList.add(questsList.get(0));
			questsList.remove(0);

			update();
		});
	}

	private void update() {
		currentPlayer.setText(playersList.get(0));


		StringBuilder playersQueueContent = new StringBuilder();
		if (playersList.size() > 1 && !Objects.equals(mode, "choose")) {

			playersQueueContent
				.append(getString(R.string.next))
				.append(": ")
				.append(playersList.get(1));

		for (int i = 2; i < playersList.size(); i++)
			playersQueueContent
				.append(", ")
				.append(playersList.get(i));
		}

		playersQueue.setText(playersQueueContent.toString());


		String quest = questsList.get(0);
		while (
			Pattern.compile("<@>|<(\\d{1,4})-(\\d{1,4})>").matcher(quest).find()
		) {

			// ping players
			quest = quest.replaceFirst("<@>",
				"@" + playersList.get(
					random.nextInt(playersList.size() - 1) + 1
				)
			);

			// add numbers
			Matcher boundsMatcher = Pattern.compile("<(\\d+)-(\\d+)>").matcher(quest);
			if (boundsMatcher.find()) {
				int lowerBound = Integer.parseInt(
					Objects.requireNonNull(boundsMatcher.group(1)));
				int upperBound = Integer.parseInt(
					Objects.requireNonNull(boundsMatcher.group(2)));

				if (lowerBound > upperBound) {
					int temp = lowerBound;
					lowerBound = upperBound;
					upperBound = temp;
				}

				quest = boundsMatcher.replaceFirst(
					String.valueOf(
						random.nextInt(upperBound - lowerBound + 1) + lowerBound));
			}

		}

		questContent.setText(quest);
	}

	private void refreshTimer() {
		seconds = random.nextInt(15) + 45;

		timer = new CountDownTimer(seconds*1000, 1000) {
			@SuppressLint("SetTextI18n")
			@Override
			public void onTick(long l) {
				timerView.setText(seconds-- + "s");
			}

			@Override
			public void onFinish() {
				timerView.setText(R.string.timeout);
				new AlertDialog.Builder(PlayActivity.this)
					.setTitle( playersList.get(0) + " " + getString(R.string.is_out))

					.setOnCancelListener(dialogInterface -> refreshTimer())

					.setPositiveButton(android.R.string.ok, (dialog, witch) -> refreshTimer())

					.show();
				playersList.remove(0);

				if (playersList.size() == 1) {
					new AlertDialog.Builder(PlayActivity.this)
						.setTitle(getString(R.string.game_over) + "!")
						.setMessage( playersList.get(0) + " " + getString(R.string.win) + "!")

						.setPositiveButton(R.string.play_again, (dialog, witch) -> startActivity(new Intent(PlayActivity.this, PlayActivity.class)))
						.setNegativeButton(R.string.leave, (dialog, witch) -> startActivity(new Intent(PlayActivity.this, MainActivity.class)))
						.setOnCancelListener(dialogInterface -> startActivity(new Intent(PlayActivity.this, MainActivity.class)))

						.show();
					return;
				}
				Collections.shuffle(questsList);

				update();
			}
		};

		timer.start();
	}
}