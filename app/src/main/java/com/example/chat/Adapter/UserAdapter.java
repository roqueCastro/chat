package com.example.chat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.activity.MessageActivity;
import com.example.chat.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private Boolean ischat;

    public UserAdapter(Context mContext, List<User> mUsers, Boolean ischat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final User user = mUsers.get(i);
        holder.username.setText(user.getUsername());

        if (user.getImageURL().equals("default")){
            holder.profile_name.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_name);
        }

        if (ischat){
            if (user.getStatus().equals("online")){
                holder.imgon.setVisibility(View.VISIBLE);
                holder.imgoff.setVisibility(View.GONE);
            }else {
                holder.imgoff.setVisibility(View.VISIBLE);
                holder.imgon.setVisibility(View.GONE);
            }
        }else {
            holder.imgoff.setVisibility(View.GONE);
            holder.imgon.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_name;
        private ImageView imgon, imgoff;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username_ui);
            profile_name = itemView.findViewById(R.id.profile_image_ui);
            imgon = itemView.findViewById(R.id.img_on);
            imgoff = itemView.findViewById(R.id.img_off);
        }
    }
}
