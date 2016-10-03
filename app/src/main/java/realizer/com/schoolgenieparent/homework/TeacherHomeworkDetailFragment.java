package realizer.com.schoolgenieparent.homework;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import realizer.com.schoolgenieparent.DrawerActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.OnBackPressFragment;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.homework.backend.DALHomework;
import realizer.com.schoolgenieparent.homework.model.ParentHomeworkListModel;
import realizer.com.schoolgenieparent.view.FullImageViewActivity;

/**
 * Created by Bhagyashri on 8/29/2016.
 */
public class TeacherHomeworkDetailFragment extends Fragment implements OnBackPressFragment {

    TextView txtSubject,txtDate,txtTeacherName,txtDescription,txtdevider;
    TextView txtstd ,txtclss;
    String htext,path;
    ImageView image1,image2,image3;
    LinearLayout imagelayout;
    ProgressDialog progressDialog;
    DALHomework qr;
    String hwDate;
    int imgid=0;
    ArrayList<ParentHomeworkListModel> presentImgList;
    String finalBitmapString="";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.teacher_homework_detail_layout, container, false);
        initiateView(rootView);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("SyncStd", ""));
        txtclss.setText(preferences.getString("SyncDiv", ""));

        Bundle bundle= getArguments();
        htext = bundle.getString("HEADERTEXT");
        path = bundle.getString("HomeworkImage");
        imgid=bundle.getInt("Imageid");
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        presentImgList=new ArrayList<>();
        txtSubject.setText(bundle.getString("SubjectName"));
        txtDate.setText(bundle.getString("HomeworkDate"));
        hwDate=bundle.getString("HomeworkDate");
        txtTeacherName.setText( bundle.getString("TeacherName"));
        String hwText=bundle.getString("HomeworkText");
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<hwText.length();i++)
        {
            char c='\\';
            if (hwText.charAt(i) =='[' || hwText.charAt(i) ==']' || hwText.charAt(i) =='"')
            {
                hwText.replace("[]\"", "");
            }
            else
            {
                sb.append(hwText.charAt(i));
            }
        }

        txtDescription.setText(sb.toString());

        if(path.equalsIgnoreCase("NoImage")) {
          /*  frameimageClik.setVisibility(View.GONE);*/
            txtdevider.setVisibility(View.GONE);
            imagelayout.setVisibility(View.GONE);
        }
        else {

          /*  frameimageClik.setVisibility(View.VISIBLE);*/
            txtdevider.setVisibility(View.VISIBLE);
            imagelayout.setVisibility(View.VISIBLE);
            String[] IMG ;
            String[] IMG1 ;
            try {
                qr= new DALHomework(getActivity());
                ArrayList<ParentHomeworkListModel> hwlst = qr.GetHomeworkInfoData(hwDate,htext);

                //getting path
                JSONArray jarr = new JSONArray(path);

                IMG = new String[jarr.length()];
                IMG1 = new String[jarr.length()];
                for(int i=0;i<jarr.length();i++)
                {
                    IMG[i] = jarr.getString(i);
                }


                boolean isPresent=false;
                int imglistno=0;
                for (int j=0;j<IMG.length;j++)
                {
                    String pathimg=getStringPath(IMG[j]);
                    for (int k=0;k<hwlst.size();k++)
                    {
                        String dbimg=getStringPath(hwlst.get(k).getImage());
                        if (pathimg.equalsIgnoreCase(dbimg)) {

                            finalBitmapString=hwlst.get(k).getImage();
                            JSONArray jarr1 = new JSONArray(hwlst.get(k).getImage());

                            for(int i=0;i<jarr1.length();i++)
                            {
                                IMG1[i] = jarr1.getString(i);
                            }
                            break;
                        }
                    }
                }



                if(jarr.length()==1) {
                    image1.setVisibility(View.VISIBLE);
                    image2.setVisibility(View.INVISIBLE);
                    image3.setVisibility(View.INVISIBLE);

                    String filePath = IMG1[0];
                    byte[] decodedString = Base64.decode(filePath, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image1.setImageBitmap(decodedByte);
                }
                else if(jarr.length()==2) {
                    image1.setVisibility(View.VISIBLE);
                    image2.setVisibility(View.VISIBLE);
                    image3.setVisibility(View.INVISIBLE);

                    String filePath = IMG1[0];
                    byte[] decodedString = Base64.decode(filePath, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image1.setImageBitmap(decodedByte);

                     filePath = IMG1[1];
                     decodedString = Base64.decode(filePath, Base64.DEFAULT);
                     decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                     image2.setImageBitmap(decodedByte);
                }
                else if(jarr.length()==3) {
                    image1.setVisibility(View.VISIBLE);
                    image2.setVisibility(View.VISIBLE);
                    image3.setVisibility(View.VISIBLE);

                    String filePath = IMG1[0];
                    byte[] decodedString = Base64.decode(filePath, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image1.setImageBitmap(decodedByte);

                    filePath = IMG1[1];
                    decodedString = Base64.decode(filePath, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image2.setImageBitmap(decodedByte);

                    filePath = IMG1[2];
                    decodedString = Base64.decode(filePath, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image3.setImageBitmap(decodedByte);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        /*txtImageClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ImageString", path);
                editor.commit();
                loadPhoto(path);
            }
        });*/

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath =  finalBitmapString+"@@@0";
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ImageString", tempPath);
                editor.commit();
                loadPhoto(0);
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath =  finalBitmapString+"@@@1";
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ImageString", tempPath);
                editor.commit();
                loadPhoto(1);
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath =  finalBitmapString+"@@@2";
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ImageString", tempPath);
                editor.commit();
                loadPhoto(2);
            }
        });




        return rootView;
    }

    public void initiateView(View view)
    {
        txtSubject = (TextView)view.findViewById(R.id.txtsubject);
        txtDate = (TextView)view.findViewById(R.id.txthomeworkdate);
        txtTeacherName = (TextView)view.findViewById(R.id.txtteacherName);
        txtDescription = (TextView)view.findViewById(R.id.txtdescription);
        txtstd  = (TextView) view.findViewById(R.id.txttclassname);
        txtclss = (TextView) view.findViewById(R.id.txttdivname);
        /*frameimageClik = (FrameLayout) view.findViewById(R.id.frameimageClik);*/
        txtdevider = (TextView)view.findViewById(R.id.txtDivider);
        image1 = (ImageView)view.findViewById(R.id.btnCapturePicture1);
        image2 = (ImageView)view.findViewById(R.id.btnCapturePicture2);
        image3 = (ImageView)view.findViewById(R.id.btnCapturePicture3);
        imagelayout = (LinearLayout)view.findViewById(R.id.imagelayout);

    }


    private void loadPhoto(int pos) {
        Intent i = new Intent(getActivity(),FullImageViewActivity.class);
        i.putExtra("FLAG",1);
        i.putExtra("HEADERTEXT",htext);
        i.putExtra("POSITION",pos);
        i.putExtra("ListNo",imgid);
        startActivity(i);
    }


    public String getStringPath(String imgpath)
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<imgpath.length();i++)
        {
            if (imgpath.charAt(i) =='[' || imgpath.charAt(i) ==']' || imgpath.charAt(i) =='"' ||
                    imgpath.charAt(i) =='\\' || imgpath.charAt(i) =='/')
            {
                imgpath.replace("[]\"", "");
            }
            else
            {
                sb.append(imgpath.charAt(i));
            }
        }

        return sb.toString();
    }

    @Override
    public void OnBackPress() {
        Singleton.setSelectedFragment(Singleton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }
}
