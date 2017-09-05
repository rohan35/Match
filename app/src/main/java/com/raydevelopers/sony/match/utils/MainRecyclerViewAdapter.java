package com.raydevelopers.sony.match.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raydevelopers.sony.match.MainActivity;
import com.raydevelopers.sony.match.R;
import com.raydevelopers.sony.match.model.Interest;
import com.raydevelopers.sony.match.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SONY on 20-08-2017.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.MyViewHolder> {
private Context mContext;
    private ArrayList<User> mUser;
    private   FirebaseUser user;
    private String key;

    public  MainRecyclerViewAdapter(Context c, ArrayList<User> user)
    {
        this.mContext=c;
        this.mUser=user;
    }

    @Override
    public MainRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(mContext).inflate(R.layout.main_rv,parent,false);
        MyViewHolder holder=new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MainRecyclerViewAdapter.MyViewHolder holder, final int position) {
        holder.name.setText(mUser.get(position).mName);
        holder.age.setText(String.valueOf(mUser.get(position).mAge));
        Picasso.with(mContext).load(mUser.get(position).mProfilePic).into(holder.imageView);

        holder.sendInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
                String key=sharedPreferences.getString("key",null);
                String bindIds=key+"match"+mUser.get(position).mkey;
                UpdateFirebaseSendUsers(bindIds);
                holder.sendInterest.setText("Interest Send");
                holder.sendInterest.setEnabled(false);

            }
        });


    }

    @Override
    public int getItemCount() {
        if(mUser.size()>0)
        return mUser.size();
        else
            return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button sendInterest;
        TextView name,age;
        public MyViewHolder(View itemView) {
            super(itemView);
            sendInterest=(Button)itemView.findViewById(R.id.sendInterest);
            imageView=(ImageView)itemView.findViewById(R.id.profile_pic);
            name=(TextView)itemView.findViewById(R.id.displayname);
            age=(TextView)itemView.findViewById(R.id.age);

        }
    }
    public void UpdateFirebaseSendUsers(final String bindIds)
    {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("interest").child(bindIds).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                  //Already sent
                }
                else {
                    Interest user1 = new Interest();
                    ref.child("interest").child(bindIds).setValue(user1);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public  void UpdateFirebaseRecieveUsers()
    {

    }
}
