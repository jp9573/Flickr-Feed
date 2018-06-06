package in.co.jaypatel.flickr.util;

import android.view.View;

/**
 * Created by Jay on 5.06.2018.
 */
public interface RowClickListener<T> {

    void onRowClicked(View view, int row, T item);

}