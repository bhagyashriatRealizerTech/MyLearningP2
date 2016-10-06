package realizer.com.schoolgenieparent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.GetImages;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.OnBackPressFragment;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.Utils.Utility;
import realizer.com.schoolgenieparent.backend.DALMyPupilInfo;
import realizer.com.schoolgenieparent.communication.TeacherQueryViewFragment;
import realizer.com.schoolgenieparent.homework.ParentHomeWorkFragment;
import realizer.com.schoolgenieparent.invitejoin.InviteToJoinActivity;
import realizer.com.schoolgenieparent.myclass.MyClassStudentFragment;
import realizer.com.schoolgenieparent.myclass.MyPupilInfoFragment;
import realizer.com.schoolgenieparent.service.ManualSyncupService;

//import com.realizer.schoolgenie.chat.TeacherQueryFragment1;
//import com.realizer.schoolgenie.funcenter.TeacherFunCenterFolderFragment;
//import com.realizer.schoolgenie.generalcommunication.TeacherGeneralCommunicationFragment;
//import com.realizer.schoolgenie.holiday.TeacherPublicHolidayFragment;
//import com.realizer.schoolgenie.homework.TeacherHomeworkFragment;
//import com.realizer.schoolgenie.myclass.TeacherMyClassStudentFragment;
//import com.realizer.schoolgenie.star.TeacherGiveStarFragment;
//import com.realizer.schoolgenie.timetable.TeacherTimeTableFragment;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnTaskCompleted {

    private static final String TAG = ParentHomeWorkFragment.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    String localPath="";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    Bitmap bitmap;
    private int counter = 0;
    ImageView iv;
    TextView profile_text;
    TextView dispname;
    StringBuilder result;
    String newString;
    Fragment fragment;
    DrawerLayout drawer;
    TextView userName;
    ImageView userImage;
    TextView userInitials;

    private Uri fileUri; // file url to store image/video
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DashBoard");

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        //userName = (TextView) header.findViewById(R.id.txt_user_name);
        userName=(TextView) header.findViewById(R.id.txt_user_name);
        userImage = (ImageView) header.findViewById(R.id.img_user_image);
        userInitials = (TextView) header.findViewById(R.id.img_user_text_image);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DrawerActivity.this);
        userName.setText(preferences.getString("DisplayName",""));

        final String urlString = preferences.getString("ThumbnailID", "");
        Log.d("Image URL", urlString);

        if(urlString.equals("") || urlString.equalsIgnoreCase("null"))
        {
            userImage.setVisibility(View.GONE);
            userInitials.setVisibility(View.VISIBLE);
            String name[]=userName.getText().toString().split(" ");
            String fname = name[0].trim().toUpperCase().charAt(0)+"";
            if(name.length>1)
            {
                String lname = name[1].trim().toUpperCase().charAt(0)+"";
                userInitials.setText(fname+lname);
            }
            else
                userInitials.setText(fname);

        }
        else
        {
            userImage.setVisibility(View.VISIBLE);
            userInitials.setVisibility(View.GONE);
            if (urlString.contains("http"))
            {
                String newURL=new Utility().getURLImage(urlString);
                if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                    new GetImages(newURL,userImage,userInitials,userName.getText().toString(),newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
                else
                {
                    File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length-1]);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                    //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                    userImage.setImageBitmap(bitmap);
                }
            }
            else
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(urlString, bmOptions);
                userImage.setImageBitmap(bitmap);
            }
        }

        userInitials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOption();
            }
        });
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOption();
            }
        });
        /* Display First Fragment at Launch*/
        //navigationView.setCheckedItem(R.id.nav_home);
        Fragment frag = new ParentDashboardFragment();
        Singleton.setSelectedFragment(frag);
        if (frag != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, frag).commit();

        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (Singleton.getSelectedFragment() instanceof ParentDashboardFragment) {
            moveTaskToBack(true);
            finish();
        } else if (Singleton.getSelectedFragment() != null && Singleton.getSelectedFragment() instanceof OnBackPressFragment) {
            ((OnBackPressFragment) Singleton.getSelectedFragment()).OnBackPress();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify search_layout parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // update the main content by replacing fragments
        fragment = null;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragment = new ParentDashboardFragment();
        }
//        else if (id == R.id.nav_myclass)
//        {
//            fragment = MyClass(1);
//        }
        else if (id == R.id.nav_homework) {
            fragment = HomeworkList("Homework");
        } else if (id == R.id.nav_timetable) {
            fragment = InviteTojoin("Invite to Join");
        } else if (id == R.id.nav_classwork) {
            fragment = HomeworkList("Classwork");
        } else if (id == R.id.nav_communication) {
            fragment = Communication("Communication");
            //           Toast.makeText(DrawerActivity.this, "In Progress..!", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_mypupil) {
            fragment = MyPupil();
        } else if (id == R.id.nav_myclass) {
            fragment = MyClassList();
            //           Toast.makeText(DrawerActivity.this, "In Progress..!", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_manual_sync)
        {
            Singleton.setContext(DrawerActivity.this);
            fragment = Singleton.getSelectedFragment();
            Intent service = new Intent(DrawerActivity.this,ManualSyncupService.class);
            Singleton.setManualserviceIntent(service);
            startService(service);
        }
//        else if (id == R.id.nav_alert)
//        {
//            fragment = GeneralCommunicationList();
//        }
//        else if (id == R.id.nav_funcenter)
//        {
//            //fragment = FunCenter();
//        }
//        else if (id == R.id.nav_star)
//        {
//            fragment = GiveStar();
//        }
        else if (id == R.id.nav_logout) {
            Logout();
        }

//        else if (id == R.id.nav_holiday)
//        {
//            fragment = PublicHoliday();
//        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

        }

        Singleton.setSelectedFragment(fragment);
        Singleton.setMainFragment(fragment);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Fragment MyPupil() {
        fragment = new MyPupilInfoFragment();
        return fragment;
    }

    public Fragment MyClassList() {
        fragment = new MyClassStudentFragment();
        return fragment;
    }


    // for My Class List
    public Fragment MyClass(int i) {
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


        String allStudentList = "1.,,Ajay Shah@@@2.,,Nilam Jadhav@@@3.,,Farhan Bodale@@@4.,,Pravin Jadhav@@@5.,,Ram Magar@@@6.,,Sahil Kadam@@@7.,,Hisha Mulye@@@8.,,Supriya Vichare@@@9.,,Ajay Shah@@@10.,,Nilam Jadhav@@@11.,,Farhan Bodale@@@12.,,Pravin Jadhav@@@13.,,Ram Magar@@@14.,,Sahil Kadam@@@15.,,Hisha Mulye@@@16.,,Supriya Vichare";

        //fragment = new TeacherMyClassStudentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("StudentClassList", allStudentList);
        fragment.setArguments(bundle);

        return fragment;
    }

    // for Homework List
    public Fragment HomeworkList(String name) {
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
        // getSupportActionBar().setTitle(Config.actionBarTitle("DashBoard",DrawerActivity.this));
        String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        fragment = new ParentHomeWorkFragment();
        Bundle bundle = new Bundle();
        bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    public Fragment Communication(String name) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DrawerActivity.this);
        String userId=preferences.getString("UidName", "");
        String sendarname=preferences.getString("Firstname","");
        String urlimage=preferences.getString("ThumbnailID","");


         fragment = new TeacherQueryViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("HEADERTEXT", name);
        bundle.putString("USERID",userId);
        bundle.putString("SENDERNAME",sendarname);
        bundle.putString("UrlImage",urlimage);
        fragment.setArguments(bundle);
        return fragment;
    }

    public Fragment InviteTojoin(String name) {
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
        // getSupportActionBar().setTitle(Config.actionBarTitle("DashBoard",DrawerActivity.this));
        //String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        fragment = new InviteToJoinActivity();
        Bundle bundle = new Bundle();
        //bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    //General Quries
    public Fragment GiveStar() {
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

        String quries = "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi_" +
                "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi";
        //fragment = new TeacherGiveStarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("StudentNameList", quries);
        fragment.setArguments(bundle);
        return fragment;
    }

    //General Communication
    public Fragment GeneralCommunicationList() {
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

        String communication = "20/11/2015,,sports day on 21 november,,Sports Day_19/11/2015,,paraents teacher1 meeting at 2 pm on 20 november,,PTA_18/11/2015,,Story reading competition,,Other_17/11/2015,,paraents teacher1 meeting at 2 pm on 18 november,,PTA" +
                "_15/11/2015,,Singing talent competition,,Other_14/11/2015,,sports day on 15 november,,Sports Day_13/11/2015,,paraents teacher1 meeting at 2 pm on 14 november,,PTA_12/11/2015,,sports day on 13 november,,Sports Day";
        //fragment = new TeacherGeneralCommunicationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GeneralCommunicationList", communication);
        fragment.setArguments(bundle);
        return fragment;
    }


    // for Syllabus List
    public Fragment Syllabus() {
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

        String syllabuslist = "First Unit Test Time Table,,NoImage_First Unit Test Syllabus,,Image_Second Unit Test Time Table,,Image_Second Unit Test Syllabus,,Image_First Semester Time Table,,NoImage_First Semester Syllabus,,Image" +
                "_Third Unit Test Time Table,,NoImage_Third unit Test Syllabus,,NoImage_Fourth Unit Test Time Table,,Image_Fourth Unit Test Syllabus,,Image_Second Semester Time Table,,Image_Second sSemester Syllabus,,Image";
        String dictationlist = "Marathi,,lesson1 and 2_Hindi,,lesson no 4 and 5_English,,lesson no 7,8 and9_History,,lesson no 1,2,3,4 and 5";
        //fragment = new TeacherTimeTableFragment();
        Bundle bundle = new Bundle();
        bundle.putString("SyllabusList", syllabuslist);
        bundle.putString("DictationList", dictationlist);
        bundle.putInt("Checked", 1);
        fragment.setArguments(bundle);
        return fragment;
    }

    //General Quries
    public Fragment Quries() {
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

        String quries = "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi_" +
                "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi";
        //fragment = new TeacherQueryFragment1();
        Bundle bundle = new Bundle();
        bundle.putString("StudentNameList", quries);
        fragment.setArguments(bundle);
        return fragment;
    }

    // for Public Holiday List
    public Fragment PublicHoliday() {
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

        String publicholiday = "Independence Day,,15/08/2015,,15/08/2015_Ganesh Chaturthi,,17/09/2015,,17/09/2015_Gandhi Jayanti,,02/10/2015,,02/10/2015_Dussehra" +
                ",,22/10/2015,,22/10/2015_Diwali Holiday,,09/11/2015,,26/11/2015_Merry Christmas,,25/12/2015,,25/12/2015_Republic Day,,26/01/2016,,26/01/2016_Mahashivratri,,17/02/2016,,17/02/2016_Holi,,06/03/2016,,06/03/2016_Gudi Padawa,,21/03/2016,,21/03/2016" +
                "_Good Friday,,03/04/2016,,03/04/2016";
        //fragment = new TeacherPublicHolidayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PublicHolidayList", publicholiday);
        fragment.setArguments(bundle);
        return fragment;
    }
    // for Fun Center List


    public void Logout()

    {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString("Login", "false");
        edit.putString("LogChk", "true");
        edit.commit();
        finish();
    }
   /* // for View Star List
    public void TrackPupil()
    {
        FragmentManager fragmentManager = getFragmentManager();
        TeacherTrackingDialogBoxActivity fragment = new TeacherTrackingDialogBoxActivity();
        fragment.setCancelable(false);
        fragment.show(fragmentManager, "Dialog!");

    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }*/


    public void getOption() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        galleryIntent.setType("image/*");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.putExtra("crop", "true");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Choose Action");

        Intent[] intentArray = {cameraIntent};

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooser, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {

        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        //External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
//                Log.d(TAG, "Oops! Failed create "
//                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create search_layout media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");


        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                if (data == null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    // down sizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                    Log.d("PATH", fileUri.getPath());
                    setPhoto(bitmap);
                    userImage.setImageBitmap(bitmap);
                    String path = encodephoto(bitmap);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("ProfilePicPath", path);
                    editor.commit();
                    launchUploadActivity(data);

                } else
                    launchUploadActivity(data);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }


    private void launchUploadActivity(Intent data) {

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userid = sharedpreferences.getString("UidName", "");
        if (data.getData() != null) {
            try {
                if (bitmap != null) {
                    //bitmap.recycle();
                }

                InputStream stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                localPath = ImageStorage.saveEventToSdCard(bitmap, "P2PDP",DrawerActivity.this);
                userImage.setImageBitmap(bitmap);
                String path = encodephoto(bitmap);

                Boolean result=isConnectingToInternet();
                if (result)
                {
                    ProfilePicAsyncTaskPost uploadimage=new ProfilePicAsyncTaskPost(this,this,userid,path);
                    uploadimage.execute();
                }
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ProfilePicPath", path);
                editor.commit();
                userInitials.setVisibility(View.GONE);
                userImage.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            bitmap = (Bitmap) data.getExtras().get("data");
            localPath = ImageStorage.saveEventToSdCard(bitmap, "P2PDP",DrawerActivity.this);
            userImage.setImageBitmap(bitmap);
            String path = encodephoto(bitmap);

            Boolean result=isConnectingToInternet();
            if (result)
            {
                ProfilePicAsyncTaskPost uploadimage=new ProfilePicAsyncTaskPost(this,this,userid,path);
                uploadimage.execute();
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ProfilePicPath", path);
            editor.commit();
            userInitials.setVisibility(View.GONE);
            userImage.setVisibility(View.VISIBLE);
        }
    }

    //Encode image to Base64 to send to server
    private void setPhoto(Bitmap bitmapm) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");

            }
        }
        else {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //4
            File file = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpeg");
            try {
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                //5
                fo.write(bytes.toByteArray());
                fo.close();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file)));

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }
    //Encode image to Base64 to send to server
    private String encodephoto(Bitmap bitmapm) {
        String imagebase64string = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] byteArrayImage = baos.toByteArray();
            imagebase64string = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagebase64string;
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Lock Drawer Avoid opening it
     */
    public void lockDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * UnLockDrawer and allow opening it
     */
    public void unlockDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void showMyActionBar() {
        getSupportActionBar().show();
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Drawer Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://realizer.com.schoolgenieparent/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Drawer Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://realizer.com.schoolgenieparent/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }

    @Override
    public void onTaskCompleted(String s) {
        if(s.equalsIgnoreCase("true"))
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DrawerActivity.this);
            DALMyPupilInfo dp=new DALMyPupilInfo(DrawerActivity.this);
            String[] student=dp.GetAllTableData(preferences.getString("StudentUserID",""));
            long n=0;
            n=dp.updateStudentInfo(student[15], student[16], student[3], student[4], student[17], student[5], student[0], student[1], student[2], student[6], student[8], student[9], student[18], student[10],
                    student[19], student[11], student[7], student[20], student[21], student[22],student[23],student[13],student[12],localPath,student[24]);
           if (n>0)
           {
               SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(DrawerActivity.this);
               SharedPreferences.Editor edit = sharedpreferences.edit();
               edit.putString("ThumbnailID", localPath);
               edit.commit();
           }
        }
    }
}
