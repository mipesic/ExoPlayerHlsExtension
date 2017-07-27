package mipesc.video.streaming.exoplayerhlsextension;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.runner.RunWith;

import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;


/**
 * Created by milospesic on 25.07.17.
 */

@RunWith(AndroidJUnit4.class)
public class ExoFactoryTest {

    private Context context;
    private ExoFactory exoFactory;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
        exoFactory = new ExoFactory(context, "TEST");
    }

    @org.junit.Test
    public void testBuildDataSourceFactory() throws Exception {
        DataSource.Factory factory = exoFactory.buildDataSourceFactory(false);
        assertTrue(factory instanceof DefaultDataSourceFactory);
    }

    @org.junit.Test
    public void testBuildMediaSource() throws Exception {
        MediaSource source = exoFactory.buildMediaSource(exoFactory.buildDataSourceFactory(false), Uri.parse("http://myaddress/playlist.m3u8"), "");
        assertTrue(source instanceof HlsMediaSource);
        source = exoFactory.buildMediaSource(exoFactory.buildDataSourceFactory(false), Uri.parse("http://myaddress/playlist.mpd"), "");
        assertTrue(source instanceof DashMediaSource);
    }


    @org.junit.Test
    public void testBuildHttpDataSourceFactory() throws Exception {
        HttpDataSource.Factory factory =  exoFactory.buildHttpDataSourceFactory(null);
        HttpDataSource source = factory.createDataSource();
        assertTrue(factory instanceof RadioHttpDataSourceFactory);
        assertTrue(source instanceof  RadioHttpDataSource);
    }
}