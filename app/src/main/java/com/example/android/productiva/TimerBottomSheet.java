package com.example.android.productiva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.shawnlin.numberpicker.NumberPicker;

public class TimerBottomSheet extends BottomSheetDialogFragment {

    private long workTimeMS, breakTimeMS;
    int cycles;
    private OnSetButtonClickListener listener;

    //Constructor
    public TimerBottomSheet(long workTimeMS, long breakTimeMS, int cycles) {
        this.workTimeMS = workTimeMS;
        this.breakTimeMS = breakTimeMS;
        this.cycles = cycles;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_btmsheet, container, false);

        //Finding views
        MaterialButton btnSetTimer = view.findViewById(R.id.timer_setupFinish);
        NumberPicker workTimePicker = view.findViewById(R.id.work_timePicker);
        NumberPicker breakTimePicker = view.findViewById(R.id.break_timePicker);
        NumberPicker cyclePicker = view.findViewById(R.id.cyclePicker);


        workTimePicker.setValue((int) (workTimeMS / 60000));
        breakTimePicker.setValue((int) (breakTimeMS / 60000));
        cyclePicker.setValue(cycles);

        workTimePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            workTimeMS = newVal * 60000;
        });

        breakTimePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            breakTimeMS = newVal * 60000;
        });

        cyclePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            cycles = newVal;
        });

        //Send back value on set button press
        btnSetTimer.setOnClickListener(v -> {
            listener.OnSetButtonClick(workTimeMS, breakTimeMS, cycles);
            dismiss();
        });

        return view;
    }

    //Set timer settings listener
    public void setOnSetupButtonClickListener(OnSetButtonClickListener listener) {
        this.listener = listener;
    }


    //Interface for OnSetButtonClickListener
    public interface OnSetButtonClickListener {
        //This applies the settings
        void OnSetButtonClick(long workTimeMS, long breakTimeMS, int cycles);
    }
}
