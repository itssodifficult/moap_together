package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChattingListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatRoomAdapter adapter;
    private List<ChatRoom> chatRooms;
    private static final String TAG = "chattingList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatRooms = new ArrayList<>();
        adapter = new ChatRoomAdapter(chatRooms, chatRoom -> {
            String chatRoomId = chatRoom.getRoomId();
            // Intent를 사용하여 다음 액티비티로 이동하고 채팅방 ID를 전달합니다.
            Intent intent = new Intent(ChattingListActivity.this, PersonalChatActivity.class);
            intent.putExtra("chatroom", chatRoomId);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        loadChatRooms();
    }

    private void loadChatRooms() {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference chatRoomsRef = FirebaseDatabase.getInstance().getReference();

        chatRoomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatRooms.clear();
                for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                    // 최상위 채팅방 노드의 자식들 중에서 내 uid가 포함된 채팅방을 찾음
                    if (roomSnapshot.getKey().contains(myUid) && !roomSnapshot.getKey().equals(myUid)) {
                        // 해당 채팅방의 하위 자식에 있는 값을 가져옴
                        ChatRoom chatRoom = new ChatRoom();
                        chatRoom.setRoomId(roomSnapshot.getKey());
                        chatRoom.setChatData(roomSnapshot.getValue(new GenericTypeIndicator<Map<String, ChatContext>>(){})); // 채팅 데이터 설정
                        chatRooms.add(chatRoom);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리
            }
        });
    }
}