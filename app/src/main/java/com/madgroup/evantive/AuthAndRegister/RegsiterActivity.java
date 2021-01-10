package com.madgroup.evantive.AuthAndRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.madgroup.evantive.DarkMode.DarkModePrefManager;
import com.madgroup.evantive.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegsiterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Spinner spinner;
    String utype;
    private static final String[] paths = {"User", "Event Planner"};
    ProgressDialog pd ;

    EditText rg_fname,rg_lname,rg_email,rg_password;

    Button registerbtn;



    //for firebase authentications purpose

    private FirebaseFirestore fStore;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsiter);
        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }


        init();
    }

    private void init() {

        spinner = (Spinner)findViewById(R.id.spinReg);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegsiterActivity.this,
                android.R.layout.simple_spinner_item,paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        rg_fname=findViewById(R.id.rg_fname);
        rg_lname=findViewById(R.id.rg_lname);
        rg_email=findViewById(R.id.rg_email);
        rg_password=findViewById(R.id.rg_password);
        registerbtn=findViewById(R.id.registerbtn);

        pd = new ProgressDialog(RegsiterActivity.this);

        mAuth = FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        utype = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void goTologin(View view) {

        startActivity(new Intent(RegsiterActivity.this, LoginActivity.class));
        finish();
    }

    public void Register(View view) {
         Validations();





    }

    private void RegisterWithFirebase() {



        mAuth.createUserWithEmailAndPassword(rg_email.getText().toString(), rg_password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            final String  userID=mAuth.getCurrentUser().getUid();
//                            //----firebasefireStore --------------------------
                            DocumentReference documentReference=fStore.collection("Users").document(userID);


                            Map<String,Object> user=new HashMap<>();
                            user.put("firstname",rg_fname.getText().toString());
                            user.put("lastname",rg_lname.getText().toString());
                            user.put("contact","");
                            user.put("address","");
                            user.put("utype",utype);


                            String state=null;
                            if(!utype.equals("Event Planner"))

                            {
                                state="Active";

                            }else{
                                state="Inactive";

                            }


                            user.put("state",state);
                            user.put("companyID","");

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG","ON Success :user Profile is Create for "+userID);
//                                    progressBar.setVisibility(View.INVISIBLE);
                                    registerbtn.setVisibility(View.VISIBLE);
                                    Toast.makeText(RegsiterActivity.this,"success !",Toast.LENGTH_SHORT).show();
                                    pd.dismiss();

                                    //to set picked data to current user data
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                            .build();
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("TAG", "User profile updated.");
                                                        ///Toast.makeText(getContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });





                                    startActivity(new Intent(RegsiterActivity.this, LoginActivity.class));finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG"," UnSuccess :user Profile is Create for "+userID);
                                }
                            });



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegsiterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            pd.dismiss();
//                            progressBar.setVisibility(View.INVISIBLE);
                            registerbtn.setVisibility(View.VISIBLE);

                        }

                        // ...
                    }
                });
    }

    private void Validations() {

        if (TextUtils.isEmpty(rg_fname.getText().toString())) {
            rg_fname.setError("First name is required !");
            return;
        }


        if (TextUtils.isEmpty(rg_lname.getText().toString())) {
            rg_lname.setError("Last name is required !");
            return;
        }

        String emailAddress = rg_email.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            rg_email.setError("invalid Email address");
        }
//
        if(rg_password.getText().length()<8 &&!isValidPassword(rg_password.getText().toString())){
            rg_password.setError( "Password must be 8 characters" );
            return;
        }



        RegisterWithFirebase();
        pd.setMessage("loading");
        pd.show();

        registerbtn.setVisibility(View.INVISIBLE);

    }


    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

}