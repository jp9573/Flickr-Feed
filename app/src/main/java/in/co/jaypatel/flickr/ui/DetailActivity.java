package in.co.jaypatel.flickr.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnPageChange;
import in.co.jaypatel.flickr.R;
import in.co.jaypatel.flickr.adapter.DetailPagerAdapter;
import in.co.jaypatel.flickr.model.ImageSize;
import in.co.jaypatel.flickr.model.PhotoModel;
import in.co.jaypatel.flickr.model.event.ClickEvent;
import in.co.jaypatel.flickr.model.event.DetailEvent;
import in.co.jaypatel.flickr.service.FlickrService;
import in.co.jaypatel.flickr.util.AppUtil;
import in.co.jaypatel.flickr.util.ParallaxPageTransformer;
import timber.log.Timber;

/**
 * Created by Jay on 5.06.2018.
 */
public class DetailActivity
        extends AbstractBaseActivity {

    private static final String EXTRA_INDEX = "EXTRA_INDEX";
    private static final String EXTRA_ITEMS = "EXTRA_ITEMS";
    private static final String ERROR_LITERAL = "-";

    private List<PhotoModel> items;
    private final FlickrService flickrService = FlickrService.INSTANCE;
    private DetailEvent detailEvent;

    @BindView(R.id.pager)
    private ViewPager pager;
    @BindView(R.id.tvOwner)
    private TextView tvOwner;
    @BindView(R.id.tvTitle)
    private TextView tvTitle;
    @BindView(R.id.tvDate)
    private TextView tvDate;
    @BindView(R.id.tvViewCount)
    private TextView tvViewCount;
    @BindView(R.id.lnrFooter)
    private View lnrFooter;
    @BindView(R.id.ivClose)
    private View ivClose;
    @BindView(R.id.ivInfo)
    private View ivInfo;
    @BindView(R.id.ivShare)
    private View ivShare;

    public static Intent createIntent(Context context, int index, List<PhotoModel> items) {
        return new Intent(context, DetailActivity.class)
                .putExtra(EXTRA_INDEX, index)
                .putExtra(EXTRA_ITEMS, (Serializable) items);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_detail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int index = getIntent().getIntExtra(EXTRA_INDEX, -1);
        items = (List<PhotoModel>) getIntent().getSerializableExtra(EXTRA_ITEMS);
        if (index == -1) {
            finish();
        } else if (!AppUtil.isConnected()) {
            showConnectionError(this);
        } else {
            pager.setPageTransformer(false, new ParallaxPageTransformer(R.id.image));
            pager.setAdapter(new DetailPagerAdapter(getSupportFragmentManager(), items));
            pager.setCurrentItem(index);
            onPageSelected(index);
        }

        AppUtil.setVectorBg(ivClose, R.drawable.ic_close_24dp, android.R.color.white, R.color.gray2);
        AppUtil.setVectorBg(ivInfo, R.drawable.ic_info_outline_24dp, android.R.color.white, R.color.gray2);
        AppUtil.setVectorBg(ivShare, R.drawable.ic_share_24dp, android.R.color.white, R.color.gray2);
    }

    @OnClick(R.id.ivClose)
    public void onCloseClick(View v) {
        finish();
    }

    @OnClick(R.id.ivInfo)
    public void onInfoClick(View v) {
        if (detailEvent == null || detailEvent.item == null) return;
        showInfoDialog(getString(R.string.description), detailEvent.item.description.toString());
    }

    @OnClick(R.id.ivShare)
    public void onShareClick(View v) {
        if (detailEvent == null || detailEvent.item == null) return;
        String subject = detailEvent.item.title._content;
        String text = items.get(pager.getCurrentItem()).getImageUrl(ImageSize.LARGE);
        startActivity(AppUtil.createShareIntent(subject, text));
    }

    @OnPageChange(R.id.pager)
    private void onPageSelected(int position) {
        //showLoadingDialog();
        tvOwner.setText(R.string.loading);
        tvTitle.setText(R.string.loading);
        tvDate.setText(R.string.loading);
        tvViewCount.setText(R.string.loading);
        flickrService.getDetailAsync(items.get(position).id);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEvent(DetailEvent event) {
        //dismissLoadingDialog();
        detailEvent = event;
        if (event.exception == null) {
            tvOwner.setText(event.item.owner.toString());
            tvTitle.setText(event.item.title.toString());
            tvDate.setText(event.item.getFormattedDate());
            tvViewCount.setText(getResources().getQuantityString(R.plurals.views, event.item.views, event.item.views));
        } else {
            tvOwner.setText(ERROR_LITERAL);
            tvTitle.setText(ERROR_LITERAL);
            tvDate.setText(ERROR_LITERAL);
            tvViewCount.setText(ERROR_LITERAL);
            showSnack(this, R.string.errorMessage);
        }
    }

    // fired by child fragment
    // child's photoView absorbs touch event so parent's touch events not fired
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClickEvent(ClickEvent event) {
        int value = lnrFooter.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        Timber.i("pagerClick: " + value);
        lnrFooter.setVisibility(value);
        ivClose.setVisibility(value);
        tvOwner.setVisibility(value);
    }

}