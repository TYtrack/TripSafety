package com.example.dell.tripsafety.SecondMap;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by PGN on 2019/3/25.
 */

public class MarqueTextView extends TextView {
    public MarqueTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public MarqueTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MarqueTextView(Context context) {
        super(context);
    }
    @Override
    public boolean isFocused() {
        //就是把这里返回true即可
        return true;
    }
}
