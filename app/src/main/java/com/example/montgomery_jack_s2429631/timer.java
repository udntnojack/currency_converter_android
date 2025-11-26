package com.example.montgomery_jack_s2429631;

import android.os.Handler;
import android.os.Message;

public class timer implements Runnable{
    private final Handler handler;

    private int delay = 60 * 60 * 1000;
    public timer(Handler handler){
        this.handler = handler;
    }

    @Override
    public void run() {
        Message message = new Message();
        message.what = 5;
        handler.sendMessage(message);
        handler.postDelayed(this, delay);
    }
}
