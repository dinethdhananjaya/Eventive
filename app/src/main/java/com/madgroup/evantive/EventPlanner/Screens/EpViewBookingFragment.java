package com.madgroup.evantive.EventPlanner.Screens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madgroup.evantive.EventPlanner.Adapter.BookingAdapter;
import com.madgroup.evantive.R;
import com.madgroup.evantive.User.Model.BookingModel;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class EpViewBookingFragment extends Fragment {
    View rootView;



    DatabaseReference databaseReference ;
    FirebaseDatabase firebaseDatabase;
    List<BookingModel> bookingList;

    BookingAdapter adapter;

    RecyclerView recyclerView;




    String companyid;
    SharedPreferences sharedpreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//get value from sessions
        sharedpreferences= getContext().getSharedPreferences("user_details", MODE_PRIVATE);
        companyid=sharedpreferences.getString("Companyid","");

        rootView=inflater.inflate(R.layout.ep_view_booking_fragment, container, false);
        recyclerView=rootView.findViewById(R.id.recyclerView);
        LoadingBookings();

        return rootView;
    }

    private void LoadingBookings() {


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Bookings");


        databaseReference.orderByChild("companyId").equalTo(companyid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList= new ArrayList<>();
                bookingList.clear();
                for (DataSnapshot postsnap : snapshot.getChildren()) {

                    BookingModel bookingModel = postsnap.getValue(BookingModel.class);
                    bookingList.add(bookingModel);

                    Log.e("data",bookingModel.getUsersEmail()+"");


                }

              adapter = new BookingAdapter(getContext(), bookingList);
               recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}