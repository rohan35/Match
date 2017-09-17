package com.raydevelopers.sony.match.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
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
import com.raydevelopers.sony.match.UserDetails;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext, UserDetails.class);
                i.putExtra(Constants.ARG_KEY,mUser.get(position).mkey);
                mContext.startActivity(i);
            }
        });

        holder.name.setText(mUser.get(position).mName+", ");
        holder.age.setText(String.valueOf(mUser.get(position).mAge));
        Picasso.with(mContext).load(mUser.get(position).mProfilePic).into(holder.imageView);
        int resultCode=getInterest(mUser.get(position).mkey);
        if(resultCode==1)
        {
            holder.sendInterest.setText("Interest Send");
            holder.sendInterest.setEnabled(false);
        }
        else if(resultCode==2)
        {

        }
        else if(resultCode==0)
        {
            holder.sendInterest.setText("freinds");
            holder.sendInterest.setEnabled(false);
        }

        holder.sendInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
                String key=sharedPreferences.getString(Constants.ARG_KEY,null);
                String bindIds=key+"match"+mUser.get(position).mkey;
                ObjectAnimator animY = ObjectAnimator.ofFloat(holder.sendInterest, "translationY", 100f, 0f);
                animY.setDuration(1000);//1sec
                animY.setInterpolator(new BounceInterpolator());
                animY.setRepeatCount(0);
                animY.start();
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
    private int getInterest(final String key)
    {  SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        String currentUser=sharedPreferences.getString(Constants.ARG_KEY,null);
        final String sentInterest=currentUser+"match"+key;
        final String receivedInterest=key+"match"+currentUser;
        final int[] returnCode={-1} ;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("interest");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    if(dataSnapshot1.getKey().equals(sentInterest))
                    {
                        Interest i=dataSnapshot1.getValue(Interest.class);
                        if(i.mConnected)
                        {
                            //freinds return 0
                            returnCode[0] =0;
                        }
                        else
                        {
                            //interest already sent return 1
                            returnCode[0] =1;
                        }

                    }
                    else if(dataSnapshot1.getKey().equals(receivedInterest))
                    {
                        Interest i=dataSnapshot1.getValue(Interest.class);
                        if(i.mConnected)
                        {
                            //freinds return 0
                            returnCode[0] =0;
                        }
                        else
                        {
                            //interest received show accept button return 2
                            returnCode[0] =2;
                         }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        System.out.println(returnCode[0]);
return  returnCode[0];
    }

}
