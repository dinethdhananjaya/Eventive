package com.madgroup.evantive.EventPlanner.Screens;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madgroup.evantive.Classes.DatePickerFragment;
import com.madgroup.evantive.EventPlanner.Model.CompanyModel;
import com.madgroup.evantive.EventPlanner.Model.Post;
import com.madgroup.evantive.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class EpAddPostFragment extends Fragment {

//root defined

    View rootView;

    //image view to picked
    ImageView image_1;
    ImageView logo_view;

    
    Spinner spincat;
    String spin_Cat;


    private static final int PReqCode = 2 ;


    Button add_btn;
    EditText post_title;
    EditText post_description;
    EditText post_location;
    EditText post_date;
    ProgressBar progressBar;
    TextView company_name_view;


    //to request the internal image
    private static final int REQUEST_CODE_PICKED_1 = 10;





    Uri pickedImgUri1;




    //get values from session and set it to card views
    String company_id;


    String company_name;
    String company_contact_number;
    String company_LogoPath;



    DatabaseReference databaseReference ;
    FirebaseDatabase firebaseDatabase;



    SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView=inflater.inflate(R.layout.ep_add_post_fragment, container, false);
//get value from sessions
        sharedpreferences= getContext().getSharedPreferences("user_details", MODE_PRIVATE);
        company_id=sharedpreferences.getString("Companyid","");


        init();
        LoadingCompanyData();
        //spinner to set value as drop down
        initspinnerfooter();
        return rootView;
    }

    private void LoadingCompanyData() {



        //for loading company data
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Company");



        databaseReference.orderByChild("CompanyId").equalTo(company_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot postsnap: snapshot.getChildren()) {

                    CompanyModel companyModel = postsnap.getValue(CompanyModel.class);


                    //get value set to global variable
                    company_name=companyModel.getCompanyName();
                    company_LogoPath=companyModel.getCompanyPath();
                    company_contact_number=companyModel.getCompanyContact();



                    Log.e("data",companyModel.getCompanyName()+"");


                    company_name_view.setText(companyModel.getCompanyName()+"");
                    Picasso.get().load( companyModel.getCompanyPath()+"" )
                            .networkPolicy( NetworkPolicy.NO_CACHE)
                            .memoryPolicy( MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .placeholder(R.drawable.loading)
                            .resize(100,100)
                            .into(logo_view );

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initspinnerfooter() {
        String[] items = new String[]{
                "Wedding", "Private party", "Corporate Events",
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        spincat.setAdapter(adapter);
        spincat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("item", (String) parent.getItemAtPosition(position));


                spin_Cat= (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

}

    private void init() {

        image_1=rootView.findViewById(R.id.image_1);
        spincat=rootView.findViewById(R.id.spincat);
        post_date=rootView.findViewById(R.id.post_date);


        add_btn=rootView.findViewById(R.id.add_btn);
        post_title=rootView.findViewById(R.id.post_title);
        post_description=rootView.findViewById(R.id.post_description);
        post_location=rootView.findViewById(R.id.post_location);
        progressBar=rootView.findViewById(R.id.progressBar);
        company_name_view=rootView.findViewById(R.id.company_name_view);
        logo_view=rootView.findViewById(R.id.logo_view);


        //to setup image picked to click On Listener
        image_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
                pickedImage1();
            }
        });



       // company_name_view.setText(company_name+"");

//        Picasso.get().load( company_LogoPath+"" )
//                .networkPolicy( NetworkPolicy.NO_CACHE)
//                .memoryPolicy( MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
//                .placeholder(R.drawable.loading)
//                .into( logo_view);



        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                add_btn.setVisibility(View.INVISIBLE);

                Log.e("item",spin_Cat);
                //validation fields
                if (!post_title.getText().toString().isEmpty()
                        && !post_description.getText().toString().isEmpty()
                        && !post_location.getText().toString().isEmpty()
                        && pickedImgUri1 != null
                      ) {

                       // Uploading all the images to firebase

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Post_Images/company"+company_id);
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri1.getLastPathSegment());


                    imageFilePath.putFile(pickedImgUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownlaodLink = uri.toString();
                                    // create post Object
                                    Post post = new Post(
                                            company_id,
                                            company_name,
                                            company_contact_number,
                                            company_LogoPath,
                                            post_title.getText().toString(),
                                            post_description.getText().toString(),
                                            post_location.getText().toString(),
                                            imageDownlaodLink,
                                            spin_Cat,
                                            post_date.getText().toString()

                                    );

                                    // Add post to firebase database

                                    addPost(post);



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // something goes wrong uploading picture


                                        Toast.makeText(getContext(),"Uploading failed !",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    add_btn.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.VISIBLE);



                                }
                            });


                        }
                    });





                } else {
                    Toast.makeText(getContext(),"Please verify all input fields and choose Post Image !", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    add_btn.setVisibility(View.VISIBLE);


                }





            }
        });

        post_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

    }

    private void addPost(Post post) {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        // get post unique ID and upadte post key
        String key = myRef.getKey();
        post.setPostKey(key);


        // add post data to firebase database

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getContext(),"Post Added successfully !", Toast.LENGTH_SHORT).show();


                progressBar.setVisibility(View.INVISIBLE);
                add_btn.setVisibility(View.VISIBLE);

            }
        });


    }



    private void pickedImage1() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUEST_CODE_PICKED_1);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICKED_1 && data != null ) {
            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri1 = data.getData() ;
            image_1.setImageURI(pickedImgUri1);

        }




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
            post_date.setText(
                    String.valueOf(year) + "/" +String.valueOf(monthOfYear+1)+ "/"+String.valueOf(dayOfMonth)  );
        }
    };
}