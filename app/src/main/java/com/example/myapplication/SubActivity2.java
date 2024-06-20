package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SubActivity2 extends Activity {

    Button btn_prev;
    Button btn_next;
    ImageView img_1, img_2, img_3, img_4, img_5, img_6;
    TextView prod_1, prod_2, prod_3, prod_4, prod_5, prod_6;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub2);

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
                Intent intent = new Intent(SubActivity2.this, SubActivity3.class);
                startActivity(intent);
            }
        });

        prod_1 = findViewById(R.id.product_title_13);
        img_1 = findViewById(R.id.product_image_13);

        img_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_19);
                //이미지뷰
                String textViewName = ((TextView) prod_1).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(SubActivity2.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_2 = findViewById(R.id.product_title_14);
        img_2 = findViewById(R.id.product_image_14);

        img_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_god);
                //이미지뷰
                String textViewName = ((TextView) prod_2).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(SubActivity2.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_3 = findViewById(R.id.product_title_15);
        img_3 = findViewById(R.id.product_image_15);

        img_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_20);
                //이미지뷰
                String textViewName = ((TextView) prod_3).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(SubActivity2.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_4 = findViewById(R.id.product_title_16);
        img_4 = findViewById(R.id.product_image_16);

        img_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_21);
                //이미지뷰
                String textViewName = ((TextView) prod_4).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(SubActivity2.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_5 = findViewById(R.id.product_title_17);
        img_5 = findViewById(R.id.product_image_17);

        img_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_22);
                //이미지뷰
                String textViewName = ((TextView) prod_5).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(SubActivity2.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_6 = findViewById(R.id.product_title_18);
        img_6 = findViewById(R.id.product_image_18);

        img_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_23);
                //이미지뷰
                String textViewName = ((TextView) prod_6).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(SubActivity2.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

    }
}
