package com.example.android.productiva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.shawnlin.numberpicker.NumberPicker;

public class TimerBottomSheet extends BottomSheetDialogFragment {

    private int workTime, breakTime, cycles;
    private OnSetButtonClickListener listener;

    //Constructor
    public TimerBottomSheet(int workTime, int breakTime, int cycles) {
        this.workTime = workTime;
        this.breakTime = breakTime;
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


        workTimePicker.setValue(workTime);
        breakTimePicker.setValue(breakTime);
        cyclePicker.setValue(cycles);

        workTimePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            workTime = newVal;
        });

        breakTimePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            breakTime = newVal;
        });

        cyclePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            cycles = newVal;
        });

        //Send back value on set button press
        btnSetTimer.setOnClickListener(v -> {
            listener.OnSetButtonClick(workTime, breakTime, cycles);
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
        void OnSetButtonClick(int workTime, int breakTime, int cycles);
    }
}
