package realizer.com.schoolgenieparent.homework.newhomework;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import realizer.com.schoolgenieparent.DrawerActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.OnBackPressFragment;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.homework.ParentHomeWorkFragment;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkModel;
import realizer.com.schoolgenieparent.homework.newhomework.adapter.NewHomeworkGalleryAdapter;
import realizer.com.schoolgenieparent.homework.newhomework.customgallery.multiselectgallery.PhotoAlbumActivity;

/**
 * Created by Bhagyashri on 10/6/2016.
 */
public class NewHomeworkActivity extends Fragment implements OnBackPressFragment {

    String htext;
    TextView datetext;
    EditText homeworktext;
    GridView gridView;
    ImageButton addImage;
    ArrayList<String> templist;
    ArrayList<TeacherHomeworkModel> hwimage;
    NewHomeworkGalleryAdapter adapter;
    ArrayList<String> base64imageList;
    DatabaseQueries qr ;
    int hid = 0;
    String date = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.new_homework_layout, container, false);
        setHasOptionsMenu(true);
        Singleton.setFialbitmaplist(new ArrayList<TeacherHomeworkModel>());
        Bundle b = getArguments();
        htext = b.getString("HEADERTEXT");
        templist = new ArrayList<>();
        qr = new DatabaseQueries(getActivity());
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        initiateView(rootView);
        if (htext.equalsIgnoreCase("HomeWork"))
        {
            homeworktext.setHint("Enter Homework Text");
        }
        else
        {
            homeworktext.setHint("Enter Classwork Text");
        }

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        int currentdate = c.get(Calendar.DATE);
        if(currentdate>=1 && currentdate<10) {
            if(month>=1 && month<10)
                date =  "0" + currentdate + "/0" + month + "/" + year;
            else
                date =  "0" + currentdate + "/" + month + "/" + year;
        }
        else
        {
            if(month>=1 && month<10)
                date =  "" + currentdate + "/0" + month + "/" + year;
            else
                date =  "" + currentdate + "/" + month + "/" + year;
        }
        String datearr[] = date.split("/");
        String date1 = datearr[1]+"/"+datearr[0]+"/"+datearr[2];
        datetext.setText(Config.getDate(date1, "D"));

        return rootView;
    }

    public void initiateView(View rootview)
    {
        addImage = (ImageButton) rootview.findViewById(R.id.addimage);
        homeworktext = (EditText) rootview.findViewById(R.id.edtmsgtxt);
        gridView= (GridView) rootview.findViewById(R.id.gallerygridView);
        datetext = (TextView) rootview.findViewById(R.id.txtDate);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PhotoAlbumActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    public class GetImagesForEvent extends AsyncTask<Void, Void,Void>
    {


        ArrayList<String> temp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // loading.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... params) {

             templist = new ArrayList<>();
             base64imageList = new ArrayList<>();
             templist.addAll(Singleton.getImageList());
             Singleton.setImageList(new ArrayList<String>());
             hwimage = new ArrayList<>();
             temp = new ArrayList<>();

            for(int i=0;i<templist.size();i++)
            {
                String path = templist.get(i).toString();
                Bitmap bitmap = ImageStorage.decodeSampledBitmapFromPath(path, 150, 150);

                TeacherHomeworkModel obj = new TeacherHomeworkModel();
                obj.setPic(bitmap);

                hwimage.add(i, obj);
                temp.add(i,path);
            }
            if(templist.size()<10)
            {
                Bitmap icon = BitmapFactory.decodeResource(NewHomeworkActivity.this.getResources(),
                        R.drawable.noicon);
                TeacherHomeworkModel obj = new TeacherHomeworkModel();
                obj.setPic(icon);
                obj.setHwTxtLst("NoIcon");
                hwimage.add(templist.size(),obj);
            }
           /* allData=qr.GetImage(getid);

            for(int i=0;i<allData.size();i++)
            {

                String image1 =allData.get(i).getImage();
                File file = ImageStorage.getEventImage(image1);

                if(file != null) {
                    // BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    //  Bitmap bitmap = BitmapFactory.decodeFile(image1, bmOptions);
                    Bitmap bitmap = ImageStorage.decodeSampledBitmapFromPath(image1,150,150);
                    TeacherFunCenterGalleryModel obj = new TeacherFunCenterGalleryModel();
                    obj = allData.get(i);
                    obj.setBitmap(bitmap);
                    allData.set(i,obj);
                }
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            if(templist.size()>0) {
                addImage.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
                adapter = new NewHomeworkGalleryAdapter(getActivity(), hwimage,temp);
                gridView.setAdapter(adapter);
                gridView.setFastScrollEnabled(true);
            }
            else
            {
                addImage.setVisibility(View.VISIBLE);
            }
            //loading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_switchclass:
                Config.hideSoftKeyboardWithoutReq(getActivity(), homeworktext);
                //SwitchClass();
                return true;
            case R.id.action_done:
                Config.hideSoftKeyboardWithoutReq(getActivity(), homeworktext);
                saveHomework();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


    public void saveHomework()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        String sub = "All_Sub";
        JSONArray imglstbase64;
        String txtlst;
        if(homeworktext.getText().toString().trim().length()>0)
            txtlst = homeworktext.getText().toString();
        else
            txtlst = "No Homework Text";


        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String givenby = sharedpreferences.getString("UidName", "");

        ArrayList<TeacherHomeworkModel>tempImageList = new ArrayList<>();
        tempImageList = Singleton.getFialbitmaplist();
        for (int i=0;i<tempImageList.size();i++)
        {
            if (tempImageList.get(i).getHwTxtLst().equals("NoIcon"))
            {
                tempImageList.remove(i);
            }
        }
        String encodedImage="";
        String hwUUID= String.valueOf(UUID.randomUUID());
        for (int i=0;i<tempImageList.size();i++)
        {
            imglstbase64 = new JSONArray();
            encodedImage = ImageStorage.saveEventToSdCard(tempImageList.get(i).getPic(), "P2P", getActivity());

            try {
                imglstbase64.put(0,encodedImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            String[] dateArr=date.split("/");
            String newDate=dateArr[1]+"/"+dateArr[0]+"/"+dateArr[2];
            long n = qr.insertHomework(givenby, sub, newDate, txtlst, imglstbase64.toString(), sharedpreferences.getString("STANDARD", ""), sharedpreferences.getString("DIVISION", ""), htext,hwUUID);
            if (n > 0) {
                // Toast.makeText(getActivity(), "Homework Inserted Successfully", Toast.LENGTH_SHORT).show();
                n = -1;

                hid = qr.getHomeworkId();
                SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                n = qr.insertQueue(hid, htext, "1", df1.format(calendar.getTime()));
            }

        }

        ParentHomeWorkFragment fragment = new ParentHomeWorkFragment();
        Singleton.setMainFragment(fragment);
        Singleton.setSelectedFragment(fragment);
        Bundle bundle = new Bundle();
        bundle.putString("HEADERTEXT", htext);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(Singleton.getImageList().size()>0)
        {
            new GetImagesForEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            addImage.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnBackPress() {
        Singleton.setSelectedFragment(Singleton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }
}
