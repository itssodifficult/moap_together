package com.example.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {
    private List<ChatRoom> chatRooms;
    private OnItemClickListener listener;
    private static final String TAG = "googlemap_example";

    public ChatRoomAdapter(List<ChatRoom> chatRooms, OnItemClickListener listener) {
        this.chatRooms = chatRooms;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(ChatRoom chatRoom);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_chat_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoom chatRoom = chatRooms.get(position);
        holder.bind(chatRoom, listener);
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView lastMessagetextView, time, chatTarget;
        private ImageView lastImage;
        private FirebaseStorage storage;

        public ViewHolder(View itemView) {
            super(itemView);
            lastMessagetextView = itemView.findViewById(R.id.lastMessageTextView);
            time = itemView.findViewById(R.id.timestampTextView);
            lastImage = itemView.findViewById(R.id.chattingListImage);
            chatTarget = itemView.findViewById(R.id.chatRoomIdTextView);
        }

        public void bind(final ChatRoom chatRoom, final OnItemClickListener listener) {
            String lastMessage = getLastMessage(chatRoom);
            String lastTime = getLastMessageTime(chatRoom);
            String chatRoomId = chatRoom.getRoomId();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String UID=null;
            if (currentUser != null) {
                UID = currentUser.getUid();
            } else {
                // 사용자가 로그인하지 않은 경우에 대한 처리
            }


            String[] chatRoomIdParts = chatRoomId.split("_");
            String otherUserId = "";
            for (String part : chatRoomIdParts) {
                if (!part.equals(UID)) {
                    otherUserId = part;
                    break;
                }
            }
            storage = FirebaseStorage.getInstance();
            getProfileImage(otherUserId);
            getChatTarget(otherUserId, nickname -> {
                if (nickname != null) {
                    Log.d("ChatRoomAdapter", nickname);
                    chatTarget.setText(nickname);
                } else {
                    Log.d("ChatRoomAdapter", "Nickname is null");
                }
            });

            lastMessagetextView.setText("마지막 대화 내용: " + lastMessage);
            time.setText("마지막 대화 일시: " + lastTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(chatRoom);
                }
            });
        }

        private static String getLastMessage(ChatRoom chatRoom) {
            Map<String, ChatContext> chatData = chatRoom.getChatData();
            if (chatData != null && !chatData.isEmpty()) {
                // 채팅 데이터 맵에서 가장 최근의 채팅 내용을 가져옵니다.
                List<ChatContext> chatContexts = new ArrayList<>(chatData.values());
                ChatContext lastChat = chatContexts.get(chatContexts.size() - 1);
                return lastChat.getContent();
            } else {
                return ""; // 채팅 내용이 없는 경우 빈 문자열 반환
            }
        }

        private static String getLastMessageTime(ChatRoom chatRoom) {
            Map<String, ChatContext> chatData = chatRoom.getChatData();
            if (chatData != null && !chatData.isEmpty()) {
                // 채팅 데이터 맵에서 가장 최근의 채팅의 타임스탬프를 가져옵니다.
                List<ChatContext> chatContexts = new ArrayList<>(chatData.values());
                ChatContext lastChat = chatContexts.get(chatContexts.size() - 1);
                return lastChat.getCrt_dt();
            } else {
                return ""; // 채팅 내용이 없는 경우 빈 문자열 반환
            }
        }
        public interface NicknameCallback {
            void onCallback(String nickname);
        }

        private void getChatTarget(String otherUserId, NicknameCallback callback) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userAccount");
            ref.child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String nickname = dataSnapshot.child("nickname").getValue(String.class);
                        callback.onCallback(nickname);
                    } else {
                        System.out.println("User not found");
                        callback.onCallback(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("Error: " + databaseError.getMessage());
                    callback.onCallback(null);
                }
            });
        }
        private void getProfileImage(String otherUserId) {
            StorageReference profilePicsRef = storage.getReference().child("profilePics");
            StorageReference imageRef = profilePicsRef.child(otherUserId + ".jpg");
            imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                String imageUrl = downloadUrl.toString();
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .into(lastImage);
            }).addOnFailureListener(exception -> {
                // 이미지를 가져오는 데 실패한 경우에 대한 처리
            });
        }
    }
}