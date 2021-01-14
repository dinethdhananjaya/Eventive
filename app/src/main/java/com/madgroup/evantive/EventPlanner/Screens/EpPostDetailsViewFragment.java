package com.madgroup.evantive.EventPlanner.Screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madgroup.evantive.EventPlanner.Model.GalleryModel;
import com.madgroup.evantive.R;
import com.madgroup.evantive.User.Adapters.GalleryForPostAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class EpPostDetailsViewFragment extends Fragment {

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

    public EpPostDetailsViewFragment(String companyId, String companyName, String companyLogopath, String imagepath, String location, String description, String companyContactNumber) {

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
        rootView=inflater.inflate(R.layout.ep_post_details_view_fragment, container, false);
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




        return rootView;
    }

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