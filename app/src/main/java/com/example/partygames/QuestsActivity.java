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
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashSet;

public class QuestsActivity extends AppCompatActivity {

	static ArrayList<String> questsList;
	static ArrayList<String> playersList;
	RecyclerView questsRecycler;
	static QuestsAdapter questsAdapter;

	SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quests);

		sharedPreferences = this.getSharedPreferences("com.example.partygames", Context.MODE_PRIVATE);

		questsList = new ArrayList<>(sharedPreferences.getStringSet("quests", new HashSet<>()));
		playersList = new ArrayList<>(sharedPreferences.getStringSet("players", new HashSet<>()));


		questsRecycler = findViewById(R.id.questsRecycler);
		questsAdapter = new QuestsAdapter(this, questsList);
		questsRecycler.setAdapter(questsAdapter);
		questsRecycler.setLayoutManager(new LinearLayoutManager(this));

		Button addQuestBtn = findViewById(R.id.questsAddBtn);
		addQuestBtn.setOnClickListener(view -> {
			EditText questInput = findViewById(R.id.questsTextInput);
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
			if (questsList.contains(content)) {
				new AlertDialog.Builder(this)
					.setTitle(R.string.quests_duplicate_title)
					.setMessage(R.string.quests_duplicate_message)

					.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel())

					.setIcon(R.drawable.round_warning_32)
					.show();
				return;
			}

			int pos = questsList.size();
			questsList.add(content);
			questsAdapter.notifyItemInserted(pos);
			questInput.setText("");
			sharedPreferences.edit().putStringSet("quests", new HashSet<>(questsList)).apply();

		});

		findViewById(R.id.questsTextInput).setOnKeyListener((v, keyCode, event) -> {
			if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
				addQuestBtn.performClick();
				return true;
			}
			return false;
		});

//		findViewById(R.id.questsNavBtn_Q).setOnClickListener(view -> openIntent(QuestsActivity.class));
		findViewById(R.id.playNavBtn_Q).setOnClickListener(view -> {
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
		findViewById(R.id.playersNavBtn_Q).setOnClickListener(view -> openIntent(PlayersActivity.class));

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