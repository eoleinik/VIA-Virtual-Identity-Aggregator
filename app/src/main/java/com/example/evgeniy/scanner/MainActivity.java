package com.example.evgeniy.scanner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE_FROM_GALLERY = 1;
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
    private InputStream myImageStream;
    private TabLayout tabLayout;

    private InputStream getMyImageStream() {
        return myImageStream;
    }

    private void setMyImageStream(InputStream imageStream) {
        this.myImageStream = imageStream;
    }

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
    public void onSaveClick(View view) {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        String firstName = ((TextView) findViewById(R.id.editTextFirstName)).getText().toString();
        String lastName = ((TextView) findViewById(R.id.editTextLastName)).getText().toString();
        String email = ((TextView) findViewById(R.id.editTextEmail)).getText().toString();
        String phone = ((TextView) findViewById(R.id.editTextPhone)).getText().toString();

        PhotoManager pm = new PhotoManager();
        String imageUrl = pm.upload(this.getMyImageStream());
        Person person = new Person(ts, firstName, lastName, phone, email, "", imageUrl);
        // Save profile to local sqlite db
        // Probably do it through DBHandler, because we might have a chance to save ID as well.
//        PersonContract.saveProfile(getApplicationContext(), person);

        DBHandler.saveProfile(person, this);

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

    public void openGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select file to upload "), SELECT_IMAGE_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_IMAGE_FROM_GALLERY) {     // if we were choosing photo
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                try {
                    InputStream myImage = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bm2 = BitmapFactory.decodeStream(myImage);
                    setMyImageStream(myImage);
                    ImageView imagePreview = (ImageView)findViewById(R.id.imagePreview);
                    imagePreview.setImageBitmap(bm2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {                                            // if we were scanning code
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

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null)
            return "Failure";
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        System.out.println("Column index: "+column_index);
        cursor.moveToFirst();
        String res = cursor.getString(column_index);
        cursor.close();
        return res;
    }
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
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
        adapter.addFragment(new MyProfileFragment(), "MY PROFILE");
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
