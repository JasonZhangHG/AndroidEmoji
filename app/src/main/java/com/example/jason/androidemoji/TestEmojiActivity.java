package com.example.jason.androidemoji;

import android.os.Bundle;
import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.support.v7.app.AppCompatActivity;

public class TestEmojiActivity extends AppCompatActivity {
    private EmojiAppCompatTextView tvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_emoji);
        tvTest = (EmojiAppCompatTextView) findViewById(R.id.tv_test);
        tvTest.setText("\uD83D\uDE04");
    }
}
