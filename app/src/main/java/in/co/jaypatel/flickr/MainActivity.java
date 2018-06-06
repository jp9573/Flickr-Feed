package in.co.jaypatel.flickr;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.tooltip.OnClickListener;
import com.tooltip.Tooltip;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.io.IOException;

import butterknife.BindView;
import in.co.jaypatel.flickr.adapter.PhotoAdapter;
import in.co.jaypatel.flickr.model.PhotoInfoModel;
import in.co.jaypatel.flickr.model.PhotoModel;
import in.co.jaypatel.flickr.model.event.SearchEvent;
import in.co.jaypatel.flickr.service.FlickrService;
import in.co.jaypatel.flickr.ui.AbstractBaseActivity;
import in.co.jaypatel.flickr.ui.DetailActivity;
import in.co.jaypatel.flickr.util.AppUtil;
import in.co.jaypatel.flickr.util.RowClickListener;
import in.co.jaypatel.flickr.util.ScreenStateManager;
import timber.log.Timber;

public class MainActivity
        extends AbstractBaseActivity
        implements RowClickListener<PhotoModel>,
        SwipeRefreshLayout.OnRefreshListener {

    private int page = 1;
    private boolean isLoading;
    private final FlickrService flickrService = FlickrService.INSTANCE;
    private PhotoAdapter adapter;
    private ScreenStateManager screenStateManager;
    Tooltip toolTip = null;
    View currentView;

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.linear)
    LinearLayout linear;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //ButterKnife.bind(this);

        swipe.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));
        swipe.setOnRefreshListener(this);

        adapter = new PhotoAdapter();
        adapter.setRowClickListener(this);
        recycler.setAdapter(adapter);
        //recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        setScrollListener();

        screenStateManager = new ScreenStateManager(linear);

        sendRequest();
    }

    @Override
    public void onRowClicked(final View view, final int row, final PhotoModel item) {
        //startActivity(DetailActivity.createIntent(this, row, adapter.getAll()));

        class FetchData extends AsyncTask<Void, Void, PhotoInfoModel> {

            @Override
            protected PhotoInfoModel doInBackground(Void... voids) {
                PhotoInfoModel photoInfoModel1 = null;
                try {
                    photoInfoModel1 = flickrService.getDetail(item.id);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return photoInfoModel1;
            }

            @Override
            protected void onPostExecute(PhotoInfoModel photoInfoModel) {
                super.onPostExecute(photoInfoModel);
                if (photoInfoModel != null) {

                    String text = "";
                    if (photoInfoModel.description != null && photoInfoModel.description.toString().length() > 1) {
                        text = photoInfoModel.description.toString();
                    } else {
                        text = "No description available";
                    }

                    toolTip.dismiss();
                    toolTip = new Tooltip.Builder(view)
                            .setText(text)
                            .setTextSize(R.dimen.fontSize)
                            .setPadding(R.dimen.popupPadding)
                            .setCornerRadius(R.dimen._4dp)
                            .setBackgroundColor(Color.parseColor("#1976d2"))
                            .show();
                    currentView = view;

                    toolTip.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(@NonNull Tooltip tooltip) {
                            startActivity(DetailActivity.createIntent(MainActivity.this, row, adapter.getAll()));
                        }
                    });
                }
            }
        }

        if (toolTip != null)
            toolTip.dismiss();

        if(toolTip != null && currentView == view) {
            toolTip.dismiss();
            toolTip = null;
        }else {
            new FetchData().execute();
            toolTip = new Tooltip.Builder(view)
                    .setText("Please wait...")
                    .setTextSize(R.dimen.fontSize)
                    .setPadding(R.dimen.popupPadding)
                    .setCornerRadius(R.dimen._4dp)
                    .setBackgroundColor(Color.parseColor("#1976d2"))
                    .show();
        }


    }

    @Override
    public void onRefresh() {
        page = 1;
        sendRequest();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SearchEvent event) {
        isLoading = false;

        // fired by pull to refresh
        if (swipe.isRefreshing()) {
            swipe.setRefreshing(false);
            adapter.clear();
        }

        if (isScreenEmpty()) {
            if (event.exception != null) {
                screenStateManager.showError(R.string.errorMessage);
            } else if (AppUtil.isNullOrEmpty(event.item)) {
                screenStateManager.showEmpty(R.string.emptyText);
            } else {
                screenStateManager.hideAll();
                adapter.addAll(event.item);
            }
        } else {
            adapter.remove(adapter.getItemCount() - 1); //remove progress item
            if (event.exception != null) {
                showSnack(this, R.string.errorMessage);
            } else if (AppUtil.isNullOrEmpty(event.item)) {
                showSnack(this, R.string.emptyText);
            } else {
                adapter.addAll(event.item);
            }
        }
    }

    private void sendRequest() {
        Timber.i("sendRequest: " + page);
        if (AppUtil.isConnected()) {
            isLoading = true;
            flickrService.searchAsync(page++);

            if (isScreenEmpty())
                screenStateManager.showLoading();
            else
                adapter.addAll(null); // so the adapter will check view_type and show progress bar at bottom
        } else {
            swipe.setRefreshing(false);
            if (isScreenEmpty()) screenStateManager.showConnectionError();
            else showConnectionError(this);
        }
    }

    private void setScrollListener() {
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && totalItemCount >= FlickrService.PAGE_SIZE) {
                    sendRequest();
                }
            }
        });
    }

    private boolean isScreenEmpty() {
        return adapter == null || adapter.getItemCount() == 0;
    }

}
