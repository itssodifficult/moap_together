package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class FirstPageActivity extends AppCompatActivity implements NavigationView

        .OnNavigationItemSelectedListener {
    ImageView firstImageView, img_2, img_3, img_4, img_5, img_6;
    TextView prod_1, prod_2, prod_3, prod_4, prod_5, prod_6;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    Button btn_main;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_page);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ActionBarDrawerToggle 초기화 및 추가
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        btn_main = findViewById(R.id.next_button);

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SubActivity.class);
                startActivity(intent);
            }
        });

        prod_1 = findViewById(R.id.product_title);
        firstImageView = findViewById(R.id.product_image);

        firstImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_1);
                //이미지뷰
                String textViewName = ((TextView) prod_1).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(FirstPageActivity.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_2 = findViewById(R.id.product_title_2);
        img_2 = findViewById(R.id.product_image_2);

        img_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_2);
                //이미지뷰
                String textViewName = ((TextView) prod_2).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(FirstPageActivity.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_3 = findViewById(R.id.product_title_3);
        img_3 = findViewById(R.id.product_image_3);

        img_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_3);
                //이미지뷰
                String textViewName = ((TextView) prod_3).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(FirstPageActivity.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_4 = findViewById(R.id.product_title_4);
        img_4 = findViewById(R.id.product_image_4);

        img_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_4);
                //이미지뷰
                String textViewName = ((TextView) prod_4).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(FirstPageActivity.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_5 = findViewById(R.id.product_title_5);
        img_5 = findViewById(R.id.product_image_5);

        img_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_5);
                //이미지뷰
                String textViewName = ((TextView) prod_5).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(FirstPageActivity.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

        prod_6 = findViewById(R.id.product_title_6);
        img_6 = findViewById(R.id.product_image_6);

        img_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = getResources().getResourceEntryName(R.drawable.product_6);
                //이미지뷰
                String textViewName = ((TextView) prod_6).getText().toString();
                //텍스트뷰
                Intent intent = new Intent(FirstPageActivity.this, ChatActivity.class);
                intent.putExtra("chatroom", imageName);
                intent.putExtra("text", textViewName);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(this, ChattingListActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_cal) {
            Intent intent = new Intent(this, CalenderActivity.class);
            startActivity(intent);
        }


        // Drawer를 닫기
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Drawer가 열려있는 경우, 뒤로가기 버튼을 누르면 Drawer를 닫습니다.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // 현재 액티비티가 MainActivity인 경우에만 뒤로가기 처리
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

}

