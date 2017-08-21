package com.raydevelopers.sony.match.model;

/**
 * Created by SONY on 22-08-2017.
 */

public class Interest {
    public String mUserName;
    public String mEmail;
    public Interest(){}
    public Interest(String username,String email)
    {
        this.mEmail=email;
        this.mUserName=username;
    }
}
