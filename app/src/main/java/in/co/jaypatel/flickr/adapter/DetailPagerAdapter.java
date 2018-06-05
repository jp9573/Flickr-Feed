package in.co.jaypatel.flickr.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import in.co.jaypatel.flickr.model.PhotoModel;
import in.co.jaypatel.flickr.ui.DetailFragment;

/**
 * Created by Jay on 5.06.2018.
 */
public class DetailPagerAdapter
        extends FragmentStatePagerAdapter {

    private final List<PhotoModel> items;

    public DetailPagerAdapter(FragmentManager fm, List<PhotoModel> items) {
        super(fm);
        this.items = items;
    }

    @Override
    public Fragment getItem(int position) {
        return DetailFragment.newInstance(items.get(position));
    }

    @Override
    public int getCount() {
        return items.size();
    }

}