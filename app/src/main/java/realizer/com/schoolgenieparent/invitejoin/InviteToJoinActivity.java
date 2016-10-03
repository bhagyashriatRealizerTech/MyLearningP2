package realizer.com.schoolgenieparent.invitejoin;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import realizer.com.schoolgenieparent.DrawerActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.OnBackPressFragment;

/**
 * Created by Win on 24/08/2016.
 */
public class InviteToJoinActivity extends Fragment implements OnBackPressFragment
{
    EditText edtName,edtNumber;
    Button btnSendInvite;
    String htext;
    TextView txtapplink;
    EditText inviteMsgTxt;
    List namelist=new ArrayList<String>();
    List mobList=new ArrayList<String>();
    ListView lstInviteNumber;
    StringBuilder sb;
    ArrayAdapter<String> adapter;
    String SENT="SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    String allmobno[];
    String mobno;
    String listMobno;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.invitetojoin_activity, container, false);
        Bundle b = getArguments();
        htext = b.getString("HEADERTEXT");
        setHasOptionsMenu(true);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        String values="";
        sb=new StringBuilder();
        edtName= (EditText) rootview.findViewById(R.id.edtInviteName);
        edtNumber= (EditText) rootview.findViewById(R.id.edtInviteNumber);
        btnSendInvite= (Button) rootview.findViewById(R.id.btnSendInvite);
        lstInviteNumber= (ListView) rootview.findViewById(R.id.lstinvitenumber);
        inviteMsgTxt= (EditText) rootview.findViewById(R.id.edtInviteMsgtxt);
        txtapplink= (TextView) rootview.findViewById(R.id.txtappLink);
        lstInviteNumber.setAdapter(null);
        btnSendInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inviteMsg=inviteMsgTxt.getText().toString();
                if (namelist.equals(null))
                {
                    Toast.makeText(getActivity(), "Add Mobile Numbers", Toast.LENGTH_SHORT).show();
                }
                else if (inviteMsg.equals(""))
                {
                    Toast.makeText(getActivity(), "Enter Message", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (!lstInviteNumber.equals(null)) {
                        for (int i = 1; i <=mobList.size(); i++)
                        {
                            //sb.append(mobList.get(i)+" ");
                        if (mobList.size()==1)
                        {
                            sb.append(mobList.get(i-1));
                        }
                        else if (i==mobList.size())
                        {
                            sb.append(mobList.get(i-1));
                        }
                        else
                        {
                            sb.append(mobList.get(i-1)+";");
                        }
                        }
                        mobno = sb.toString();

                        Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_SHORT).show();


                        //String mobno = mobno;
                        String allmobno[]=mobno.split(";");
                        String smss = inviteMsgTxt.getText().toString()+" \n"+txtapplink.getText().toString();
                        sendSMS(allmobno, smss);
                        Toast.makeText(getActivity(), "Message sent \n"+smss, Toast.LENGTH_SHORT).show();
                        sb = null;
                        namelist=null;
                        inviteMsgTxt.setText("");
                        lstInviteNumber.setAdapter(null);
                    } else {
                        Toast.makeText(getActivity(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return rootview;
    }
    public void sendSMS(String phoneNumber[], String message)
    {

//        try {
//
//            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//            sendIntent.putExtra("address", phoneNumber);
//            sendIntent.putExtra("sms_body", message);
//            sendIntent.setType("vnd.android-dir/mms-sms");
//            startActivity(sendIntent);
//
//        } catch (Exception e) {
//            Toast.makeText(getActivity(),
//                    "SMS faild, please try again later!",
//                    Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }


        PendingIntent sentPI = PendingIntent.getBroadcast(getActivity(), 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(getActivity(), 0,
                new Intent(DELIVERED), 0);

//---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getActivity(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getActivity(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getActivity(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getActivity(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getActivity(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

//---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getActivity(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getActivity(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        SmsManager sms = SmsManager.getDefault();
        for(String number : phoneNumber) {
            sms.sendTextMessage(number, null, message, sentPI, deliveredPI);
        }


    }

    private void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
    }

    @Override
    public void OnBackPress() {
        sb = null;
        namelist=null;
        inviteMsgTxt.setText("");
        lstInviteNumber.setAdapter(null);
        Intent intent=new Intent(getActivity(),DrawerActivity.class);
        startActivity(intent);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_add, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                namelist.add(edtName.getText().toString()+" "+edtNumber.getText().toString());
                mobList.add(edtNumber.getText().toString());
                adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,namelist);
                lstInviteNumber.setAdapter(adapter);
                edtName.setText("");
                edtNumber.setText("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
