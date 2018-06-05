package in.co.jaypatel.flickr.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Jay on 5.06.2018.
 */
public abstract class AbstractBaseFragment
        extends Fragment {

    protected abstract int getLayout();

    @Override
    public void onStart() {
        super.onStart();
        Timber.i("onStart");
        //EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.i("onStop");
        //EventBus.getDefault().unregister(this);
    }

}