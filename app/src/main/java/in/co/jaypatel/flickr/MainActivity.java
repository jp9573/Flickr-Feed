package in.co.jaypatel.flickr;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import in.co.jaypatel.flickr.adapter.PhotoAdapter;
import in.co.jaypatel.flickr.model.PhotoModel;
import in.co.jaypatel.flickr.model.event.SearchEvent;
import in.co.jaypatel.flickr.service.FlickrService;
import in.co.jaypatel.flickr.ui.AbstractBaseActivity;
import in.co.jaypatel.flickr.ui.DetailActivity;
import in.co.jaypatel.flickr.util.AppUtil;
import in.co.jaypatel.flickr.util.RowClickListener;
import in.co.jaypatel.flickr.util.ScreenStateManager;
import timber.log.Timber;

import static in.co.jaypatel.flickr.ui.AbstractBaseActivity.showConnectionError;
import static in.co.jaypatel.flickr.ui.AbstractBaseActivity.showSnack;

public class MainActivity
        extends AbstractBaseActivity
        implements RowClickListener<PhotoModel>,
        SwipeRefreshLayout.OnRefreshListener {

    private int page = 1;
    private boolean isLoading;
    private final FlickrService flickrService = FlickrService.INSTANCE;
    private PhotoAdapter adapter;
    private ScreenStateManager screenStateManager;

    @BindView(R.id.swipe)
    private SwipeRefreshLayout swipe;
    @BindView(R.id.linear)
    private LinearLayout linear;
    @BindView(R.id.recycler)
    private RecyclerView recycler;

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
    public void onRowClicked(int row, PhotoModel item) {
        startActivity(DetailActivity.createIntent(this, row, adapter.getAll()));
    }

    @Override
    public void onRefresh() {
        page = 1;
        sendRequest();
    }

    private void sendRequest() {
        Timber.i("sendRequest: " + page);
        if (AppUtil.isConnected()) {
            isLoading = true;
            flickrService.searchAsync(page++);

            if (isScreenEmpty())
                screenStateManager.showLoading();
            else
                adapter.addAll(null); // add null , so the adapter will check view_type and show progress bar at bottom
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
