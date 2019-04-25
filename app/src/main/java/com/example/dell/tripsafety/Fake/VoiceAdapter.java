package com.example.dell.tripsafety.Fake;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dell.tripsafety.R;

import java.util.List;

public class VoiceAdapter extends BaseQuickAdapter<Voice, BaseViewHolder> {

    public VoiceAdapter(@LayoutRes int layoutResId, @Nullable List<Voice> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Voice item) {
        //可链式调用赋值
        helper.setText(R.id.tv_title, item.getTitle())
                .setImageResource(R.id.iv_img, R.mipmap.icon_test_2);

        //获取当前条目position
        //int position = helper.getLayoutPosition();
    }

}
