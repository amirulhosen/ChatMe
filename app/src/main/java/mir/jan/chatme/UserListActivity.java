package mir.jan.chatme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity implements ChatAdapter.ItemClickListener {
    ChatAdapter adapter;
    ArrayList<User> chatUsers;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("Users");
        chatUsers = new ArrayList<>();
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    if (!ds.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                        User user = ds.getValue(User.class);
                        user.uid = ds.getKey();
                        if (user.displayName != null)
                            chatUsers.add(user);
                    }
                }
                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.chatListView);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new ChatAdapter(this, chatUsers);
                adapter.setClickListener(this);
                recyclerView.setAdapter(adapter);
            } else {
                Log.d("TAG", task.getException().getMessage()); //Don't ignore potential errors!
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this,
                ChatActivity.class);
        intent.putExtra("uid", chatUsers.get(position).uid);
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}


class User {
    public String email, mobile, displayName, online, photo, uid;
}
