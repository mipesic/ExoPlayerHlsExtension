package mipesc.video.streaming.exoplayerhlsextension;

import android.app.Application;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.util.Util;


import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

import mipesc.video.streaming.exoplayerhlsextension.tracking.AnalyticsProvider;
import mipesc.video.streaming.exoplayerhlsextension.tracking.Error;
import mipesc.video.streaming.exoplayerhlsextension.tracking.Event;
import mipesc.video.streaming.exoplayerhlsextension.tracking.EventCollector;
import mipesc.video.streaming.exoplayerhlsextension.tracking.Metric;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();
    private int state = ExoPlayer.STATE_IDLE;

    public void testPlayback() {
        ExoFactory factory = new ExoFactory(getContext(), "TEST_UA");
        SimpleExoPlayer player = factory.buildExoPlayer();
        player.setPlayWhenReady(true);
        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {}
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}
            @Override
            public void onLoadingChanged(boolean isLoading) {}
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                lock.lock();
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    assertTrue(state == ExoPlayer.STATE_IDLE);
                } else if (playbackState == ExoPlayer.STATE_READY){
                    assertTrue(state == ExoPlayer.STATE_BUFFERING);
                    state = playbackState;
                    cond.signal();
                }
                state = playbackState;
                lock.unlock();
            }
            @Override
            public void onPlayerError(ExoPlaybackException error) {}

            @Override
            public void onPositionDiscontinuity() {}

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
        Uri trackUri = Uri.parse("http://www.streambox.fr/playlists/test_001/stream.m3u8");
        int type = Util.inferContentType(trackUri);
        MediaSource mediaSource = factory.buildMediaSource(factory.buildDataSourceFactory(true), trackUri, "");
        player.prepare(mediaSource, true, false);

        try {
            lock.lock();
            while(state != ExoPlayer.STATE_READY) {
                cond.await(10, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void testEventCollectorShouldReportTimeToPlay() throws Exception {
         final Metric m = new Metric();
            m.type = "undefined";
            EventCollector collector = new EventCollector(getContext(), new AnalyticsProvider() {
                @Override
                public void reportMetric(Metric metric) {
                    lock.lock();
                    m.type = metric.type;
                    m.value = metric.value;
                    cond.signal();
                    lock.unlock();
                }

                @Override
                public void reportError(Error error) {

                }
            });
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy/hh:mm:ss");

            collector.signal(new Event(Event.EventType.PLAYBACK_INIT, dateFormat.parse("27/07/2017/19:45:45")));
            collector.signal(new Event(Event.EventType.PLAYBACK_STARTED, dateFormat.parse("27/07/2017/19:45:50")));
        lock.lock();
        while (m.type.equals("undefined"))
            cond.await(5, TimeUnit.SECONDS);
        lock.unlock();
        assertTrue(m.type.equals("time-to-play"));
        assertTrue(m.value == 5000l);
    }

    public void testEventCollectorShouldReportTimeToBuffer() throws Exception {
        final Metric m = new Metric();
        m.type = "";
        EventCollector collector = new EventCollector(getContext(), new AnalyticsProvider() {
            @Override
            public void reportMetric(Metric metric) {
                lock.lock();
                m.type = metric.type;
                m.value = metric.value;
                if (m.type.equals("time-to-buffer"))
                    cond.signal();
                lock.unlock();
            }

            @Override
            public void reportError(Error error) {

            }
        });
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy/hh:mm:ss");

        collector.signal(new Event(Event.EventType.PLAYBACK_INIT));
        collector.signal(new Event(Event.EventType.PLAYBACK_STARTED));
        collector.signal(new Event(Event.EventType.BUFFERING_STARTED, dateFormat.parse("27/07/2017/19:46:45")));
        collector.signal(new Event(Event.EventType.PLAYBACK_STARTED, dateFormat.parse("27/07/2017/19:46:56")));
        lock.lock();
        while (!m.type.equals("time-to-buffer"))
            cond.await(5, TimeUnit.SECONDS);
        lock.unlock();
        assertTrue(m.type.equals("time-to-buffer"));
        assertTrue(m.value == 11000l);
    }
}