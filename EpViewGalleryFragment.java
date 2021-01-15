package com.madgroup.evantive.EventPlanner.Screens;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.madgroup.evantive.EventPlanner.Adapter.GalleryAdapter;
import com.madgroup.evantive.EventPlanner.Model.CompanyModel;
import com.madgroup.evantive.EventPlanner.Model.GalleryModel;
import com.madgroup.evantive.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class EpViewGalleryFragment extends Fragment {


    View rootView;
    private static final int PReqCode = 2 ;
    //to request the internal image
    private static final int REQUEST_CODE_PICKED_1 = 10;
    Uri pickedImgUri1;



    ImageView ImagePicker;
    ImageView company_logo;
    EditText company_name;
    RecyclerView recyclerView;

    ProgressDialog pd;


    StorageReference storageReference ;

    private DatabaseReference mDatabaseRef;



    //for loading
    DatabaseReference databaseReference ;
    FirebaseDatabase firebaseDatabase;
    List<GalleryModel> galleryModelList;
    GalleryAdapter galleryAdapter;


    String Comanyid;

    SharedPreferences sharedpreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView=inflater.inflate(R.layout.ep_view_gallery_fragment, container, false);

        //get value from sessions
        sharedpreferences= getContext().getSharedPreferences("user_details", MODE_PRIVATE);
        Comanyid=sharedpreferences.getString("Companyid","");



        pd = new ProgressDialog(getContext());
        init();

        return rootView;
    }

    private void init() {
        galleryModelList = new ArrayList<>();


        //mStorageRef = FirebaseStorage.getInstance().getReference("Gallery");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Gallery");
        ImagePicker=rootView.findViewById(R.id.ImagePicker);
        company_logo=rootView.findViewById(R.id.company_logo);
        company_name=rootView.findViewById(R.id.company_name);
        recyclerView=rootView.findViewById(R.id.recyclerView);



        ImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestForPermission();

                pickedImage1();
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
          //  image_1.setImageURI(pickedImgUri1);


            Uploading();
        }



    }

    private void Uploading() {

        pd.setMessage("loading");
        pd.show();

        if (pickedImgUri1 != null) {


             storageReference = FirebaseStorage.getInstance().getReference().child("Gallery/company"+Comanyid);
            final StorageReference imageFilePath = storageReference.child(pickedImgUri1.getLastPathSegment());


            imageFilePath.putFile(pickedImgUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageDownlaodLink = uri.toString();
                            // create post Object

                            GalleryModel gallery=new GalleryModel(Comanyid,

                                    imageDownlaodLink
                                    );

                            // Add post to firebase database

                            addGallery(gallery);



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // something goes wrong uploading picture


                            Toast.makeText(getContext(),"Uploading failed !",Toast.LENGTH_SHORT).show();
                           pd.dismiss();



                        }
                    });


                }
            });

        }else
        {

            Toast.makeText(getContext(),"Please  choose Post Image !", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }


    }

    private void addGallery(GalleryModel gallery) {

        String uploadId = mDatabaseRef.push().getKey();

     //   String key = mDatabaseRef.getKey();
        gallery.setmKey(uploadId);

        mDatabaseRef.child(uploadId).setValue(gallery).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(),"Image Added  to Gallery successfully !", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        Loading();
    }

    private void Loading() {


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Gallery");



        databaseReference.orderByChild("mCompanyId").equalTo(Comanyid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                galleryModelList.clear();
                for (DataSnapshot postsnap: snapshot.getChildren()) {

                    GalleryModel galleryModel = postsnap.getValue(GalleryModel.class);
                    galleryModelList.add(galleryModel) ;

                   // Log.e("data",galleryModel.getmImageUrl()+"");


                }


                galleryAdapter = new GalleryAdapter(getContext(),galleryModelList);

                recyclerView.setLayoutManager(new GridLayoutManager( getContext(),3 ));

                recyclerView.setAdapter(galleryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        //for loading company data
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Company");



        databaseReference.orderByChild("CompanyId").equalTo(Comanyid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot postsnap: snapshot.getChildren()) {

                    CompanyModel companyModel = postsnap.getValue(CompanyModel.class);


                    Log.e("data",companyModel.getCompanyName()+"");
                    company_name.setText(companyModel.getCompanyName()+"");


                    Picasso.get().load( companyModel.getCompanyPath()+"" )
                            .networkPolicy( NetworkPolicy.NO_CACHE)
                            .memoryPolicy( MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .placeholder(R.drawable.loading)
                            .resize(100,100)
                            .into(company_logo );

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}