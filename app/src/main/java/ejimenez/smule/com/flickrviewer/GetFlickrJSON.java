package ejimenez.smule.com.flickrviewer;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetFlickrJSON extends GetRawJSON {

    private String TAG = GetFlickrJSON.class.getSimpleName();
    private List<Photo> mPhotos;
    private Uri mDestinationUri;

    public GetFlickrJSON(String searchCriteria, boolean matchAll) {
        super(null);
        createAndUpdateUri(searchCriteria, matchAll);
        mPhotos = new ArrayList<Photo>();
    }

    public void execute() {
        super.setmRawURL(mDestinationUri.toString());
        DownloadJSONData downloadJSONData = new DownloadJSONData();
        Log.v(TAG, "Built URI = " + mDestinationUri.toString());
        downloadJSONData.execute(mDestinationUri.toString());
    }

    public boolean createAndUpdateUri(String searchCriteria, boolean matchAll) {
        final String FLICKR_API_BASE_URL = "https://api.flickr.com/services/feeds/photos_public.gne";
        final String TAGS_PARAM = "tags";
        final String TAGMODE_PARAM = "tagmode";
        final String FORMAT_PARAM = "format";
        final String NO_JSON_CALLBACK_PARAM = "nojsoncallback";

        mDestinationUri = Uri.parse(FLICKR_API_BASE_URL).buildUpon()
                .appendQueryParameter(TAGS_PARAM, searchCriteria)
                .appendQueryParameter(TAGMODE_PARAM, matchAll ? "ALL" : "ANY")
                .appendQueryParameter(FORMAT_PARAM, "json")
                .appendQueryParameter(NO_JSON_CALLBACK_PARAM, "1")
                .build();

        return mDestinationUri != null;
    }

    public List<Photo> getPhotos() {
        return mPhotos;
    }

    public void processResult() {
        if (getmDownloadStatus() != DownloadStatus.OK) {
            Log.e(TAG, "Error downloading the raw data");
            return;
        }


        // All the fields that come back from the JSON object
        final String FLICKR_ITEMS = "items";
        final String FLICKR_TITLE = "title";
        final String FLICKR_AUTHOR_ID = "author_id";
        final String FLICKR_TAGS = "tags";
        final String FLICKR_MEDIA = "media";
        final String FLICKR_PHOTO_URL = "m";
        final String FLICKR_AUTHOR = "author";
        final String FLICKR_LINK = "link";

        try {
            JSONObject jsonData = new JSONObject(getmData());
            JSONArray itemsArray = jsonData.getJSONArray(FLICKR_ITEMS);
            for (int x = 0; x < itemsArray.length(); x++) {
                JSONObject jsonPhoto = itemsArray.getJSONObject(x);
                String title = jsonPhoto.getString(FLICKR_TITLE);
                String author = jsonPhoto.getString(FLICKR_AUTHOR);
                //String link = jsonPhoto.getString(FLICKR_LINK);
                String authorId = jsonPhoto.getString(FLICKR_AUTHOR_ID);
                String tags = jsonPhoto.getString(FLICKR_TAGS);

                JSONObject jsonMedia = jsonPhoto.getJSONObject(FLICKR_MEDIA);
                String photoURL = jsonMedia.getString(FLICKR_PHOTO_URL);
                String link = photoURL.replaceFirst("_m.", "_b.");

                Photo photoObject = new Photo(title, author, authorId, link, tags, photoURL);

                this.mPhotos.add(photoObject);
            }

            for (Photo singlePhoto : mPhotos) {
                Log.v(TAG, singlePhoto.toString());
            }
        } catch (JSONException jsonError) {
            jsonError.printStackTrace();
            Log.e(TAG, "Error Processing JSON Data");
        }
    }

    public class DownloadJSONData extends DownloadRawData {

        protected void onPostExecute(String webData) {
            super.onPostExecute(webData);
            processResult();
        }

        protected String doInBackground(String... params) {
            String[] parameters = {
                    mDestinationUri.toString()
            };
            return super.doInBackground(parameters);
        }


    }

}

