package com.example.brewapps.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.example.brewapps.R;
import com.example.brewapps.Local.Movie;

import com.example.brewapps.adapter.RecyclerViewadapter;
import com.example.brewapps.viewModel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements RecyclerViewadapter.OnItemClickListener {


    public static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    RecyclerViewadapter recyclerViewadapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<Movie> movies = new ArrayList<Movie>();
    private MainActivityViewModel viewModel;

    CompositeDisposable compositeDisposable= new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Now Playing Movies");
        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getMoviesFromApi();
                if(swipeRefreshLayout.isRefreshing())
                {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });
         //Log.d(TAG,"In oncreate()");
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        viewModel.getMoviesFromApi();
        LoadRecyclerView(movies);



        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int pos=viewHolder.getAbsoluteAdapterPosition();
                Movie movie = recyclerViewadapter.getMoviewithPosition(pos);
                 viewModel.deleteMovie(movie);
                 recyclerViewadapter.notifyDataSetChanged();
            }
        });
        helper.attachToRecyclerView(recyclerView);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
       //viewModel.clear();
        compositeDisposable.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        compositeDisposable.add(viewModel.getMovies()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Movie>>() {
                    @Override
                    public void accept(List<Movie> moviesList) throws Throwable {
                        if(moviesList!=null) {
                            movies=(ArrayList) moviesList;
                           recyclerViewadapter.updateList((ArrayList)moviesList);
                           recyclerViewadapter.notifyDataSetChanged();

                        }
                    }
                } ));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });


        return true;
    }
    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Movie> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (Movie item : movies) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getOriginalTitle().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            recyclerViewadapter.filterList(filteredlist);
        }
    }

    private void getNowplayingMovies() {


     /* compositeDisposable.add(viewModel.getMoviesRoom()
              .subscribeOn(Schedulers.computation())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new Consumer<List<Movie>>() {
                             @Override
                             public void accept(List<Movie> moviesList) throws Throwable {
                                 movies = (ArrayList<Movie>) moviesList;
                                 if(moviesList.size()==0)
                                 {
                                     viewModel.fetchFromApitoRoom();
                                 }
                                 else {
                                     LoadRecyclerView(movies);
                                 }
                             }
                         }, new Consumer<Throwable>() {
                             @Override
                             public void accept(Throwable throwable) throws Throwable {
                                 Log.d(TAG,"error in getting"+throwable.getCause());
                             }
                         }
              ));*/

    }



    private void LoadRecyclerView(List<Movie> movieList) {
       // Log.d(TAG,"MovieList in LoadRecyclerView"+movieList.size());
        recyclerView =(RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        else
        {
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        }
      recyclerViewadapter = new RecyclerViewadapter(getApplicationContext(),movieList,this);
       // Log.d(TAG,"i am going to load adapter");
        recyclerView.setAdapter(recyclerViewadapter);

        recyclerViewadapter.notifyDataSetChanged();


    }


    @Override
    public void onClick(Movie movie) {
       // Log.d("Adapter","I am in on click()");
       // Log.d("Adapter","I am in on click() movie"+movie.getOriginalTitle());
           if(!movie.getOriginalTitle().isEmpty())
           {
            Intent intent = new Intent(this, MovieActivity.class);
            intent.putExtra("movie",movie);
            this.startActivity(intent);
        }
    }
}