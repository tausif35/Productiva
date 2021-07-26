package com.example.android.productiva.Notes;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.productiva.R;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.NoteRecyclerViewHolder> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private final Context context;
    private final ArrayList<NoteData> dataSet;
    private onItemClickListener listener;

    //Constructor
    public NoteRecyclerAdapter(ArrayList<NoteData> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;


    }

    // Set recycler item click listener
    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public NoteRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_adapter_layout, parent, false);

        return new NoteRecyclerViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(NoteRecyclerViewHolder holder, int position) {
        holder.textViewTitle.setText(dataSet.get(position).getTitle());
        holder.textViewSubtitle.setText(dataSet.get(position).getSubtitle());
        holder.textViewDateTime.setText(dateFormat.format(dataSet.get(position).getTimestamp()));
        holder.cardContainer.setCardBackgroundColor(Color.parseColor(dataSet.get(position).getColorHex()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    //Interface for onItemClickListener
    public interface onItemClickListener {
        void onItemClick(int position);
    }

    //Inner view holder class
    public static class NoteRecyclerViewHolder extends RecyclerView.ViewHolder {


        public final TextView textViewTitle;
        public final TextView textViewSubtitle;
        public final TextView textViewDateTime;
        public final MaterialCardView cardContainer;

        //Inner classConstructor
        public NoteRecyclerViewHolder(View itemView, onItemClickListener listener) {
            super(itemView);

            //Find views
            textViewTitle = itemView.findViewById(R.id.recycler_noteTitle);
            textViewSubtitle = itemView.findViewById(R.id.recycler_noteSubtitle);
            textViewDateTime = itemView.findViewById(R.id.recycler_noteDateTime);
            cardContainer = itemView.findViewById(R.id.recycler_card);


            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }


}
