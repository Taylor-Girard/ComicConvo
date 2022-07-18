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
import com.taylorgirard.comicconvo.viewholders.IncomingMessageViewHolder;
import com.taylorgirard.comicconvo.viewholders.MessageViewHolder;
import com.taylorgirard.comicconvo.viewholders.OutgoingMessageViewHolder;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

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

        View contactView;

        switch(viewType){
            case MESSAGE_INCOMING:
                contactView = inflater.inflate(R.layout.message_incoming, parent, false);
                return new IncomingMessageViewHolder(contactView, context);
            case MESSAGE_OUTGOING:
                contactView = inflater.inflate(R.layout.message_outgoing, parent, false);
                return new OutgoingMessageViewHolder(contactView, context);
            default:
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

}
