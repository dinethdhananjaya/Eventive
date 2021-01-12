package com.madgroup.evantive.EventPlanner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.madgroup.evantive.R;
import com.madgroup.evantive.User.Model.BookingModel;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    Context context;
    List<BookingModel> bookingModelList;
    public BookingAdapter(Context context, List<BookingModel> bookingModelList) {
        this.context = context;
        this.bookingModelList = bookingModelList;



    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate( R.layout.ep_card_booking_view,parent,false) ;
        final BookingViewHolder viewHolder = new BookingViewHolder(view) ;

        viewHolder.conatiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(bookingModelList.get(viewHolder.getAdapterPosition()).getUsersEmail(),bookingModelList.get(viewHolder.getAdapterPosition()).getUsersName());
            }
        });


        return viewHolder;
    }

    private void sendEmail(String usersEmail, String usersName) {
        Intent intentMail = new Intent(Intent.ACTION_SEND);
        intentMail.setType("message/rfc822");
        intentMail.putExtra(Intent.EXTRA_EMAIL, new String[]{
                usersEmail }); // the To mail.
        intentMail.putExtra(Intent.EXTRA_SUBJECT, "Dear :"+usersName);
// now we have created the mail, lets try and send it.
        try {
            context.startActivity(Intent.createChooser(intentMail, "Message to User to do what next"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.fullname.setText(bookingModelList.get(position).getUsersName()+"");
        holder.post_date.setText(" "+bookingModelList.get(position).getDate()+"");
        holder.post_email.setText(" "+bookingModelList.get(position).getUsersEmail()+"");

        if(!bookingModelList.get(position).getUsersImagePath().isEmpty()){
            Picasso.get().load( bookingModelList.get(position).getUsersImagePath()+"" )
                    .networkPolicy( NetworkPolicy.NO_CACHE)
                    .memoryPolicy( MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.loading)
                    .into( holder.image_user_profile );
        }


    }

    @Override
    public int getItemCount() {
        return bookingModelList.size();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder{

        ImageView image_user_profile;
        TextView fullname;
        TextView post_date;
        TextView post_email;


        CardView conatiner;


        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            image_user_profile=itemView.findViewById(R.id.image_user_profile);
            fullname=itemView.findViewById(R.id.fullname);
            post_date=itemView.findViewById(R.id.post_date);
            post_email=itemView.findViewById(R.id.post_email);
            conatiner=itemView.findViewById(R.id.conatiner);

        }
    }
}
