package realizer.com.schoolgenieparent.Utils;

import android.app.Fragment;
import android.content.Context;
import android.os.ResultReceiver;

import realizer.com.schoolgenieparent.ProgressWheel;

/**
 * Created by Bhagyashri on 4/2/2016.
 */
public class Singleton {

    private static Singleton _instance;
    public static ResultReceiver resultReceiver;
    public static Context context;
    public static Fragment fragment;
    public static Fragment mainFragment;

    private Singleton()
    {

    }

    public static Singleton getInstance()
    {
        if (_instance == null)
        {
            _instance = new Singleton();
        }
        return _instance;
    }

    public static ResultReceiver getResultReceiver() {
        return resultReceiver;
    }
    public static ProgressWheel getMessageCenter() {
        return messageCenter;
    }
    public static void setMessageCenter(ProgressWheel messageCenter) {
        Singleton.messageCenter = messageCenter;
    }
    public static ProgressWheel messageCenter = null;
    public static void setResultReceiver(ResultReceiver resultReceiver) {
        Singleton.resultReceiver = resultReceiver;
    }

    public static Fragment getSelectedFragment() {
        return fragment;
    }

    public static void setSelectedFragment(Fragment fragment) {
        Singleton.fragment = fragment;
    }

    public static Fragment getMainFragment() {
        return mainFragment;
    }

    public static void setMainFragment(Fragment mainFragment) {
        Singleton.mainFragment = mainFragment;
    }
}

