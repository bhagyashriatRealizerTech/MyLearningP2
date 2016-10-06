package realizer.com.schoolgenieparent.invitejoin;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
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

    Context context = null;
    ContactsAdapter objAdapter;
    ListView lv = null;
    EditText edtSearch = null;
    LinearLayout llContainer = null;
    Button btnOK = null;
    RelativeLayout rlPBContainer = null;
    StringBuffer sb = new StringBuffer();
    StringBuffer sb2=new StringBuffer();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.contact_list_fragment, container, false);
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
        btnOK = (Button) rootview.findViewById(R.id.ok_button);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                getSelectedContacts();
                String s=sb.toString().trim();
                String s2=sb2.toString().trim();
                Bundle b1=new Bundle();
                b1.putString("numList",s);
                b1.putString("nameList", s2);
//                getActivity().finish();
                Singleton.setSelectedFragment(Singleton.getMainFragment());
                InviteToJoinActivity fragment = new InviteToJoinActivity();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                b1.putString("HEADERTEXT", "Invite to Other");
                fragment.setArguments(b1);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
                Singleton.setSelectedFragment(fragment);
                Singleton.setMainFragment(fragment);
                //SendMessage();
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                // When user changed the Text
                String text = edtSearch.getText().toString()
                        .toLowerCase(Locale.getDefault());
                objAdapter.filter(text);
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
        String s = sb.toString().trim();
        String name=sb2.toString().trim();
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(context, "Select atleast one Contact",
                    Toast.LENGTH_SHORT).show();
        } else {
            s = s.substring(0, s.length() - 1);
            name=name.substring(0, name.length() - 1);
            Toast.makeText(context, "Selected Contacts : " +name+" "+s,
                    Toast.LENGTH_SHORT).show();
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
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            CheckBox chk = (CheckBox) view
                                    .findViewById(R.id.contactcheck);
                            ContactModel bean = ContactsListClass.phoneList
                                    .get(position);
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
               btnOK.setVisibility(View.GONE);
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
                btnOK.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void OnBackPress() {
        Singleton.setSelectedFragment(Singleton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }

    /*public void SendMessage()
    {
                View view=View.inflate(getActivity(),R.layout.invite_message_layout, null);
                AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(getActivity());
                alertdialogbuilder.setView(view);
                AlertDialog dialog = alertdialogbuilder.create();
                dialog.show();

    }*/
}
