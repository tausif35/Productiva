package com.example.android.productiva;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 5000;

    //variables
    Animation topAnim, bottomAnim, titleAnim;
    ImageView logo;
    TextView title, tagline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);
        titleAnim = AnimationUtils.loadAnimation(this,R.anim.title_anim);

        //Hooks
        logo = findViewById(R.id.imageView);
        title = findViewById(R.id.textView2);
        tagline = findViewById(R.id.textView3);

        logo.setAnimation(bottomAnim);
        title.setAnimation(topAnim);
        tagline.setAnimation(titleAnim);


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(MainActivity.this,Dashboard.class);
//                startActivity(intent);
//                finish();
//            }
//        },  SPLASH_SCREEN);
        RelativeLayout lay = findViewById(R.id.WelcomeLayout);
        lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Dashboard.class);
                startActivity(intent);
                finish();
            }
        });

    }
}