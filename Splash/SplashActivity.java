package com.madgroup.evantive.Splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.madgroup.evantive.EventPlanner.EventPlannerMainActivity;
import com.madgroup.evantive.R;
import com.madgroup.evantive.User.UserMainActivity;

public class SplashActivity extends AppCompatActivity {
    ImageView img;

    SharedPreferences sharedpreferences;

    String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        img=findViewById( R.id.image );

        Animation myanim= AnimationUtils.loadAnimation( this, R.anim.mytransition );
        img.startAnimation(myanim);



        Thread timer= new Thread(){
            public void run(){
                try{
                    sleep( 3000 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    //to check current user types
                    sharedpreferences= getSharedPreferences("user_details", MODE_PRIVATE);
                    userType=sharedpreferences.getString("usertype","");


//  Toast.makeText(SplashActivity.this,userType+"",Toast.LENGTH_SHORT).show();


                    Log.d("UserType",userType+"");
                    if(userType.equals("Event Planner")){

                        startActivity(new Intent(SplashActivity.this, EventPlannerMainActivity.class));
                        finish();
                    }else{

                        startActivity(new Intent(SplashActivity.this, UserMainActivity.class));
                        finish();
                    }

                }
            }
        };
        timer.start();
    }
}