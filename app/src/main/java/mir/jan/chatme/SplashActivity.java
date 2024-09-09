package mir.jan.chatme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("ChatMe", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null);
        Intent intent;
        if (uid != null) {
            intent = new Intent(this,
                    ChatActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            intent = new Intent(this,
                    RegistrationActivity.class);
        }
        intent.putExtra("uid", uid);
        startActivity(intent);
        this.finish();
    }
}
