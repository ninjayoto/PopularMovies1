package app.com.ninja.android.popularmovies1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends Activity{

//    private String mPosterPath;
    public MovieDetails() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_view);

        Intent intent = this.getIntent();

        Bundle data = intent.getExtras();
        MovieInfo obj = data.getParcelable("MovieInfo");

        TextView movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieTitle.setText(obj.getMovieTitle());

        ImageView view = (ImageView) findViewById(R.id.posterPath);
        Picasso.with(this)
                .load(obj.getPosterImage())
                .into(view);


        TextView releaseDate = (TextView) findViewById(R.id.releaseDate);
        String str = obj.getReleaseDate()
                .substring(0,4);
        releaseDate.setText(str);


        TextView userRating = (TextView) findViewById(R.id.userRating);
        userRating.setText(Double.toString(obj.getUserRating()) + "/10");

        TextView overview = (TextView) findViewById(R.id.overview);
        overview.setText(obj.getOverview());

    }
}