package com.lc.demo.view;

import android.os.Build;
import android.util.Log;
import android.view.View;

public class SpecUtils {

    public static void parseSpec(String tag,int widthSpec,int heightSpec){
        String wSpec = parse(widthSpec);
        String hSpec = parse(heightSpec);
        Log.d(tag,"width "+wSpec + " | height:" + hSpec);
    }



    private static String parse(int spec){
        String info = "Spec mode:";
        int mode = View.MeasureSpec.getMode(spec);
        int value = View.MeasureSpec.getSize(spec);
        if (mode == View.MeasureSpec.EXACTLY){
            info += " EXACTLY";
        }else if (mode == View.MeasureSpec.AT_MOST){
            info += " AT_MOST";
        }else{
            info += " UNSPECIFIED";
        }

        return info + (",value:" + value);
    }
}
