package com.example.partygames;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.QuestViewHolder> {

	private final LayoutInflater inflater;
	private final ArrayList<RecyclerItem> items;

	public RecyclerAdapter(Context context, ArrayList<RecyclerItem> items) {
		inflater = LayoutInflater.from(context);
		this.items = items;
	}

	public static class QuestViewHolder extends RecyclerView.ViewHolder {

		public final TextView itemContent;
		public final CheckBox itemCheckBox;

		public final RecyclerAdapter questsAdapter;


		public QuestViewHolder(@NonNull View itemView, RecyclerAdapter adapter) {
			super(itemView);

			itemContent = itemView.findViewById(R.id.itemContent);
			itemCheckBox = itemView.findViewById(R.id.itemCheckBox);

			questsAdapter = adapter;
		}
	}

	@NonNull
	@Override
	public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = inflater.inflate(R.layout.recycler_item, parent, false);
		return new QuestViewHolder(itemView, this);
	}

	@Override
	public void onBindViewHolder(@NonNull QuestViewHolder holder, @SuppressLint("RecyclerView") final int position) {
		holder.itemContent.setText(items.get(getItemViewType(position)).getContent());
		holder.itemCheckBox.setChecked(items.get(getItemViewType(position)).isChecked());
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public int getItemViewType(int position) {
		return position;
	}

	public void setCheckedForAllItems(boolean checked) {
		items.forEach(x -> x.setChecked(checked));
		notifyItemRangeChanged(0, items.size());
	}
	public int removeSelectedItems() {
		int amount = 0;

		for (int i = items.size() - 1; i >= 0; i--)
			if (items.get(i).isChecked()) {
				amount++;
				items.remove(i);
				notifyItemRemoved(i);
			}

		return amount;
	}
}
