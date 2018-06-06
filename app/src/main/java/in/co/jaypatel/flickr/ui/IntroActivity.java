package in.co.jaypatel.flickr.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import in.co.jaypatel.flickr.MainActivity;
import in.co.jaypatel.flickr.R;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            settings.edit().putBoolean("my_first_time", false).apply();
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        int[] images = new int[]{R.drawable.o1, R.drawable.o2, R.drawable.o3};

        addSlide(AppIntroFragment.newInstance("Welcome", null, "Flickr Feed show the latest feeds of Flickr.", null, images[0], Color.parseColor("#FFFFFF"), Color.parseColor("#3b3b3b"), Color.parseColor("#3b3b3b")));
        addSlide(AppIntroFragment.newInstance("Single tap on image", null, "By taping on image you will get to see the description of an image.", null,  images[1], Color.parseColor("#FFFFFF"), Color.parseColor("#3b3b3b"), Color.parseColor("#3b3b3b")));
        addSlide(AppIntroFragment.newInstance("Advance Feature", null, "By tapping once on the description popup, you can show the whole image in full screen mode. From there you can share, view, zoom, or slide to another image.", null, images[2], Color.parseColor("#FFFFFF"), Color.parseColor("#3b3b3b"), Color.parseColor("#3b3b3b")));

        setBarColor(Color.parseColor("#FFFFFF"));
        setSeparatorColor(Color.parseColor("#ebebeb"));

        setNextArrowColor(Color.parseColor("#000000"));
        setColorSkipButton(Color.parseColor("#000000"));
        setColorDoneText(Color.parseColor("#3b3b3b"));
        setIndicatorColor(Color.parseColor("#ebebeb"), Color.parseColor("#000000"));
        showSkipButton(true);
        setProgressButtonEnabled(true);

        setFlowAnimation();

        setVibrate(true);
        setVibrateIntensity(30);

    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        loadMainActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        loadMainActivity();
    }

    @Override
    public void onSkipPressed() {
        super.onSkipPressed();
    }

}

