package in.digitaldealsolution.bharatdarshan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIME_OUT = 2000;
    private SharedPreferences mSahredPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(() -> {

            mSahredPref = getSharedPreferences("SharedPref", MODE_PRIVATE);
            boolean isFirstTime = mSahredPref.getBoolean("firstTime", true);

            Intent intent;
            if(isFirstTime){
                intent = new Intent(SplashScreen.this, OnboardingActivity.class);
                mSahredPref.edit().putBoolean("firstTime", false).commit();
            }
            else{
                intent = new Intent(SplashScreen.this, MainActivity.class);
            }
            startActivity(intent);
            finish();

        },SPLASH_SCREEN_TIME_OUT);
    }
}