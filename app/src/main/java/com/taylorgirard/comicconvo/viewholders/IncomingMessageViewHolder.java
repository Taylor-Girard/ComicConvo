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

/**ViewHolder for incoming messages on the individual messaging page*/

public class IncomingMessageViewHolder extends MessageViewHolder{

    ImageView imageOther;
    TextView body;
    TextView name;
    Context context;

    public IncomingMessageViewHolder(@NonNull View itemView, Context context) {
        super(itemView, context);
        this.context = context;
        imageOther = (ImageView) itemView.findViewById(R.id.ivProfileOther);
        body = (TextView) itemView.findViewById(R.id.tvBody);
        name = (TextView) itemView.findViewById(R.id.tvName);
    }

    @Override
    public void bindMessage(Message message) {
        body.setText(message.getBody());
        ParseFile profilePic = null;
        try {
            name.setText(message.getSender().fetchIfNeeded().getUsername());
            profilePic = message.getSender().fetchIfNeeded().getParseFile("profilePic");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (profilePic != null) {
            Glide.with(context).load(profilePic.getUrl()).transform(new CircleCrop()).into(imageOther);
        }
    }

}
