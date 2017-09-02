package com.raydevelopers.sony.match.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raydevelopers.sony.match.R;
import com.raydevelopers.sony.match.model.Chat;

import java.util.ArrayList;

/**
 * Created by SONY on 28-08-2017.
 */

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter{
    private  Context mContext;
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;
    private ArrayList<Chat> mineChats=new ArrayList<>();
    private ArrayList<Chat> otherChats=new ArrayList<>();

    private  String userKey="";
    public ChatRecyclerViewAdapter(Context c, ArrayList<Chat> mine){
        this.mContext=c;
        this.mineChats=mine;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        userKey=sharedPreferences.getString("key",null);

        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.chat_mine_rv_layout, parent, false);
                viewHolder = new MyViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.chat_rv_layout, parent, false);
                viewHolder = new OtherViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(userKey.equals(mineChats.get(position).mSender))
        {
            MyViewHolder viewHolder=(MyViewHolder)holder;
            viewHolder.chatView.setText(mineChats.get(position).mMessage);
            System.out.println(mineChats.get(position).mMessage);
        }
        else
        {
            OtherViewHolder otherViewHolder=(OtherViewHolder)holder;
            otherViewHolder.chatView.setText(mineChats.get(position).mMessage);


        }

    }

    @Override
    public int getItemCount() {
        if((mineChats!=null))
        {
            return (mineChats.size());
        }
        else
        {
        return 0;
    }}

    @Override
    public int getItemViewType(int position) {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        userKey=sharedPreferences.getString("key",null);

        if(userKey.equals(mineChats.get(position).mSender))
        {
            return VIEW_TYPE_ME;
        }
        else {
            return VIEW_TYPE_OTHER;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView chatView;
        public MyViewHolder(View itemView) {
            super(itemView);
            chatView=(TextView)itemView.findViewById(R.id.chat);
        }
    }
    public class OtherViewHolder extends RecyclerView.ViewHolder {
        TextView chatView;
        public OtherViewHolder(View itemView) {
            super(itemView);
            chatView=(TextView)itemView.findViewById(R.id.chat);
        }
    }
}
