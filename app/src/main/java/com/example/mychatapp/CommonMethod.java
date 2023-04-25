package com.example.mychatapp;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CommonMethod {
    CommonMethod(Context context, Class<?> nextClass){
        Intent intent = new Intent(context,nextClass);
        context.startActivity(intent);
    }

    CommonMethod(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
