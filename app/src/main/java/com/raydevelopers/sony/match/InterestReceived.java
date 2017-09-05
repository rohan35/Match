package com.raydevelopers.sony.match;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raydevelopers.sony.match.model.Interest;
import com.raydevelopers.sony.match.model.User;
import com.raydevelopers.sony.match.utils.ReceivedRecylerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SONY on 23-08-2017.
 */

public class InterestReceived extends AppCompatActivity {
    ArrayList<User> user_details=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ReceivedRecylerViewAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.received_layout);
        mRecyclerView=(RecyclerView)findViewById(R.id.received_rv);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InterestReceived.this);
        final String key = sharedPreferences.getString("key", null);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("interest");
        ref.orderByChild("mConnected").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postDataSnapShot:dataSnapshot.getChildren())
                {

                String users[] = postDataSnapShot.getKey().split("match");
                    System.out.println(users[1]);
                if (users[1].equals(key)&&users.length>0) ;
                {
                    getUserDetails(users[0]);
                }
            }}

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getUserDetails(final String key) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.mkey=dataSnapshot.getKey();
                user_details.add(user);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InterestReceived.this,
                        LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mAdapter=new ReceivedRecylerViewAdapter(InterestReceived.this,user_details,key);
                mRecyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}