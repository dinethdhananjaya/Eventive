package com.madgroup.evantive.EventPlanner.Screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madgroup.evantive.EventPlanner.Adapter.PostAdapterEv;
import com.madgroup.evantive.EventPlanner.Model.Post;
import com.madgroup.evantive.R;

import java.util.ArrayList;
import java.util.List;

public class EpViewPostFragment extends Fragment {


    View rootView;
    RecyclerView recyclerView;
    SearchView search_bar;
    DatabaseReference databaseReference ;
    FirebaseDatabase firebaseDatabase;
    List<Post> postList;
    PostAdapterEv postAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView=inflater.inflate(R.layout.ep_view_post_fragment, container, false);

        init();
        return rootView;
    }

    private void init() {

        search_bar=rootView.findViewById(R.id.search_bar);
        recyclerView=rootView.findViewById(R.id.recyclerView);
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


                postAdapter = new PostAdapterEv(getContext(),postList);

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
        LoadPosts("");
    }
}