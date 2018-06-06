package in.co.jaypatel.flickr.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import in.co.jaypatel.flickr.R;
import in.co.jaypatel.flickr.model.ImageSize;
import in.co.jaypatel.flickr.model.PhotoModel;
import in.co.jaypatel.flickr.model.event.ClickEvent;
import in.co.jaypatel.flickr.util.AppUtil;
import timber.log.Timber;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Jay on 5.06.2018.
 */
public class DetailFragment
        extends AbstractBaseFragment {

    private static final String EXTRA_ITEM = "EXTRA_ITEM";

    @BindView(R.id.image) PhotoView image;

    public static Fragment newInstance(PhotoModel item) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_ITEM, item);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_detail;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PhotoModel item = (PhotoModel) getArguments().getSerializable(EXTRA_ITEM);
        AppUtil.bindImage(item.getImageUrl(ImageSize.LARGE), image, false);
        image.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                Timber.i("setOnViewTapListener");
                EventBus.getDefault().post(new ClickEvent());
            }
        });
    }

}