package com.madgroup.evantive.EventPlanner.Screens;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.evantive.DarkMode.DarkModePrefManager;
import com.madgroup.evantive.R;
import com.squareup.picasso.Picasso;

public class EpHomeFragment extends Fragment {


    View rootView;
    CardView addPostCard;
    CardView view_gallery;
    CardView showBookings;
    CardView view_post;
    ImageView image_logo;
    ImageView image_user_profile;


    TextView username;

    //for testing
    String TAG="EpHomeFragment";
    //for cloud store refer
    FirebaseFirestore fStore;
    StorageReference storageReference;
    DatabaseReference databaseReference ;
    FirebaseDatabase firebaseDatabase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        rootView=inflater.inflate(R.layout.ep_home_fragment, container, false);

        init();

        //check available mode from sessions
        if(new DarkModePrefManager(getContext()).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            image_logo.setImageResource(R.drawable.dark_logo);

        }

        LoadUserData();
        return rootView;
    }


    private void LoadUserData() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        fStore= FirebaseFirestore.getInstance();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid()+"";

            //Toast.makeText(getContext(),"User Id:"+currentUserId,Toast.LENGTH_SHORT).show();
            Log.e("User Id:",currentUserId+"");


            DocumentReference documentReference=fStore.collection("Users").document(currentUserId);


            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    //Doing work
                    //get and set the value of current user
                    username.setText("Hello ! "+documentSnapshot.getString("firstname")+" "+documentSnapshot.getString("lastname"));


                    //
                    //to set picked data to current user data
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(documentSnapshot.getString("firstname")+" "+documentSnapshot.getString("lastname"))
                            // .setPhotoUri(uri)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "User profile updated.");
                                        // Toast.makeText(getContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                }
            });
            storageReference= FirebaseStorage.getInstance().getReference();
            StorageReference profileRef=storageReference.child("Users/"+ FirebaseAuth.getInstance().getUid()+"/Profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Picasso.get().load(uri)
                            .placeholder(R.drawable.loading)
                            .into(image_user_profile);


                    //to set picked data to current user data
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
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

                }
            });

        }



    }

    private void init() {
        addPostCard=rootView.findViewById(R.id.addPostCard);
        view_gallery=rootView.findViewById(R.id.view_gallery);
        showBookings=rootView.findViewById(R.id.showBookings);
        view_post=rootView.findViewById(R.id.view_post);
        image_logo=rootView.findViewById(R.id.image_logo);
        username=rootView.findViewById(R.id.username);
        image_user_profile=rootView.findViewById(R.id.image_user_profile);

        // go to add post EpAddPostFragment
        addPostCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"clicked");
                getFragmentManager().beginTransaction().replace(R.id.content, new EpAddPostFragment())
                        .addToBackStack(null).commit();
            }
        });



        view_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.content, new EpViewGalleryFragment())
                        .addToBackStack(null).commit();
            }
        });

        showBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.content, new EpViewBookingFragment())
                        .addToBackStack(null).commit();
            }
        });

        view_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.content, new EpViewPostFragment())
                        .addToBackStack(null).commit();
            }
        });
    }
}