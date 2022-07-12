package com.taylorgirard.comicconvo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.models.Comic;
import com.taylorgirard.comicconvo.models.Message;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder>{

    Context context;
    List<Message> messages;

    public MessageListAdapter(Context context, List<Message> messages){
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View messageView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageListAdapter.ViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivMatchPhoto;
        TextView tvMatchName;
        TextView tvMessageBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMatchPhoto = itemView.findViewById(R.id.ivMatchPhoto);
            tvMatchName = itemView.findViewById(R.id.tvMatchName);
            tvMessageBody = itemView.findViewById(R.id.tvMessageBody);
        }

        public void bind(Message message){
            ParseUser match;
            Boolean isUser;
            String senderId = message.getSenderId();
            String userId = ParseUser.getCurrentUser().getObjectId();
            if (!senderId.equals(userId)){
                match = message.getSender();
                isUser = false;
            } else {
                match = message.getReceiver();
                isUser = true;
            }
            try {
                tvMatchName.setText(match.fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (isUser){
                tvMessageBody.setText("You: " + message.getBody());
            } else{
                tvMessageBody.setText(match.getUsername() + ": " + message.getBody());
            }
            ParseFile profile = match.getParseFile("profilePic");
            Glide.with(context).load(profile.getUrl()).transform(new CircleCrop()).into(ivMatchPhoto);
        }
    }
}
