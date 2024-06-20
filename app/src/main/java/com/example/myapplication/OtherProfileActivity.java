package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OtherProfileActivity extends AppCompatActivity  {
    private static final String TAG = "OtherProfileActivity";
    Button btn;
    private DatabaseReference mDatabaseRef;
    private EditText metNickName, metAge,metIntroduction;
    private ImageView mProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherprofile);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("userAccount");
        metIntroduction=findViewById(R.id.et_introduction);
        metNickName = findViewById(R.id.et_nickname);
        metAge = findViewById(R.id.et_age);
        mProfileImage = findViewById(R.id.img_profile);
        btn=findViewById(R.id.button);

        Intent intent = getIntent();
        String otherUserUID = null;
        if (intent != null) {
            otherUserUID = intent.getStringExtra("uid");
        }
        viewOtherUserProfile(otherUserUID);

        String finalOtherUserUID = otherUserUID;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chatroom = combineUserIDs(finalOtherUserUID); // 채팅방 이름 생성
                Intent intent = new Intent(OtherProfileActivity.this, PersonalChatActivity.class);
                intent.putExtra("chatroom", chatroom);
                intent.putExtra("otherUid", finalOtherUserUID);
                startActivity(intent);
            }
        });
    }
    private String combineUserIDs(String otherUid) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            // 자신의 UID와 상대방의 UID를 조합하여 채팅방 이름 생성
            if (uid.compareTo(otherUid) < 0) {
                return uid + "_" + otherUid;
            } else {
                return otherUid + "_" + uid;
            }
        }
        return null;
    }

    private void viewOtherUserProfile(String otherUserUID) {
        mDatabaseRef.child(otherUserUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    if (userAccount != null) {
                        metNickName.setText(userAccount.getNickname());
                        metAge.setText(userAccount.getAge());
                        if (userAccount.getProfileImageUrl() != null && !userAccount.getProfileImageUrl().isEmpty()) {
                            Glide.with(OtherProfileActivity.this).load(userAccount.getProfileImageUrl()).into(mProfileImage);
                        }
                        if (userAccount.getIntroduction() != null && !userAccount.getIntroduction().isEmpty()) {
                            metIntroduction.setText(userAccount.getIntroduction());
                        }
                    }
                } else {
                    Toast.makeText(OtherProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OtherProfileActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}