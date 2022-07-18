package com.taylorgirard.comicconvo.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.models.Message;

public class OutgoingMessageViewHolder extends MessageViewHolder{

    ImageView imageMe;
    TextView body;
    Context context;

    public OutgoingMessageViewHolder(@NonNull View itemView, Context context) {
        super(itemView, context);
        this.context = context;
        imageMe = (ImageView) itemView.findViewById(R.id.ivProfileMe);
        body = (TextView) itemView.findViewById(R.id.tvBody);

    }

    @Override
    public void bindMessage(Message message){
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
