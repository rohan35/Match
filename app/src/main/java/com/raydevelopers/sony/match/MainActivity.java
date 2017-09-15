package com.raydevelopers.sony.match;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.share.internal.ShareInternalUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raydevelopers.sony.match.model.User;
import com.raydevelopers.sony.match.utils.Constants;
import com.raydevelopers.sony.match.utils.MainRecyclerViewAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
private  ArrayList<User> mUsersList=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private AVLoadingIndicatorView loadingIndicatorView;
    private MainRecyclerViewAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        loadingIndicatorView=(AVLoadingIndicatorView)findViewById(R.id.indicator);
        setSupportActionBar(toolbar);
        mRecyclerView=(RecyclerView)findViewById(R.id.people_rv);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        startAnim();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        TextView name=(TextView)v.findViewById(R.id.name);
        TextView email=(TextView)v.findViewById(R.id.email);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
            getUsers();


        } else {
            // No user is signed in

        }
    }

    void startAnim(){
        loadingIndicatorView.show();

    }
    void stopAnim(){
        loadingIndicatorView.hide();
         }
    private void getUsers()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    User users=postSnapshot.getValue(User.class);
                    System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    if(users.mUserName.equals(sharedPreferences.getString(Constants.ID,null)))
                    {

                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString(Constants.ARG_KEY,postSnapshot.getKey());
                        editor.apply();
                    }

                    mUsersList.add(new User(users.mName,users.mUserName,users.mAge
                    ,users.mSex,users.mProfilePic,postSnapshot.getKey()));

                    mAdapter=new MainRecyclerViewAdapter(getApplicationContext(),mUsersList);
                    mRecyclerView.setAdapter(mAdapter);
                    GridLayoutManager gridLayoutManager=new GridLayoutManager(MainActivity.this,2);
                    mRecyclerView.setLayoutManager(gridLayoutManager);

                    System.out.println(mUsersList.toString());

                    mAdapter.notifyDataSetChanged();
                    stopAnim();


                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.interest_received) {
            // Handle the camera action
            Intent i=new Intent(MainActivity.this,InterestReceived.class);
            startActivity(i);
        } else if (id == R.id.friends) {
            Intent i=new Intent(MainActivity.this,AcceptedInterest.class);
            startActivity(i);
        } else if (id == R.id.messanger) {
            Intent i=new Intent(MainActivity.this,MessangerActivity.class);
            startActivity(i);}
            else if(id==R.id.logout){
            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.remove(Constants.ID).clear();
           // editor.remove(Constants.ARG_FIREBASE_TOKEN).clear();
            editor.apply();
            FirebaseAuth.getInstance().signOut();
            Intent i=new Intent(MainActivity.this,SignupActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
