package realizer.com.schoolgenieparent.myclass;

import android.app.Fragment;
import android.content.Intent;
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
import realizer.com.schoolgenieparent.Utils.OnBackPressFragment;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.backend.DALMyPupilInfo;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;


public class MyPupilInfoFragment extends Fragment implements OnBackPressFragment{
    TextView name,studclass,studdiv,rollno,dob,hobbies,bloodgroup,parentname,contactno,address,emergencyContact,profile_init;
    TextView nameT,studclassT,studdivT,rollnoT,dobT,hobbiesT,bloodgroupT,parentnameT,contactnoT,addressT,emergencyContactT;
    DALMyPupilInfo qr;
    ImageView profile_pic;
    String img;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.mypupil_layout, container, false);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Pupil Information", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");
        name = (TextView)rootView.findViewById(R.id.txtPupilName);
        name.setTypeface(face);

        studclass = (TextView)rootView.findViewById(R.id.txtClass);
        studdiv = (TextView)rootView.findViewById(R.id.txtDivision);
        rollno = (TextView)rootView.findViewById(R.id.txtRollNo);
        rollnoT = (TextView)rootView.findViewById(R.id.txtRollNoT);
        dob = (TextView)rootView.findViewById(R.id.txtDOB);
        dobT = (TextView)rootView.findViewById(R.id.txtDOBT);
        hobbies = (TextView)rootView.findViewById(R.id.txthobbies);
        hobbiesT = (TextView)rootView.findViewById(R.id.txthobbiesT);
        bloodgroup = (TextView)rootView.findViewById(R.id.txtBloodGroup);
        bloodgroupT = (TextView)rootView.findViewById(R.id.txtBloodGroupT);
        parentname = (TextView)rootView.findViewById(R.id.txtparentName);
        parentnameT = (TextView)rootView.findViewById(R.id.txtparentNameT);
        contactno = (TextView)rootView.findViewById(R.id.txtContactNo);
        contactnoT = (TextView)rootView.findViewById(R.id.txtContactNoT);
        emergencyContact = (TextView)rootView.findViewById(R.id.txtEmergencyContactNo);
        emergencyContactT = (TextView)rootView.findViewById(R.id.txtEmergencyContactNoT);
        address = (TextView)rootView.findViewById(R.id.txtAddress);
        addressT = (TextView)rootView.findViewById(R.id.txtAddressT);
        profile_pic = (ImageView) rootView.findViewById(R.id.profilepicpupil);
        profile_init = (TextView)rootView.findViewById(R.id.txtinitialPupil);
        profile_init.setTypeface(face);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
       /* String s = preferences.getString("STANDARD", "");
        String d = preferences.getString("DIVISION", "");
        Bundle b = getArguments();
        String rno = b.getString("RollNo","");*/
        qr = new DALMyPupilInfo(getActivity());
        String stud[]= qr.GetAllTableData(preferences.getString("StudentUserID",""));

        name.setText(stud[0]+" "+stud[1]+" "+stud[2]);

        studclass.setText(stud[3]);
        studdiv.setText(stud[4]);
        if( !stud[6].equals("") &&  !stud[6].equals(null) &&  !stud[6].equals("null")) {
            String timestamp = stud[6].trim().split("\\(")[1].trim().split("\\-")[0];
            if(timestamp.isEmpty())
                timestamp = stud[6].trim().split("\\(")[1].trim().split("\\-")[1];
            if(!timestamp.isEmpty()) {
                Date createdOn = new Date(Long.parseLong(timestamp));
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                String formattedDate = sdf.format(createdOn);
                dob.setText(formattedDate);
            }
            else
            {
                dob.setText("-");
            }
        }
        else
            dob.setText("No Birthday Found");

        parentname.setText(stud[9]+" "+stud[2]);
        contactno.setText(stud[10]);

        if (stud[14]!=null && !stud[14].equalsIgnoreCase("null"))
        {
            profile_init.setVisibility(View.GONE);
            profile_pic.setVisibility(View.VISIBLE);
            String urlString = stud[14];
            StringBuilder sb=new StringBuilder();
            for(int j=0;j<urlString.length();j++)
            {
                char c='\\';
                if (urlString.charAt(j) =='\\')
                {
                    urlString.replace("\"","");
                    sb.append("/");
                }
                else
                {
                    sb.append(urlString.charAt(j));
                }
            }
            String newURL=sb.toString();
            if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                new GetImages(newURL,profile_pic,profile_init,stud[0]+" "+stud[2],newURL.split("/")[newURL.split("/").length-1]).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,newURL);
            else
            {
                File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length-1]);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                profile_pic.setImageBitmap(bitmap);
            }
        }
        else
        {
            String firstName=stud[0];
            String lastName=stud[2];
            profile_pic.setVisibility(View.GONE);
            profile_init.setVisibility(View.VISIBLE);
            profile_init.setText( String.valueOf(firstName.charAt(0)).toUpperCase()+ String.valueOf(lastName.charAt(0)).toUpperCase());
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void OnBackPress() {
        Intent intent=new Intent(getActivity(),DrawerActivity.class);
        startActivity(intent);
    }
}
