package mir.jan.chatme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private Button Btn;
    private AlertDialog progressDialog;
    SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // taking instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        progressDialog = ProgressDialog.getAlertDialog(this);
        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        Btn = findViewById(R.id.login);

        // Set on Click Listener on Sign-in button
        Btn.setOnClickListener(v -> {
            if (!emailTextView.getText().toString().trim().isBlank() && !passwordTextView.getText().toString().trim().isBlank())
                loginUserAccount();
            else {
// hide the progress bar
                progressDialog.dismiss();

                // Create the object of AlertDialog Builder class
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                // Set the message show for the Alert time
                builder.setMessage("Please enter valid email and password");

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

    private void loginUserAccount() {

        // show the visibility of progress bar to show loading
        progressDialog.show();
        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // validations for input email and password
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

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),
                                                "Login successful!!",
                                                Toast.LENGTH_LONG)
                                        .show();

                                // hide the progress bar
                                progressDialog.dismiss();

                                // if sign-in is successful
                                // intent to home activity

                                sharedPreferences = getSharedPreferences("ChatMe",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("uid", mAuth.getUid());
                                editor.apply();

                                Intent intent
                                        = new Intent(LoginActivity.this,
                                        InviteActivity.class);
                                intent.putExtra("uid",mAuth.getUid());
                                startActivity(intent);
                            } else {

                                // sign-in failed
                                Toast.makeText(getApplicationContext(),
                                                "Login failed!!",
                                                Toast.LENGTH_LONG)
                                        .show();

                                // hide the progress bar
                                progressDialog.dismiss();
                            }
                        });
    }
}

