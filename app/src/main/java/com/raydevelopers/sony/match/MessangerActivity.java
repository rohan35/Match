package com.raydevelopers.sony.match;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

/**
 * Created by SONY on 30-08-2017.
 */

public class MessangerActivity extends AppCompatActivity {
    private ArrayList<String> usersList=new ArrayList<>();
    private ArrayList<User> users=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private AVLoadingIndicatorView loadingIndicatorView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messanger_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mRecyclerView=(RecyclerView)findViewById(R.id.message_rv);
        loadingIndicatorView=(AVLoadingIndicatorView)findViewById(R.id.indicator);
        startAnim();
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

                                break;

                        }
                            getUsers();}

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

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(DataSnapshot dataSnapshot) {
                                 for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                                 {
                                     System.out.println(dataSnapshot1.getKey());

                                     for(int i=0;i<usersList.size();i++) {
                                         if (dataSnapshot1.getKey().equals(usersList.get(i)))
                                         {
                                             User user=dataSnapshot1.getValue(User.class);
                                             user.mkey=dataSnapshot1.getKey();
                                             System.out.println(user.mName);
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
                                          stopAnim();
                                      }


                                      @Override
                                      public void onCancelled(DatabaseError databaseError) {

                                      }
                                  }
        );
    }
    void startAnim()
    {
        loadingIndicatorView.setVisibility(View.VISIBLE);
    }
    void stopAnim()
    {
        loadingIndicatorView.setVisibility(View.GONE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
