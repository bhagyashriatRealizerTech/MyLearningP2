package realizer.com.schoolgenieparent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import realizer.com.schoolgenieparent.homework.TeacherHomeworkFragment;
import realizer.com.schoolgenieparent.leftdrawer.DrawerItem;
import realizer.com.schoolgenieparent.leftdrawer.DrawerListAdapter;

/**
 * Created by Win on 23/08/2016.
 */
public class ParentActivity extends AppCompatActivity
{
    private DrawerLayout mDrawerLayout;

    private ListView mDrawerList;
    Fragment fragment;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<DrawerItem> navDrawerItems;
    private DrawerListAdapter adapter;
    LinearLayout drawerll;
    ImageView iv;
    TextView profile_text;
    TextView dispname;
    StringBuilder result;
    String newString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_activity);
        mTitle = mDrawerTitle = getTitle();
        iv = (ImageView)findViewById(R.id.profilepic);
        profile_text=(TextView)findViewById(R.id.txtinitialPupil);

        dispname = (TextView)findViewById(R.id.txtdispName);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String dpname= preferences.getString("DisplayName","");
        String thumbnailurl= preferences.getString("ThumbnailID", "");

        if (thumbnailurl!=null)
        {

        }
        else
        {
            String name[]=dpname.split(" ");
            if (name.length>1) {
                profile_text.setText( String.valueOf(name[0].charAt(0)) + String.valueOf(name[1].charAt(0)));
            }
        }
        dispname.setText(dpname);

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.drawer_items);

        // nav drawer icons from resources

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        drawerll = (LinearLayout) findViewById(R.id.drawerll);

        navDrawerItems = new ArrayList<DrawerItem>();
        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.drawer_icons);

        // adding nav drawer items to array
        // Home
        //navDrawerItems

        navDrawerItems.add(new DrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[6],navMenuIcons.getResourceId(6, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[7],navMenuIcons.getResourceId(7, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[8],navMenuIcons.getResourceId(8, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[9],navMenuIcons.getResourceId(9, -1)));

        navDrawerItems.add(new DrawerItem(navMenuTitles[10],navMenuIcons.getResourceId(10, -1)));

        // Recycle the typed array

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new DrawerListAdapter(getApplicationContext(),	navDrawerItems);

        mDrawerList.setAdapter(adapter);


        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setTitle("DashBoard");

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {

            public void onDrawerClosed(View view)
            {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = df.format(c.getTime());
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = df.format(c.getTime());
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null)
        {
            // on first time display view for first nav item
            displayView(0);
        }

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString("LogChk", "true");
        edit.commit();
    }
    private class SlideMenuClickListener implements	ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public void onBackPressed()
    {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawers();
        }
        else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void displayView(int position)
    {
        // update the main content by replacing fragments
        Fragment fragment = null;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        switch (position)
        {
            case 0:
                fragment = new ParentDashboardFragment();
                break;
            case 1:
                //MyClass(1);
                // update selected item and title, then close the drawer
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(drawerll);
                break;
            case 2:
                fragment = HomeworkList("Homework");
                break;
            case 3:
                //fragment = GiveStar();
                break;
            case 4:
                //fragment =Syllabus();
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(drawerll);
                break;
            case 5:
                //fragment = Quries();
                break;
            case 6:
                //fragment = FunCenter();
                break;
            case 7:
                //fragment = GeneralCommunicationList();
                break;
            case 8:
                fragment = HomeworkList("Classwork");
                break;
            case 9:
                //fragment = PublicHoliday();
                break;
            case 10:
                //Logout();
                break;

            default:
                break;
        }

        if (fragment != null)
        {
            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(drawerll);

        }
        else
        {
            // error in creating fragment
            //Log.e("TeacherActivity", "Error in creating fragment");
        }
    }
    public Fragment HomeworkList(String name)
    {
       /* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*/

        String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        fragment = new TeacherHomeworkFragment();
        Bundle bundle = new Bundle();
        bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT",name);
        fragment.setArguments(bundle);
        return fragment;
    }

}
