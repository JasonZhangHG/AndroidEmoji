package com.example.jason.androidemoji.adapter;

import android.content.Context;
import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.example.jason.androidemoji.R;

public class EmojiGvAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mEmojis;

    public EmojiGvAdapter(Context context, String[] eachPageEmojis) {
        this.mContext = context;
        this.mEmojis = eachPageEmojis;
    }

    @Override
    public int getCount() {
        return null == mEmojis ? 0 : mEmojis.length;
    }

    @Override
    public String getItem(int position) {
        return null == mEmojis ? "" : mEmojis[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_emoji, null);
            holder.emojiTv = (EmojiAppCompatTextView) convertView.findViewById(R.id.tv_emoji);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 27) {
            //第28个显示删除按钮
            holder.emojiTv.setBackgroundResource(R.drawable.ic_emojis_delete);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.emojiTv.getLayoutParams();
            lp.bottomMargin = (int) mContext.getResources().getDimension(R.dimen.dp12);
        } else {
            holder.emojiTv.setText(getItem(position));
        }
        return convertView;
    }

    private static class ViewHolder {
        private EmojiAppCompatTextView emojiTv;
    }


}
