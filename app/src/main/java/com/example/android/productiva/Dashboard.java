package com.example.android.productiva;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.android.productiva.Notes.NotesActivity;
import com.example.android.productiva.Timer.Timer;
import com.example.android.productiva.ToDo.ToDoActivity;

public class Dashboard extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        //Variables
        CardView card1 = findViewById(R.id.todo);
        CardView card2 = findViewById(R.id.notes);
        CardView card3 = findViewById(R.id.timer);
        CardView card4 = findViewById(R.id.settings);

        //Next Activity Trigger
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Dashboard.this, ToDoActivity.class);
                startActivity(intent);
            }
        });


        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Dashboard.this, NotesActivity.class);
                startActivity(intent);
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Dashboard.this, Timer.class);
                startActivity(intent);
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Dashboard.this, About.class);
                startActivity(intent);
            }
        });
    }
}