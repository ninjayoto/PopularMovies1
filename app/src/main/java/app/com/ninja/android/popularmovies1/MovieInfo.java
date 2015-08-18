package app.com.ninja.android.popularmovies1;

import android.os.Parcel;
import android.os.Parcelable;

//class to hold the Movie Data
public class MovieInfo implements Parcelable{

    public String getMovieTitle() { return mMovieTitle;}

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public double getUserRating() {
        return mUserRating;
    }

    private String mMovieTitle;

    public String getPosterImage() {
        return mPosterImage;
    }

    private String mPosterImage;
    private String mOverview;
    private String mReleaseDate;
    private double mUserRating;

    public MovieInfo(String movieTitle, String posterImage, String overview, String releaseDate, double userRating) {
        mMovieTitle = movieTitle;
        mPosterImage = posterImage;
        mOverview = overview;
        mUserRating = userRating;
        mReleaseDate = releaseDate;
    }

    //Parcelling Part
    public MovieInfo(Parcel in){
        String[] data = new String[5];
        double rating;

        in.readStringArray(data);
        this.mMovieTitle = data[0];
        this.mPosterImage = data[1];
        this.mOverview = data[2];
        this.mReleaseDate = data[3];
        this.mUserRating = Double.parseDouble(data[4]);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.mMovieTitle,
                this.mPosterImage,
                this.mOverview,
                this.mReleaseDate,
                Double.toString(this.mUserRating)});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public MovieInfo createFromParcel(Parcel in){
            return new MovieInfo(in);
        }
        public MovieInfo[] newArray(int size){
            return new MovieInfo[size];
        }
    };
}