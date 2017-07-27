package mipesc.video.streaming.exoplayerhlsextension;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSpec;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoJUnit;

import org.junit.Rule;
import static org.junit.Assert.*;
import org.mockito.junit.MockitoRule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by milospesic on 25.07.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RadioHttpDataSourceTest {

    private static final DataSpec TEST_DATA_SPEC = new DataSpec(Uri.parse("http://google.com"));
    private RadioHttpDataSource dataSource;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    RadioHttpDataSourceListener listener;

    Context context = InstrumentationRegistry.getContext();

    @Before
    public void setup() {
        System.setProperty( "dexmaker.dexcache",
                InstrumentationRegistry.getTargetContext().getCacheDir().getPath());
        MockitoAnnotations.initMocks(this);
        RadioHttpDataSourceFactory factory = new RadioHttpDataSourceFactory("test", null, listener);
        dataSource = (RadioHttpDataSource) factory.createDataSource();
    }
    @Test
    public void testOpenShouldInvokeOnHttpTransferStart() throws Exception {
        ArgumentCaptor<DataSpec> argument = ArgumentCaptor.forClass(DataSpec.class);
        dataSource.open(TEST_DATA_SPEC);
        Mockito.verify(listener).onHttpTransferStart(argument.capture());
        assertTrue(argument.getValue().equals(TEST_DATA_SPEC));
    }

    @Test
    public void testReadEOSShouldInvokeOnHttpTransferEnd() throws Exception {
        ArgumentCaptor<DataSpec> argument = ArgumentCaptor.forClass(DataSpec.class);
        dataSource.open(TEST_DATA_SPEC);
        byte[] readBuffer = new byte[512];
        while(dataSource.read(readBuffer, 0, 512) != C.RESULT_END_OF_INPUT);
        Mockito.verify(listener).onHttpTransferEnd(argument.capture());
        assertTrue(argument.getValue().equals(TEST_DATA_SPEC));
    }

    @Test
    public void testRead() throws Exception {
        final ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        RadioHttpDataSourceFactory fact = new RadioHttpDataSourceFactory("test", null, new RadioHttpDataSourceListener() {
            @Override
            public void onHttpTransferStart(DataSpec spec) {
            }
            @Override
            public void onHttpTransferEnd(DataSpec spec) {
            }
            @Override
            public void onBytesTransferred(DataSpec spec, byte[] transferedBytes) {
                try {
                    responseBytes.write(transferedBytes);
                }
                catch (IOException e) {

                }
            }
        });
        RadioHttpDataSource radioHttpDataSource = (RadioHttpDataSource)fact.createDataSource();
        radioHttpDataSource.open(TEST_DATA_SPEC);
        ByteArrayOutputStream controlledSequence = new ByteArrayOutputStream();
        byte[] readBytes = new byte[512];
        long result = 0;
        while((result=radioHttpDataSource.read(readBytes, 0, 512)) != C.RESULT_END_OF_INPUT) {
            controlledSequence.write(readBytes, 0, (int) result);
        }

        assertTrue(Arrays.equals(controlledSequence.toByteArray(), responseBytes.toByteArray()));

    }
}