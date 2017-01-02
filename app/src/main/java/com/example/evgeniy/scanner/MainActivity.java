package com.example.evgeniy.scanner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    private static final int UPDATE_PROFILE = 2;
    public static boolean isConnected = false;
    private final int[] tabIcons = {
            R.drawable.qr_code,
            R.drawable.contacts,
            R.drawable.person
    };
    private final BroadcastReceiver NetworkStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new CheckInternetTask().execute();
        }
    };
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private void updateConnected() {
        final TextView connectionStatusText = (TextView) findViewById(R.id.connectionStatus);

        if (isConnected) {
            if (!connectionStatusText.getText().toString().equals(getString(R.string.disconnected)))
                return;
            connectionStatusText.setText(getString(R.string.connected));
            connectionStatusText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorConnected));
            connectionStatusText.animate().alpha(0.0f).setStartDelay(1000).setDuration(500).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            connectionStatusText.setVisibility(View.GONE);
                        }
                    });
        } else {
            connectionStatusText.setAlpha(1.0f);
            connectionStatusText.setText(getString(R.string.disconnected));
            connectionStatusText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorConnected));
            connectionStatusText.setVisibility(View.VISIBLE);
        }
    }

    public ViewPagerAdapter getViewAdapter() {
        return (ViewPagerAdapter) viewPager.getAdapter();
    }

    public void scanBarcodeCustomLayout(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan the other person's code");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    //region  Profile fragment


    public void onEditClick(View view) {
        Intent detailIntent = new Intent(view.getContext(), ProfileEditActivity.class);
        startActivityForResult(detailIntent, UPDATE_PROFILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_PROFILE) {
            if (resultCode == RESULT_OK) {
                System.out.println("ABOUT TO RESTART FRAGMENT");

                ViewPagerAdapter adapter = (ViewPagerAdapter)viewPager.getAdapter();
                MyProfileFragment frg = (MyProfileFragment)adapter.getItem(2);
                frg.loadProfile();
            }
        } else {
            // if we were scanning code
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    DBHandler.addContactFromJSON(result.getContents(), this);
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }


    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_key), getString(R.string.twitter_secret));
        Fabric.with(this, new Twitter(authConfig));
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        new CheckInternetTask().execute();
        registerReceiver(NetworkStatusReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
//        Topbar:
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        viewPager.setCurrentItem(1);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(NetworkStatusReceiver);
        super.onDestroy();
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
        Fragment profileFragment = new MyProfileFragment();
        adapter.addFragment(profileFragment, "MY PROFILE");
        viewPager.setAdapter(adapter);
    }

    private class CheckInternetTask extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... params) {
            try {
                InetAddress ip = InetAddress.getByName(getString(R.string.internet_test));
                return !ip.toString().equals("");

            } catch (Exception e) {
                Log.d("MainActivity", e.getMessage());
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            isConnected = result;
            updateConnected();
        }
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
