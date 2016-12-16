package com.example.evgeniy.scanner;

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

    private static final ArrayList<Person> personList = new ArrayList<>();

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static void updatePersonList(Context context) {
        personList.clear();
        personList.addAll(PersonContract.getContacts(context));
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

        ListView contactsView = (ListView) view.findViewById(R.id.contactsList);

        updatePersonList(getActivity());

        contactsView.setAdapter(new ContactAdapter(context, personList));

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