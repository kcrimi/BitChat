package com.example.kevin.bitchat;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
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

    public static void fetchMessagesAfter(String sender, String recipient, Date after, final Listener listener){
        ParseQuery<ParseObject> mainQuery = messageQuery(sender, recipient);
        mainQuery.whereGreaterThan("createdAt",after);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ArrayList<Message> messages = new ArrayList<Message>();
                    for (ParseObject parseObject : list) {
                        Message message = new Message(parseObject.getString("text"), parseObject.getString("sender"));
                        message.setDate(parseObject.getCreatedAt());
                        messages.add(message);
                    }
                    listener.onAddMessages(messages);
                }
            }
        });;
    }

    public static void fetchMessages(String sender, String recipient, final Listener listener){
        ParseQuery<ParseObject> mainQuery = messageQuery(sender, recipient);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ArrayList<Message> messages = new ArrayList<Message>();
                    for (ParseObject parseObject : list) {
                        Message message = new Message(parseObject.getString("text"), parseObject.getString("sender"));
                        message.setDate(parseObject.getCreatedAt());
                        messages.add(message);
                    }
                    listener.onFetchedMessages(messages);
                }
            }
        });;
    }

    private static ParseQuery<ParseObject> messageQuery(String sender, String recipient){
        ParseQuery<ParseObject> querySent = ParseQuery.getQuery("Message");
        querySent.whereEqualTo("sender", sender);
        querySent.whereEqualTo("recipient", recipient);

        ParseQuery<ParseObject> queryReceived = ParseQuery.getQuery("Message");
        queryReceived.whereEqualTo("recipient", sender);
        queryReceived.whereEqualTo("sender", recipient);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(querySent);
        queries.add(queryReceived);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.orderByAscending("createdAt");

        return mainQuery;
    }

    public interface Listener{
        public void onFetchedMessages(ArrayList<Message> messages);
        public void onAddMessages(ArrayList<Message> messages);

    }
}
