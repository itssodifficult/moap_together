package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ImageView imageView1 = findViewById(R.id.imageView3);
        //ImageView imageView2 = findViewById(R.id.imageView2);

        // 왼쪽 아래로 내려오는 애니메이션 로드 및 시작
        Animation translateDown = AnimationUtils.loadAnimation(this, R.anim.translate_up);
        imageView1.startAnimation(translateDown);

        // 오른쪽 위로 올라가는 애니메이션 로드 및 시작
        //Animation translateUp = AnimationUtils.loadAnimation(this, R.anim.translate_down);
        //imageView2.startAnimation(translateUp);

        // 핸들러를 사용하여 1.5초 후에 LoginActivity로 전환
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // LoginActivity로 전환
                startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
                // 현재 액티비티 종료
                finish();
            }
        }, 1500); // 1500 밀리초 = 1.5초
    }
}