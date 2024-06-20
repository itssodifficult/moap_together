package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class PersonalChatActivity extends AppCompatActivity {
    private Button act;
    private String chatroom;
    private String otherUid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);
        act = findViewById(R.id.btn);
        act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalChatActivity.this, MapActivity.class);
                intent.putExtra("otherUid",otherUid);
                startActivity(intent);
            }
        });
        chatroom = getIntent().getStringExtra("chatroom");
        otherUid = getIntent().getStringExtra("otherUid");

        // ChatMsgFragment 인스턴스 생성
        ChatMsgFragment chatMsgFragment = new ChatMsgFragment();
        Bundle bundle = new Bundle();
        bundle.putString("chatroom", chatroom);
        chatMsgFragment.setArguments(bundle);

        Se_LocalDbConnector localDbConnector = new Se_LocalDbConnector(this);
        chatMsgFragment.setLocalDbConnector(localDbConnector);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 띄우려는 프래그먼트를 지정하고, 레이아웃 리소스 ID를 지정하여 추가
        transaction.add(R.id.fragment_container, chatMsgFragment);

        // 트랜잭션을 커밋하여 프래그먼트를 화면에 표시
        transaction.commit();
        ((Se_Application)getApplication()).Init_Value();
    }
}
