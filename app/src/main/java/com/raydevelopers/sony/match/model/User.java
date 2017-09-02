package com.raydevelopers.sony.match.model;

/**
 * Created by SONY on 19-08-2017.
 */

public class User {
    public String mName;
    public String mUserName;
    public int mAge;
    public String mSex;
    public String mProfilePic;
    public String mkey;
    public User mUser;

    public User() {
        //Default construcutor;
    }
    public User(String name,String username,int age,String sex,String profilePic)
    {
        this.mName=name;
        this.mUserName=username;
        this.mAge=age;
        this.mSex=sex;
        this.mProfilePic=profilePic;
    }
    public User(String name,String username,int age,String sex,String profilePic,String key)
    {
        this.mName=name;
        this.mUserName=username;
        this.mAge=age;
        this.mSex=sex;
        this.mProfilePic=profilePic;
        this.mkey=key;
    }

  public  User(User user,String key)
  {
      this.mUser=user;
      this.mkey=key;
  }

}

