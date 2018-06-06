package in.co.jaypatel.flickr.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import in.co.jaypatel.flickr.R;
import in.co.jaypatel.flickr.util.AppUtil;
import timber.log.Timber;

/**
 * Created by Jay on 5.06.2018.
 */
public abstract class AbstractBaseActivity
        extends AppCompatActivity {

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    protected abstract int getLayout();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setIcon(R.drawable.ic_info_outline_24dp)
                .setMessage(message)
                .setNegativeButton(R.string.close, null)
                .show();
    }

    protected static void showSnack(Activity activity, @StringRes int msgId) {
        AppUtil.createSnackbar(activity, msgId).show();
    }

    protected static void showConnectionError(final Activity activity) {
        AppUtil.createSnackbar(activity, R.string.connectionErrorMessage)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                }).show();
    }

}