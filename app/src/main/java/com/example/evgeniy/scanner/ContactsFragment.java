package com.example.evgeniy.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ContactsFragment extends Fragment{

    private static ArrayList<Person> personList = new ArrayList<>();
    private static ContactAdapter adapter;
    private ListView contactsView;

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static void update(Context context) {
        personList = (ArrayList<Person>) PersonContract.getContacts(context);
        if (adapter != null)
            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        Context context = view.getContext();

        contactsView = (ListView) view.findViewById(R.id.contactsList);

        update(getActivity());

        adapter = new ContactAdapter(context, personList);
        contactsView.setAdapter(adapter);

        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Person selectedPerson = personList.get(position);
                Intent detailIntent = new Intent(view.getContext(), ScrollingProfileActivity.class);
                detailIntent.putExtra("person", selectedPerson);
                startActivity(detailIntent);
            }
        });

        return view;
    }
}