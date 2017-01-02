package com.example.evgeniy.scanner;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

class DBHandler {
    private static int updateRequests;

    @Nullable
    private static Person jsonToPerson(String json) {
        try {
            JSONObject jsonObject = new JSONArray(json).getJSONObject(0);
            int id = jsonObject.getInt("id");
            String firstName = jsonObject.getString("firstName");
            String lastName = jsonObject.getString("lastName");
            String email = jsonObject.getString("email");
            String phone = jsonObject.getString("phone");
            String address = jsonObject.getString("address");
            String timestamp = jsonObject.getString("timestamp");
            String picture = jsonObject.getString("picture");
            return new Person(id, timestamp, firstName, lastName, phone, email, address, picture);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void addContactFromJSON(String json, final Context context) {
        JSONObject jsonObject;
        final int id;
        if (!MainActivity.isConnected) {
            Toast.makeText(context, "Must be connected to scan", Toast.LENGTH_LONG).show();
            return;
        }

        Person me = PersonContract.getProfile(context);
        if (me == null) {
            Toast.makeText(context, "Profile must be created before adding contacts", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            jsonObject = new JSONObject(json);
            id = jsonObject.getInt("id");
            /* TODO: for local storage if disconnected
            jsonObject.getString("firstName");
            jsonObject.getString("lastName");
            jsonObject.getString("email");
            jsonObject.getString("phone");*/
        } catch (JSONException ex) {
            Toast.makeText(context, "Invalid QR code", Toast.LENGTH_LONG).show();
            return;
        }

        // Check person exists
        RequestQueue queue = VolleyHandler.getInstance(context).getRequestQueue();
        String url = String.format("http://api.a16_sd206.studev.groept.be/getPersonById/%s", id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                        try {
                            JSONArray jArray = new JSONArray(response);
                            if (jArray.length() == 0)
                                Toast.makeText(context, "Invalid QR code", Toast.LENGTH_LONG).show();
                            else
                                DBHandler.addContact(context, PersonContract.getMyId(context), id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyApp", error.getMessage());
                    }
                });

        queue.add(stringRequest);
        // Get person info to add to contact
    }

    private static void addContact(final Context context, int myId, final int theirId) {
        // Add relationship to remote
        RequestQueue queue = VolleyHandler.getInstance(context).getRequestQueue();
        String url_me_them = String.format("http://api.a16_sd206.studev.groept.be/addContact/%s/%s",
                myId, theirId);
        String url_them_me = String.format("http://api.a16_sd206.studev.groept.be/addContact/%s/%s",
                theirId, myId);

        StringRequest firstRequest = new StringRequest(Request.Method.GET, url_me_them,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                        getNewContact(context, theirId, true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyApp", error.getMessage());
                    }
                });

        StringRequest secondRequest = new StringRequest(Request.Method.GET, url_them_me,
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

        queue.add(firstRequest);
        queue.add(secondRequest);
    }

    static void removeContact(final Context context, final int theirId) {
        RequestQueue queue = VolleyHandler.getInstance(context).getRequestQueue();
        String url = String.format("http://api.a16_sd206.studev.groept.be/removeContact/%s/%s",
                PersonContract.getMyId(context), theirId);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                        PersonContract.removeContact(context, theirId);
                        Intent mainActivityIntent = new Intent(context, MainActivity.class);
                        context.startActivity(mainActivityIntent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyApp", error.getMessage());
                    }
                });

        queue.add(stringRequest);
    }

    /**
     * Gets contact from remote and add to local DB.
     *
     * @param context                Main activity context
     * @param id                     ID of contact to be added
     * @param returnToContactDetails Is this contact being added as a result of QRCode scan?
     *                               If so, contact details will be brought up afterwards, otherwise not.
     */
    private static void getNewContact(final Context context, final int id, final boolean returnToContactDetails) {
        RequestQueue queue = VolleyHandler.getInstance(context).getRequestQueue();
        String url = String.format("http://api.a16_sd206.studev.groept.be/getPersonById/%s", id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                        try {
                            JSONArray jArray = new JSONArray(response);
                            if (jArray.length() == 0)
                                Toast.makeText(context, "Invalid QR code", Toast.LENGTH_LONG).show();
                            else {
                                Person person = jsonToPerson(response);
                                int res = PersonContract.addContact(context, person);
                                if (res > 0) {
                                    new PhotoManager(context).downloadAndSaveLocally(person);
                                } else {
                                    Toast.makeText(context, "Contact already added!", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyApp", error.getMessage());
                    }
                });

        queue.add(stringRequest);
    }

    /**
     * Gets contact from remote and update local DB.
     *
     * @param context Application context
     * @param id      ID of contact to be addedrwise not.
     */
    private static void updateContact(final Context context, final int id) {
        RequestQueue queue = VolleyHandler.getInstance(context).getRequestQueue();
        String url = String.format("http://api.a16_sd206.studev.groept.be/getPersonById/%s", id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                        try {
                            JSONArray jArray = new JSONArray(response);
                            if (jArray.length() == 0)
                                Toast.makeText(context, "Invalid QR code", Toast.LENGTH_LONG).show();
                            else {
                                Person person = jsonToPerson(response);
                                int res = PersonContract.updateContact(context, person);
                                if (res > 0) {
                                    new PhotoManager(context).downloadAndSaveLocally(person);
                                } else {
                                    Toast.makeText(context, "Contact already added!", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyApp", error.getMessage());
                    }
                });

        queue.add(stringRequest);
    }

    static void contactPhotoDownloaded(Context context, Person person) {
        if (!(context instanceof MainActivity))
            return;

        ContactsFragment frg = (ContactsFragment) ((MainActivity) context).getViewAdapter().getItem(1);
        frg.updatePersonList(context);
        SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) ((MainActivity) context).findViewById(R.id.swipe_container);

        if (swipeLayout.isRefreshing()) {
            updateRequests--;
            if (updateRequests == 0)
                ((SwipeRefreshLayout)
                        ((MainActivity) context).findViewById(
                                R.id.swipe_container)).setRefreshing(false);
            return;
        }

        Intent detailIntent = new Intent(context, ScrollingProfileActivity.class);
        detailIntent.putExtra("person", person);
        context.startActivity(detailIntent);
    }

    static void startUpdateContacts(final Context context) {
        int myId = PersonContract.getMyId(context);
        if (myId == -1)
            return;
        // Once contacts & timestamps have been fetched, updateContacts will be called
        getContactTimestamps(context, myId);
    }

    /**
     * Gets local contacts and compares with up to date contactTimestamps.
     * Any contacts with differing timestamps will be re-downloaded.
     *
     * @param context           Main activity context
     * @param contactTimestamps HashMap of timestamps with contact id as key
     */
    private static void updateContacts(final Context context, SparseArray<String> contactTimestamps) {
        ArrayList<Person> contactList = (ArrayList<Person>) PersonContract.getContacts(context);
        ArrayList<Integer> contactIds = new ArrayList<>();

        updateRequests = 0;

        for (Person contact : contactList)
            contactIds.add(contact.getId());

        for (Person contact : contactList) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.UK);
            try {
                Date newTime = formatter.parse(contactTimestamps.get(contact.getId()));
                Date oldTime = formatter.parse(contact.getTimestamp());

                if (newTime.compareTo(oldTime) > 0) {
                    updateRequests++;
                    updateContact(context, contact.getId());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < contactTimestamps.size(); i++) {
            int contactId = contactTimestamps.keyAt(i);

            // If contactId from remote is not in local, get contact (will also add to local)
            if (!contactIds.contains(contactId)) {
                updateRequests++;
                getNewContact(context, contactId, false);
            }
        }

        if (updateRequests == 0)
            ((SwipeRefreshLayout)
                    ((MainActivity) context).findViewById(
                            R.id.swipe_container)).setRefreshing(false);

    }

    private static void getContactTimestamps(final Context context, int id) {
        RequestQueue queue = VolleyHandler.getInstance(context).getRequestQueue();
        String url = String.format("http://api.a16_sd206.studev.groept.be/getContactTimestamps/%s", id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            SparseArray<String> contactTimestamps = new SparseArray<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                contactTimestamps.put(obj.getInt("contact"), obj.getString("timestamp"));
                            }
                            updateContacts(context, contactTimestamps);
                        } catch (JSONException e) {
                            Log.d("MyApp", "Unable to parse contact timestamps JSON");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyApp", error.getMessage());

                    }
                });
        queue.add(stringRequest);
    }

    static void saveProfile(Person person, Context context) {
        Person localPerson = PersonContract.getProfile(context);
        if (localPerson == null) {
            createNewPerson(person, context);
        } else {
            updateExistingPerson(localPerson, person, context);
        }

    }

    private static void createNewPerson(final Person person, final Context context) {
        // if no local ID
        RequestQueue queue = VolleyHandler.getInstance(context).getRequestQueue();
        String url = String.format("http://api.a16_sd206.studev.groept.be/createPerson/%s/%s/%s/%s/%s/%s",
                person.getFirstName(), person.getLastName(), person.getEmail(), person.getPhone(), person.getAddress(), person.getPicture());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                        DBHandler.assignNewlyCreatedId(person, context);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyApp", error.getMessage());
                        //MyProfileFragment.saveFailedUI(context);
                    }
                });

        queue.add(stringRequest);
    }

    private static void assignNewlyCreatedId(final Person person, final Context context) {
        RequestQueue queue = VolleyHandler.getInstance(context).getRequestQueue();
        String url = String.format("http://api.a16_sd206.studev.groept.be/getPersonByAttributes/%s/%s/%s/%s",
                person.getFirstName(), person.getLastName(), person.getEmail(), person.getPhone());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            int createdId;
                            JSONArray jArray = new JSONArray(response);
                            createdId = Integer.parseInt(jArray.getJSONObject(0).getString("id"));
                            person.setId(createdId);
                            PersonContract.saveProfile(context, person);
                            Toast.makeText(context, "Profile created successfully", Toast.LENGTH_SHORT).show();
                            if (context instanceof ProfileEditActivity)
                                ((ProfileEditActivity) context).saveSuccess();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Can't updatePersonList profile", Toast.LENGTH_SHORT).show();
                            //MyProfileFragment.saveFailedUI(context);
                        }
                        Log.d("MyApp", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyApp", error.getMessage());
                    }
                });

        queue.add(stringRequest);
    }

    private static void updateExistingPerson(final Person oldPerson, final Person newPerson, final Context context) {
        // if no local ID
        RequestQueue queue = VolleyHandler.getInstance(context).getRequestQueue();
        String url = String.format(Locale.UK, "http://api.a16_sd206.studev.groept.be/updatePerson/%s/%s/%s/%s/%s/%s/%d",
                newPerson.getFirstName(), newPerson.getLastName(), newPerson.getEmail(), newPerson.getPhone(), newPerson.getAddress(), newPerson.getPicture(), oldPerson.getId());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                        newPerson.setId(oldPerson.getId());
                        PersonContract.saveProfile(context, newPerson);
                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        if (context instanceof ProfileEditActivity)
                            ((ProfileEditActivity) context).saveSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyApp", error.getMessage());
                        //MyProfileFragment.saveFailedUI(context);
                        Toast.makeText(context, "Can't updatePersonList profile", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(stringRequest);
    }
}
