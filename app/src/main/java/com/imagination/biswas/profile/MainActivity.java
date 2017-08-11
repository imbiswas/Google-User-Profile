package com.imagination.biswas.profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener
{
    private LinearLayout layoutSignin,layoutHistory;
    private RelativeLayout layoutProfile;
    private SignInButton login;
    private TextView username,email;
    private ImageView pic;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;
    private static final int REQUEST_CODE=100;
    private Button signout;
    //for navigation bar
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutSignin= (LinearLayout) findViewById(R.id.signinLayout);
        layoutHistory= (LinearLayout) findViewById(R.id.historyLayout);
        layoutProfile= (RelativeLayout) findViewById(R.id.userDetailLayout);

        googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();
        login= (SignInButton) findViewById(R.id.login);
        username= (TextView) findViewById(R.id.username);
        email= (TextView) findViewById(R.id.email);
        pic= (ImageView) findViewById(R.id.imageView);
        signout= (Button) findViewById(R.id.signoutbtn);
        login.setSize(SignInButton.SIZE_WIDE);
        login.setScopes(googleSignInOptions.getScopeArray());


        login.setOnClickListener(this);
        signout.setOnClickListener(this);
        layoutProfile.setVisibility(View.GONE);


        //for navigation bar
        mDrawerLayout= (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle= new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.login:
                signIn();
                break;
            case R.id.signoutbtn:
                signOut();
                break;
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn(){
    Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQUEST_CODE);
    }
    private void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }
    private void handleResult(GoogleSignInResult result){
    if (result.isSuccess()){
        GoogleSignInAccount account=result.getSignInAccount();
        String name=account.getDisplayName();
        String Email=account.getEmail();
        String img_url=account.getPhotoUrl().toString();
        username.setText(name);
        email.setText(Email);
        Glide.with(this).load(img_url).into(pic);
        updateUI(true);
    }
    else {
        updateUI(false);
    }
    }
    private void updateUI(boolean isLogin){
        if (isLogin){
            layoutProfile.setVisibility(View.VISIBLE);
            layoutSignin.setVisibility(View.GONE);
        }
        else {
            layoutProfile.setVisibility(View.GONE);
            layoutSignin.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE){
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);


        }
    }
}
