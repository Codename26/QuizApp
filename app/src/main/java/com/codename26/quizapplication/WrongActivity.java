package com.codename26.quizapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class WrongActivity extends AppCompatActivity {
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong);

        initAds();

        TextView textViewHighscore = (TextView) findViewById(R.id.textViewHighscore);
        Intent intent = getIntent();
        int highScore = intent.getIntExtra(MainActivity.HIGHSCORE_EXTRA, 0);
        textViewHighscore.setText(String.valueOf(highScore));

        TextView textViewScore = (TextView) findViewById(R.id.textViewScore);
        int score = intent.getIntExtra(MainActivity.SCORE_EXTRA, 0);
        textViewScore.setText(String.valueOf(score));

        TextView textViewScoreTitle = (TextView) findViewById(R.id.textViewScoreTitle);
        textViewScoreTitle.setText(R.string.score);
        TextView textViewHighscoreTitle = (TextView) findViewById(R.id.textViewHighscoreTitle);
        textViewHighscoreTitle.setText(R.string.highscore);
    }

    public void restartListener(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initAds() {
        MobileAds.initialize(this, "ca-app-pub-3783355790760246~2854403172");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("D26F81323E64450FF84AB8B71E56E7DD")
                .build();
        mAdView.loadAd(adRequest);
    }
}
