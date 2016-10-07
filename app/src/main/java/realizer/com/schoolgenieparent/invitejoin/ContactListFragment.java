package realizer.com.schoolgenieparent.invitejoin;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import realizer.com.schoolgenieparent.DrawerActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.OnBackPressFragment;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.invitejoin.adapter.ContactsAdapter;
import realizer.com.schoolgenieparent.invitejoin.adapter.ContactsListClass;
import realizer.com.schoolgenieparent.invitejoin.model.ContactModel;

/**
 * Created by Win on 05/10/2016.
 */
public class ContactListFragment extends Fragment implements OnBackPressFragment
{
    ListView lst;
    String htext;
    EditText edtmessage;
    TextView textView,noContact;
    String mobno;
    String SENT="SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    Context context = null;
    ContactsAdapter objAdapter;
    ListView lv = null;
    List mobList=new ArrayList<String>();
    EditText edtSearch = null;
    LinearLayout llContainer = null;
    //    Button btnOK = null;
    RelativeLayout rlPBContainer = null;
    StringBuffer sb = new StringBuffer();
    StringBuffer sb2=new StringBuffer();
    String s;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.contact_list_fragment, container, false);
        setHasOptionsMenu(true);
        Bundle b = getArguments();
        htext = b.getString("HEADERTEXT");
        //lst= (ListView) rootview.findViewById(R.id.listcontact);
        //setHasOptionsMenu(true);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        context=getActivity();
        rlPBContainer = (RelativeLayout) rootview.findViewById(R.id.pbcontainer);
        edtSearch = (EditText) rootview.findViewById(R.id.input_search);
        llContainer = (LinearLayout) rootview.findViewById(R.id.data_container);
        noContact= (TextView) rootview.findViewById(R.id.textviewNocontact);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                // When user changed the Text
                String text = edtSearch.getText().toString().toLowerCase(Locale.getDefault());
                objAdapter.filter(text);
                if (objAdapter.getCount()==0)
                {
//                    Toast.makeText(getActivity(), "No Contacts", Toast.LENGTH_SHORT).show();
                    llContainer.setVisibility(View.GONE);
                    noContact.setVisibility(View.VISIBLE);
                }
                else
                {
                    llContainer.setVisibility(View.VISIBLE);
                    noContact.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        addContactsInList();
        return rootview;
    }

    private void getSelectedContacts() {
        // TODO Auto-generated method stub
        StringBuffer sb3=new StringBuffer();
        for (ContactModel bean : ContactsListClass.phoneList) {
            if (bean.isSelected()) {
                sb.append(bean.getNumber().replace(" ",""));
                sb.append(",");
                sb2.append(bean.getName().replace(" ",""));
                sb2.append(",");
                sb3.append(bean.getImage());
                sb3.append(",");
            }
        }
        s = sb.toString().trim();
        String name=sb2.toString().trim();
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(context, "Select atleast one Contact",
                    Toast.LENGTH_SHORT).show();
        } else {
            s = s.substring(0, s.length() - 1);
            name=name.substring(0, name.length() - 1);
//            Toast.makeText(context, "Selected Contacts : " +name+" "+s,
//                    Toast.LENGTH_SHORT).show();
        }
    }
    private void addContactsInList() {
        // TODO Auto-generated method stub
        Thread thread = new Thread() {
            @Override
            public void run() {
                showPB();
                try {
                    Cursor phones = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, null, null, null);
                    try {
                        ContactsListClass.phoneList.clear();
                    } catch (Exception e) {
                    }
                    while (phones.moveToNext()) {
                        String phoneName = phones
                                .getString(phones
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String phoneNumber = phones
                                .getString(phones
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String phoneImage = phones
                                .getString(phones
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

                        ContactModel cp = new ContactModel();


                        cp.setName(phoneName);
                        cp.setNumber(phoneNumber);
                        cp.setImage(phoneImage);
                        ContactsListClass.phoneList.add(cp);
                    }
                    phones.close();
                    lv = new ListView(context);
                    lv.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            llContainer.addView(lv);
                        }
                    });
                    Collections.sort(ContactsListClass.phoneList,
                            new Comparator<ContactModel>() {
                                @Override
                                public int compare(ContactModel lhs,
                                                   ContactModel rhs) {
                                    return lhs.getName().compareTo(
                                            rhs.getName());
                                }
                            });
                    objAdapter = new ContactsAdapter(getActivity(), ContactsListClass.phoneList);
                    lv.setAdapter(objAdapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            CheckBox chk = (CheckBox) view.findViewById(R.id.contactcheck);
                            ContactModel bean = ContactsListClass.phoneList.get(position);
                            if (bean.isSelected()) {
                                bean.setSelected(false);
                                chk.setChecked(false);
                            } else {
                                bean.setSelected(true);
                                chk.setChecked(true);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hidePB();
            }
        };
        thread.start();
    }
    void showPB() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                rlPBContainer.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.GONE);
                //btnOK.setVisibility(View.GONE);
            }
        });
    }
    void hidePB() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                rlPBContainer.setVisibility(View.GONE);
                edtSearch.setVisibility(View.VISIBLE);
                //btnOK.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void OnBackPress() {
        Intent intent=new Intent(getActivity(),DrawerActivity.class);
        startActivity(intent);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_sendmessage, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                getSelectedContacts();
                final Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");

                if (TextUtils.isEmpty(s))
                {
                    //Toast.makeText(getActivity(), "Please Select Contact", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    LayoutInflater inflater=getActivity().getLayoutInflater();
                    View dialoglayout = inflater.inflate(R.layout.invite_message_layout, null);
                    Button send = (Button) dialoglayout.findViewById(R.id.btn_send);
                    Button cancel = (Button)dialoglayout.findViewById(R.id.btn_cancel);
                    edtmessage=(EditText)dialoglayout.findViewById(R.id.edtmessage);
                    textView=(TextView) dialoglayout.findViewById(R.id.txtappLink);
                    send.setTypeface(face);
                    cancel.setTypeface(face);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(dialoglayout);
                    final AlertDialog alertDialog = builder.create();

                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StringBuilder sb1=new StringBuilder();
                            String[] nums =s.split(",");
                            for (int i=0;i<nums.length;i++)
                            {
                                nums[i].replace("(","");
                                nums[i].replace(")","");
                                mobList.add(nums[i]);
                            }
                            //Toast.makeText(getActivity(), mobList.toString(), Toast.LENGTH_SHORT).show();
                            String inviteMsg = edtmessage.getText().toString();
                            if (inviteMsg.equals("")) {
                                Toast.makeText(getActivity(), "Enter Message", Toast.LENGTH_SHORT).show();
                            }
                            else if (TextUtils.isEmpty(inviteMsg))
                            {
                                Toast.makeText(getActivity(), "Enter Message", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if (!mobList.equals(null)) {
                                    for (int i = 1; i <= mobList.size(); i++) {
                                        //sb.append(mobList.get(i)+" ");
                                        if (mobList.size() == 1) {
                                            sb1.append(mobList.get(i - 1));
                                        } else if (i == mobList.size()) {
                                            sb1.append(mobList.get(i - 1));
                                        } else {
                                            sb1.append(mobList.get(i - 1) + ";");
                                        }
                                    }
                                    mobno = sb1.toString();
                                    //Toast.makeText(getActivity(), sb1.toString(), Toast.LENGTH_SHORT).show();
                                    String allmobno[] = mobno.split(";");
                                    String smss = edtmessage.getText().toString() + " \n" + textView.getText().toString();
                                    sendSMS(allmobno, smss);
                                    Toast.makeText(getActivity(), "Message sent" , Toast.LENGTH_SHORT).show();
                                    sb1 = null;
                                    edtmessage.setText("");
                                    alertDialog.dismiss();
                                    Intent intent=new Intent(getActivity(),DrawerActivity.class);
                                    startActivity(intent);
                                    //lstInviteNumber.setAdapter(new contact_list_adapter(getActivity(), namelist,cnum));
                                } else {
                                    Toast.makeText(getActivity(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

}
