package itp341.otegbade.opeoluwa.myfinal.project.app;

import android.app.Application;
import android.util.Log;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

/**
 * Created by sonu on 19/01/18.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //initialise twitter config
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("i14ZZ1EmWjxwIjxe6cxDTIuQD", "x8hpviZATwdrAwqJehEOKf1G0hFZ5kv5RVZxFZS7ZjB0vfJXQo"))//replace your twitter API Key and Secret here
                .debug(true)
                .build();
        Twitter.initialize(config);
    }
}