package com.raydevelopers.sony.match.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.raydevelopers.sony.match.R;
import com.raydevelopers.sony.match.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by SONY on 31-08-2017.
 */

public class AcceptedRecyclerViewAdapter extends RecyclerView.Adapter<AcceptedRecyclerViewAdapter.MyViewHolder> {
private Context mContext;
    private ArrayList<User> mUsers=new ArrayList<>();
    public AcceptedRecyclerViewAdapter(Context c, ArrayList<User> users)
    {
        this.mContext=c;
        this.mUsers=users;
    }
    @Override
    public AcceptedRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.accepted_rv_layout,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(AcceptedRecyclerViewAdapter.MyViewHolder holder, final int position) {
        Picasso.with(mContext).load(mUsers.get(position).mProfilePic).into(holder.profile_image);
        holder.name.setText(mUsers.get(position).mName);
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext,ChatHandler.class);
                i.putExtra(Constants.ARG_RECEIVER_KEY,mUsers.get(position).mkey);
                mContext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mUsers.size()>0)
        {
            return mUsers.size();
        }
        else
        {
        return 0;
    }}

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        TextView name;
        Button chat;
        public MyViewHolder(View itemView) {
            super(itemView);
            profile_image=(ImageView)itemView.findViewById(R.id.interested_image);
            name=(TextView)itemView.findViewById(R.id.interested_name);
            chat=(Button)itemView.findViewById(R.id.chat);
        }
    }
}
