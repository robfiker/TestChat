package fiker.testchat;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainChatActivity extends AppCompatActivity {
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private Button mSendButton;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mChatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        setupDisplayName();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void setupDisplayName(){
        mDisplayName = "Anonymous";
    }

    private void sendMessage(){
        String input = mInputText.getText().toString();
        if(!input.equals("")){
            InstantMessage chat = new InstantMessage(input, mDisplayName);
            mDatabaseReference.child("room1").child("messages").push().setValue(chat);
            mInputText.setText("");
        }
    }

    public void onStart(){
        super.onStart();
        mChatListAdapter = new ChatListAdapter(this, mDatabaseReference, mDisplayName);
        mChatListView.setAdapter(mChatListAdapter);
    }

    @Override
    public void onStop(){
        super.onStop();
        mChatListAdapter.cleanup();
    }
}

