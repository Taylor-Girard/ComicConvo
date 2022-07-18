package com.taylorgirard.comicconvo.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.taylorgirard.comicconvo.activities.IndividualMessageActivity;
import com.taylorgirard.comicconvo.models.Message;

import java.util.List;

/**Adapter that binds message objects to the list of previous conversations on the messages page*/

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivMatchPhoto;
        TextView tvMatchName;
        TextView tvMessageBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMatchPhoto = itemView.findViewById(R.id.ivMatchPhoto);
            tvMatchName = itemView.findViewById(R.id.tvMatchName);
            tvMessageBody = itemView.findViewById(R.id.tvMessageBody);
            itemView.setOnClickListener(this);
        }

        public void bind(Message message){
            ParseUser match;
            Boolean isUser = isUserMessage(message);
            match = (isUser) ? message.getReceiver() : message.getSender();
            try {
                tvMatchName.setText(match.fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvMessageBody.setText((isUser) ? "You: " + message.getBody() :
                            match.getUsername() + ": " + message.getBody());
            ParseFile profile = match.getParseFile("profilePic");
            Glide.with(context).load(profile.getUrl()).transform(new CircleCrop()).into(ivMatchPhoto);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ParseUser match;

            if (position != RecyclerView.NO_POSITION){
                Message message = messages.get(position);
                Boolean isUser = isUserMessage(message);
                if (!isUser){
                    match = message.getSender();
                } else {
                    match = message.getReceiver();
                }
                //Create intent to go to the individual message activity
                Intent intent = new Intent(context, IndividualMessageActivity.class);
                intent.putExtra("Match", match);
                context.startActivity(intent);

            }
        }

        public boolean isUserMessage(Message message){
            String senderId = message.getSenderId();
            String userId = ParseUser.getCurrentUser().getObjectId();

            return senderId.equals(userId);
        }
    }
}
