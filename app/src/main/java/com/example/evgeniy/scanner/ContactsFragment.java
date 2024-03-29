package com.example.evgeniy.scanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ContactsFragment extends Fragment{

    private ContactAdapter contactAdapter;
    private SwipeRefreshLayout swipeView;

    public ContactsFragment() {
        // Required empty public constructor
    }

    public void updatePersonList(final Context context) {
        if (contactAdapter != null)
            contactAdapter.updateDataSource(PersonContract.getContacts(context));
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

        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (MainActivity.isConnected)
                    DBHandler.startUpdateContacts(getContext());
                else
                    swipeView.setRefreshing(false);
            }
        });

        // On create refresh:
        if (MainActivity.isConnected) {
            if (!swipeView.isRefreshing() && PersonContract.getMyId(getContext()) != -1) {
                swipeView.setRefreshing(true);
                DBHandler.startUpdateContacts(getContext());
            }
        }

        contactAdapter = new ContactAdapter(context, new ArrayList<Person>());
        contactsView.setAdapter(contactAdapter);

        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Person selectedPerson = (Person) contactAdapter.getItem(position);
                Intent detailIntent = new Intent(view.getContext(), ScrollingProfileActivity.class);
                detailIntent.putExtra("person", selectedPerson);
                startActivity(detailIntent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        updatePersonList(this.getActivity());
        super.onResume();
    }
}