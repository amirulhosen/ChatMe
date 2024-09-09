package mir.jan.chatme;

import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView, userName;
    private Button Btn;
    private TextView loginLink;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        userName = findViewById(R.id.username);
        Btn = findViewById(R.id.btnregister);
        loginLink = findViewById(R.id.loginLink);
        progressbar = findViewById(R.id.progressbar);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent
                        = new Intent(RegistrationActivity.this,
                        LoginActivity.class);
                startActivity(intent);
            }
        });
        // Set on Click Listener on Registration button
        Btn.setOnClickListener(v -> {
            if (emailTextView.getText() != null && passwordTextView.getText() != null && userName.getText() != null)
                registerNewUser();
            else {

                // hide the progress bar
                progressbar.setVisibility(View.GONE);

                // Create the object of AlertDialog Builder class
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);

                // Set the message show for the Alert time
                builder.setMessage("Please enter valid email, password and username");

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
        });
    }

    private void registerNewUser() {

        // show the visibility of progress bar to show loading
        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // create new user or register new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName.getText().toString())
                                .build();
                        firebaseUser.updateProfile(profileUpdates);
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference usersRef = rootRef.child("Users/" + user.getUid());
                        usersRef.setValue(profileUpdates).isSuccessful();

                        // hide the progress bar
                        progressbar.setVisibility(View.GONE);

                        // if the user created intent to login activity
                        Intent intent
                                = new Intent(RegistrationActivity.this,
                                UserListActivity.class);
                        startActivity(intent);
                    } else {

                        // hide the progress bar
                        progressbar.setVisibility(View.GONE);

                        // Create the object of AlertDialog Builder class
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);

                        // Set the message show for the Alert time
                        builder.setMessage("This email is already registered, please login");

                        // Set Alert Title
                        builder.setTitle("Info");

                        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                        builder.setCancelable(false);

                        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            Intent intent
                                    = new Intent(this,
                                    LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });

                        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                        builder.setNegativeButton("No", (dialog, which) -> {
                            // If user click no then dialog box is canceled.
                            dialog.cancel();
                        });

                        // Create the Alert dialog
                        AlertDialog alertDialog = builder.create();
                        // Show the Alert Dialog box
                        alertDialog.show();
                    }
                });
    }
}

