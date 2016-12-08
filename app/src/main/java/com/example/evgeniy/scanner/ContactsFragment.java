package com.example.evgeniy.scanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ContactsFragment extends Fragment{

    private ListView contactsView;
    private final ArrayList<Person> personList = new ArrayList<>();

    public ContactsFragment() {
        // Required empty public constructor
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

        Person p1 = new Person("", "Jack", "Sparrow", "000", "js@mail.com", "addr");
        Person p2 = new Person("", "Queen", "Elizabeth", "111", "qe@mail.com", "Buckingham palace");

        personList.add(p1);
        personList.add(p2);

        ContactAdapter adapter = new ContactAdapter(context, personList);
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