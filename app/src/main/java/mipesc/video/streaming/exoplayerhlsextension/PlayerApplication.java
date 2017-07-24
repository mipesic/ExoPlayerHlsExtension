package mipesc.video.streaming.exoplayerhlsextension;

import android.app.Application;
import android.util.Log;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by milospesic on 23.07.17.
 */
public class PlayerApplication extends Application {
    protected String userAgent;

    @Override
    public void onCreate() {
        Log.d("PlayerApplication", "Creating the app");
        super.onCreate();
        userAgent = Util.getUserAgent(this, "ExoHlsPlayer");
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new RadioHttpDataSourceFactory(userAgent, bandwidthMeter);
    }
}
