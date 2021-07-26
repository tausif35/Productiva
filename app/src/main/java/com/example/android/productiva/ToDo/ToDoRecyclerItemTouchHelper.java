package com.example.android.productiva.ToDo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.productiva.R;

public class ToDoRecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ToDoAdapter adapter;
    private Context context;
    private ToDoRecyclerItemTouchListener swipeListener;

    public ToDoRecyclerItemTouchHelper(ToDoAdapter adapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.context = context;
        this.swipeListener = (ToDoRecyclerItemTouchListener) context;

    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {

            // Show warning alert dialog
            new AlertDialog.Builder(context)
                    .setTitle("Are you sure?")
                    .setMessage("This will delete this task's data completely")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Ok", (dialog, which) -> { // When user confirms
                        swipeListener.onItemSwipeLeft(position, true);
                        MediaPlayer playSound = MediaPlayer.create(context, R.raw.removed);
                        playSound.setOnCompletionListener(mp -> stopAlert(playSound));
                        playSound.start();
                    }).setOnDismissListener(dialog -> swipeListener.onItemSwipeLeft(position, false))
                    .show();


        } else if (direction == ItemTouchHelper.RIGHT) {
            swipeListener.onItemSwipeRight(position);
        }
    }

    //Draw background with icon for recyclerView swipe gesture
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        if (dX > 0) {
            icon = ContextCompat.getDrawable(context, R.drawable.ic_edit);
            icon.setTint(Color.WHITE);
            background = new ColorDrawable(ContextCompat.getColor(context, R.color.colorEdit));
        } else {
            icon = ContextCompat.getDrawable(context, R.drawable.ic_remove);
            icon.setTint(Color.WHITE);
            background = new ColorDrawable(ContextCompat.getColor(context, R.color.colorDelete));
        }

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }

    //Interface for ToDoRecyclerItemTouchListener
    public interface ToDoRecyclerItemTouchListener {
        void onItemSwipeLeft(int position, boolean isConfirm);

        void onItemSwipeRight(int position);
    }

    private void stopAlert(MediaPlayer playSound) {
        if (playSound != null) {
            playSound.release();
            playSound = null;
        }
    }
}