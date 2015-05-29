package com.example.kevin.bitchat;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

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

    public static void fetchMessages(String sender, String recipient, final Listener listener){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.whereEqualTo("sender", sender);
        query.whereEqualTo("recipient", recipient);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ArrayList<Message> messages = new ArrayList<Message>();
                for (ParseObject parseObject: list){
                    Message message = new Message((String)parseObject.get("text"),(String)parseObject.get("sender"));
                    messages.add(message);
                }
                listener.onFetchedMessages(messages);
            }
        });;
    }

    public interface Listener{
        public void onFetchedMessages(ArrayList<Message> messages);
    }
}
