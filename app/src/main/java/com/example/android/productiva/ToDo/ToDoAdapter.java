package com.example.android.productiva.ToDo;

import android.content.Context;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.productiva.R;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {

    private List<ToDoModel> taskList;
    private ToDoActivity mToDoActivity;
    private Context context;
    private ToDoDatabaseHandler db;


    //Constructor
    public ToDoAdapter(List<ToDoModel> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
        db = new ToDoDatabaseHandler(context);
    }

    @Override
    public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_list_task_layout, parent, false);
        ToDoViewHolder toDoViewHolder = new ToDoViewHolder(view);

        return toDoViewHolder;
    }

    @Override
    public void onBindViewHolder(ToDoViewHolder holder, int position) {
        ToDoModel item = taskList.get(position);
        holder.checkTask.setText(item.getTask());
        holder.checkTask.setChecked(item.getStatus() != 0 ? true : false);
        holder.checkTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                    holder.checkTask.setPaintFlags(holder.checkTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    MediaPlayer playSound = MediaPlayer.create(context, R.raw.task_finished);
                    playSound.setOnCompletionListener(mp -> stopAlert(playSound));
                    playSound.start();
                } else {
                    db.updateStatus(item.getId(), 0);
                    holder.checkTask.setPaintFlags(holder.checkTask.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }


    private void stopAlert(MediaPlayer playSound) {
        if (playSound != null) {
            playSound.release();
            playSound = null;
        }
    }

    //Inner view holder class
    public static class ToDoViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkTask;

        //Constructor
        public ToDoViewHolder(View itemView) {
            super(itemView);
            checkTask = itemView.findViewById(R.id.todoCheckBox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }


}
