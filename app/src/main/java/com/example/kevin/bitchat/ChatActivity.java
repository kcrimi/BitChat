package com.example.kevin.bitchat;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;


public class ChatActivity extends ActionBarActivity implements View.OnClickListener, MessageDataSource.Listener{

    public static final String CONTACT_NUMBER = "Contact Number";

    private ArrayList<Message> mMessages;
    private MessageAdapter mAdapter;
    private String mRecipient;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRecipient = getIntent().getStringExtra(CONTACT_NUMBER);

        mMessages = new ArrayList<Message>();
        mMessages.add(new Message("Hello", "16024787348"));

        mListView = (ListView)findViewById(R.id.messages_list);
        mAdapter = new MessageAdapter(mMessages);
        mListView.setAdapter(mAdapter);

        Button sendMessage= (Button)findViewById(R.id.send_message);
        sendMessage.setOnClickListener(this);

        MessageDataSource.fetchMessages(ContactDataSource.getCurrentUser().getPhoneNumber(),
                mRecipient,
                this);
    }

    @Override
    public void onClick(View v) {
        EditText newMessageView = (EditText)findViewById(R.id.new_message);
        String newMessage = newMessageView.getText().toString();
        newMessageView.setText("");
        Message message = new Message(newMessage, ContactDataSource.getCurrentUser().getPhoneNumber());
        mMessages.add(message);
        mAdapter.notifyDataSetChanged();
        MessageDataSource.sendMessage(message.getSender(), mRecipient, message.getText());
    }

    @Override
    public void onFetchedMessages(ArrayList<Message> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(mMessages.size() - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MessageAdapter extends ArrayAdapter<Message> {

        MessageAdapter(ArrayList<Message> messages){
            super(ChatActivity.this, R.layout.messages_list_item, R.id.message, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView =  super.getView(position, convertView, parent);
            Message message = getItem(position);

            TextView nameView = (TextView)convertView.findViewById(R.id.message);
            nameView.setText(message.getText());

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();

            if (message.getSender().equals(ContactDataSource.getCurrentUser().getPhoneNumber())){
                nameView.setBackground(getDrawable(R.drawable.bubble_right_green));
                layoutParams.gravity = Gravity.RIGHT;
            }else{
                nameView.setBackground(getDrawable(R.drawable.bubble_left_gray));
                layoutParams.gravity = Gravity.LEFT;
            }

            nameView.setLayoutParams(layoutParams);

            return convertView;
        }

    }
}
