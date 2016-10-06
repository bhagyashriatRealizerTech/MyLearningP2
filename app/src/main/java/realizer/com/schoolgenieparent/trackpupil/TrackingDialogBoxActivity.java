package realizer.com.schoolgenieparent.trackpupil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;
import realizer.com.schoolgenieparent.trackpupil.asynctask.TrackingAsyckTaskGet;

/**
 * Created by Win on 11/30/2015.
 */
public class TrackingDialogBoxActivity extends DialogFragment {
    String Latitude;
    String Longitude;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.parent_tracking_alert_dailog, null);

        final EditText username=(EditText)view.findViewById(R.id.driverUsername);
        final EditText userid=(EditText)view.findViewById(R.id.driverUserID);
        Button showinfo = (Button) view.findViewById(R.id.ShowInfo);
        Button shoiwmap = (Button) view.findViewById(R.id.ShowMap);
        Button cancel = (Button) view.findViewById(R.id.Cancel);
        showinfo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (username.getText().toString().equals("")) {
                                                Toast.makeText(getActivity(), "Please enter username...", Toast.LENGTH_LONG).show();
                                            } else {
                                                TrackShowInfo fragment = new TrackShowInfo();
                                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("USERNAME", username.getText().toString());
                                                bundle.putString("USERID", userid.getText().toString());
                                                fragment.setArguments(bundle);
                                                fragmentTransaction.addToBackStack(null);
                                                fragmentTransaction.replace(R.id.frame_container, fragment);
                                                fragmentTransaction.commit();
                                                dismiss();

                                                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                                SharedPreferences.Editor edit = sharedpreferences.edit();
                                                edit.putString("USERNAME", username.getText().toString());
                                                edit.putString("USERID", userid.getText().toString());
                                                edit.putString("Tracking", "false");
                                                edit.commit();
                                            }
                                        }
                                    }

        );

        shoiwmap.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick (View v){
                                            if (username.getText().toString().equals("")) {
                                                Toast.makeText(getActivity(), "Please enter username...", Toast.LENGTH_LONG).show();
                                            }
                                            else  if (userid.getText().toString().equals("")) {
                                                Toast.makeText(getActivity(), "Please enter user id...", Toast.LENGTH_LONG).show();
                                            }else {
                                                TrackingAsyckTaskGet obj = new TrackingAsyckTaskGet(username.getText().toString(),userid.getText().toString(), getActivity());
                                                try {
                                                    StringBuilder result1;
                                                    result1 = obj.execute().get();
                                                    String dailyDriverList = result1.toString();
                                                    try {
                                                        //JSONObject obj = new JSONObject(s);
                                                        JSONArray locList = new JSONArray(dailyDriverList);
                                                      //  for(int i=0;i<locList.length();i++) {
                                                            JSONObject obj1 = locList.getJSONObject(locList.length()-1);

                                                            Intent intent = new Intent(getActivity(), TrackShowMap.class);
                                                            Bundle bundle = new Bundle();
                                                            bundle.putString("USERNAME", username.getText().toString());
                                                            bundle.putString("USERID", userid.getText().toString());
                                                            bundle.putString("LATITUDE", obj1.getString("latitude"));
                                                            bundle.putString("LONGITUDE", obj1.getString("longitude"));
                                                            intent.putExtras(bundle);
                                                            startActivity(intent);

                                                        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                                        SharedPreferences.Editor edit = sharedpreferences.edit();
                                                        edit.putString("USERNAME", username.getText().toString());
                                                        edit.putString("USERID", userid.getText().toString());
                                                        edit.putString("Tracking", "false");
                                                        edit.commit();
                                                        Singleton.setIsShowMap(true);
                                                        dismiss();
                                                      //  }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
/*                                                    String Temp = dailyDriverList.replaceAll("\"", "");
                                                    if (Temp.equals(",")) {
                                                        Toast.makeText(getActivity(), "Server Not Responding " + dailyDriverList, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        String[] driverList = dailyDriverList.split("@@@");

                                                        for (String studDailyAtt : driverList) {
                                                            String[] schooldriDetails = studDailyAtt.split(",");
                                                            for (int i = 0; i < schooldriDetails.length; i++) {
                                                                Latitude = (schooldriDetails[0].replaceAll("\"", ""));
                                                                Log.d("Latitude is=", Latitude);
                                                                Longitude = (schooldriDetails[1].replaceAll("\"", ""));
                                                                Log.d("Longitude is=", Longitude);
                                                                break;
                                                            }
                                                        }

                                                        Intent i = new Intent(getActivity(), TrackShowMap.class);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("USERNAME", username.getText().toString());
                                                        bundle.putString("LATITUDE", Latitude);
                                                        bundle.putString("LONGITUDE", Longitude);
                                                        i.putExtras(bundle);
                                                        startActivity(i);
                                                    }*/
                                                }catch(InterruptedException e){
                                                    e.printStackTrace();
                                                }catch(ExecutionException e){
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
                                    }

        );


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor edit = sharedpreferences.edit();
                if (Singleton.isIsShowMap())
                {
                    edit.putString("Tracking", "false");
                }
                else
                {
                    edit.putString("Tracking", "true");
                }
                edit.commit();
                dismiss();
            }
        });


        builder.setTitle("Pupil Tracking");
        builder.setView(view);

        return builder.create();
    }
}
