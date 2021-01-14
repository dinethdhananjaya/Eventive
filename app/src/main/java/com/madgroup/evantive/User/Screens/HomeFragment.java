package com.madgroup.evantive.User.Screens;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.evantive.DarkMode.DarkModePrefManager;
import com.madgroup.evantive.EventPlanner.Model.Post;
import com.madgroup.evantive.R;
import com.madgroup.evantive.User.Adapters.PostAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {

    //root defined

    View rootView;
    //home screen variable initialization
    TextView usernameHome;
    TextView username;
    SearchView search_bar;
    ImageView image_user_home;
    CardView weddingCard;
    CardView privatePartCard;
    CardView corporateEvent;


    ImageView image_user_profile;






    //to check if user is logging or not
    boolean isLoggedIn;
    SharedPreferences sharedpreferences;


    DatabaseReference databaseReference ;
    FirebaseDatabase firebaseDatabase;
    List<Post> postList;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    ImageView image_logo;



    //for cloud store refer
    FirebaseFirestore fStore;



    //current user details

    FirebaseAuth mAuth;


    StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView= inflater.inflate(R.layout.user_home_fragment, container, false);



        //get value from sessions
        sharedpreferences= getContext().getSharedPreferences("user_details", MODE_PRIVATE);
        isLoggedIn=sharedpreferences.getBoolean("isLoggedIn",false);



        //init the variables
        init();
        LoadPosts("");

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
            String currentUserId=mAuth.getCurrentUser().getUid()+"";

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
        usernameHome=rootView.findViewById(R.id.username);
        image_logo=rootView.findViewById(R.id.image_logo);
        recyclerView=rootView.findViewById(R.id.recyclerView);
        search_bar=rootView.findViewById(R.id.search_bar);
        weddingCard=rootView.findViewById(R.id.weddingCard);
        privatePartCard=rootView.findViewById(R.id.privatePartCard);
        corporateEvent=rootView.findViewById(R.id.corporateEvent);
        username=rootView.findViewById(R.id.username);
        image_user_profile=rootView.findViewById(R.id.image_user_profile);

        mAuth= FirebaseAuth.getInstance();


        if(!isLoggedIn){

            image_user_profile.setVisibility(View.INVISIBLE);
            username.setVisibility(View.INVISIBLE);

        }

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                LoadPosts(s);
//                Toast.makeText(getContext(),s+"",
//                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        weddingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 LoadByWeddings();
            }
        });


        privatePartCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByPrivateParty();
            }
        });


        corporateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCorprateEvent();
            }
        });
    }

    private void LoadByCorprateEvent() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");
        databaseReference.orderByChild("category").equalTo("Corporate Events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                postList = new ArrayList<>();

                for (DataSnapshot postsnap: snapshot.getChildren()) {

                    Post post = postsnap.getValue(Post.class);
                    postList.add(post) ;

                    Log.e("data",post.getTitle()+"");


                }


                postAdapter = new PostAdapter(getContext(),postList);

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                recyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadByPrivateParty() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");
        databaseReference.orderByChild("category").equalTo("Private party").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                postList = new ArrayList<>();

                for (DataSnapshot postsnap: snapshot.getChildren()) {

                    Post post = postsnap.getValue(Post.class);
                    postList.add(post) ;

                    Log.e("data",post.getTitle()+"");


                }


                postAdapter = new PostAdapter(getContext(),postList);

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                recyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void LoadByWeddings() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");
        databaseReference.orderByChild("category").equalTo("Wedding").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                postList = new ArrayList<>();

                for (DataSnapshot postsnap: snapshot.getChildren()) {

                    Post post = postsnap.getValue(Post.class);
                    postList.add(post) ;

                    Log.e("data",post.getTitle()+"");


                }


                postAdapter = new PostAdapter(getContext(),postList);

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                recyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();


        //get value from sessions
        sharedpreferences= getContext().getSharedPreferences("user_details", MODE_PRIVATE);
        isLoggedIn=sharedpreferences.getBoolean("isLoggedIn",false);



        LoadPosts("");
    }

    private void LoadPosts(String s) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");



        databaseReference.orderByChild("title").startAt(s.toUpperCase()).endAt(s + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList = new ArrayList<>();

                for (DataSnapshot postsnap: snapshot.getChildren()) {

                    Post post = postsnap.getValue(Post.class);
                    postList.add(post) ;

                    Log.e("data",post.getTitle()+"");


                }


                postAdapter = new PostAdapter(getContext(),postList);

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                recyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}