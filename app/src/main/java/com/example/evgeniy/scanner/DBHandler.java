package com.example.evgeniy.scanner;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

class DBHandler {
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
        final int myId = me.getId();

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
        RequestQueue queue = Volley.newRequestQueue(context);
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
                                DBHandler.addContact(context, myId, id);
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
        RequestQueue queue = Volley.newRequestQueue(context);
        String url_me_them = String.format("http://api.a16_sd206.studev.groept.be/addContact/%s/%s",
                myId, theirId);
        String url_them_me = String.format("http://api.a16_sd206.studev.groept.be/addContact/%s/%s",
                theirId, myId);

        StringRequest firstRequest = new StringRequest(Request.Method.GET, url_me_them,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                        getNewContact(context, theirId);
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

    private static void getNewContact(final Context context, final int id) {
        RequestQueue queue = Volley.newRequestQueue(context);
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

    static void contactPhotoDownloaded(Context context, Person person) {
        ContactsFragment.updatePersonList(context);
        Intent detailIntent = new Intent(context, ScrollingProfileActivity.class);
        detailIntent.putExtra("person", person);
        context.startActivity(detailIntent);
    }

    static void updateContacts(final Context context) {
        ArrayList<Person> contactList = (ArrayList<Person>) PersonContract.getContacts(context);

        // getContacts from remote
        // are there ids in remote that are not present in local?
        //      add contact
        // compare timestamps
        //      if changed update contact and re download image
    }

    static ArrayList<Person> getContactsFromRemote(final Context context) {
        return null;
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
        RequestQueue queue = Volley.newRequestQueue(context);
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
        RequestQueue queue = Volley.newRequestQueue(context);
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
        RequestQueue queue = Volley.newRequestQueue(context);
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
