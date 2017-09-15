package com.raydevelopers.sony.match;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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
import com.raydevelopers.sony.match.utils.Constants;
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
        mRecyclerView=(RecyclerView)findViewById(R.id.message_rv);
    final String key = sharedPreferences.getString(Constants.ARG_KEY, null);
       final DatabaseReference ref=FirebaseDatabase.getInstance().getReference("chat");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                if(dataSnapshot1.getKey().contains(key))
                {
                    ref.child(dataSnapshot1.getKey()).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot2:dataSnapshot.getChildren())
                            {
                                System.out.println(dataSnapshot2.getValue());
                                Chat chat=dataSnapshot2.getValue(Chat.class);
                            if(chat.mSender.equals(key)) {
                                usersList.add(chat.mReceiver);
                            }
                            else
                            {
                                usersList.add(chat.mSender);
                            }
                            getUsers();
                                break;

                        }}

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }}
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
