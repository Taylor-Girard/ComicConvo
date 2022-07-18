package com.taylorgirard.comicconvo.viewholders;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taylorgirard.comicconvo.models.Message;

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

    public MessageViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
    }

    public abstract void bindMessage(Message message);

}
