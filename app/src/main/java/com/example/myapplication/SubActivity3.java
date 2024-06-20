package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SubActivity3 extends Activity {

    Button btn_prev;
    Button btn_next;
    ImageView img_1, img_2, img_3, img_4, img_5, img_6;
    TextView prod_1, prod_2, prod_3, prod_4, prod_5, prod_6;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub3);

        btn_prev = findViewById(R.id.previous_button);
        btn_next=findViewById(R.id.next_button);

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다음 액티비티를 시작하기 위한 Intent 생성
                Intent intent = new Intent(SubActivity3.this, FirstPageActivity.class);
                startActivity(intent);
            }
        });

        prod_1 = findViewById(R.id.product_title_19);
        img_1 = findViewById(R.id.product_image_19);

        img_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_26);
                //이미지뷰
                String textViewName = ((TextView) prod_1).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(SubActivity3.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_2 = findViewById(R.id.product_title_20);
        img_2 = findViewById(R.id.product_image_20);

        img_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_27);
                //이미지뷰
                String textViewName = ((TextView) prod_2).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(SubActivity3.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_3 = findViewById(R.id.product_title_21);
        img_3 = findViewById(R.id.product_image_21);

        img_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_28);
                //이미지뷰
                String textViewName = ((TextView) prod_3).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(SubActivity3.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_4 = findViewById(R.id.product_title_22);
        img_4 = findViewById(R.id.product_image_22);

        img_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_29);
                //이미지뷰
                String textViewName = ((TextView) prod_4).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(SubActivity3.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_5 = findViewById(R.id.product_title_23);
        img_5 = findViewById(R.id.product_image_23);

        img_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_30);
                //이미지뷰
                String textViewName = ((TextView) prod_5).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(SubActivity3.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_6 = findViewById(R.id.product_title_24);
        img_6 = findViewById(R.id.product_image_24);

        img_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_31);
                //이미지뷰
                String textViewName = ((TextView) prod_6).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(SubActivity3.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

    }
}


