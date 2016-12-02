package com.example.evgeniy.scanner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MyProfileFragment extends Fragment{

    public MyProfileFragment() {
        // Required empty public constructor
    }

    private void loadProfile(View v) {
        Person person = PersonContract.getProfile(getActivity().getApplicationContext());
        if (person == null)
            return;
        ((EditText) v.findViewById(R.id.editTextFirstName)).setText(person.getFirstName());
        ((EditText) v.findViewById(R.id.editTextLastName)).setText(person.getLastName());
        ((EditText) v.findViewById(R.id.editTextEmail)).setText(person.getEmail());
        ((EditText) v.findViewById(R.id.editTextPhone)).setText(person.getPhone());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        loadProfile(v);
        return v;
    }
}