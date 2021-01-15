package com.madgroup.evantive.User.Screens;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.madgroup.evantive.AuthAndRegister.LoginActivity;
import com.madgroup.evantive.Classes.DatePickerFragment;
import com.madgroup.evantive.EventPlanner.Model.GalleryModel;
import com.madgroup.evantive.R;
import com.madgroup.evantive.User.Adapters.GalleryForPostAdapter;
import com.madgroup.evantive.User.Model.BookingModel;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PostDetailesFragment extends Fragment {

    LinearLayout expandableView;
    TextView view_gallery;
    TextView post_description;
    TextView post_contact;
    TextView post_location;
    ImageView company_logo;
    ImageView header_image;

    View rootView;

FloatingActionButton booking_btn;

    //get the value from constructor

    String companyId;
    String companyName;
    String companyLogopath;
    String imagepath;
    String location;
    String description;
    String companyContactNumber;





    //for loading Gallery
    DatabaseReference databaseReference ;
    FirebaseDatabase firebaseDatabase;
    List<GalleryModel> galleryModelList;
    GalleryForPostAdapter galleryAdapter;
    RecyclerView recyclerView;



    //to check if user is logging or not
    boolean isLoggedIn;
    SharedPreferences sharedpreferences;


    EditText picked_date;

    StorageReference storageReference;

    ProgressDialog pd ;

    public PostDetailesFragment(String companyId, String companyName, String companyLogopath, String imagepath, String location, String description, String companyContactNumber) {
        this.companyId=companyId;
        this.companyName=companyName;
        this.companyLogopath=companyLogopath;
        this.imagepath=imagepath;
        this.location=location;
        this.description=description;
        this.companyContactNumber=companyContactNumber;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView=inflater.inflate(R.layout.user_post_detailes_fragment, container, false);

        expandableView =rootView.findViewById(R.id.expandableView);

        post_description =rootView.findViewById(R.id.post_description);
        post_contact =rootView.findViewById(R.id.post_contact);
        post_location =rootView.findViewById(R.id.post_location);
        company_logo =rootView.findViewById(R.id.company_logo);
        header_image =rootView.findViewById(R.id.header_image);
        recyclerView =rootView.findViewById(R.id.recyclerView);
        booking_btn =rootView.findViewById(R.id.booking_btn);



        //set the retive value
        post_description.setText(description);
        post_location.setText(location);
        post_contact.setText(companyContactNumber);


        Picasso.get().load( companyLogopath+"" )
                .networkPolicy( NetworkPolicy.NO_CACHE)
                .memoryPolicy( MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.loading)
                .into( company_logo);


        Picasso.get().load( imagepath+"" )
                .networkPolicy( NetworkPolicy.NO_CACHE)
                .memoryPolicy( MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.loading)
                .into( header_image);


        view_gallery =rootView.findViewById(R.id.view_gallery);
        view_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandableView.getVisibility()==View.GONE){
//                    TransitionManager.beginDelayedTransition( new AutoTransition());
                    expandableView.setVisibility(View.VISIBLE);
                    //arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                } else {
//                    TransitionManager.beginDelayedTransition( new AutoTransition());
                    expandableView.setVisibility(View.GONE);
                  //  arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });


        final CollapsingToolbarLayout collapsingToolbarLayout=rootView.findViewById( R.id.collapsingToolbarLayout );
        collapsingToolbarLayout.setTitleEnabled( true );
        collapsingToolbarLayout.setTitle( companyName+"" );


        Loading();





        //get value from sessions
        sharedpreferences= getContext().getSharedPreferences("user_details", MODE_PRIVATE);
        isLoggedIn=sharedpreferences.getBoolean("isLoggedIn",false);


        booking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isLoggedIn){


                    PickupShowAlertBox();
                    Toast.makeText(getContext(),"User Login !",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getContext(),"User is not Login yet  !",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }

            }
        });

        pd = new ProgressDialog(getContext());
        return rootView;
    }

    private void PickupShowAlertBox() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Picked a Date");
        View dialogLayout = inflater.inflate(R.layout.custom_alert_to_booking, null);

        Button cancel_button=dialogLayout.findViewById(R.id.cancel_button);
        Button ok_button=dialogLayout.findViewById(R.id.ok_button);

        builder.setView(dialogLayout);


         picked_date=dialogLayout.findViewById(R.id.picked_date);

        picked_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        final AlertDialog alert = builder.create();

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alert.dismiss();
            }
        });


        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(picked_date.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Please picked a date !",Toast.LENGTH_LONG).show();
                    return;
                }else{

                 //   Toast.makeText(getContext(),"Please picked a date `11 !",Toast.LENGTH_LONG).show();

                      AddToDatabase();
                      alert.dismiss();
                }



            }
        });
        alert.show();

    }

    private void AddToDatabase() {
         pd.show();
        //Load User Profile


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            String uid = null;
            String name = null;
            String email = null;
            String photoUrl = null;


            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                 uid = profile.getUid();

                // Name, email address, and profile photo Url
                 name = profile.getDisplayName();
                 email = profile.getEmail();
                 photoUrl = profile.getPhotoUrl().toString();

//                Toast.makeText(getContext(),photoUrl+"",Toast.LENGTH_LONG).show();
//                Toast.makeText(getContext(),email+"",Toast.LENGTH_LONG).show();
//                Toast.makeText(getContext(),name+"",Toast.LENGTH_LONG).show();
               // Toast.makeText(getContext(),companyId+"",Toast.LENGTH_LONG).show();

              //  Log.e("path", photoUrl.getPath());

            }


            BookingModel bookingModel = new BookingModel(
                    companyId,
                    user.getUid(),
                    name+"",
                    photoUrl+"",
                    email,
                    picked_date.getText().toString()
            );
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Bookings").push();

            // add post data to firebase database

            myRef.setValue(bookingModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(getContext(),"Bookings Added successfully !", Toast.LENGTH_SHORT).show();
                    pd.dismiss();


                }
            });

        }


       // Log.e("picked date",UriLinkUser+"");
    }




    private void showDatePicker() {
        DatePickerFragment dateObj = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        dateObj.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        dateObj.setCallBack(ondate);
        dateObj.show(getFragmentManager(), "Date Picker");
    }
    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            picked_date.setText(
                    String.valueOf(year) + "/" +String.valueOf(monthOfYear+1)+ "/"+String.valueOf(dayOfMonth)  );
        }
    };



    private void Loading() {


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Gallery");


        databaseReference.orderByChild("mCompanyId").equalTo(companyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                galleryModelList= new ArrayList<>();
                galleryModelList.clear();
                for (DataSnapshot postsnap : snapshot.getChildren()) {

                    GalleryModel galleryModel = postsnap.getValue(GalleryModel.class);
                    galleryModelList.add(galleryModel);

                 Log.e("data",galleryModel.getmImageUrl()+"");


                }

                galleryAdapter = new GalleryForPostAdapter(getContext(), galleryModelList);

                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

                recyclerView.setAdapter(galleryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}