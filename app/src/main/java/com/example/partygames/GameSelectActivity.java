package com.example.partygames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class GameSelectActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_select);

		Intent intent = new Intent();

		findViewById(R.id.queueBtn).setOnClickListener(view -> {
			intent.putExtra("mode", "queue");
			startActivity(intent);
		});

		findViewById(R.id.chooseBtn).setOnClickListener(view -> {
			intent.putExtra("mode", "choose");
			startActivity(intent);
		});

		findViewById(R.id.quickBtn).setOnClickListener(view -> {
			intent.putExtra("mode", "quick");
			startActivity(intent);
		});


	}
}