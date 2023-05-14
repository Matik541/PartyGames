package com.example.partygames;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;

public class QuestsActivity extends AppCompatActivity {

	static ArrayList<String> questsList;
	static ArrayList<String> playersList;
	ArrayList<RecyclerItem> recyclerList;
	RecyclerView questsRecycler;
	static RecyclerAdapter recyclerAdapter;

	SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quests);

		sharedPreferences = this.getSharedPreferences("com.example.partygames", Context.MODE_PRIVATE);

		questsList = new ArrayList<>(sharedPreferences.getStringSet("quests", new HashSet<>()));
		playersList = new ArrayList<>(sharedPreferences.getStringSet("players", new HashSet<>()));

		recyclerList = new ArrayList<>();
		questsList.forEach(quest -> recyclerList.add(new RecyclerItem(quest)));

		questsRecycler = findViewById(R.id.questsRecycler);
		recyclerAdapter = new RecyclerAdapter(this, recyclerList);
		questsRecycler.setAdapter(recyclerAdapter);
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

			int pos = recyclerList.size();
			recyclerList.add(new RecyclerItem(content));
			recyclerAdapter.notifyItemInserted(pos);
			questInput.setText("");

			updatePreferences();
		});

		findViewById(R.id.questsTextInput).setOnKeyListener((v, keyCode, event) -> {
			if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
				addQuestBtn.performClick();
				return true;
			}
			return false;
		});

		findViewById(R.id.questsHintBtn).setOnClickListener(view -> new AlertDialog.Builder(this)
			.setTitle(R.string.quests_hints)
			.setMessage(
				Html.fromHtml(
					"<h5>" + getString(R.string.game_mechanic) + ":</h5>" +
						"\t" + getString(R.string.use) + "<strong>&lt;@&gt;</strong>" + getString(R.string.ping_random_player) + "<br>" +
						"\t" + getString(R.string.use) + "<strong>&lt;min-max&gt;</strong>" + getString(R.string.get_random_min_max) + "<br><br>" +
						"<h5>" + getString(R.string.remember) + ":</h5>" +
						"\t" + getString(R.string.more_quests_better_gameplay) + "<br>" +
						"\t" + getString(R.string.be_creative) + "!<br>" +
						"\t" + getString(R.string.good_luck_have_fun) + "!",
					HtmlCompat.FROM_HTML_MODE_COMPACT
				)
			)
			.show());

		findViewById(R.id.deleteSelectedQuestsBtn).setOnClickListener(view -> new AlertDialog.Builder(this)
			.setTitle(getString(R.string.remove_items) + "?")
			.setMessage(R.string.remove_selected_items)

			.setPositiveButton(R.string.remove, (dialog, which) -> {
				int amount = recyclerAdapter.removeSelectedItems();
				Snackbar.make(view, getString(R.string.removed) + ": " + amount + " " + getString(R.string.items), 2000).show();
				updatePreferences();
			} )
			.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())

			.show());

//		findViewById(R.id.questsNavBtn_Q).setOnClickListener(view -> openIntent(QuestsActivity.class));
		findViewById(R.id.playNavBtn_Q).setOnClickListener(view -> {
			updatePreferences();
			if (playersList.size() >= 3 && questsList.size() >= 5) {
				openIntent(PlayActivity.class);
			} else {
				new AlertDialog.Builder(this)
					.setTitle(getString(R.string.start_game))
					.setMessage(
						getString(R.string.not_enough_alert) +
							"\n" +
							getString(R.string.players) + ": " + playersList.size() +
							" (" + getString(R.string.required) + " 3)\n" +
							getString(R.string.questions) + ": " + questsList.size() +
							" (" + getString(R.string.required) + " 5)\n"
					)

					.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel())

					.show();
			}
		});
		findViewById(R.id.playersNavBtn_Q).setOnClickListener(view -> openIntent(PlayersActivity.class));

	}

	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		updatePreferences();
	}

	private void openIntent(Class<?> tClass) {
		Intent intent = new Intent(this, tClass);

		updatePreferences();

		startActivity(intent);
	}

	private void updatePreferences() {
		questsList.clear();
		recyclerList.forEach(item -> questsList.add(item.getContent()));

		sharedPreferences.edit().putStringSet("quests", new HashSet<>(questsList)).apply();
		sharedPreferences.edit().putStringSet("players", new HashSet<>(playersList)).apply();
	}
}