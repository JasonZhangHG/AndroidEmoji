package com.example.jason.androidemoji.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

public class EmotionKeyboardSwitchHelper {
    private static final String TAG = "EmotionKeyboardSwitchHelper";
    private static final String KEY_INPUT_HEIGHT = "key_input_height";
    private static final String SP_EMOJI_KEYBORD = "sp_emoji_keybord";
    private Activity mContext;
    private InputMethodManager mInputManager;
    private SharedPreferences mSp;
    private View mContentView;
    private View mEditView;
    private View mEmotionLayout;
    private Handler mHandler = new Handler();

    private EmotionKeyboardSwitchHelper(Activity ctx) {
        this.mContext = ctx;
        this.mInputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        this.mSp = ctx.getSharedPreferences(SP_EMOJI_KEYBORD, Context.MODE_PRIVATE);
        //主窗口总是会被调整大小，从而保证软键盘显示空间
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public static EmotionKeyboardSwitchHelper with(Activity ctx) {
        return new EmotionKeyboardSwitchHelper(ctx);
    }

    /**
     * 绑定相关控件
     *
     * @param contentView 内容区域,直接父节点必须要是LinearLayout
     * @param editView    编辑框,即EditText
     * @param emojiBtn    打开表情的按钮
     * @param emojiLayout 表情面板
     */
    public void bind(View contentView, View editView, View emojiBtn, View emojiLayout) {
        if (null == contentView || null == editView || null == emojiBtn || null == emojiLayout) {
            throw new IllegalArgumentException("param should not be null");
        }
        if (!(contentView.getParent() instanceof LinearLayout)) {
            throw new IllegalArgumentException("contentView's directly parent node must be LinearLayout");
        }
        this.mContentView = contentView;
        this.mEditView = editView;
        this.mEmotionLayout = emojiLayout;

        //输入框点击监听
        mEditView.requestFocus();
        mEditView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mEmotionLayout.isShown()) {
                        lockContentHeight();//锁定内容高度
                        hideEmotionLayout();//隐藏表情布局，显示软件盘
                        //软件盘显示后，释放内容高度
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                unlockContentHeightDelayed();
                            }
                        }, 200L);
                    }
                }
                return false;
            }
        });
        //表情按钮点击监听
        emojiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout.isShown()) {
                    lockContentHeight();//锁定内容高度
                    hideEmotionLayout();//隐藏表情布局，显示软件盘
                    unlockContentHeightDelayed();//软件盘显示后，释放内容高度
                } else {
                    if (isSoftInputShown()) {
                        lockContentHeight();//锁定内容高度
                        showEmotionLayout();//显示表情布局,隐藏软键盘
                        unlockContentHeightDelayed();
                    } else {
                        showEmotionLayout();//两者都没显示，直接显示表情布局
                    }
                }
            }
        });

    }

    /**
     * Activity按返回键的时候调用
     */
    public boolean onBackPress() {
        hideSoftInput();
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            return false;
        }
        return true;

    }

    /**
     * 锁定内容高度，防止跳闪
     */
    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    /**
     * 释放被锁定的内容高度
     */
    private void unlockContentHeightDelayed() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200);
    }

    /**
     * 显示表情面板
     */
    private void showEmotionLayout() {
        int softInputHeight = getSoftInputHeight();
        if (softInputHeight <= 0) {
            softInputHeight = mSp.getInt(KEY_INPUT_HEIGHT, 450);
        }
        hideSoftInput();//隐藏软键盘
        if (softInputHeight > 0) {
            mEmotionLayout.getLayoutParams().height = softInputHeight;
        }
        mEmotionLayout.setVisibility(View.VISIBLE);//显示表情
    }

    /**
     * 隐藏表情布局
     */
    private void hideEmotionLayout() {
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            showSoftInput();//显示软键盘
        }
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void showSoftInput() {
        mEditView.requestFocus();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditView, 0);
            }
        }, 200);
    }

    /**
     * 隐藏软件盘
     */
    private void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditView.getWindowToken(), 0);
    }

    /**
     * 是否显示软件盘
     *
     * @return
     */
    private boolean isSoftInputShown() {
        return getSoftInputHeight() > 0;
    }

    /**
     * 获取软件盘的高度
     *
     * @return
     */
    private int getSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = mContext.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;//r.bottom是从屏幕顶端开始算的,因此已经包含了状态栏高度
        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getNavigationBarHeight();
        }
        Log.d(TAG, "getSoftInputHeight:" + softInputHeight);
        //存一份到本地
        if (softInputHeight > 0) {
            mSp.edit().putInt(KEY_INPUT_HEIGHT, softInputHeight).commit();
        }
        return softInputHeight;
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    private int getNavigationBarHeight() {
        Resources resources = mContext.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 获取软键盘高度
     *
     * @return
     */
    public int getKeyBoardHeight() {
        return mSp.getInt(KEY_INPUT_HEIGHT, 400);
    }
}
