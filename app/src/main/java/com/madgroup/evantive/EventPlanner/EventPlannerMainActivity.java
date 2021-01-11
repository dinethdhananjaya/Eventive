package com.madgroup.evantive.EventPlanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.legacy.app.ActionBarDrawerToggle;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.evantive.AuthAndRegister.LoginActivity;
import com.madgroup.evantive.DarkMode.DarkModePrefManager;
import com.madgroup.evantive.R;
import com.squareup.picasso.Picasso;

public class EventPlannerMainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    static final float END_SCALE = 0.7f;
    FrameLayout contentView;
    StorageReference storageReference;



    String currentUserId;
    SharedPreferences sharedpreferences;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_planner_activity_main);



        //check available mode from sessions
        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        }


        sharedpreferences= getSharedPreferences("user_details", MODE_PRIVATE);
        // currentUserId=sharedpreferences.getString("UserID","");

        navigationDrawer();
        bottomNavigation();

        LoadnavigationBar();

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

                       // getSupportFragmentManager().beginTransaction().replace(R.id.content, new EpHomeFragment()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);

                        break;
                    case R.id.nav_profile:
                        navigationView.setCheckedItem(R.id.nav_profile);
                        //getSupportFragmentManager().beginTransaction().replace(R.id.content, new EpProfileFragment()).commit();

                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_dark_mode:
                        navigationView.setCheckedItem(R.id.nav_dark_mode);

                        //code for setting dark mode
                        //true for dark mode, false for day mode, currently toggling on each click
                        DarkModePrefManager darkModePrefManager = new DarkModePrefManager(EventPlannerMainActivity.this);
                        darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        recreate();

                        break;

                    case R.id.nav_logout:
                        navigationView.setCheckedItem(R.id.nav_logout);

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

        mAuth.signOut();
        sharedpreferences =getSharedPreferences("user_details", Context.MODE_PRIVATE);


        //sessions clear when user log out
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("isLoggedIn",false);
        editor.putString("Companyid","");
        editor.putString("usertype","");
        editor.clear();
        editor.commit();

        startActivity(new Intent(this, LoginActivity.class));
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

       // getSupportFragmentManager().beginTransaction().replace(R.id.content, new EpHomeFragment()).commit();

        BottomNavigationView bottomNavigationView = findViewById( R.id.bottom_navi );
        bottomNavigationView.setSelectedItemId( R.id.home );


        bottomNavigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {

                    case R.id.home:
                       // getSupportFragmentManager().beginTransaction().replace(R.id.content, new EpHomeFragment()).commit();

                        return true;
                    case R.id.menu:
                        //  Toast.makeText(MainActivity.this,"Clicked !",Toast.LENGTH_SHORT).show();
                        if (drawerLayout.isDrawerVisible(GravityCompat.START))
                            drawerLayout.closeDrawer(GravityCompat.START);
                        else drawerLayout.openDrawer(GravityCompat.START);
                        overridePendingTransition( 0, 0 );
                        return true;

                    case R.id.profile:
                        Toast.makeText(EventPlannerMainActivity.this,"Profile Clicked !", Toast.LENGTH_SHORT).show();

                        //getSupportFragmentManager().beginTransaction().replace(R.id.content, new EpProfileFragment()).commit();

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