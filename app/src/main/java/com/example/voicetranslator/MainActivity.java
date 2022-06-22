package com.example.voicetranslator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int SPEECH_REQUEST_CODE = 0;
    private TextView tv;

    FirebaseTranslatorOptions options;
    FirebaseTranslator translator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       options = new FirebaseTranslatorOptions.Builder()
               // specifying our source language.
               .setSourceLanguage(FirebaseTranslateLanguage.HE)
               //  displaying our target language.
               .setTargetLanguage(FirebaseTranslateLanguage.EN)
               // after that we are building our options.
               .build();

        FirebaseApp.initializeApp(this);
        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        tv = findViewById(R.id.tv_translate);
        ImageButton mic = findViewById(R.id.mic);
        mic.setOnClickListener(v -> displaySpeechRecognizer());
    }


    // Connects the app to the phone's microphone through the background
    // And returns the information from the microphone to {@onActivityResult}
    public void displaySpeechRecognizer(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }


    /**
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data  An Intent, which can return result data to the caller
     *
     * The action accepts the sentence said into the microphone and introduces it to the user
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK)
        {
            List<String>result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            String text = result.get(0);
            translateModel(text);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void translateModel(String text){
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();

        // download our modal.
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(unused -> translateText(text));
    }

    public void translateText(String text){
        translator.translate(text).addOnSuccessListener(s -> tv.setText(s));
    }
}