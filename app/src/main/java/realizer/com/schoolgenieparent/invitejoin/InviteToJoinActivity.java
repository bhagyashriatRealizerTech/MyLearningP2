package realizer.com.schoolgenieparent.invitejoin;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import realizer.com.schoolgenieparent.Utils.Singleton;

/**
 * Created by Win on 24/08/2016.
 */
public class InviteToJoinActivity extends Fragment implements OnBackPressFragment
{

    Button btnSendInvite;
    String htext;
    TextView txtapplink;
    EditText inviteMsgTxt;
    List namelist=new ArrayList<String>();
    List numberlist=new ArrayList<String>();
    List namelist2=new ArrayList<String>();
    List mobList=new ArrayList<String>();
    ListView lstInviteNumber;
    StringBuilder sb;
    ArrayAdapter<String> adapter;
    String SENT="SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    String allmobno[];
    String mobno;
    String listMobno;
    String[] value=null;
    List<String> contact,cnum;
    String numLst;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.invitetojoin_activity, container, false);
        Bundle b = getArguments();
        htext = b.getString("HEADERTEXT");
        setHasOptionsMenu(true);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        contact=new ArrayList<>();
        cnum=new ArrayList<>();
        String values="";
        sb=new StringBuilder();
        btnSendInvite= (Button) rootview.findViewById(R.id.btnSendInvite);
        lstInviteNumber= (ListView) rootview.findViewById(R.id.lstinvitenumber);
        inviteMsgTxt= (EditText) rootview.findViewById(R.id.edtInviteMsgtxt);
        txtapplink= (TextView) rootview.findViewById(R.id.txtappLink);
        lstInviteNumber.setAdapter(null);


        StringBuilder sb1=new StringBuilder();
        value=sb1.toString().split(",");
        Bundle b2=getArguments();
        String nameLst=b2.getString("nameList");
        numLst=b2.getString("numList");
        if (TextUtils.isEmpty(nameLst)) {
            namelist2.removeAll(namelist);
            namelist2.add("Select Contact");
        } else {
            String[] name=nameLst.split(",");
            String[] number=numLst.split(",");
            for (int i=0;i<name.length;i++)
            {
                namelist.add(name[i]);
                numberlist.add(number[i]);
                namelist2.add(name[i]+" "+number[i]);
            }
        }
        TextView tv= (TextView) rootview.findViewById(R.id.deleteImg);
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.contact_list2,R.id.contactname1, namelist2);
        lstInviteNumber.setAdapter(adapter);
//        if (namelist.contains("Select Contact"))
//        {
//            tv.setVisibility(View.GONE);
//        }
//        else
//        {
//            tv.setVisibility(View.VISIBLE);
//        }
        lstInviteNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                namelist2.remove(position);
                lstInviteNumber.setAdapter(adapter);
            }
        });
        btnSendInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String namelist1;
                for (int i=0;i<namelist2.size();i++)
                {
                    namelist1=namelist2.get(i).toString();
                    mobList.add(namelist1.split(" ")[namelist1.split(" ").length - 1]);
                }
                Toast.makeText(getActivity(), mobList.toString(), Toast.LENGTH_SHORT).show();
                String[] numlist=numLst.split(",");
                String inviteMsg = inviteMsgTxt.getText().toString();
                if (namelist.equals(null)) {
                    Toast.makeText(getActivity(), "Add Mobile Numbers", Toast.LENGTH_SHORT).show();
                } else if (inviteMsg.equals("")) {
                    Toast.makeText(getActivity(), "Enter Message", Toast.LENGTH_SHORT).show();
                } else {
                    if (!lstInviteNumber.equals(null)) {
                        for (int i = 1; i <= mobList.size(); i++) {
                            //sb.append(mobList.get(i)+" ");
                            if (mobList.size() == 1) {
                                sb.append(mobList.get(i - 1));
                            } else if (i == mobList.size()) {
                                sb.append(mobList.get(i - 1));
                            } else {
                                sb.append(mobList.get(i - 1) + ";");
                            }
                        }
                        mobno = sb.toString();

                        Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_SHORT).show();
                        //String mobno = mobno;
                        String allmobno[] = mobno.split(";");
                        String smss = inviteMsgTxt.getText().toString() + " \n" + txtapplink.getText().toString();
                        sendSMS(allmobno, smss);
                        Toast.makeText(getActivity(), "Message sent \n" , Toast.LENGTH_SHORT).show();
                        sb = null;
                        namelist.removeAll(namelist);
                        inviteMsgTxt.setText("");
                        lstInviteNumber.setAdapter(null);
                        namelist.add("No Contact Added");
                        //lstInviteNumber.setAdapter(new contact_list_adapter(getActivity(), namelist,cnum));
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
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                ContactListFragment fragment = new ContactListFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                Bundle b1=new Bundle();
                b1.putString("HEADERTEXT","Select Contact");
                fragment.setArguments(b1);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
                Singleton.setSelectedFragment(fragment);

//                View view=View.inflate(getActivity(),R.layout.contact_list,null);
//                AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(getActivity());
//                alertdialogbuilder.setTitle(Html.fromHtml("<font color='#fec10e'>Select Contact for Invite</font>"));
////                alertdialogbuilder.setView(view);
//                int count = value.length;
//                boolean[] is_checked = new boolean[count];
//                alertdialogbuilder.setMultiChoiceItems(value, is_checked, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//
//                    }
//                });
//                alertdialogbuilder.setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                ListView list = ((AlertDialog) dialog).getListView();
//                                // make selected item in the comma seprated string
//                                StringBuilder stringBuilder = new StringBuilder();
//                                for (int i = 0; i < list.getCount(); i++) {
//                                    boolean checked = list.isItemChecked(i);
//                                    if (checked) {
//                                        if (namelist.contains("No Contact Added"))
//                                        {
//                                            namelist.remove(0);
//                                            cnum.remove(0);
//                                            namelist.add(list.getItemAtPosition(i));
//                                        }
//                                        else if (namelist.contains(list.getItemAtPosition(i)))
//                                        {
//                                            Toast.makeText(getActivity(), "This contact is selected", Toast.LENGTH_SHORT).show();
//                                        }
//                                        else {
//                                                namelist.add(list.getItemAtPosition(i));
//                                                //String num[]=selectedText.split(" ");
//                                        }
//                                    }
//                                }
//                                //lstInviteNumber.setAdapter(new contact_list_adapter(getActivity(), namelist,cnum));
//                            }
//                        });
//
//                alertdialogbuilder.setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        });
//                AlertDialog dialog = alertdialogbuilder.create();
//                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
