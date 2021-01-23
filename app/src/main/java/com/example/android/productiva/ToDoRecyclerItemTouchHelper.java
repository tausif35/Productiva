package com.example.android.productiva;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.productiva.ToDoAdapter.ToDoAdapter;

public class ToDoRecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ToDoAdapter adapter;
    private Context context;
    private ToDoRecyclerItemTouchListener swipeListener;

    //Interface for ToDoRecyclerItemTouchListener
    public interface ToDoRecyclerItemTouchListener {
        void onItemSwipeLeft(int position, boolean isConfirm);

        void onItemSwipeRight(int position);
    }


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
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Delete Task");
            builder.setMessage("Are you sure you want to delete this Task?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    swipeListener.onItemSwipeLeft(position, true);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Not needed
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            //On alert dialog cancel or dismiss
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    swipeListener.onItemSwipeLeft(position, false);
                }
            });


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
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_edit);
            icon.setTint(Color.WHITE);
            background = new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        } else {
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete);
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
}