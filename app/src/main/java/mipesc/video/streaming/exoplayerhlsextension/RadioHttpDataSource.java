package mipesc.video.streaming.exoplayerhlsextension;

import android.util.Log;

import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Predicate;

/**
 * Created by milospesic on 23.07.17.
 */
public class RadioHttpDataSource extends DefaultHttpDataSource {
    private static final String TAG = "RadioHttpDataSource";

    RadioHttpDataSource(String userAgent, Predicate<String> contentTypePredicate,
                        TransferListener<? super DefaultHttpDataSource> listener, int connectTimeoutMillis,
                        int readTimeoutMillis, boolean allowCrossProtocolRedirects,
                        RequestProperties defaultRequestProperties) {
        super(userAgent, contentTypePredicate, listener, connectTimeoutMillis, readTimeoutMillis, allowCrossProtocolRedirects, defaultRequestProperties);
    }

    public RadioHttpDataSource(String userAgent, Predicate<String> contentTypePredicate) {
        super(userAgent, contentTypePredicate);
    }

    @Override
    public long open(DataSpec dataSpec) throws HttpDataSourceException {
        Log.d(TAG, dataSpec.uri.toString());
        return super.open(dataSpec);
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws HttpDataSourceException {
        return super.read(buffer, offset, readLength);
    }
}
