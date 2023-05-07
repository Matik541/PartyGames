package com.example.partygames;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Layout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.LinkedList;

public class QuestsAdapter extends RecyclerView.Adapter<QuestsAdapter.QuestViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<String> quests;

    public QuestsAdapter(Context context, ArrayList<String> quests){
        inflater = LayoutInflater.from(context);
        this.quests = quests;
    }
    public static class QuestViewHolder extends RecyclerView.ViewHolder {

        public final TextView questTextView;
        public final FloatingActionButton questDeleteBtn;

        public final QuestsAdapter questsAdapter;


        public QuestViewHolder(@NonNull View itemView, QuestsAdapter adapter) {
            super(itemView);

            questTextView = itemView.findViewById(R.id.text_view_quest);
            questDeleteBtn = itemView.findViewById(R.id.fab_delete_quest);

            questsAdapter = adapter;
        }
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.quest_item, parent, false);
        return new QuestViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.questTextView.setText(quests.get(getItemViewType(position)));

//        holder.questTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                edit on click...
//            }
//        });

        holder.questDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quests.remove(getItemViewType(position));
                notifyItemRemoved(getItemViewType(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }

    public int getItemViewType(int position) {
        return position;
    }

}
