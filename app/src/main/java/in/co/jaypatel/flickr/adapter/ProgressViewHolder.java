package in.co.jaypatel.flickr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by Jay on 5.06.2018.
 */
class ProgressViewHolder
        extends RecyclerView.ViewHolder {

    public ProgressViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}