package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


public class ChatActivity extends AppCompatActivity {
    private String chatroom;
    private String tvid;
    private ProductInfo p;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatroom = getIntent().getStringExtra("chatroom");
        tvid=getIntent().getStringExtra("text");

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
        /*
        p=new ProductInfo();
        Bundle productInfoBundle = new Bundle();
        productInfoBundle.putString("chatroom", chatroom);
        productInfoBundle.putString("text", tvid);
        p.setArguments(productInfoBundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.product_container, p)
                .commit();*/
        p=new ProductInfo();
        Bundle productInfoBundle = new Bundle();
        productInfoBundle.putString("chatroom", chatroom);
        productInfoBundle.putString("text", tvid);
        p.setArguments(productInfoBundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.product_container, p)
                .commit();
    }

}
