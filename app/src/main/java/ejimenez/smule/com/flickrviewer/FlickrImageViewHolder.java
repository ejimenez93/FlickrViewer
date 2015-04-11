package ejimenez.smule.com.flickrviewer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public class FlickrImageViewHolder extends RecyclerView.ViewHolder {
    protected ImageView thumbnail;

    public FlickrImageViewHolder(View view) {
        super(view);
        this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
    }
}
