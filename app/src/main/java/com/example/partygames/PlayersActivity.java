package com.example.partygames;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashSet;

public class PlayersActivity extends AppCompatActivity {

	static ArrayList<String> questsList;
	static ArrayList<String> playersList;
	RecyclerView playersRecycler;
	static PlayersAdapter playersAdapter;

	SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_players);

		sharedPreferences = this.getSharedPreferences("com.example.partygames", Context.MODE_PRIVATE);

		questsList = new ArrayList<>(sharedPreferences.getStringSet("quests", new HashSet<>()));
		playersList = new ArrayList<>(sharedPreferences.getStringSet("players", new HashSet<>()));

		playersRecycler = findViewById(R.id.playersRecycler);
		playersAdapter = new PlayersAdapter(this, playersList);
		playersRecycler.setAdapter(playersAdapter);
		playersRecycler.setLayoutManager(new LinearLayoutManager(this));

		Button addPlayerBtn = findViewById(R.id.playersAddBtn);
		addPlayerBtn.setOnClickListener(view -> {
			EditText questInput = findViewById(R.id.playersTextInput);
			String content = questInput.getText().toString();

			if (content.equals("")) {
				new AlertDialog.Builder(this)
					.setTitle(R.string.empty_input_title)
					.setMessage(R.string.empty_input_message)


					.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel())

					.setIcon(R.drawable.round_warning_32)
					.show();
				return;
			}
			if (playersList.contains(content)) {
				new AlertDialog.Builder(this)
					.setTitle(R.string.players_duplicate_title)
					.setMessage(R.string.players_duplicate_message)


					.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel())

					.setIcon(R.drawable.round_warning_32)
					.show();
				return;
			}

			int pos = playersList.size();
			playersList.add(content);
			playersAdapter.notifyItemInserted(pos);
			questInput.setText("");
			sharedPreferences.edit().putStringSet("players", new HashSet<>(playersList)).apply();
		});

		findViewById(R.id.playersTextInput).setOnKeyListener((v, keyCode, event) -> {
			if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
				addPlayerBtn.performClick();
				return true;
			}
			return false;
		});

		findViewById(R.id.playersHintBtn).setOnClickListener(view -> new AlertDialog.Builder(this)
			.setTitle(R.string.players_hints)
			.setMessage(
				Html.fromHtml(
					"\t" + getString(R.string.palyer_name_can_contain) + ": " +
						getString(R.string.upper_and_lower_case_letters) + ", " +
						getString(R.string.digits) + "," +
						" . " + getString(R.string.and) +
						" _ " + "<br>" +
						"\t" + getString(R.string.unique_player_names) + "<br>"
				)
			)
			.show());


		findViewById(R.id.questsNavBtn_P).setOnClickListener(view -> openIntent(QuestsActivity.class));
		findViewById(R.id.playNavBtn_P).setOnClickListener(view -> {
			if (questsList.size() >= 5 && playersList.size() >= 3) {
				openIntent(PlayActivity.class);
			} else {
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
//		findViewById(R.id.playersNavBtn_P).setOnClickListener(view -> openIntent(PlayersActivity.class));
	}

	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		sharedPreferences.edit().putStringSet("quests", new HashSet<>(questsList)).apply();
		sharedPreferences.edit().putStringSet("players", new HashSet<>(playersList)).apply();
	}

	private void openIntent(Class<?> tClass) {
		Intent intent = new Intent(this, tClass);
		sharedPreferences.edit().putStringSet("quests", new HashSet<>(questsList)).apply();
		sharedPreferences.edit().putStringSet("players", new HashSet<>(playersList)).apply();
		startActivity(intent);
	}
}