package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.myapplication.ChatMsgVO;
import com.example.myapplication.Se_Application;
import com.example.myapplication.R;
import com.example.myapplication.PlaceholderContent;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class ChatMsgFragment extends Fragment implements View.OnClickListener, ChatAdapter.OnItemClickListener {

    private final String TAG = getClass().getSimpleName();
    private EditText content_et;
    private ImageView send_iv;
    private RecyclerView rv;
    private ChatAdapter mAdapter;
    private String chatroom = "";
    private List<ChatMsgVO> msgList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private Se_LocalDbConnector localDbConnector;
    private FirebaseAuth mAuth;
    private String otherUid;

    private boolean isInitialLoadComplete = false;

    public ChatMsgFragment() {
    }

    public static ChatMsgFragment newInstance() {
        return new ChatMsgFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_msg, container, false);

        content_et = view.findViewById(R.id.content_et);
        send_iv = view.findViewById(R.id.send_iv);
        rv = view.findViewById(R.id.rv);

        send_iv.setOnClickListener(this);

        if (getArguments() != null) {
            chatroom = getArguments().getString("chatroom");
            otherUid = getArguments().getString("otherUid");
        }

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ChatAdapter(msgList, this); // 어댑터에 클릭 리스너 설정
        rv.setAdapter(mAdapter);

        Log.d(TAG, "Adapter attached to RecyclerView");

        myRef = database.getReference(chatroom);

        /////////////////////////////dgsgdgsd
        /*
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mAdapter.notifyDataSetChanged();
                rv.scrollToPosition(msgList.size() - 1);
                isInitialLoadComplete = true; // 초기 로드 완료
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 오류 처리
            }
        });*/
        //////dgdgsgsg

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChild added");
                ChatMsgVO chatMsgVO = dataSnapshot.getValue(ChatMsgVO.class);
                if (chatMsgVO != null) {
                    //boolean isInitialLoad = msgList.isEmpty();
                    msgList.add(chatMsgVO);
                    mAdapter.notifyDataSetChanged(); // 변경된 데이터 세트를 RecyclerView에 알림
                    rv.scrollToPosition(msgList.size() - 1);
                    Log.d(TAG, msgList.size() + "");
///////////////////////////////////////////////////////////

                        //sendNotification(chatMsgVO);
                    /////////////////////////////////
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        Log.d(TAG, "chatroom = " + chatroom);

        return view;
    }

    public static ChatMsgFragment newInstance(String otherUid) {
        ChatMsgFragment fragment = new ChatMsgFragment();
        Bundle args = new Bundle();
        args.putString("otherUid", otherUid);
        fragment.setArguments(args);
        return fragment;
    }

    public void setLocalDbConnector(Se_LocalDbConnector localDbConnector) {
        this.localDbConnector = localDbConnector;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_iv) {
            if (content_et.getText().toString().trim().length() >= 1) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String uid = currentUser.getUid();
                Log.d(TAG, "입력처리");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String content = content_et.getText().toString().trim();

                fetchNickname(uid, df.format(new Date()).toString(), content);
                content_et.setText("");
                ///////
                //sendNotification(content);
                /////
            } else {
                Toast.makeText(getActivity(), "메시지를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemClick(ChatMsgVO item) {
        // 상대방 프로필 보기 버튼 클릭 시 처리
        String uid = item.getUid();
        // 다음 액티비티에 uid 전달
        Intent intent = new Intent(getActivity(), OtherProfileActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }
    ////////////////////////
    /////////////////////////////기머루미러뤼뤼유렁ㄴ리ㅜㄹ
    /////////////fsdfnklsfnlkdsnfklsdnflk
    private void sendNotification(String chatMsgVO) {
        String channelId = "chat_notifications";
        String channelName = "Chat Notifications";
        String channelDescription = "Notifications for new chat messages";

        // 알림 채널 생성 (API 26 이상)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        // 알림 클릭 시 열릴 인텐트 설정
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("chatroom", chatroom);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getActivity(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        // 알림 빌더 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // 알림 아이콘 설정
                .setContentTitle(getString(R.string.app_name)) // 앱 이름을 제목으로 설정
                .setContentText("받은 채팅: " + chatMsgVO) // 부제목 설정
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 알림 클릭 시 실행될 인텐트 설정
                .setAutoCancel(true); // 클릭하면 알림 자동 삭제

        // 고유한 알림 ID 생성 (예: 메시지의 타임스탬프 사용)
        int notificationId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

        // 알림 표시
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
        notificationManager.notify(notificationId, builder.build());
    }

    ///kdflksdnfkldsnfklndsklfnsdklfn
    
    ///////////ㄹㅇㄹㅇㄹ/ㅇㄹㅇㄹㅇㄹㅇㄹㅇㄹㅇㄹ/ㅇㄹ/ㅇ/ㄹ
    private void fetchNickname(final String uid, final String date, final String content) {
        DatabaseReference nicknameRef = FirebaseDatabase.getInstance().getReference().child("userAccount").child(uid).child("nickname");
        nicknameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nickname = dataSnapshot.getValue(String.class);
                if (nickname != null) {
                    // 닉네임을 가져온 후에 채팅 메시지 생성
                    ChatMsgVO msgVO = new ChatMsgVO(uid, nickname, date, content);
                    myRef.push().setValue(msgVO);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 오류 처리
            }
        });
    }

    public String getOtherUid() {
        return otherUid;
    }
}