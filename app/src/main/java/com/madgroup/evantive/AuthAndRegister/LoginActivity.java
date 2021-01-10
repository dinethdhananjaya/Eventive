package com.madgroup.evantive.AuthAndRegister;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.madgroup.evantive.DarkMode.DarkModePrefManager;
import com.madgroup.evantive.R;


public class LoginActivity extends AppCompatActivity {

    ProgressDialog pd ;

    EditText login_email,login_password;

    Button loginbtn;

    private FirebaseAuth mAuth;


//session preparing for Set the values
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        init();
    }

    private void init() {

        pd = new ProgressDialog(LoginActivity.this);
        login_email=findViewById(R.id.login_email);
        login_password=findViewById(R.id.login_password);
        loginbtn=findViewById(R.id.loginbtn);
        mAuth = FirebaseAuth.getInstance();



    }

    public void goToRegister(View view) {

        startActivity(new Intent(LoginActivity.this,RegsiterActivity.class));
        finish();
    }

    public void Login(View view) {

        pd.setMessage("loading");
        pd.show();

        loginbtn.setVisibility(View.INVISIBLE);



        LoginWithFirebase();



    }

    private void LoginWithFirebase() {


        mAuth.signInWithEmailAndPassword(login_email.getText().toString(), login_password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            //  db.collection("users").document(user.getUid()).get()
                            String  userID=mAuth.getCurrentUser().getUid();
                            DocumentReference docRef = db.collection("Users").document(userID);

                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            //user type check and set the navigetions
                                            String utype=document.getString("utype");
                                            String state=document.getString("state");
                                            String companyId=document.getString("companyID");
                                            //  Toast.makeText(getApplicationContext(), utype, Toast.LENGTH_LONG).show();

                                            if(state.equals("Inactive")){
                                                Toast.makeText(getApplicationContext(),"Your Account has not activated yet !", Toast.LENGTH_LONG).show();
                                                pd.dismiss();
                                                loginbtn.setVisibility(View.VISIBLE);
                                                return;
                                            }

                                            //Toast.makeText(getApplicationContext(),"Your Account has not activated yet !"+utype, Toast.LENGTH_LONG).show();


                                            if(utype.equals("User")){
                                                Log.d( "utype",utype );

                                                pd.dismiss();
                                                Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();

                                                //set the session
                                                sharedpreferences =getSharedPreferences("user_details", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.putBoolean("isLoggedIn",true);
                                                editor.putString("usertype",utype);
                                                editor.commit();


                                                //startActivity(new Intent(LoginActivity.this, UserMainActivity.class));
                                                 finish();
                                            }else {





                                                sharedpreferences =getSharedPreferences("user_details", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.putString("Companyid",companyId);
                                                editor.putString("usertype",utype);
                                                editor.putBoolean("isLoggedIn",true);
                                                editor.commit();


                                                Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                                                //startActivity(new Intent(LoginActivity.this, EventPlannerMainActivity.class));
                                                finish();
                                            }

                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Incorrect User ID !", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            pd.dismiss();
                            loginbtn.setVisibility(View.VISIBLE);

                        }

                        // ...
                    }
                });
    }
}