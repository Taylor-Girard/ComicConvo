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
import com.taylorgirard.comicconvo.models.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int MESSAGE_OUTGOING = 123;
    private static final int MESSAGE_INCOMING = 321;

    private List<Message> messages;
    private Context context;
    private ParseUser match;
    private ParseUser user = ParseUser.getCurrentUser();

    public MessageAdapter(Context context, ParseUser match, List<Message> messages){
        this.messages = messages;
        this.match = match;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESSAGE_INCOMING){
            View contactView = inflater.inflate(R.layout.message_incoming, parent, false);
            return new IncomingMessageViewHolder(contactView);
        } else if (viewType == MESSAGE_OUTGOING){
            View contactView = inflater.inflate(R.layout.message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(contactView);
        } else{
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bindMessage(message);
    }

    @Override
    public int getItemViewType(int position) {
        if (isMe(position)){
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean isMe(int position){
        Message message = messages.get(position);
        return message.getSenderId() != null && message.getSenderId().equals(user.getObjectId());
    }

    public abstract class MessageViewHolder extends RecyclerView.ViewHolder{

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindMessage(Message message);
    }

    public class IncomingMessageViewHolder extends MessageViewHolder{

        ImageView imageOther;
        TextView body;
        TextView name;

        public IncomingMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageOther = (ImageView) itemView.findViewById(R.id.ivProfileOther);
            body = (TextView) itemView.findViewById(R.id.tvBody);
            name = (TextView) itemView.findViewById(R.id.tvName);
        }

        @Override
        void bindMessage(Message message) {
            body.setText(message.getBody());
            try {
                name.setText(message.getSender().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ParseFile profilePic = null;
            try {
                profilePic = message.getSender().fetchIfNeeded().getParseFile("profilePic");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (profilePic != null) {
                Glide.with(context).load(profilePic.getUrl()).transform(new CircleCrop()).into(imageOther);
            }
        }
    }

    public class OutgoingMessageViewHolder extends MessageViewHolder{

        ImageView imageMe;
        TextView body;

        public OutgoingMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMe = (ImageView) itemView.findViewById(R.id.ivProfileMe);
            body = (TextView) itemView.findViewById(R.id.tvBody);

        }

        @Override
        void bindMessage(Message message){
            ParseFile profilePic = null;
            try {
                profilePic = message.getSender().fetchIfNeeded().getParseFile("profilePic");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (profilePic != null) {
                Glide.with(context).load(profilePic.getUrl()).transform(new CircleCrop()).into(imageMe);
            }
            body.setText(message.getBody());
        }
    }
}
