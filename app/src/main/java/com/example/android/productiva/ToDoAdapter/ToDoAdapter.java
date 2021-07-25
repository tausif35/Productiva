package com.example.android.productiva.ToDoAdapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.productiva.R;
import com.example.android.productiva.ToDoList;
import com.example.android.productiva.ToDoModel.ToDoModel;
import com.example.android.productiva.ToDoUtils.ToDoDatabaseHandler;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {

    private List<ToDoModel> taskList;
    private ToDoList toDoList;
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
