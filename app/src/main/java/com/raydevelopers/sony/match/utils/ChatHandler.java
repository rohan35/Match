package com.raydevelopers.sony.match.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raydevelopers.sony.match.R;
import com.raydevelopers.sony.match.model.Chat;

import java.util.ArrayList;

/**
 * Created by SONY on 22-08-2017.
 */

public class ChatHandler extends AppCompatActivity {
    private Button send;
    private String sender_receiver_key="";
    private  String receiver_sender_key="";
    private RecyclerView mRecyclerView;
    private String senderKey,receiverKey;
    private EditText message;
    private String timeStamp;
    private  ArrayList<Chat> meChatList=new ArrayList<>();
    private static ArrayList<Chat> otherChatList=new ArrayList<>();
    private ChatRecyclerViewAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        message=(EditText)findViewById(R.id.message);
        mRecyclerView=(RecyclerView)findViewById(R.id.chat_view);
        Intent i=getIntent();
        //getIntent
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        senderKey=sharedPreferences.getString("key",null);
        receiverKey=i.getStringExtra("receiver_key");
        sender_receiver_key=senderKey+"_"+receiverKey;
        receiver_sender_key=receiverKey+"_"+senderKey;

        getUserchats();
        mAdapter=new ChatRecyclerViewAdapter(ChatHandler.this,meChatList);
        System.out.println(meChatList.size());
        LinearLayoutManager
                linearLayoutManager=new LinearLayoutManager(ChatHandler.this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter.notifyDataSetChanged();


        send=(Button)findViewById(R.id.send_message);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserChatMessages();
            }
        });
    }

    public void updateUserChatMessages()
    {
        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
        final String userId = ref.child("chat").child(sender_receiver_key).push().getKey();
        ref.child("chat").child(sender_receiver_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                Chat chat=new Chat(senderKey,receiverKey,message.getText().toString(),ts);

                ref.child("chat").child(sender_receiver_key).child(userId).setValue(chat);
                message.getText().clear();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getUserchats()
    {

        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
        ref.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(sender_receiver_key))
                {
                    ref.child("chat").child(sender_receiver_key).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat=dataSnapshot.getValue(Chat.class);
                            meChatList.add(chat);
                            mAdapter.notifyDataSetChanged();
                            System.out.println(meChatList.size());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else if(dataSnapshot.hasChild(receiver_sender_key))
                {
                    ref.child("chat").child(receiver_sender_key).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat=dataSnapshot.getValue(Chat.class);
                            meChatList.add(chat);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else
                {
                    Log.e("", "getMessageFromFirebaseUser: no such room available");

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//unable to get messages
            }
        });

    }
}
