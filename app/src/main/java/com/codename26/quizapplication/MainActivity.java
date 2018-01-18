package com.codename26.quizapplication;

import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String HIGHSCORE_EXTRA = "com.codename26.quizapplication.extra.HIGHSCORE";
    public static final String SCORE_EXTRA = "com.codename26.quizapplication.extra.SCORE";
    public static final String FILENAME = "file.txt";
    private ImageView imageViewTop;
    private ImageView imageViewBottom;
    private ImageView imageVS;
    private TextView mTextViewTitleTop;
    private TextView mTextViewTitleBottom;
    private TextView mTextViewCounterTop;
    private TextView mTextViewCounterBottom;
    private TextView mTextMonthCaptionTop;
    private TextView mTextMonthCaptionBottom;
    private TextView textScore;
    private AdView mAdView;
    private QuizObject quizObjectTop;
    private QuizObject quizObjectBottom;
    private Animation anim;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor c;
    private int currentScore = 0;
    private int highScore;
    private SharedPreferences sharedPref;
    private int rand;

    private enum ActiveImage {TOP, BOTTOM}


    ActiveImage activeImage;
    private Handler mHandler = new Handler();
    private InterstitialAd mInterstitialAd;
    final String LOG_TAG = "myLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get Highscore from SharedPreferences
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_highscore), this.MODE_PRIVATE);
        dbHelper = new DBHelper(this);
        initDB();
        initGameField();
        initAds();

    }

    //Get Writable DB or create new one and fill it with data
    private void initDB() {
        dbHelper.getReadableDatabase();
        quizObjectTop = dbHelper.readDB();
        quizObjectBottom = dbHelper.readDB();
       checkEndGame();
    }

    private void checkEndGame() {
        if (quizObjectTop == null || quizObjectBottom == null){
            initEndGame();
        }
    }
    //Init all views and first screen
    private void initGameField() {
        textScore = (TextView) findViewById(R.id.textScore);
        imageViewTop = (ImageView) findViewById(R.id.imageViewTop);
        imageViewTop.setOnClickListener(this);
        imageViewBottom = (ImageView) findViewById(R.id.imageViewBottom);
        imageViewBottom.setOnClickListener(this);
        mTextViewTitleBottom = (TextView) findViewById(R.id.textImageBottom);
        mTextViewTitleTop = (TextView) findViewById(R.id.textImageTop);
        mTextViewCounterTop = (TextView) findViewById(R.id.textCounterTop);
        mTextViewCounterBottom = (TextView) findViewById(R.id.textCounterBottom);
        mTextMonthCaptionBottom = (TextView) findViewById(R.id.textMonthCaptionBottom);
        mTextMonthCaptionTop = (TextView) findViewById(R.id.textMonthCaptionTop);
        imageVS = (ImageView) findViewById(R.id.imageViewVS);

        imageViewTop.setImageResource(quizObjectTop.getImageId());
        imageViewBottom.setImageResource(quizObjectBottom.getImageId());

        mTextViewTitleTop.setText(quizObjectTop.getTitle());
        mTextViewTitleBottom.setText(quizObjectBottom.getTitle());
        initAnim();

        mTextViewTitleTop.startAnimation(anim);
        mTextViewTitleBottom.startAnimation(anim);
        activeImage = ActiveImage.TOP;

        int highScore = sharedPref.getInt(getString(R.string.highscore), 0);
        TextView textHighScore = (TextView) findViewById(R.id.textHighScore);
        textHighScore.setText(String.valueOf(highScore));

        textScore.setText(String.valueOf(currentScore));


    }
    //update game field after each action
    private void updateGameField() {

        mHandler.postDelayed(new Runnable() {
            public void run() {
                mTextViewCounterBottom.setText("");
                mTextViewTitleBottom.setText("");
                mTextMonthCaptionBottom.setText("");
                mTextViewCounterTop.setText("");
                mTextMonthCaptionTop.setText("");
                mTextViewTitleTop.setText("");
            }
        }, 500);

        anim = AnimationUtils.loadAnimation(this, R.anim.scale_to_0);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageViewBottom.setImageResource(0);
                imageViewBottom.setImageResource(quizObjectBottom.getImageId());
                imageViewTop.setImageResource(0);
                imageViewTop.setImageResource(quizObjectTop.getImageId());

                anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_from_0_to_1);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_from_0_to_1);
                        mTextViewTitleBottom.setText(quizObjectBottom.getTitle());
                        mTextViewTitleBottom.startAnimation(anim);
                        mTextViewTitleTop.setText(quizObjectTop.getTitle());
                        mTextViewTitleBottom.startAnimation(anim);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageViewBottom.startAnimation(anim);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageViewBottom.startAnimation(anim);
        anim = AnimationUtils.loadAnimation(this, R.anim.scale_to_0);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageViewTop.setImageResource(0);
                imageViewTop.setImageResource(quizObjectTop.getImageId());

                anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_from_0_to_1);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_from_0_to_1);
                        mTextViewTitleTop.setText(quizObjectTop.getTitle());
                        mTextViewTitleTop.startAnimation(anim);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageViewTop.startAnimation(anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageViewTop.startAnimation(anim);

        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_to_0);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageVS.setImageResource(R.drawable.versus);
                Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_from_0_to_1);
                imageVS.startAnimation(anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageVS.startAnimation(anim);
        textScore.setText(String.valueOf(currentScore));

    }

    private void initAnim() {
        anim = AnimationUtils.loadAnimation(this, R.anim.scale_from_0_to_1);
    }

    private void initAds() {
        MobileAds.initialize(this, "ca-app-pub-3783355790760246~2854403172");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("D26F81323E64450FF84AB8B71E56E7DD")
                .build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3783355790760246/4521163476");
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("D26F81323E64450FF84AB8B71E56E7DD").build());
    }
//handle click on any of two images on game field
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewTop:
                if (quizObjectTop.getNumberOfSearchQueries() > quizObjectBottom.getNumberOfSearchQueries()) {
                    animationCorrect();
                } else {
                    animationWrong();
                }

                break;
            case R.id.imageViewBottom:
                if (quizObjectBottom.getNumberOfSearchQueries() > quizObjectTop.getNumberOfSearchQueries()) {
                    animationCorrect();
                } else animationWrong();
                break;
        }

    }

    private void animationCorrect() {
        Animation animCaption = AnimationUtils.loadAnimation(this, R.anim.scale);

        mTextMonthCaptionBottom.setText(R.string.month);
        mTextMonthCaptionBottom.startAnimation(animCaption);
        mTextMonthCaptionTop.setText(R.string.month);
        mTextMonthCaptionTop.startAnimation(animCaption);

        startCountAnimation();

        anim = AnimationUtils.loadAnimation(this, R.anim.scale_to_0);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageVS.setImageResource(R.drawable.correct);
                anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_from_0_to_1);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        currentScore++;

                        quizObjectTop = dbHelper.readDB();
                        quizObjectBottom = dbHelper.readDB();
                        checkEndGame();
                        updateGameField();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageVS.startAnimation(anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageVS.startAnimation(anim);
    }
//after wrong animation, we init. end game method
    private void animationWrong() {

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.scale_from_0_to_1);
        if (activeImage == ActiveImage.TOP) {
            mTextMonthCaptionBottom.setText(R.string.month);
            mTextMonthCaptionBottom.startAnimation(anim);
        } else {
            mTextMonthCaptionTop.setText(R.string.month);
            mTextMonthCaptionTop.startAnimation(anim);
        }
        startCountAnimation();
        anim = AnimationUtils.loadAnimation(this, R.anim.scale_to_0);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageVS.setImageResource(R.drawable.wrong);
                Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_from_0_to_1);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        initEndGame();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageVS.startAnimation(anim);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageVS.startAnimation(anim);
    }

    //We start counting animation, that takes 1 second to complete. After that we start scaling from 0 to 1 animation
    private void startCountAnimation() {

        ValueAnimator animator = ValueAnimator.ofInt(0, quizObjectBottom.getNumberOfSearchQueries());
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                mTextViewCounterBottom.setText(animation.getAnimatedValue().toString());
            }
        });
        anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_from_0_to_1);
        mTextViewCounterBottom.startAnimation(anim);
        animator.start();

        ValueAnimator animator1 = ValueAnimator.ofInt(0, quizObjectTop.getNumberOfSearchQueries());
        animator1.setDuration(1000);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation1) {
                mTextViewCounterTop.setText(animation1.getAnimatedValue().toString());
            }
        });
        anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_from_0_to_1);
        mTextViewCounterTop.startAnimation(anim);
        animator1.start();


    }

//fill DB with info, taken from text file in assets dir
//before game ends, we have to reset all shown cards in DB

//before game pauses, we have to save highscore
    private void saveHighScore() {
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_highscore), this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        highScore = sharedPref.getInt(getString(R.string.highscore), 0);
        if (highScore < currentScore) {
            editor.putInt(getString(R.string.highscore), currentScore);
            editor.commit();
            highScore = currentScore;
        }

    }

    @Override
    protected void onPause() {
        saveHighScore();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.resetWasShown();
    }
//if player is wrong, ad screen is shown, after which end game activity starts.
    private void initEndGame() {
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                saveHighScore();
                Intent intent = new Intent(MainActivity.this, WrongActivity.class);
                intent.putExtra(HIGHSCORE_EXTRA, highScore);
                intent.putExtra(SCORE_EXTRA, currentScore);
                startActivity(intent);
                finish();
            }

        });

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            saveHighScore();
            Intent intent = new Intent(MainActivity.this, WrongActivity.class);
            intent.putExtra(HIGHSCORE_EXTRA, highScore);
            intent.putExtra(SCORE_EXTRA, currentScore);
            startActivity(intent);
            finish();

        }
    }
}
