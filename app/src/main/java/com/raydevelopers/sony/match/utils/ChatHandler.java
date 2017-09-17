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
import com.raydevelopers.sony.match.fcm.FcmNotificationBuilder;
import com.raydevelopers.sony.match.model.Chat;
import com.raydevelopers.sony.match.model.User;
import com.wang.avi.AVLoadingIndicatorView;

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
    public static boolean active = false;
    private EditText message;
    private String senderFirebaseToken;
    private String receiverFirebaseToken;
    private AVLoadingIndicatorView loadingIndicatorView;
    private String timeStamp;
    private  ArrayList<Chat> meChatList=new ArrayList<>();
    private static ArrayList<Chat> otherChatList=new ArrayList<>();
    private ChatRecyclerViewAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        loadingIndicatorView=(AVLoadingIndicatorView)findViewById(R.id.indicator);
        message=(EditText)findViewById(R.id.message);
        mRecyclerView=(RecyclerView)findViewById(R.id.chat_view);
        startAnim();
        Intent i=getIntent();
        //getIntent
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        senderKey=sharedPreferences.getString(Constants.ARG_KEY,null);
        receiverKey=i.getStringExtra(Constants.ARG_RECEIVER_KEY);
        sender_receiver_key=senderKey+"match"+receiverKey;
        receiver_sender_key=receiverKey+"match"+senderKey;
        senderFirebaseToken=sharedPreferences.getString(Constants.ARG_FIREBASE_TOKEN,null);
        getToken(receiverKey);


        getUserchats();
        mAdapter=new ChatRecyclerViewAdapter(ChatHandler.this,meChatList);
        System.out.println(meChatList.size());
        LinearLayoutManager
                linearLayoutManager=new LinearLayoutManager(ChatHandler.this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter.notifyDataSetChanged();
stopAnim();

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
        ref.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(sender_receiver_key)) {
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts = tsLong.toString();
                    Chat chat = new Chat(senderKey, receiverKey, message.getText().toString(), ts);

                    ref.child("chat").child(sender_receiver_key).child(ts).setValue(chat);
                    message.getText().clear();



                        sendPushNotificationToReceiver(chat.mSender,
                                chat.mMessage,
                                senderFirebaseToken,
                               receiverFirebaseToken);
                    }
                else if(dataSnapshot.hasChild(receiver_sender_key))
                {
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts = tsLong.toString();
                    Chat chat = new Chat(senderKey,receiverKey, message.getText().toString(), ts);

                    ref.child("chat").child(receiver_sender_key).child(ts).setValue(chat);
                    message.getText().clear();
                        sendPushNotificationToReceiver(chat.mSender,
                                chat.mMessage,
                                senderFirebaseToken,
                                receiverFirebaseToken);
                      }
                else {
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts = tsLong.toString();
                    Chat chat = new Chat(senderKey, receiverKey, message.getText().toString(), ts);

                    ref.child("chat").child(sender_receiver_key).child(ts).setValue(chat);
                    message.getText().clear();
                        sendPushNotificationToReceiver(chat.mSender,
                            chat.mMessage,
                            senderFirebaseToken,
                                receiverFirebaseToken);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void  getToken(final String key)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    receiverFirebaseToken = user.mFirebaseToken;
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void sendPushNotificationToReceiver(String username,
                                                String message,
                                                String firebaseToken,
                                                String receiverFirebaseToken) {
        FcmNotificationBuilder.initialize()
                .title(username)
                .message(message)
                .username(username)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
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
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
    void startAnim()
    {
        loadingIndicatorView.setVisibility(View.VISIBLE);
    }
    void stopAnim()
    {
        loadingIndicatorView.setVisibility(View.GONE);
    }
}
