package com.example.evgeniy.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.qr_code,
            R.drawable.contacts,
            R.drawable.person

    };

    public void scanBarcodeCustomLayout(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan something");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    //region  Profile fragment
    public void onSaveClick(View view) {
        String firstName = ((TextView) findViewById(R.id.editTextFirstName)).getText().toString();
        String lastName = ((TextView) findViewById(R.id.editTextLastName)).getText().toString();
        String email = ((TextView) findViewById(R.id.editTextEmail)).getText().toString();
        String phone = ((TextView) findViewById(R.id.editTextPhone)).getText().toString();

        Person person = new Person(firstName, lastName, phone, email, "");
        // Save profile to local sqlite db
        PersonContract.SaveProfile(getApplicationContext(), person);

        saveProfileRemote(person);

        ((TextView) findViewById(R.id.textViewFirstName)).setText(firstName);
        ((TextView) findViewById(R.id.textViewLastName)).setText(lastName);
        ((TextView) findViewById(R.id.textViewEmail)).setText(email);
        ((TextView) findViewById(R.id.textViewPhone)).setText(phone);

        findViewById(R.id.linerLayoutView).setVisibility(View.VISIBLE);
        findViewById(R.id.linerLayoutEdit).setVisibility(View.INVISIBLE);

        findViewById(R.id.buttonEdit).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonSave).setVisibility(View.INVISIBLE);
    }

    public void onEditClick(View view) {
        String firstName = ((TextView) findViewById(R.id.textViewFirstName)).getText().toString();
        String lastName = ((TextView) findViewById(R.id.textViewLastName)).getText().toString();
        String email = ((TextView) findViewById(R.id.textViewEmail)).getText().toString();
        String phone = ((TextView) findViewById(R.id.textViewPhone)).getText().toString();

        ((TextView) findViewById(R.id.editTextFirstName)).setText(firstName);
        ((TextView) findViewById(R.id.editTextLastName)).setText(lastName);
        ((TextView) findViewById(R.id.editTextEmail)).setText(email);
        ((TextView) findViewById(R.id.editTextPhone)).setText(phone);

        findViewById(R.id.linerLayoutView).setVisibility(View.INVISIBLE);
        findViewById(R.id.linerLayoutEdit).setVisibility(View.VISIBLE);

        findViewById(R.id.buttonEdit).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonSave).setVisibility(View.VISIBLE);
    }

    private void saveProfileRemote(Person person) {
        // TODO: Remote storage
        /*String name = ((TextView) findViewById(R.id.editTextName)).getText().toString();
        String email = ((TextView) findViewById(R.id.editTextEmail)).getText().toString();
        String phone = ((TextView) findViewById(R.id.editTextPhone)).getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = String.format("http://api.a16_sd206.studev.groept.be/createPerson/%s/%s/%s/%s/%s/%s",
                name, "", email, phone, "", "");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyApp", error.getMessage());
                    }
                });
        queue.add(stringRequest);*/
    }
    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Topbar:
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            return;
        actionBar.setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        TabLayout.Tab tab0 = tabLayout.getTabAt(0);
        TabLayout.Tab tab1 = tabLayout.getTabAt(1);
        TabLayout.Tab tab2 = tabLayout.getTabAt(2);

        if (tab0 == null)
            return;
        tab0.setIcon(tabIcons[0]);

        if (tab1 == null)
            return;
        tab1.setIcon(tabIcons[1]);

        if (tab2 == null)
            return;
        tab2.setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyCodeFragment(), "MY CODE");
        adapter.addFragment(new ContactsFragment(), "CONTACTS");
        adapter.addFragment(new MyProfileFragment(), "MY PROFILE");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
