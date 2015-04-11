package ejimenez.smule.com.flickrviewer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrImageViewHolder> {
    private final String TAG = FlickrRecyclerViewAdapter.class.getSimpleName();
    private List<Photo> mPhotosList;
    private Context mContext;

    public FlickrRecyclerViewAdapter(Context context, List<Photo> photosList) {
        this.mPhotosList = photosList;
        mContext = context;
    }

    @Override
    public FlickrImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse, null);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FlickrImageViewHolder flickrImageViewHolder, int i) {

        Photo photoItem = mPhotosList.get(i);

        Log.d(TAG, " Processing: " + photoItem.getTitle() + " --> " + Integer.toString(i));

        Picasso.with(mContext).load(photoItem.getImage())
                .placeholder(R.drawable.placeholder)
                .into(flickrImageViewHolder.thumbnail);
    }


    @Override
    public int getItemCount() {
        return (mPhotosList != null ? mPhotosList.size() : 0);
    }

    public void loadNewData(List<Photo> newPhotos) {
        mPhotosList = newPhotos;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position) {
        return (mPhotosList != null) ? mPhotosList.get(position) : null;
    }
}

