package realizer.com.schoolgenieparent.trackpupil;

import android.app.Fragment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import realizer.com.schoolgenieparent.DrawerActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.OnBackPressFragment;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;
import realizer.com.schoolgenieparent.trackpupil.asynctask.TrackingAsyckTaskGet;

/**
 * Created by shree on 11/17/2015.
 */
public class TrackShowInfo extends Fragment implements OnBackPressFragment{

    TextView txtusername, txtaddress, txtcurrentlatitude, txtcurrentlongitude;
    String currentLatitude, currentLongitude;
    Bundle bundle;
    String Latitude;
    String Longitude;
    public TrackShowInfo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.driver_info_layout, container, false);

        txtusername = (TextView) rootView.findViewById(R.id.txtusername);
        txtaddress = (TextView) rootView.findViewById(R.id.txtaddress);
        txtcurrentlatitude = (TextView) rootView.findViewById(R.id.txtcurrentlatitude);
        txtcurrentlongitude = (TextView) rootView.findViewById(R.id.txtcurrentlongitude);
        bundle = new Bundle();
        bundle = this.getArguments();
        String userName = bundle.getString("USERNAME");
        String userId = bundle.getString("USERID");
        txtusername.setText(userName);
        StringBuilder result;
        TrackingAsyckTaskGet obj = new TrackingAsyckTaskGet(userName,userId, getActivity());
        try {
            result = obj.execute().get();
            String Temp = result.toString().replaceAll("\"", "");
            if (Temp.equals(""))
            {
                Toast.makeText(getActivity(), "Server not responding,Please try again later", Toast.LENGTH_LONG).show();
            }
            else {
                String dailyDriverList = result.toString();

               /* String[] schooldriDetails = dailyDriverList.split(",");
                for (int i = 0; i < schooldriDetails.length; i++) {
                    Latitude = (schooldriDetails[0].replaceAll("\"", ""));
                    Log.d("Latitude is=", Latitude);
                    Longitude = (schooldriDetails[2].replaceAll("\"", ""));
                    Log.d("Longitude is=", Longitude);
                    break;
                }
*/
                try {
                    JSONArray jsonarray = new JSONArray(result.toString());
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        Latitude = jsonobject.getString("latitude");
                        Longitude = jsonobject.getString("longitude");
                        break;
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }


               /* StringBuilder sb=new StringBuilder();
                for(int k=0;k<Latitude.length();k++)
                {

                    if (Latitude.charAt(k) =='[' ||Latitude.charAt(k) =='{' ||Latitude.charAt(k) =='}')
                    {

                    }
                    else
                    {
                        sb.append(Latitude.charAt(k));
                    }
                }

                if ()*/

              /*  Latitude=Latitude.replaceAll("[^a-zA-Z]","");
                Longitude=Longitude.replaceAll("[^a-zA-Z]","");*/
                txtcurrentlatitude.setText(Latitude);
                txtcurrentlongitude.setText(Longitude);

                Double lat = Double.parseDouble(Latitude);

                Double lng = Double.parseDouble(Longitude);

                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                //String result = null;
                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    txtaddress.setText(address+"\n"+city+","+state+"\n"+country);
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "There is no any address", Toast.LENGTH_SHORT).show();
                    Log.e("Track.LocalizedMessage", e.getLocalizedMessage());
                    Log.e("Track(StackTrace)", e.getStackTrace().toString());
                    Log.e("Track(Cause)", e.getCause().toString());
                    Log.wtf("Track(Msg)", e.getMessage());
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("Track.LocalizedMessage", e.getLocalizedMessage());
            Log.e("Track(StackTrace)", e.getStackTrace().toString());
            Log.e("Track(Cause)", e.getCause().toString());
            Log.wtf("Track(Msg)", e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e("Track.LocalizedMessage", e.getLocalizedMessage());
            Log.e("Track(StackTrace)", e.getStackTrace().toString());
            Log.e("Track(Cause)", e.getCause().toString());
            Log.wtf("Track(Msg)", e.getMessage());
        }

        return rootView;
    }

    @Override
    public void OnBackPress() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }
}

