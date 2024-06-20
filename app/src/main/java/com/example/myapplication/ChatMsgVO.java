package com.example.myapplication;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatMsgVO {
    private String uid;
    private String crt_dt;
    private String content;
    private String nickname;

    public ChatMsgVO(){
    }

    public ChatMsgVO(String uid, String nickname,String crt_dt,String content){
        this.uid=uid;
        this.nickname=nickname;
        this.crt_dt=crt_dt;
        this.content=content;
    }
    public void getNickname(String uid, final ValueEventListener valueEventListener) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("userAccount").child(uid).child("nickname");

        databaseRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public String getUid() {return uid;}
    public void setUid(String emailid) {this.uid=emailid;}
    public String getCrt_dt() {return crt_dt;}
    public void setCrt_dt(String crt_dt) {this.crt_dt=crt_dt;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content=content;}
    public String getNickname() {return nickname;}
    public void setNickname(String nickname) {this.nickname=nickname;}

    @Override
    public String toString(){
        return "ChatMsgVO{"+
                "emailId='"+uid+'\''+
                ", crt_dt='"+crt_dt+'\''+
                ", content='"+content+'\''+
                '}';
    }
}