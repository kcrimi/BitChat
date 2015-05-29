package com.example.kevin.bitchat;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by kevin on 5/29/15.
 */
public class MessageDataSource {

    public static void sendMessage(String sender, String recipient, String text){
        ParseObject message = new ParseObject("Message");
        message.put("sender",sender);
        message.put("recipient",recipient);
        message.put("text", text);
        message.saveInBackground();
    }
}
