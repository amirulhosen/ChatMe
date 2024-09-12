package mir.jan.chatme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InviteActivity extends AppCompatActivity {
    TextView idView;
    TextInputEditText friendId;
    AlertDialog progressDialog;
    Button search;
    FirebaseAuth firebaseAuth;
    Boolean isuserFoud = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        idView = findViewById(R.id.idTextView);
        friendId = findViewById(R.id.friendId);
        search = findViewById(R.id.search);
        progressDialog = ProgressDialog.getAlertDialog(InviteActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();
        String myId = firebaseAuth.getCurrentUser().getUid();
        idView.setText(String.format(myId.substring(myId.length() - 6, myId.length())));

        search.setOnClickListener(view -> {
            progressDialog.show();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference usersRef = rootRef.child("Users");
            isuserFoud = false;
            usersRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        if (ds.getKey().toLowerCase().contains(friendId.getText().toString().trim().toLowerCase())) {
                            isuserFoud = true;
                            Intent intent = new Intent(InviteActivity.this,
                                    ChatActivity.class);
                            intent.putExtra("uid", ds.getKey());
                            startActivity(intent);
                            break;
                        }
                    }

                    if (!isuserFoud) {
//                        AlertDialog alertDialog = DialogManager.buildDialog(this,"Sorry, no user found with this ID","Error","Ok", null);
//                        alertDialog.show();
//                        alertDialog.
                        AlertDialog.Builder builder = new AlertDialog.Builder(InviteActivity.this);

                        // Set the message show for the Alert time
                        builder.setMessage("Sorry, no user found with this ID");

                        // Set Alert Title
                        builder.setTitle("Error");

                        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                        builder.setCancelable(false);

                        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                        builder.setPositiveButton("Ok", (dialog, which) -> {
                            dialog.cancel();
                        });

                        // Create the Alert dialog
                        AlertDialog alertDialog = builder.create();
                        // Show the Alert Dialog box
                        alertDialog.show();
                    }
                }
            });
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}