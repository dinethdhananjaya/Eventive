package com.madgroup.evantive.EventPlanner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.madgroup.evantive.EventPlanner.Model.Post;
import com.madgroup.evantive.EventPlanner.Screens.EpPostDetailsViewFragment;
import com.madgroup.evantive.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapterEv extends RecyclerView.Adapter<PostAdapterEv.PostViewHolder>{


    Context context ;
    List<Post> postList;

    public PostAdapterEv(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate( R.layout.user_post_card_view,parent,false) ;
        final  PostViewHolder viewHolder = new  PostViewHolder(view) ;
        viewHolder.conatiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content,new EpPostDetailsViewFragment(

                        postList.get(viewHolder.getAdapterPosition()).getCompanyId(),
                        postList.get(viewHolder.getAdapterPosition()).getCompanyName(),
                        postList.get(viewHolder.getAdapterPosition()).getCompanyLogopath(),
                        postList.get(viewHolder.getAdapterPosition()).getImagepath(),
                        postList.get(viewHolder.getAdapterPosition()).getLocation(),
                        postList.get(viewHolder.getAdapterPosition()).getDescription(),
                        postList.get(viewHolder.getAdapterPosition()).getCompanyContactNumber()



                ))
                        .addToBackStack(null)
                        .commit();
            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.title_post.setText(postList.get(position).getTitle()+"");
        holder.post_date.setText(postList.get(position).getDate()+"");
        holder.post_location.setText(postList.get(position).getLocation()+"");



        if(!postList.get(position).getImagepath().isEmpty()){
            Picasso.get().load( postList.get(position).getImagepath()+"" )
                    .networkPolicy( NetworkPolicy.NO_CACHE)
                    .memoryPolicy( MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.loading)
                    .into( holder.post_image );
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        CardView conatiner;
        TextView title_post;
        TextView post_date;
        TextView post_location;
        ImageView post_image;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            conatiner=itemView.findViewById(R.id.conatiner);
            title_post=itemView.findViewById(R.id.title_post);
            post_date=itemView.findViewById(R.id.post_date);
            post_location=itemView.findViewById(R.id.post_location);
            post_image=itemView.findViewById(R.id.post_image);
        }
    }


}
