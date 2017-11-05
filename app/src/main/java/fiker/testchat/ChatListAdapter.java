package fiker.testchat;

import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter{
    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mDataSnapshotList; //every time u receive data from Firebase in comes in this data type

    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            mDataSnapshotList.add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public ChatListAdapter(Activity activity, DatabaseReference ref, String name){
        mActivity = activity;
        mDatabaseReference = ref.child("room1").child("messages");
        mDatabaseReference.addChildEventListener(mListener);
        mDisplayName = name;
        mDataSnapshotList = new ArrayList<>();
    }

    //An inner class
    static class ViewHolder{ //Holds all the views in a single chat row
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {
        return mDataSnapshotList.size();
    }

    @Override
    //originally was generic Object, it expects you to change it to the type you're populating the list view with, and it also has a pos in the LV
    public InstantMessage getItem(int position) {
        DataSnapshot snapshot = mDataSnapshotList.get(position);
        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //if there's an existing message of the screen, populate the existing row
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_msg_row, parent, false); //store the chat_msg_row stuff in a convertView

            final ViewHolder holder = new ViewHolder(); //will hold everything that is in an individual message row
            holder.authorName = (TextView) convertView.findViewById(R.id.author);
            holder.body = (TextView) convertView.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams(); //just layout parameters to keep the layout looks

            convertView.setTag(holder); //temporarily store the current message in a convertView
        }

        final InstantMessage message = getItem(position); //get the message at the appropriate pos from Firebase
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        String author = message.getAuthor();
        holder.authorName.setText(author);

        String msg = message.getMessage();
        holder.body.setText(msg);

        return convertView;
    }

    public void cleanup(){
        mDatabaseReference.removeEventListener(mListener);
    }
}
