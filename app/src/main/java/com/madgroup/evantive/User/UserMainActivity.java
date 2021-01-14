package com.madgroup.evantive.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.evantive.DarkMode.DarkModePrefManager;
import com.madgroup.evantive.R;
import com.madgroup.evantive.User.Screens.HomeFragment;
import com.madgroup.evantive.User.Screens.ProfileFragment;
import com.squareup.picasso.Picasso;


public class UserMainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    static final float END_SCALE = 0.7f;
    FrameLayout contentView;



    //to check if user is logging or not
    boolean isLoggedIn;
    SharedPreferences sharedpreferences;


    StorageReference storageReference;


    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_main);

        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        //get value from sessions
        sharedpreferences= getSharedPreferences("user_details", MODE_PRIVATE);
        isLoggedIn=sharedpreferences.getBoolean("isLoggedIn",false);


        navigationDrawer();
        bottomNavigation();




       // Toast.makeText(this,isLoggedIn+"",Toast.LENGTH_SHORT).show();



//load Navigation bar

        LoadnavigationBar();
        //navigation view





    }

    private void LoadnavigationBar() {


        navigationView=findViewById(R.id.navigation_view);
        View heder=navigationView.getHeaderView( 0 );
        ImageView navPro=heder.findViewById(R.id.navPro);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            storageReference= FirebaseStorage.getInstance().getReference();

            StorageReference profileRef=storageReference.child("Users/"+ FirebaseAuth.getInstance().getUid()+"/Profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri)
                            .placeholder(R.drawable.loading)
                            .into(navPro);
                }
            });
        }
    }


    private void navigationDrawer() {

        drawerLayout = findViewById(R.id.drawer_layout);
        contentView = findViewById(R.id.content);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        navigationView.setCheckedItem(R.id.nav_home);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_profile:
                        navigationView.setCheckedItem(R.id.nav_profile);

                        getSupportFragmentManager().beginTransaction().replace(R.id.content, new ProfileFragment()).commit();

                        drawerLayout.closeDrawer(GravityCompat.START);

                        break;
                    case R.id.nav_dark_mode:
                        navigationView.setCheckedItem(R.id.nav_dark_mode);

                        //code for setting dark mode
                        //true for dark mode, false for day mode, currently toggling on each click
                        DarkModePrefManager darkModePrefManager = new DarkModePrefManager(UserMainActivity.this);
                        darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        startActivity(new Intent(UserMainActivity.this, UserMainActivity.class));
                        finish();

                        break;

                    case R.id.nav_logout:
                        navigationView.setCheckedItem(R.id.nav_logout);
                        drawerLayout.closeDrawer(GravityCompat.START);

                        logout();
                        break;
                }
                return false;
            }
        });

        animateNavigationDrawer();

    }

    private void logout() {
        mAuth= FirebaseAuth.getInstance();

        Toast.makeText(this,"logout",Toast.LENGTH_LONG).show();
        mAuth.signOut();
        sharedpreferences =getSharedPreferences("user_details", Context.MODE_PRIVATE);


        //sessions clear when user log out
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("isLoggedIn",false);
        editor.putString("usertype","");



        editor.clear();
        editor.commit();



        startActivity(new Intent(this, UserMainActivity.class));
        finish();
    }

    private void animateNavigationDrawer() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.colorPrimary));
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //Scale the view based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                //Translating the view accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslaiton = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslaiton);

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void bottomNavigation() {

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();

        BottomNavigationView bottomNavigationView = findViewById( R.id.bottom_navi );
        bottomNavigationView.setSelectedItemId( R.id.home );


        //f is not logging user hidden the bottom navigation drawer
        if(!isLoggedIn){
            bottomNavigationView.setVisibility(View.INVISIBLE);
        }


        bottomNavigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {

                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();

                        return true;
                    case R.id.menu:
                        //  Toast.makeText(MainActivity.this,"Clicked !",Toast.LENGTH_SHORT).show();
                        if (drawerLayout.isDrawerVisible(GravityCompat.START))
                            drawerLayout.closeDrawer(GravityCompat.START);
                        else drawerLayout.openDrawer(GravityCompat.START);
                        overridePendingTransition( 0, 0 );
                        return true;

                    case R.id.profile:
                        Toast.makeText(UserMainActivity.this,"Profile Clicked !", Toast.LENGTH_SHORT).show();

                        getSupportFragmentManager().beginTransaction().replace(R.id.content, new ProfileFragment()).commit();

//                        PatientProfile patientProfile = new PatientProfile();
//                        transaction.replace(R.id.content,patientProfile);
                        overridePendingTransition( 0, 0 );
                        return true;
                }

                transaction.addToBackStack(null);
                transaction.commit();
                return false;


            }
        } );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

}