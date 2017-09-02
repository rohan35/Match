package com.raydevelopers.sony.match.utils;

        import android.content.Context;
        import android.content.Intent;
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

        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.raydevelopers.sony.match.R;
        import com.raydevelopers.sony.match.model.Interest;
        import com.raydevelopers.sony.match.model.User;
        import com.squareup.picasso.Picasso;

        import java.util.ArrayList;
        import java.util.HashMap;

/**
 * Created by SONY on 24-08-2017.
 */

public class ReceivedRecylerViewAdapter extends RecyclerView.Adapter<ReceivedRecylerViewAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<User> mUserDetails=new ArrayList<>();
    private String mRecievedUsersKey;
    public ReceivedRecylerViewAdapter(Context c,ArrayList<User> details,String key)
    {
        this.mContext=c;
        this.mUserDetails=details;


    }
    @Override
    public ReceivedRecylerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.received_rv_layout,parent,false);
        MyViewHolder viewHolder=new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ReceivedRecylerViewAdapter.MyViewHolder holder, final int position) {
        Picasso.with(mContext).load(mUserDetails.get(position).mProfilePic).into(holder.user_image);
        holder.user_name.setText(mUserDetails.get(position).mName);
        final DatabaseReference ref= FirebaseDatabase.getInstance().getReference("interest");
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        final String senderKey=sharedPreferences.getString("key",null);
        holder.accept.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
                Interest interest=new Interest(true);
                ref.child(mUserDetails.get(position).mkey+"_"+senderKey).setValue(interest);
               holder.accept.setVisibility(View.GONE);
               holder.decline.setVisibility(View.GONE);
               holder.chat.setVisibility(View.VISIBLE);

            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext,ChatHandler.class);
                i.putExtra("receiver_key",mUserDetails.get(position).mkey);
                mContext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mUserDetails.size()>0)
            return mUserDetails.size();
        else
            return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView user_image;
        TextView user_name;
        Button accept,decline,chat;
        public MyViewHolder(View itemView) {
            super(itemView);
            user_image=(ImageView)itemView.findViewById(R.id.interested_image);
            user_name=(TextView)itemView.findViewById(R.id.interested_name);
            accept=(Button)itemView.findViewById(R.id.accept);
            decline=(Button)itemView.findViewById(R.id.decline);
            chat=(Button)itemView.findViewById(R.id.chat_from_received);
        }
    }
}
