package com.example.jason.androidemoji;

import android.os.Bundle;
import android.support.text.emoji.widget.EmojiAppCompatEditText;
import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.jason.androidemoji.adapter.EmojiVpAdapter;
import com.example.jason.androidemoji.utils.EmotionKeyboardSwitchHelper;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.tv_info) EmojiAppCompatTextView mInfoTv;
    @BindView(R.id.btn_emoji) Button mEmojiBtn;
    @BindView(R.id.edt_msg) EmojiAppCompatEditText mMsgEdt;
    @BindView(R.id.btn_send) Button mSendBtn;
    @BindView(R.id.vp_emoji) ViewPager mEmojiVp;
    @BindView(R.id.ll_point) LinearLayout mVpPointLl;
    @BindView(R.id.fl_emoji) FrameLayout mEmojiFl;

    private EmotionKeyboardSwitchHelper mEmotionKeyboardSwitchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mEmotionKeyboardSwitchHelper = EmotionKeyboardSwitchHelper.with(this);
        initView();
        initListener();
    }

    private void initListener() {
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInfoTv.append(mMsgEdt.getText() + "\r\n");
                mMsgEdt.setText("");
            }
        });
        mEmotionKeyboardSwitchHelper.bind(mInfoTv, mMsgEdt, mEmojiBtn, mEmojiFl);
    }

    private void initView() {
        initViewPager();
    }

    /**
     * 设置ViewPager表情
     */
    private void initViewPager() {
        EmojiVpAdapter adapter = new EmojiVpAdapter(this);
        mEmojiVp.setAdapter(adapter);
        //表情点击监听
        adapter.setOnEmojiClickListener(new EmojiVpAdapter.OnEmojiClickListener() {
            @Override
            public void onClick(String emoji) {
                if ("del".equals(emoji)) {
                    //表示点击的是删除按钮
                    KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL);
                    mMsgEdt.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                } else {
                    mMsgEdt.append(emoji);
                }
            }
        });
        mEmojiVp.setCurrentItem(0);
        //关联指示器点
        adapter.setupWithPagerPoint(mEmojiVp, mVpPointLl);
    }


    @Override
    public void onBackPressed() {
        if (mEmotionKeyboardSwitchHelper.onBackPress()) {
            super.onBackPressed();
        }
    }
}
