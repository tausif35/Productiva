package com.example.android.productiva.Notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.productiva.R;
import com.google.android.material.card.MaterialCardView;

public class ColorRecyclerAdapter extends RecyclerView.Adapter<ColorRecyclerAdapter.colorRecyclerViewHolder> {

    private final Context context;
    int[] colors;
    private onItemClickListener listener;

    //Constructor
    public ColorRecyclerAdapter(int[] colors, Context context) {
        this.context = context;
        this.colors = colors;

    }

    // Set recycler item click listener
    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public colorRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_layout, parent, false);

        return new colorRecyclerViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(colorRecyclerViewHolder holder, int position) {
        holder.colorContainer.setCardBackgroundColor(colors[position]);
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    //Interface for onItemClickListener
    public interface onItemClickListener {
        void onItemClick(int position);
    }

    //Inner view holder class
    public static class colorRecyclerViewHolder extends RecyclerView.ViewHolder {

        public final MaterialCardView colorContainer;

        //Inner classConstructor
        public colorRecyclerViewHolder(View itemView, onItemClickListener listener) {
            super(itemView);

            //Find views
            colorContainer = itemView.findViewById(R.id.colorContainer);

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
