package realizer.com.schoolgenieparent.view;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.GetImages;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.Utils.Utility;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkListModel;

/**
 * Created by Win on 11/25/2015.
 */
public class FullImageViewActivity extends FragmentActivity {
    static int NUM_ITEMS ;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    static ProgressWheel loading;
    static String[] IMG;
    //static String[] TEXT;
    static ImageView imageView;
    static ActionBar bar;
    static Bitmap decodedByte;
    static TextView txtcnt;
    static Bitmap barr[];
    static ImageView imgv[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.fragment_page);
        loading = (ProgressWheel)findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        Bundle bundle = getIntent().getExtras();
        String headertext = bundle.getString("HEADERTEXT");
        int position= bundle.getInt("POSITION");
        int positionList= bundle.getInt("ListNo");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String path = preferences.getString("HomeworkImage", "");
        String hwtext = preferences.getString("HomeworkText", "");

        ArrayList<TeacherHomeworkListModel> allImages=Singleton.getAllImages();
        try {
            if (path.equals("http://farmaciileremedia.ro/image/cache/data/Produse/cosmetice/No_available_image-500x505.gif"))
            {
                //IMG[0]=path;
                JSONArray jarr = new JSONArray(path);
                NUM_ITEMS = jarr.length();
                IMG = new String[NUM_ITEMS];

                barr = new Bitmap[NUM_ITEMS];
                imgv = new ImageView[NUM_ITEMS];

                for(int i=0;i<NUM_ITEMS;i++)
                {
                    IMG[i] = jarr.getString(i);
                }
            }
            else
            {
                JSONArray jarr = new JSONArray(path);
                NUM_ITEMS = jarr.length();
                IMG = new String[NUM_ITEMS];

                barr = new Bitmap[NUM_ITEMS];
                imgv = new ImageView[NUM_ITEMS];

                for(int i=0;i<NUM_ITEMS;i++)
                {
                    IMG[i] = jarr.getString(i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager1);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext());
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
        viewPager.setAdapter(imageFragmentPagerAdapter);
        viewPager.setCurrentItem(position);
    }



    @Override
    public void onBackPressed() {
        /*Singleton.setFragment(Singleton.getMainFragment());
        finish();*/
        Singleton.setSelectedFragment(Singleton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();

        finish();
    }

    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.fullimageview_parent, container, false);
            imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            txtcnt = (TextView) swipeView.findViewById(R.id.txtcounter);

            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            Log.d("FILENAME", "" + IMG[position]);
            String filePath = IMG[position];
            if (filePath.contains("http"))
            {
                String newURL=new Utility().getURLImage(filePath);
                if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                    new GetImages(newURL,imageView,null,null,newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
                else
                {
                    File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length-1]);
                    BitmapFactory.Options bmOptions1 = new BitmapFactory.Options();
                    decodedByte = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions1);
                    imageView.setImageBitmap(decodedByte);
                }
            }
            else
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                decodedByte = BitmapFactory.decodeFile(filePath, bmOptions);
                imageView.setImageBitmap(decodedByte);
            }

            txtcnt.setText("" + (position + 1) + " / " + NUM_ITEMS);
            imgv[position] = imageView;
            loading.setVisibility(View.GONE);
            return swipeView;
        }

        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_imageview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rotate:
                RotateImg();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void RotateImg()
    {
        Bitmap bitmap = barr[viewPager.getCurrentItem()];
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        imgv[viewPager.getCurrentItem()].setImageBitmap(rotated);
    }
}
