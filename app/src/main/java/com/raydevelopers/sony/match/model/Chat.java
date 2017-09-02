package com.raydevelopers.sony.match.model;

/**
 * Created by SONY on 22-08-2017.
 */

public class Chat {
    public String mSender;
    public String mReceiver;
    public String mMessage;
    public String mTimeStamp;
    public Chat(){}
    public Chat(String sender,String receiver,String message,String timeStamp)
    {
        this.mSender=sender;
        this.mReceiver=receiver;
        this.mMessage=message;
        this.mTimeStamp=timeStamp;

    }
}
