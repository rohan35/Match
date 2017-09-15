package com.raydevelopers.sony.match;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raydevelopers.sony.match.model.Interest;
import com.raydevelopers.sony.match.model.User;
import com.raydevelopers.sony.match.utils.AcceptedRecyclerViewAdapter;
import com.raydevelopers.sony.match.utils.Constants;

import java.util.ArrayList;

/**
 * Created by SONY on 31-08-2017.
 */

public class AcceptedInterest extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private TextView emptyView;
    ArrayList<String> listOfusers=new ArrayList<>();
    ArrayList<String> users=new ArrayList<>();
    ArrayList<User> checkUsers=new ArrayList<>();
    ArrayList<User> finalList=new ArrayList<>();
    private  String key="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accepted_layout);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        key = sharedPreferences.getString(Constants.ARG_KEY, null);
        mRecyclerView=(RecyclerView)findViewById(R.id.accepted_rv);
        emptyView=(TextView)findViewById(R.id.not_accepted);
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("interest");
        ref.orderByChild("mConnected").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postDataSnapshot:dataSnapshot.getChildren())
                {

                    if(postDataSnapshot.getKey().contains(key))
                    {
                    listOfusers.add(postDataSnapshot.getKey());
                }}

                for(int i=(listOfusers.size()-1);i>=0;i--)
                {

                    String[] temp;

                   temp= listOfusers.get(i).split("match");
                    if(temp[0].equals(key))
                    {
                        users.add(temp[1]);
                    }
                    else
                    {
                        users.add(temp[0]);
                    }
                }
                getUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }
    public void getUsers()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                User user=postSnapShot.getValue(User.class);
                    user.mkey=postSnapShot.getKey();
                    checkUsers.add(user);
                }

                    for (int i = 0; i <checkUsers.size(); i++) {
                        for (int j=0;j<users.size();j++){
                            System.out.println(users.get(j));
                        if (checkUsers.get(i).mkey.equals(users.get(j))) {
                            if(!(users.get(j).equals(key)))
                            {
                            finalList.add(checkUsers.get(i));
                        }}}
                    }
                   /* if(finalList.isEmpty())
                    {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {*/

                        AcceptedRecyclerViewAdapter adapter = new AcceptedRecyclerViewAdapter(AcceptedInterest.this, finalList);
                        mRecyclerView.setAdapter(adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AcceptedInterest.this,
                                LinearLayoutManager.VERTICAL, false);
                        mRecyclerView.setLayoutManager(linearLayoutManager);
                        adapter.notifyDataSetChanged();
                    //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
