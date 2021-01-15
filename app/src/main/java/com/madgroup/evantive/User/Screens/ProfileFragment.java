package com.madgroup.evantive.User.Screens;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madgroup.evantive.R;
import com.madgroup.evantive.User.UserMainActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {



    View rootView;
    ImageView image_user_profile;
    ImageView log_out;
    EditText fname;
    EditText lname;
    EditText contact;
    EditText address;

    private static final int PReqCode = 2 ;
    StorageReference storageReference;
    CollapsingToolbarLayout collapsingToolbarLayout;



    FirebaseFirestore fStore;
    FirebaseAuth mAuth;


    //updating

    FloatingActionButton uploadbutton;


    SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView=inflater.inflate(R.layout.user_profile_fragment, container, false);
        mAuth= FirebaseAuth.getInstance();

        storageReference= FirebaseStorage.getInstance().getReference();

        init();
        LoadProfile();
        return rootView;
    }

    private void LoadProfile() {

        StorageReference profileRef=storageReference.child("Users/"+ FirebaseAuth.getInstance().getUid()+"/Profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri)
                       .placeholder(R.drawable.loading)
                        .into(image_user_profile);

            }
        });



        fStore= FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            String currentUserId=mAuth.getCurrentUser().getUid()+"";

            DocumentReference documentReference=fStore.collection("Users").document(currentUserId);


            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    //Doing work
                    //get and set the value of current user
                    collapsingToolbarLayout.setTitle(""+documentSnapshot.getString("firstname")+" "+documentSnapshot.getString("lastname"));


                    fname.setText(documentSnapshot.getString("firstname")+"");
                    lname.setText(documentSnapshot.getString("lastname")+"");
                    contact.setText(documentSnapshot.getString("contact")+"");
                    address.setText(documentSnapshot.getString("address")+"");



                }
            });
        }

    }

    private void init() {

        image_user_profile=rootView.findViewById(R.id.image_user_profile);
        fname=rootView.findViewById(R.id.fname);
        lname=rootView.findViewById(R.id.lname);
        contact=rootView.findViewById(R.id.contact);
        address=rootView.findViewById(R.id.address);
        log_out=rootView.findViewById(R.id.log_out);
        uploadbutton=rootView.findViewById(R.id.uploadbutton);
        collapsingToolbarLayout=rootView.findViewById( R.id.collapsingToolbarLayoutProEdit );
        collapsingToolbarLayout.setTitleEnabled( true );

        image_user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkAndRequestForPermission();


                Intent OpenGalleryIntent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(OpenGalleryIntent,1000);
            }
        });


        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadingData();
            }
        });


        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"logout",Toast.LENGTH_LONG).show();
                mAuth.signOut();
                sharedpreferences =getContext().getSharedPreferences("user_details", Context.MODE_PRIVATE);


                //sessions clear when user log out
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("isLoggedIn",false);
                editor.putString("usertype","");



                editor.clear();
                editor.commit();



                startActivity(new Intent(getContext(), UserMainActivity.class));
                getActivity().finish();
            }
        });
    }

    private void uploadingData() {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> user=new HashMap<>();
        user.put("firstname",fname.getText().toString());
        user.put("lastname",lname.getText().toString());
        user.put("contact",contact.getText().toString());
        user.put("address",address.getText().toString());
        user.put("utype","User");


        db.collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        //to set picked data to current user data
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fname.getText().toString()+" "+lname.getText().toString())
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TAG", "User profile updated.");
                                            Toast.makeText(getContext(),"updating done !",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });





                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"failed----1",Toast.LENGTH_SHORT).show();                    }
                });

    }

    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(getContext(),"Please accept for required permission", Toast.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }


            return;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri=data.getData();
                // image_user_profile.setImageURI(imageUri);


                uploadImageToFirebase(imageUri);

            }

        }
    }

    private void uploadImageToFirebase(Uri imageUri) {

        storageReference= FirebaseStorage.getInstance().getReference();


        //uploade image to firebase storage
        final StorageReference fileRef=storageReference.child("Users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                //retriving
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(image_user_profile);


                        //to set picked data to current user data


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fname.getText().toString()+" "+lname.getText().toString())
                                .setPhotoUri(uri)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TAG", "User profile updated.");
                                            Toast.makeText(getContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Image Not Retive !",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Image Uploaded is Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

}