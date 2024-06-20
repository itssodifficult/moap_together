package com.example.myapplication;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.ChatMsgVO;
import com.example.myapplication.Se_Application;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final List<ChatMsgVO> mValues;
    @SuppressLint("RestrictedApi")
    private Context context;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private Se_LocalDbConnector localDbConnector;
    private FirebaseAuth mAuth;
    private OnItemClickListener listener;

    public ChatAdapter(List<ChatMsgVO> items, OnItemClickListener listener) {
        mValues = items;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(ChatMsgVO item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_chat_msg, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ChatMsgVO vo = mValues.get(position);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        String otherUid = vo.getUid();

        if (vo.getUid().equals(uid)) {
            // 본인의 메시지
            holder.other_cl.setVisibility(View.GONE);
            holder.my_cl.setVisibility(View.VISIBLE);

            holder.date_tv2.setText(vo.getCrt_dt());
            holder.content_tv2.setText(vo.getContent());
        } else {
            // 상대방의 메시지
            holder.other_cl.setVisibility(View.VISIBLE);
            holder.my_cl.setVisibility(View.GONE);

            vo.getNickname(otherUid, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String nickname = snapshot.getValue(String.class);
                    holder.userid_tv.setText(nickname);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 에러 처리
                }
            });
            holder.date_tv.setText(vo.getCrt_dt());
            holder.content_tv.setText(vo.getContent());
            setOtherImageView(otherUid, holder.otherImageView);
        }

        // 이미지뷰에 대한 클릭 이벤트 처리
        holder.otherImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(vo);
            }
        });
    }

    private void setOtherImageView(String otherUid, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profilePics/" + otherUid + ".jpg");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(imageView.getContext()).load(uri).into(imageView);
        }).addOnFailureListener(exception -> {
            // 이미지 로드 실패 시 처리
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout my_cl, other_cl;
        public TextView userid_tv, date_tv, content_tv, date_tv2, content_tv2;
        public ImageView otherImageView;

        public ViewHolder(View view) {
            super(view);
            my_cl = view.findViewById(R.id.my_cl);
            other_cl = view.findViewById(R.id.other_cl);
            userid_tv = view.findViewById(R.id.userid_tv);
            date_tv = view.findViewById(R.id.date_tv);
            content_tv = view.findViewById(R.id.content_tv);
            date_tv2 = view.findViewById(R.id.date_tv2);
            content_tv2 = view.findViewById(R.id.content_tv2);
            otherImageView = view.findViewById(R.id.imageView);
        }
    }

    public ChatMsgVO getItem(int position) {
        return mValues.get(position);
    }
}