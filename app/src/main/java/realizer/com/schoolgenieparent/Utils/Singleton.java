package realizer.com.schoolgenieparent.Utils;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import java.util.ArrayList;

import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkListModel;
import realizer.com.schoolgenieparent.view.ProgressWheel;

/**
 * Created by Bhagyashri on 4/2/2016.
 */
public class Singleton {

    public static boolean isShowMap=false;
    public static Intent manualserviceIntent = null;
    public static Intent autoserviceIntent = null;
    private static Singleton _instance;
    public static ResultReceiver resultReceiver;
    public static Context context;
    public static Fragment fragment;
    public static Fragment mainFragment;
    public static ProgressWheel messageCenter = null;

    private Singleton()
    {

    }

    public static ArrayList<TeacherHomeworkListModel> getAllImages() {
        return allImages;
    }

    public static void setAllImages(ArrayList<TeacherHomeworkListModel> allImages) {
        Singleton.allImages = allImages;
    }

    public  static ArrayList<TeacherHomeworkListModel> allImages;

    public static Intent getManualserviceIntent() {
        return manualserviceIntent;
    }

    public static void setManualserviceIntent(Intent manualserviceIntent) {
        Singleton.manualserviceIntent = manualserviceIntent;
    }

    public static Intent getAutoserviceIntent() {
        return autoserviceIntent;
    }

    public static void setAutoserviceIntent(Intent autoserviceIntent) {
        Singleton.autoserviceIntent = autoserviceIntent;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Singleton.context = context;
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

    public static boolean isIsShowMap() {
        return isShowMap;
    }

    public static void setIsShowMap(boolean isShowMap) {
        Singleton.isShowMap = isShowMap;
    }
}

