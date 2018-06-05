package in.co.jaypatel.flickr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.co.jaypatel.flickr.R;

/**
 * Created by Jay on 5.06.2018.
 */
class PhotoViewHolder
        extends RecyclerView.ViewHolder {

    @BindView(R.id.image)
    ImageView image;

    public PhotoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}