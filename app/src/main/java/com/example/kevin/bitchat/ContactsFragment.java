package com.example.kevin.bitchat;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ContactsFragment extends android.app.Fragment implements
        AdapterView.OnItemClickListener, ContactDataSource.Listener{

    private static final String TAG = "Contacts Fragment";
    private Listener mListener;
    private ArrayList<Contact> mContacts = new ArrayList<>();
    private ContactAdapter mAdapter;


    public ContactsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, null);
        ListView listView = (ListView)v.findViewById(R.id.list);
        listView.setOnItemClickListener(this);

        ContactDataSource dataSource = new ContactDataSource(getActivity(), this);
        mAdapter = new ContactAdapter(mContacts);
        listView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, dataSource);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onContactSelected(mContacts.get(position));
    }

    @Override
    public void onFetchedContacts(ArrayList<Contact> contacts) {
        mContacts.clear();
        mContacts.addAll(contacts);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Listener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface Listener {
        public void onContactSelected(Contact contact);
    }

    private class ContactAdapter extends ArrayAdapter<Contact>{

        ContactAdapter(ArrayList<Contact> contacts){
            super(getActivity(), R.layout.contact_list_item, R.id.name, contacts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView =  super.getView(position, convertView, parent);
            Contact contact = getItem(position);
            TextView nameView = (TextView)convertView.findViewById(R.id.name);
            nameView.setText(contact.getPhoneNumber());
            return convertView;
        }

    }
}
