package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProductInfo extends Fragment {

    private ImageView iv;
    private TextView tv;
    private String chatroom;
    private String text;
    private final String TAG = getClass().getSimpleName();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.product_info, container, false);
        iv = rootView.findViewById(R.id.imageView2);
        Log.d(TAG, "이미지뷰: " + String.valueOf(iv));
        tv = rootView.findViewById(R.id.textView);
        Log.d(TAG, "텍스트뷰: " + String.valueOf(tv));

        if (getArguments() != null) {
            chatroom = getArguments().getString("chatroom");
            text = getArguments().getString("text");
        }

        if (chatroom != null) {
            setChatroom(chatroom);
        }
        if (text != null) {
            setText(text);
        }

        return rootView;
    }

    public void setChatroom(String chatroom) {
        this.chatroom = chatroom;
        Log.d(TAG, "이미지뷰2: " + String.valueOf(iv));
        if(iv!=null) {
            int imageResId = getResources().getIdentifier(chatroom, "drawable", requireActivity().getPackageName());
            Log.d(TAG, "이미지: " + String.valueOf(imageResId));
            iv.setImageResource(imageResId);
        }
    }

    public void setText(String text) {
        this.text = text;
        Log.d(TAG, "텍스트뷰2: " + String.valueOf(tv));
        if(tv!=null) {
            Log.d(TAG, "텍스트: " + text);
            tv.setText(text);
        }
    }

}
