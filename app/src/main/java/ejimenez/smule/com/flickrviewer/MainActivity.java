package ejimenez.smule.com.flickrviewer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<Photo> mPhotosList = new ArrayList<Photo>();
    private RecyclerView mRecyclerView;
    private FlickrRecyclerViewAdapter flickrRecyclerViewAdapter;
    private SearchView mSearchView;
    private String query;
    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateToolbar();

        query = getSavedPreferenceData(FLICKR_QUERY);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        flickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(MainActivity.this, new ArrayList<Photo>());
        mRecyclerView.setAdapter(flickrRecyclerViewAdapter);



        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, ViewPhotoDetailsActivity.class);
                intent.putExtra(PHOTO_TRANSFER, flickrRecyclerViewAdapter.getPhoto(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();

        query = replaceSpacesWithComma(getSavedPreferenceData(FLICKR_QUERY));


        if(query.length() > 0) {
            ProcessPhotos processPhotos = new ProcessPhotos(query, true);
            processPhotos.execute();
        }
    }

    private String getSavedPreferenceData(String key) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPref.getString(key, "");
    }


    /**
     * Replaces any spaces with commas. Needed to send comma-separated tags to Flickr API
     * @param query
     * @return the query if it doesn't contain spaces, else return the modifed query
     */
    private String replaceSpacesWithComma(String query) {
        if (query.contains(" ")) {
            return query.replace(" ", ",");
        }

        return query;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.search_view);

        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setIconifiedByDefault(true); // Default to a closed state upon application start
        mSearchView.setIconified(true);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                sharedPref.edit().putString(FLICKR_QUERY, s).apply();
                query = replaceSpacesWithComma(getSavedPreferenceData(FLICKR_QUERY));
                mSearchView.clearFocus();
                if(query.length() > 0) {
                    ProcessPhotos processPhotos = new ProcessPhotos(query, true);
                    processPhotos.execute();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                sharedPref.edit().putString(FLICKR_QUERY, s).apply();
                query = replaceSpacesWithComma(getSavedPreferenceData(FLICKR_QUERY));
                if(query.length() > 0) {
                    ProcessPhotos processPhotos = new ProcessPhotos(query, true);
                    processPhotos.execute();
                }
                return true;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public class ProcessPhotos extends GetFlickrJSON {
        public ProcessPhotos(String searchCriteria, boolean matchAll) {
            super(searchCriteria, matchAll);
        }

        public void execute() {
            super.execute();
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJSONData {

            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                flickrRecyclerViewAdapter.loadNewData(getPhotos());
            }
        }
    }


}
