package com.raydevelopers.sony.match.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.raydevelopers.sony.match.R;
import com.raydevelopers.sony.match.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SONY on 30-08-2017.
 */

public class MessangerRecyclerViewAdapter extends RecyclerView.Adapter<MessangerRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<User> users;
    public MessangerRecyclerViewAdapter(Context c,ArrayList<User> users1)
    {
     this.mContext=c;
        this.users=users1;
    }
    @Override
    public MessangerRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.messanger_rv_layout,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(v);
        return  myViewHolder;
    }

    @Override
    public void onBindViewHolder(MessangerRecyclerViewAdapter.MyViewHolder holder, int position) {
        Picasso.with(mContext).load(users.get(position).mProfilePic).into(holder.profilePic);
        holder.name.setText(users.get(position).mName);
    }

    @Override
    public int getItemCount() {
        if(users.size()>0)
        {
            return users.size();
        }
        else
        {
        return 0;
    }}

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView name,lastMessage;
        public MyViewHolder(View itemView) {
            super(itemView);
            profilePic=(ImageView)itemView.findViewById(R.id.profile_image);
            name=(TextView)itemView.findViewById(R.id.profile_name);
            lastMessage=(TextView)itemView.findViewById(R.id.last_message);
        }
    }
}
