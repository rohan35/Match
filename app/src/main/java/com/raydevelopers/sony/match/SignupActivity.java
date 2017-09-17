package com.raydevelopers.sony.match;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raydevelopers.sony.match.model.User;
import com.raydevelopers.sony.match.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SONY on 14-08-2017.
 */

public class SignupActivity  extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private String TAG = "tag";
    private  CallbackManager mCallbackManager;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile","user_photos");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            Intent i=new Intent(SignupActivity.this,MainActivity.class);
            startActivity(i);

        }
       // updateUI(currentUser);
    }
    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            getUser(token);


                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                });
    }

public void getUser(final AccessToken token)
{
    GraphRequest request = GraphRequest.newMeRequest(
            token,
            new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(
                        JSONObject object,
                        GraphResponse response) {
                    // Application code
                    try {
                        System.out.println(object.toString());
                        JSONObject ageObject=new JSONObject(object.getString("age_range"));
                        JSONObject picture=new JSONObject(object.getString("picture"));
                        JSONObject data=picture.getJSONObject("data");
                        System.out.println(data.getString("url"));
                        int age=ageObject.getInt("min");
                        String gender=object.getString("gender");
                        String genderInterest;
                        if(gender.equals("male")){
                            genderInterest="female";
                        }
                        else {
                            genderInterest="male";
                        }
                        User user1 =new User(object.getString("name"),object.getString("id"),
                                age,object.getString("gender"),data.getString("url"));
                        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(
                                SignupActivity.this);

                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString(Constants.ID,object.getString("id"));
                        editor.commit();
                        user1.mFirebaseToken=sharedPreferences.getString(Constants.ARG_FIREBASE_TOKEN,null);
                        checkUserExistence(user1);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
    Bundle parameters = new Bundle();
    parameters.putString("fields", "id,name,link,email,gender,age_range,picture.type(large)");
    request.setParameters(parameters);
    request.executeAsync();
}
public void checkUserExistence(final User user)
{
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    final String userId = ref.child("users").push().getKey();
    SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(
            SignupActivity.this);
    String id=sharedPreferences.getString(Constants.ID,null);

    ref.child("users").orderByChild("mUserName").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists())
            {
                //Username exist
            }
            else {
                //User doesnt exist
                //Create New
                ref.child("users").child(userId).setValue(user);


            }
            Intent i=new Intent(SignupActivity.this,MainActivity.class);
            startActivity(i);
            finish();

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });


}
}
