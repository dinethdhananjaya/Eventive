package com.madgroup.evantive.EventPlanner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.evantive.EventPlanner.Model.GalleryModel;
import com.madgroup.evantive.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryAdapter extends  RecyclerView.Adapter<GalleryAdapter.GalleryHolder> {


    Context context ;
    List<GalleryModel> galleryModelList;

    public GalleryAdapter(Context context, List<GalleryModel> galleryModelList) {
        this.context = context;
        this.galleryModelList = galleryModelList;
    }

    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate( R.layout.ep_gallery_card_view,parent,false) ;
        final GalleryHolder viewHolder = new GalleryHolder(view) ;

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(context,"Clicked !"+galleryModelList.get(viewHolder.getAdapterPosition()).getmImageUrl(),Toast.LENGTH_SHORT).show();

                DeleteImage(galleryModelList.get(viewHolder.getAdapterPosition()).getmImageUrl(),galleryModelList.get(viewHolder.getAdapterPosition()).getmKey());
            }
        });
        return viewHolder;
    }

    private void DeleteImage(String imageUrl, String getmKey) {

        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Gallery");


        StorageReference imageRef = mStorage.getReferenceFromUrl(imageUrl);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference("Gallery").child(getmKey).removeValue();
                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBindViewHolder(@NonNull GalleryHolder holder, int position) {

        if(!galleryModelList.get(position).getmImageUrl().isEmpty()){
            Picasso.get().load( galleryModelList.get(position).getmImageUrl()+"" )
                    .networkPolicy( NetworkPolicy.NO_CACHE)
                    .memoryPolicy( MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.loading)
                    .into( holder.image_view );
        }
    }

    @Override
    public int getItemCount() {
        return galleryModelList.size();
    }


    public class GalleryHolder extends RecyclerView.ViewHolder{


        ImageView image_view;
        ImageView delete;

        public GalleryHolder(@NonNull View itemView) {
            super(itemView);

            image_view=itemView.findViewById(R.id.image_view);
            delete=itemView.findViewById(R.id.delete);
        }
    }
}
