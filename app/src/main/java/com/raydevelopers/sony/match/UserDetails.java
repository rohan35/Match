package com.raydevelopers.sony.match;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raydevelopers.sony.match.model.User;
import com.raydevelopers.sony.match.utils.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by SONY on 17-09-2017.
 */

public class UserDetails extends AppCompatActivity {
    private ImageView imageView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_user);
        Intent i=getIntent();
        imageView=(ImageView)findViewById(R.id.user_image);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String key=i.getStringExtra(Constants.ARG_KEY);
        getUser(key);

    }
    private void getUser(String key)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Picasso.with(getApplicationContext()).load(user.mProfilePic).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
