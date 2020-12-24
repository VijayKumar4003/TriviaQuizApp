package com.infowithvijay.triviaquizapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {


    Button buttonA,buttonB,buttonC,buttonD;
    TextView questionText,txtTotalQuesText,timeText,coinText,txtCorrect,txtWrong;


    TriviaQuizHelper triviaQuizHelper;

    TriviaQuestion currentQuestion;

    List<TriviaQuestion> list;

    int qid = 1;

    int sizeofQuiz = 10; // total size of Quiz

    private final Handler handler = new Handler();
    private final Handler handler2 = new Handler();

    AnimationDrawable anim;

    CountDownTimer countDownTimer;

    int timeValue = 95;

    private TimerDialog timerDialog;

    int correct=0;
    int wrong = 0;
    int coins = 0;

    Animation correctAnsAnimation;
    Animation wrongAnsAnimation;

    int FLAG = -1;
    PlayAudio playAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionText = findViewById(R.id.txtTriviaQuestion);
        txtTotalQuesText = findViewById(R.id.txtTotalQuestion);
        buttonA = findViewById(R.id.buttonA);
        buttonB = findViewById(R.id.buttonB);
        buttonC = findViewById(R.id.buttonC);
        buttonD = findViewById(R.id.buttonD);

        timeText = findViewById(R.id.txtTimer);
        coinText = findViewById(R.id.txtCoin);

        txtCorrect = findViewById(R.id.txtCorrect);
        txtWrong = findViewById(R.id.txtWrong);

        timerDialog = new TimerDialog(this);

        correctAnsAnimation = AnimationUtils.loadAnimation(this,R.anim.correct_ans_animation);
        correctAnsAnimation.setRepeatCount(3);

        wrongAnsAnimation = AnimationUtils.loadAnimation(this,R.anim.wrong_ans_animation);
        wrongAnsAnimation.setRepeatCount(3);

        playAudio = new PlayAudio(this);

        triviaQuizHelper = new TriviaQuizHelper(this);

        triviaQuizHelper.getReadableDatabase();

        list = triviaQuizHelper.getAllQuestions();




        Collections.shuffle(list);

        currentQuestion = list.get(qid);

        txtTotalQuesText.setText(qid + "/" + sizeofQuiz);

        txtCorrect.setText(String.valueOf(correct));
        txtWrong.setText(String.valueOf(wrong));
        coinText.setText(String.valueOf(coins));


        countDownTimer = new CountDownTimer(320000,1200) {
            @Override
            public void onTick(long l) {

                timeText.setText(String.valueOf(timeValue));

                timeValue = timeValue - 1;  // time = time - 1;

                if (timeValue == -1){
                    disableOptions();
                    FLAG = 3;
                    playAudio.setAudioforEvent(FLAG);
                    timerDialog.timerDialog();
                }
            }

            @Override
            public void onFinish() {

              // timerDialog.timerDialog();

            }
        }.start();

        updateQueAnsOptions();


    }

    private void updateQueAnsOptions() {

        buttonA.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.buttonBG));
        buttonB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.buttonBG));
        buttonC.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.buttonBG));
        buttonD.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.buttonBG));

        questionText.setText(currentQuestion.getQuestion());
        buttonA.setText(currentQuestion.getOption1());
        buttonB.setText(currentQuestion.getOption2());
        buttonC.setText(currentQuestion.getOption3());
        buttonD.setText(currentQuestion.getOption4());

        countDownTimer.cancel();  //  the bug :(
        countDownTimer.start();

    }


    private void SetNewQuestion(){

        qid++;

        txtTotalQuesText.setText(qid + "/" + sizeofQuiz);

        currentQuestion = list.get(qid);

        enableOptions();

        updateQueAnsOptions();
    }


    private void correctTextUpdate(int correct){

        txtCorrect.setText(String.valueOf(correct));

    }

    private void wrongTextUpdate(int wrong){

        txtWrong.setText(String.valueOf(wrong));
    }

    private void coinsUpdateText(int coins){
        coinText.setText(String.valueOf(coins));
    }

    public void buttonA(View view) {

        countDownTimer.cancel();

        disableOptions();

        buttonA.setBackgroundResource(R.drawable.flash_background);
        anim = (AnimationDrawable) buttonA.getBackground();
        anim.start();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (currentQuestion.getOption1().equals(currentQuestion.getAnswerNr())){

                    buttonA.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                    buttonA.startAnimation(correctAnsAnimation);
                    correct++;
                    correctTextUpdate(correct);

                    FLAG = 1;
                    playAudio.setAudioforEvent(FLAG);

                    coins = coins + 10;
                    coinsUpdateText(coins);

                    Log.i("QuizInfo","Correct");

                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (qid != sizeofQuiz){

                                SetNewQuestion();

                            }else {

                                finalResult();
                            }


                        }
                    },2000);
                }else {

                    buttonA.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                    buttonA.startAnimation(wrongAnsAnimation);
                    wrong++;
                    wrongTextUpdate(wrong);
                    FLAG = 2;
                    playAudio.setAudioforEvent(FLAG);
                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if(currentQuestion.getOption2().equals(currentQuestion.getAnswerNr())){
                                buttonB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            }else if (currentQuestion.getOption3().equals(currentQuestion.getAnswerNr())){
                                buttonC.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            }else {
                                buttonD.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            }
                        }
                    },2000);

                    Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (qid != sizeofQuiz){

                                SetNewQuestion();

                            }else {
                                finalResult();
                            }
                        }
                    },3000);


                }


            }
        },5000);

    }

    public void buttonB(View view) {

        countDownTimer.cancel();
        disableOptions();
        buttonB.setBackgroundResource(R.drawable.flash_background);
        anim = (AnimationDrawable) buttonB.getBackground();
        anim.start();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (currentQuestion.getOption2().equals(currentQuestion.getAnswerNr())){

                    buttonB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                    buttonB.startAnimation(correctAnsAnimation);
                    correct++;
                    correctTextUpdate(correct);
                    FLAG = 1;
                    playAudio.setAudioforEvent(FLAG);
                    coins = coins + 10;
                    coinsUpdateText(coins);

                    Log.i("QuizInfo","Correct");

                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (qid != sizeofQuiz){

                                SetNewQuestion();

                            }else {
                                finalResult();
                            }


                        }
                    },2000);
                }else {

                    buttonB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                    buttonB.startAnimation(wrongAnsAnimation);
                    wrong++;
                    wrongTextUpdate(wrong);
                    FLAG = 2;
                    playAudio.setAudioforEvent(FLAG);
                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if(currentQuestion.getOption1().equals(currentQuestion.getAnswerNr())){
                                buttonA.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            }else if (currentQuestion.getOption3().equals(currentQuestion.getAnswerNr())){
                                buttonC.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            }else {
                                buttonD.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            }
                        }
                    },2000);

                    Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (qid != sizeofQuiz){

                                SetNewQuestion();

                            }else {
                                finalResult();
                            }
                        }
                    },3000);


                }


            }
        },5000);


    }

    public void buttonC(View view) {

        countDownTimer.cancel();
        disableOptions();
        buttonC.setBackgroundResource(R.drawable.flash_background);
        anim = (AnimationDrawable) buttonC.getBackground();
        anim.start();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (currentQuestion.getOption3().equals(currentQuestion.getAnswerNr())){

                    buttonC.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                    buttonC.startAnimation(correctAnsAnimation);
                    correct++;
                    correctTextUpdate(correct);
                    FLAG = 1;
                    playAudio.setAudioforEvent(FLAG);
                    coins = coins + 10;
                    coinsUpdateText(coins);

                    Log.i("QuizInfo","Correct");

                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (qid != sizeofQuiz){

                                SetNewQuestion();

                            }else {
                                finalResult();
                            }


                        }
                    },2000);
                }else {

                    buttonC.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                    buttonC.startAnimation(wrongAnsAnimation);
                    wrong++;
                    wrongTextUpdate(wrong);
                    FLAG = 2;
                    playAudio.setAudioforEvent(FLAG);
                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if(currentQuestion.getOption2().equals(currentQuestion.getAnswerNr())){
                                buttonB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            }else if (currentQuestion.getOption1().equals(currentQuestion.getAnswerNr())){
                                buttonA.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            }else {
                                buttonD.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            }
                        }
                    },2000);

                    Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (qid != sizeofQuiz){

                                SetNewQuestion();

                            }else {
                               finalResult();
                            }
                        }
                    },3000);


                }


            }
        },5000);

    }

    public void buttonD(View view) {

        countDownTimer.cancel();
        disableOptions();
        buttonD.setBackgroundResource(R.drawable.flash_background);
        anim = (AnimationDrawable) buttonD.getBackground();
        anim.start();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (currentQuestion.getOption4().equals(currentQuestion.getAnswerNr())){

                    buttonD.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                    buttonD.startAnimation(correctAnsAnimation);
                    wrong++;
                    wrongTextUpdate(wrong);
                    FLAG = 1;
                    playAudio.setAudioforEvent(FLAG);
                    correct++;
                    correctTextUpdate(correct);

                    coins = coins + 10;
                    coinsUpdateText(coins);

                    Log.i("QuizInfo","Correct");

                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (qid != sizeofQuiz){

                                SetNewQuestion();

                            }else {
                                finalResult();
                            }


                        }
                    },2000);
                }else {

                    buttonD.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                    buttonD.startAnimation(wrongAnsAnimation);

                    FLAG = 2;
                    playAudio.setAudioforEvent(FLAG);

                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if(currentQuestion.getOption2().equals(currentQuestion.getAnswerNr())){
                                buttonB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            }else if (currentQuestion.getOption3().equals(currentQuestion.getAnswerNr())){
                                buttonC.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            }else {
                                buttonA.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            }
                        }
                    },2000);

                    Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (qid != sizeofQuiz){

                                SetNewQuestion();

                            }else {
                                finalResult();
                            }
                        }
                    },3000);


                }


            }
        },5000);

    }


    @Override
    protected void onResume() {
        super.onResume();
        countDownTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        countDownTimer.cancel();
        finish();
    }

   private void disableOptions(){
        buttonA.setEnabled(false);
        buttonB.setEnabled(false);
        buttonC.setEnabled(false);
        buttonD.setEnabled(false);

   }

   private void enableOptions(){
       buttonA.setEnabled(true);
       buttonB.setEnabled(true);
       buttonC.setEnabled(true);
       buttonD.setEnabled(true);

   }


   private void finalResult(){

        Intent resultIntent = new Intent(QuizActivity.this,Result.class);
        resultIntent.putExtra(Constants.TOTAL_QUESTIONS,sizeofQuiz);
        resultIntent.putExtra(Constants.COINS,coins);
        resultIntent.putExtra(Constants.WRONG,wrong);
        resultIntent.putExtra(Constants.CORRECT,correct);
        startActivity(resultIntent);


   }






}
