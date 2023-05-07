package com.example.partygames;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<String> players;

    public PlayersAdapter(Context context, ArrayList<String> players){
        inflater = LayoutInflater.from(context);
        this.players = players;
    }
    public static class PlayerViewHolder extends RecyclerView.ViewHolder {

        public final TextView playerTextView;
        public final FloatingActionButton playerDeleteBtn;

        public final PlayersAdapter playersAdapter;


        public PlayerViewHolder(@NonNull View itemView, PlayersAdapter adapter) {
            super(itemView);

            playerTextView = itemView.findViewById(R.id.text_view_player);
            playerDeleteBtn = itemView.findViewById(R.id.fab_delete_player);

            playersAdapter = adapter;
        }
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.player_item, parent, false);
        return new PlayerViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.playerTextView.setText(players.get(getItemViewType(position)));

//        holder.playerTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                edit on click...
//            }
//        });

        holder.playerDeleteBtn.setOnClickListener(view -> {
            players.remove(getItemViewType(position));
            notifyItemRemoved(getItemViewType(position));
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public int getItemViewType(int position) {
        return position;
    }

}
