package mir.jan.chatme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.text.format.DateFormat;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView profile, block;
    TextView name, userstatus;
    EditText msg;
    ImageButton send;
    FirebaseAuth firebaseAuth;
    String uid, myuid, image;
    ValueEventListener valueEventListener;
    List<ModelChat> chatList;
    AdapterChat adapterChat;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;
    SharedPreferences sharedPreferences;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firebaseAuth = FirebaseAuth.getInstance();
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.chat_bg));
        // initialise the text views and layouts
        profile = findViewById(R.id.profiletv);
        name = findViewById(R.id.nameptv);
        userstatus = findViewById(R.id.onlinetv);
        msg = findViewById(R.id.messaget);
        send = findViewById(R.id.sendmsg);
        block = findViewById(R.id.block);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.chatrecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        uid = getIntent().getStringExtra("uid");
        // getting uid of another user using intent
        firebaseDatabase = FirebaseDatabase.getInstance();

        checkUserStatus();
        users = firebaseDatabase.getReference();
        send.setOnClickListener(v -> {
            notify = true;
            String message = msg.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {//if empty
                Toast.makeText(ChatActivity.this, "Please Write Something Here", Toast.LENGTH_LONG).show();
            } else {
                sendmessage(message);
            }
            msg.setText("");
        });

        Query userquery = users.child("Users").child(uid);
        userquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // retrieve user data
                String nameh = "" + dataSnapshot.child("displayName").getValue();
                name.setText(nameh);
                image = "" + dataSnapshot.child("image").getValue();
                String onlinestatus = "" + dataSnapshot.child("onlineStatus").getValue();
                String typingto = "" + dataSnapshot.child("typingTo").getValue();
                if (typingto.equals(myuid)) {// if user is typing to my chat
                    userstatus.setText("Typing....");// type status as typing
                } else {
                    if (onlinestatus.equals("online")) {
                        userstatus.setText(onlinestatus);
                    } else {
                        try {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(Long.parseLong(onlinestatus));
                            String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                            userstatus.setText("Last Seen:" + timedate);
                        }catch (Exception ex){
                            Log.e("",ex.toString());
                        }
                    }
                }
                name.setText(nameh);
                try {
                    Glide.with(ChatActivity.this).load(image).placeholder(R.drawable.boy).into(profile);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        readMessages();
    }


    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
    }

    @Override
    protected void onResume() {
//        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void checkOnlineStatus(String status) {
        if (myuid != null) {
            // check online status
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users").child(myuid);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("onlineStatus", status);
            dbref.updateChildren(hashMap);
        }
    }

    private void checkTypingStatus(String typing) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users").child(myuid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        dbref.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                checkUserStatus();
                checkOnlineStatus("online");
            }
        });
        super.onStart();
    }

    private void readMessages() {
        // show message after retrieving data
        chatList = new ArrayList<>();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Chats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelChat modelChat = dataSnapshot1.getValue(ModelChat.class);
                    if (modelChat.getSender().equals(myuid) &&
                            modelChat.getReceiver().equals(uid) ||
                            modelChat.getReceiver().equals(myuid)
                                    && modelChat.getSender().equals(uid)) {
                        chatList.add(modelChat); // add the chat in chatlist
                    }
                    adapterChat = new AdapterChat(ChatActivity.this, chatList, image);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                    recyclerView.scrollToPosition(chatList.size()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendmessage(final String message) {
        // creating a reference to store data in firebase
        // We will be storing data using current time in "Chatlist"
        // and we are pushing data using unique id in "Chats"
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myuid);
        hashMap.put("receiver", uid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("dilihat", false);
        hashMap.put("type", "text");
        databaseReference.child("Chats").push().setValue(hashMap);
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("ChatList").child(uid).child(myuid);
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ref1.child("id").setValue(myuid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ChatList").child(myuid).child(uid);
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    ref2.child("id").setValue(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            myuid = user.getUid();

            sharedPreferences = getSharedPreferences("ChatMe",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("uid", myuid);
            editor.apply();
        }
    }
}
