package app.com.ninja.android.popularmovies1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class GridViewActivity extends ActionBarActivity {

    private static final String STATE_MOVIES ="state movies" ;
    private GridView gridView;
    ImageAdapter imageAdapter;
    private SharedPreferences sharedPrefs;
    ArrayList<MovieInfo> movieDetailsObj;

    public GridViewActivity() {


 }



@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    setContentView(R.layout.activity_main);


    gridView = (GridView) findViewById(R.id.gridview);
    imageAdapter = new ImageAdapter(this);

//    if (savedInstanceState != null && sharedPrefs==spChanged) {
//        movieDetailsObj = savedInstanceState.getParcelableArrayList(STATE_MOVIES);
//        gridView.setAdapter(imageAdapter);
//
// } else {

        movieDetailsObj = new ArrayList<MovieInfo>();
        updateMoviesList();
        Toast.makeText(this, "Downloading data", Toast.LENGTH_LONG).show();

//    }

    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MovieInfo obj = (MovieInfo) imageAdapter.getItem(position);

            Intent intent = new Intent(getApplicationContext(), MovieDetails.class);

            intent.putExtra("MovieInfo", obj);
            startActivity(intent);
        }
    });


}

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_MOVIES, movieDetailsObj);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
            SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                      String key) {
//                    updateMoviesList();
                }
            };

//    @Override
    protected void onResume(Bundle savedInstanceState) {
        super.onResume();

        if (savedInstanceState !=null && sharedPrefs == spChanged) {
           movieDetailsObj = savedInstanceState.getParcelableArrayList(STATE_MOVIES);
            gridView.setAdapter(imageAdapter);


        } else {
            movieDetailsObj = new ArrayList<MovieInfo>();
            updateMoviesList();
            Toast.makeText(this, "Downloading data", Toast.LENGTH_LONG).show();

        }
    }


    private void updateMoviesList() {
        //This creates an AsyncTask FetchMovieTask()
        FetchMovieTask movieTask = new FetchMovieTask();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        movieTask.execute();
    }

    public class ImageAdapter extends BaseAdapter{
        private final String LOG_TAG = ImageAdapter.class.getSimpleName();
        private Context mContext;

        //Constructor which takes context as inputs
        public ImageAdapter(Context context) {
            mContext = context;
        }


        @Override
        //return the no. of Views to be displayed
        public int getCount() {
            return movieDetailsObj.size();
        }

        @Override
        public Object getItem(int position) {
            return movieDetailsObj.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView view = (ImageView) convertView;
            if (view == null) {
                view = new ImageView(mContext);
                view.setLayoutParams(new GridView.LayoutParams(200, 300));
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);

            }


            //Picasso
            Picasso.with(mContext)
                    .load(movieDetailsObj
                    .get(position)
                    .getPosterImage())
                    .into(view);
            return view;
        }


    }

    public class FetchMovieTask extends AsyncTask<Void, Void, String[]> {


        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();


        private String[] getMovieDataFromJson(String movieJSONString)
                throws JSONException{

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String TITLE = "original_title";
            final String POSTERPATH= "poster_path";
            final String OVERVIEW = "overview";
            final String RELEASEDATE = "release_date";
            final String RATING = "vote_average";


            JSONObject movieJson = new JSONObject(movieJSONString);
            JSONArray resultsArray = movieJson.getJSONArray(RESULTS);

            int numMovies = resultsArray.length();
            String[] resultImageStrs = new String[numMovies];

            for(int i=0;i<numMovies;i++){

                JSONObject res = resultsArray.getJSONObject(i);

                resultImageStrs[i] = res.getString(POSTERPATH);
                movieDetailsObj.add(new MovieInfo(res.getString(TITLE),
                        "http://image.tmdb.org/t/p/w185" + res.getString(POSTERPATH),
                        res.getString(OVERVIEW),
                        res.getString(RELEASEDATE),
                        res.getDouble(RATING)));

            }

            return resultImageStrs;

        }
        @Override
        protected String[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJSONString = null;

            try{

                final String MOVIES_BASE_URL ="http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_KEY = "api_key";

                //Enter API Key here REPLACE_API_KEY

                String REPLACE_API_KEY = getResources().getString(R.string.REPLACE_API_KEY);

                String sortBy = sharedPrefs.getString(getString(R.string.pref_sortby_key), getString(R.string.pref_sortby_default));


                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortBy)
                        .appendQueryParameter(API_KEY,REPLACE_API_KEY).build();

                String myUri = builtUri.toString();

                URL url = new URL(myUri);

                //open the connection
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line=reader.readLine())!= null){
                    // Since it's JSON, adding a newline isn't necessary
                    // but makes easier to read in the debugger

                    buffer.append(line + '\n');
                }
                if(buffer.length() == 0){
                    return null;
                }

                movieJSONString = buffer.toString();



            }catch (IOException e){
                // If the code didn't successfully get the data, there's no point in attempting
                // to parse it.
                Log.e(LOG_TAG, "Error", e);
                return null;
            }finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error Closing Stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJSONString);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            imageAdapter.notifyDataSetChanged();
            gridView.setAdapter(imageAdapter);


        }

    }

}