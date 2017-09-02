package com.raydevelopers.sony.match;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raydevelopers.sony.match.model.Chat;
import com.raydevelopers.sony.match.model.User;
import com.raydevelopers.sony.match.utils.MessangerRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Created by SONY on 30-08-2017.
 */

public class MessangerActivity extends AppCompatActivity {
    private ArrayList<String> usersList=new ArrayList<>();
    private ArrayList<User> users=new ArrayList<>();
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messanger_layout);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    final String key = sharedPreferences.getString("key", null);
       final DatabaseReference ref=FirebaseDatabase.getInstance().getReference("chat");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getKey().contains(key))
                {
                    ref.child(dataSnapshot.getKey()).orderByChild("mTimeStamp").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Chat chat=dataSnapshot.getValue(Chat.class);
                            if(chat.mSender.equals(key)) {
                                usersList.add(chat.mReceiver);
                            }
                            else
                            {
                                usersList.add(chat.mSender);
                            }
                       getUsers();
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getUsers()
    {

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(DataSnapshot dataSnapshot) {
                                 for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                                 {
                                     for(int i=0;i<usersList.size();i++) {
                                         if (dataSnapshot1.getKey().equals(usersList.get(i)))
                                         {
                                             User user=dataSnapshot1.getValue(User.class);
                                             user.mkey=dataSnapshot1.getKey();
                                             users.add(user);
                                         }
                                     }
                                 }
                                          MessangerRecyclerViewAdapter adapter=new MessangerRecyclerViewAdapter(
                                                  MessangerActivity.this,users);
                                          mRecyclerView.setAdapter(adapter);
                                          LinearLayoutManager linearLayoutManager=new
                                                  LinearLayoutManager(MessangerActivity.this
                                                  ,LinearLayoutManager.VERTICAL,false);
                                          mRecyclerView.setLayoutManager(linearLayoutManager);
                                          adapter.notifyDataSetChanged();
                                      }


                                      @Override
                                      public void onCancelled(DatabaseError databaseError) {

                                      }
                                  }
        );
    }
}
