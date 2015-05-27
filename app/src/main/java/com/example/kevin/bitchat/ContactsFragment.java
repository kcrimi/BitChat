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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class ContactsFragment extends android.app.Fragment implements
        AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "Contacts Fragment";
    private Listener mListener;
    private SimpleCursorAdapter mCursorAdapter;


    public ContactsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, null);
        ListView listView = (ListView)v.findViewById(R.id.list);
        listView.setOnItemClickListener(this);

        String[] columns = {ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        int[] ids = {R.id.number,R.id.name};

        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.contact_list_item,
                null,
                columns,
                ids,
                0
        );
        listView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(0, null, this);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = ((SimpleCursorAdapter) parent.getAdapter()).getCursor();
        cursor.moveToPosition(position);

        Log.d(TAG, "Phone is "+cursor.getString(1));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone._ID,ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
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
        public void onFragmentInteraction(Uri uri);
    }
}
