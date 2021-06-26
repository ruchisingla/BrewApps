package com.example.brewapps.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.brewapps.R;
import com.example.brewapps.Local.Movie;
import com.example.brewapps.imageutils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<Movie> mList;
    private Movie selectedMovie;
    private OnItemClickListener listener ;
    private ImageLoader imageLoader;


    public RecyclerViewadapter(Context context, List<Movie> mList,OnItemClickListener listener) {
        this.context = context;
        this.mList = mList;
        this.listener =listener;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getItemViewType(int position) {
        if(mList.get(position).getVoteAverage()>7)
        return R.layout.movie_item_dropath;
        else
            return R.layout.movie_list_item;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder =null;
        View v=null;
         if(viewType == R.layout.movie_list_item) {
              v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
             viewHolder=new MyViewHolder(v);
         }
         else
         {
             v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item_dropath, parent, false);
             viewHolder = new DropPathViewHolder(v);
         }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Log.d("Adapter", "i am in adapter onBindViewHolder" + mList.get(position).getOriginalTitle());
        if (holder.getItemViewType() == R.layout.movie_list_item) {
            MyViewHolder myViewHolder =(MyViewHolder)holder;
            myViewHolder.title.setText(mList.get(position).getOriginalTitle());
            myViewHolder.des.setText(mList.get(position).getOverview());
            String imagePath = "https://image.tmdb.org/t/p/w342" + mList.get(position).getPosterPath();
            Glide.with(context).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.loading).into(myViewHolder.image);
        }
        else
        {
            DropPathViewHolder dropPathViewHolder = (DropPathViewHolder) holder;
            String imagePath = "https://image.tmdb.org/t/p/original" + mList.get(position).getBackdropPath();
           Glide.with(context).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.loading).into(dropPathViewHolder.imageView);

             // imageLoader.DisplayImage(imagePath,R.drawable.loading,dropPathViewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if(mList.size()!=0) {
           // Log.d("Adapter","i am in adapter getItemCount()"+mList.size());
            return mList.size();
        }
        else
        return 0;
    }


    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
         private ImageView image;
         private TextView title,des;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image =(ImageView)itemView.findViewById(R.id.ivMovie);
            title =(TextView) itemView.findViewById(R.id.tvTitle);
            des =(TextView) itemView.findViewById(R.id.tvDes);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            int pos =getAbsoluteAdapterPosition();
            //Log.d("Adapter","position"+pos);
            if(pos!= RecyclerView.NO_POSITION) {
                selectedMovie = mList.get(pos);

                listener.onClick(selectedMovie);
            }
        }
    }
    public class DropPathViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
          private ImageView imageView;
        public DropPathViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.ivTrailer);
                itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int pos =getAbsoluteAdapterPosition();
            //Log.d("Adapter","position"+pos);
            if(pos!= RecyclerView.NO_POSITION) {
                selectedMovie = mList.get(pos);
                listener.onClick(selectedMovie);
            }
        }
    }
    public interface OnItemClickListener {
        public void onClick(Movie movie);
    }
    public  void updateList(ArrayList<Movie> updatedList){
        mList = updatedList;
        notifyDataSetChanged();
    }
    public Movie getMoviewithPosition(int position)
    {

        return  mList.get(position);
    }
    // method for filtering our recyclerview items.
    public void filterList(ArrayList<Movie> filterllist) {
        // below line is to add our filtered
        // list in our course array list.
        mList = filterllist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }
}
