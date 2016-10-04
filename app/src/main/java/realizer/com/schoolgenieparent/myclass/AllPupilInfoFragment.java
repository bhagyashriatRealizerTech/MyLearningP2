package realizer.com.schoolgenieparent.myclass;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import realizer.com.schoolgenieparent.DrawerActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.GetImages;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;
import realizer.com.schoolgenieparent.view.ProgressWheel;


public class AllPupilInfoFragment extends Fragment  {
    TextView name,studclass,studdiv,rollno,dob,hobbies,bloodgroup,parentname,contactno,address,emergencyContact,profile_init;
    TextView nameT,studclassT,studdivT,rollnoT,dobT,hobbiesT,bloodgroupT,parentnameT,contactnoT,addressT,emergencyContactT;
    DatabaseQueries qr;
    ImageView profile_pic;
    ProgressWheel loading;
    String img;
    String uid;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.mypupil_layout, container, false);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Pupil Information", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");
        name = (TextView)rootView.findViewById(R.id.txtPupilName);
        name.setTypeface(face);

        studclass = (TextView)rootView.findViewById(R.id.txtClass);
        //studclassT = (TextView)rootView.findViewById(R.id.txtClassT);
       /* studclass.setTypeface(face);
        studclassT.setTypeface(face);*/

        studdiv = (TextView)rootView.findViewById(R.id.txtDivision);
       // studdivT = (TextView)rootView.findViewById(R.id.txtDivisionT);
       /* studdiv.setTypeface(face);
        studdivT.setTypeface(face);*/

        rollno = (TextView)rootView.findViewById(R.id.txtRollNo);
        //rollno.setTypeface(face);
        rollnoT = (TextView)rootView.findViewById(R.id.txtRollNoT);
       // rollnoT.setTypeface(face);

        dob = (TextView)rootView.findViewById(R.id.txtDOB);
      //  dob.setTypeface(face);
        dobT = (TextView)rootView.findViewById(R.id.txtDOBT);
       // dobT.setTypeface(face);

        hobbies = (TextView)rootView.findViewById(R.id.txthobbies);
       // hobbies.setTypeface(face);
        hobbiesT = (TextView)rootView.findViewById(R.id.txthobbiesT);
        //hobbiesT.setTypeface(face);

        bloodgroup = (TextView)rootView.findViewById(R.id.txtBloodGroup);
      //  bloodgroup.setTypeface(face);
        bloodgroupT = (TextView)rootView.findViewById(R.id.txtBloodGroupT);
       // bloodgroupT.setTypeface(face);

        parentname = (TextView)rootView.findViewById(R.id.txtparentName);
       // parentname.setTypeface(face);
        parentnameT = (TextView)rootView.findViewById(R.id.txtparentNameT);
      //  parentnameT.setTypeface(face);

        contactno = (TextView)rootView.findViewById(R.id.txtContactNo);
       // contactno.setTypeface(face);
        contactnoT = (TextView)rootView.findViewById(R.id.txtContactNoT);
       // contactnoT.setTypeface(face);

        emergencyContact = (TextView)rootView.findViewById(R.id.txtEmergencyContactNo);
      //  emergencyContact.setTypeface(face);
        emergencyContactT = (TextView)rootView.findViewById(R.id.txtEmergencyContactNoT);
       // emergencyContactT.setTypeface(face);

        address = (TextView)rootView.findViewById(R.id.txtAddress);
      //  address.setTypeface(face);
        addressT = (TextView)rootView.findViewById(R.id.txtAddressT);
       // addressT.setTypeface(face);

        loading = (ProgressWheel)rootView.findViewById(R.id.loading);

        profile_pic = (ImageView) rootView.findViewById(R.id.profilepicpupil);
        profile_init = (TextView)rootView.findViewById(R.id.txtinitialPupil);
        profile_init.setTypeface(face);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String s = preferences.getString("STANDARD", "");
        String d = preferences.getString("DIVISION", "");
        uid = preferences.getString("StudentUserID", "");

        new GEtPupilInfoAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return rootView;
    }

    public class GEtPupilInfoAsyncTask extends AsyncTask<Void,Void,JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {

            Bundle b = getArguments();
            String rno = b.getString("RollNo", "");
            qr = new DatabaseQueries(getActivity());
            String stud = qr.GetAllTableData(uid);
            JSONObject jObj = null;

            try {
                JSONArray arr = new JSONArray(stud);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    if (rno.equals(obj.getString("userId"))) {

                        jObj = obj;

                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jObj;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(JSONObject obj) {
            super.onPostExecute(obj);
            loading.setVisibility(View.GONE);
            try {
                name.setText(obj.getString("fName") + " " + obj.getString("mName") + " " + obj.getString("lName"));

                studclass.setText(obj.getString("std"));
                studdiv.setText(obj.getString("division"));
                // rollno.setText(obj.getString("classRollNo"));

                if (!obj.getString("dob").equals("") && !obj.getString("dob").equals(null) && !obj.getString("dob").equals("null")) {
                    String timestamp = obj.getString("dob").trim().split("\\(")[1].trim().split("\\-")[0];
                    if (timestamp.isEmpty())
                        timestamp = obj.getString("dob").trim().split("\\(")[1].trim().split("\\-")[1];
                    if (!timestamp.isEmpty()) {
                        Date createdOn = new Date(Long.parseLong(timestamp));
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                        String formattedDate = sdf.format(createdOn);
                        dob.setText(formattedDate);
                    } else {
                        dob.setText("-");
                    }
                } else
                    dob.setText("No Birthday Found");
                //hobbies.setText(obj.getString("hobbies"));
                // bloodgroup.setText(obj.getString("bldGrp"));
                parentname.setText(obj.getString("mName") + " " + obj.getString("lName"));
                contactno.setText(obj.getString("contactNo"));
                // address.setText(obj.getString("address"));

                if (obj.getString("ThumbnailURL") != null && !obj.getString("ThumbnailURL").equalsIgnoreCase("null")) {
                    profile_init.setVisibility(View.GONE);
                    profile_pic.setVisibility(View.VISIBLE);
                    String urlString = obj.getString("ThumbnailURL");
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < urlString.length(); j++) {
                        char c = '\\';
                        if (urlString.charAt(j) == '\\') {
                            urlString.replace("\"", "");
                            sb.append("/");
                        } else {
                            sb.append(urlString.charAt(j));
                        }
                    }
                    String newURL = sb.toString();
                    if (!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                        new GetImages(newURL, profile_pic, profile_init, obj.getString("fName") + " " + obj.getString("lName"), newURL.split("/")[newURL.split("/").length - 1]).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newURL);
                    else {
                        File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                        //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                        profile_pic.setImageBitmap(bitmap);
                    }
                } else {
                    String firstName = obj.getString("fName");
                    String lastName = obj.getString("lName");
                    profile_pic.setVisibility(View.GONE);
                    profile_init.setVisibility(View.VISIBLE);
                    profile_init.setText(String.valueOf(firstName.charAt(0)).toUpperCase() + String.valueOf(lastName.charAt(0)).toUpperCase());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*@Override
    public void onFragmentBackPressed() {
        Singlton.setSelectedFragment(Singlton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }*/

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
