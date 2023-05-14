package com.example.partygames;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;

public class PlayersActivity extends AppCompatActivity {

	static ArrayList<String> questsList;
	static ArrayList<String> playersList;
	ArrayList<RecyclerItem> recyclerList;
	RecyclerView playersRecycler;
	static RecyclerAdapter recyclerAdapter;

	SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_players);

		sharedPreferences = this.getSharedPreferences("com.example.partygames", Context.MODE_PRIVATE);

		questsList = new ArrayList<>(sharedPreferences.getStringSet("quests", new HashSet<>()));
		playersList = new ArrayList<>(sharedPreferences.getStringSet("players", new HashSet<>()));

		recyclerList = new ArrayList<>();
		for (String quest : playersList) {
			recyclerList.add(new RecyclerItem(quest));
		}


		playersRecycler = findViewById(R.id.playersRecycler);
		recyclerAdapter = new RecyclerAdapter(this, recyclerList);
		playersRecycler.setAdapter(recyclerAdapter);
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
			if (content.matches(".*[<>].*")) {
				new AlertDialog.Builder(this)
					.setTitle(getString(R.string.invalid_name) + "!")
					.setMessage(
						Html.fromHtml(
							getString(R.string.player_name_cant_contain) + ": " +
								"<strong> &lt; </strong>" + getString(R.string.and) +
								"<strong> &gt; </strong>",
							HtmlCompat.FROM_HTML_MODE_COMPACT
						)
					)

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
			recyclerList.add(new RecyclerItem(content));
			recyclerAdapter.notifyItemInserted(pos);
			questInput.setText("");

			updatePreferences();
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
					"\t" + getString(R.string.player_name_cant_contain) + ": " +
						"<strong> &lt; </strong>" + getString(R.string.and) +
						"<strong> &gt; </strong>" + "<br>" +
						"\t" + getString(R.string.unique_player_names) + "<br>",
					HtmlCompat.FROM_HTML_MODE_COMPACT
				)
			)
			.show());


		findViewById(R.id.deleteSelectedPlayersBtn).setOnClickListener(view -> new AlertDialog.Builder(this)
			.setTitle(getString(R.string.remove_items) + "?")
			.setMessage(R.string.remove_selected_items)

			.setPositiveButton(R.string.remove, (dialog, which) -> {
				int amount = recyclerAdapter.removeSelectedItems();
				Snackbar.make(view, getString(R.string.removed) + ": " + amount + " " + getString(R.string.items), 2000).show();
				updatePreferences();
			})
			.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())

			.show());

		findViewById(R.id.questsNavBtn_P).setOnClickListener(view -> openIntent(QuestsActivity.class));
		findViewById(R.id.playNavBtn_P).setOnClickListener(view -> {
			updatePreferences();
			if (playersList.size() >= 3 && questsList.size() >= 5 ) {
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
//		findViewById(R.id.playersNavBtn_P).setOnClickListener(view -> openIntent(PlayersActivity.class));
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
		playersList.clear();
		recyclerList.forEach(item -> playersList.add(item.getContent()));

		sharedPreferences.edit().putStringSet("quests", new HashSet<>(questsList)).apply();
		sharedPreferences.edit().putStringSet("players", new HashSet<>(playersList)).apply();
	}
}