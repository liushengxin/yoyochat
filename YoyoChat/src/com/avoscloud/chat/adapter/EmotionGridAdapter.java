package com.avoscloud.chat.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.avoscloud.chat.ui.view.ViewHolder;
import com.avoscloud.chat.R;

/**
 * Created by lzw on 14-9-25.
 */
public class EmotionGridAdapter extends BaseListAdapter<String> {

  public EmotionGridAdapter(Context ctx) {
    super(ctx);
  }

  @Override
  public View getView(int position, View conView, ViewGroup parent) {
    if (conView == null) {
      LayoutInflater inflater = LayoutInflater.from(ctx);
      conView = inflater.inflate(R.layout.chat_emotion_item, null);
    }
    ImageView emotionImageView = ViewHolder.findViewById(conView, R.id.emotionImageView);
    String pkgName = ctx.getPackageName();
    String emotion = (String) getItem(position);
    emotion = emotion.substring(1);
    Drawable drawable = ctx.getResources().getDrawable(ctx.getResources().getIdentifier(emotion, "drawable", pkgName));
    emotionImageView.setImageDrawable(drawable);
    return conView;
  }
}
