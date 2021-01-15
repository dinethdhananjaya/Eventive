package com.madgroup.evantive.User.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.madgroup.evantive.EventPlanner.Model.GalleryModel;
import com.madgroup.evantive.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryForPostAdapter extends  RecyclerView.Adapter<GalleryForPostAdapter.GalleryHolder>  {

    Context context ;
    List<GalleryModel> galleryModelList;

    public GalleryForPostAdapter(Context context, List<GalleryModel> galleryModelList) {
        this.context = context;
        this.galleryModelList = galleryModelList;
    }

    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate( R.layout.user_gallery_card_view,parent,false) ;
        final GalleryHolder viewHolder = new GalleryHolder(view) ;
        return viewHolder;
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


        public GalleryHolder(@NonNull View itemView) {
            super(itemView);

            image_view=itemView.findViewById(R.id.image_view);
        }
    }
}
