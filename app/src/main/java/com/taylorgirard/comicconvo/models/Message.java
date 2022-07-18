package com.taylorgirard.comicconvo.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**Parse class for a message to get its sender, receiver, and body*/

@ParseClassName("Message")
public class Message extends ParseObject {

    public Message(){
        //empty constructor
    }

    public ParseUser getSender(){
        return getParseUser("Sender");
    }

    public String getSenderId(){
        return getSender().getObjectId();
    }

    public ParseUser getReceiver(){
        return getParseUser("Receiver");
    }

    public String getReceiverId(){
        return getReceiver().getObjectId();
    }

    public String getBody(){
        return  getString("Body");
    }


}
