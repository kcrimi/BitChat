package com.example.kevin.bitchat;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Date;
import java.util.logging.Handler;


public class ChatActivity extends ActionBarActivity implements View.OnClickListener, MessageDataSource.Listener{

    public static final String CONTACT_NUMBER = "Contact Number";
    public static final String TAG = "Chat Activity";


    private ArrayList<Message> mMessages;
    private MessageAdapter mAdapter;
    private String mRecipient;
    private ListView mListView;
    private Date mLastMessageDate = new Date();

    private android.os.Handler mHandler = new android.os.Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable called");
            MessageDataSource.fetchMessagesAfter(ContactDataSource.getCurrentUser().getPhoneNumber(),
                    mRecipient,
                    mLastMessageDate,
                    ChatActivity.this);
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRecipient = getIntent().getStringExtra(CONTACT_NUMBER);

        mMessages = new ArrayList<Message>();

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
        mAdapter.notifyDataSetChanged();
        MessageDataSource.sendMessage(message.getSender(), mRecipient, message.getText());
    }

    @Override
    public void onFetchedMessages(ArrayList<Message> messages) {
        mMessages.clear();
        addMessages(messages);
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void onAddMessages(ArrayList<Message> messages) {
        addMessages(messages);
    }

    private void addMessages(ArrayList<Message> messages){

        mMessages.addAll(messages);
        mAdapter.notifyDataSetChanged();
        if (mMessages.size() > 0){
            mListView.setSelection(mMessages.size() - 1);
            Message message = mMessages.get(mMessages.size() - 1);
            mLastMessageDate = message.getDate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
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

            int sdk = Build.VERSION.SDK_INT;
            if (message.getSender().equals(ContactDataSource.getCurrentUser().getPhoneNumber())){
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN){
                    nameView.setBackground(getDrawable(R.drawable.bubble_right_green));
                }else{
                    nameView.setBackgroundDrawable(getDrawable(R.drawable.bubble_right_green));
                }
                layoutParams.gravity = Gravity.RIGHT;
            }else{
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN){
                    nameView.setBackground(getDrawable(R.drawable.bubble_left_gray));
                }else{
                    nameView.setBackgroundDrawable(getDrawable(R.drawable.bubble_left_gray));
                }
                layoutParams.gravity = Gravity.LEFT;
            }

            nameView.setLayoutParams(layoutParams);

            return convertView;
        }

    }
}
