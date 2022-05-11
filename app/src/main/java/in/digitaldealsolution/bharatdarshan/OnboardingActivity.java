package in.digitaldealsolution.bharatdarshan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import in.digitaldealsolution.bharatdarshan.Adapter.OnboardingAdapter;
import in.digitaldealsolution.bharatdarshan.Fragment.Onboarding2Fragment;
import in.digitaldealsolution.bharatdarshan.Fragment.Onboarding1Fragment;
import in.digitaldealsolution.bharatdarshan.Fragment.Onboarding3Fragment;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private OnboardingAdapter adapter;
    private FloatingActionButton fabBack, fabNext;
    private Button finish;
    private TextView onboardingNum, skipTxt;
    private  Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        viewPager2 = findViewById(R.id.onboarding_viewPager);
        intent = new Intent(OnboardingActivity.this, MainActivity.class);
        adapter = new OnboardingAdapter(getSupportFragmentManager(), getLifecycle());
        fabBack = findViewById(R.id.fab_back);
        fabNext = findViewById(R.id.fab_next);
        finish = findViewById(R.id.finish_btn);

        onboardingNum = findViewById(R.id.onboarding_Num);
        skipTxt = findViewById(R.id.skip_txt);

//        add Fragments
        adapter.addFragment( new Onboarding1Fragment());
        adapter.addFragment( new Onboarding2Fragment());
        adapter.addFragment( new Onboarding3Fragment());

        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        viewPager2.setAdapter(adapter);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                onboardingNum.setText(position+1 + "/3");
                if(position == 0) {
                    fabBack.hide();
                }
                else{
                    fabBack.show();
                }

                if (position==2){
                    fabNext.hide();
                    finish.setVisibility(View.VISIBLE);
                }
                else{
                    finish.setVisibility(View.GONE);
                    fabNext.show();
                }

            }
        });

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position  = viewPager2.getCurrentItem();
                if(position>0){
                    viewPager2.setCurrentItem(position-1);
                }
            }
        });

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewPager2.getCurrentItem();
                if(position<3);{
                    viewPager2.setCurrentItem(position+1);
                }
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(intent);
                finish();
            }
        });

        skipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
                finish();
            }
        });
    }
}