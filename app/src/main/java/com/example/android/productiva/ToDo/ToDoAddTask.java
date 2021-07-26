package com.example.android.productiva.ToDo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.example.android.productiva.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ToDoAddTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;
    private ToDoDatabaseHandler db;
    private BottomSheetListener listener;
    private int position = -1;
    private boolean isUpdate = false;

    //Constructor
    public ToDoAddTask() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.to_do_list_new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        db = new ToDoDatabaseHandler(getActivity());

        newTaskText = view.findViewById(R.id.newTaskText);
        newTaskSaveButton = view.findViewById(R.id.newTaskButton);
        newTaskSaveButton.setEnabled(false);
        newTaskSaveButton.setBackgroundColor(Color.GRAY);

        //Get bundle data to update an existing task
        Bundle bundle = getArguments();
        if (bundle != null) {
            position = bundle.getInt("position");
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task);
            if (task.length() > 0) {
                newTaskSaveButton.setEnabled(true);
                newTaskSaveButton.setText("Edit & Save");
                newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
        }

        //Enable or disable add button based on if new task text input is empty or not
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setBackgroundColor(Color.GRAY);
                } else if (s.length() > 0) {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Add new task or update existing task on task add button press
        boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                if (finalIsUpdate) {
                    db.updateTask(bundle.getInt("id"), text);
                } else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    db.insertTask(task);
                }
                listener.onAddItem(true, position);
                dismiss();
            }
        });

        return view;
    }

    //When bottom sheet is canceled
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (position > -1) {
            listener.onAddItem(false, position);
        }
    }

    //Attach bottom sheet listener to context
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }

    //Interface for BottomSheetListener
    public interface BottomSheetListener {
        void onAddItem(boolean isAdded, int position);
    }
}
