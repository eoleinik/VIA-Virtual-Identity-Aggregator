package com.example.evgeniy.scanner;

import android.content.Context;
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
            // TODO: picture
            String picture = jsonObject.getString("picture");
            return new Person(id, timestamp, firstName, lastName, phone, email, address, picture);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void addContactFromJSON(String json, Context context) {
        JSONObject jsonObject;
        final int id;
        final Context temp_context = context;
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
                                Toast.makeText(temp_context, "Invalid QR code", Toast.LENGTH_LONG).show();
                            else
                                DBHandler.addRelationship(temp_context, myId, id);
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

    private static void addRelationship(Context context, int myId, final int theirId) {
        // Add relationship to remote
        final Context temp_context = context;
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format("http://api.a16_sd206.studev.groept.be/createRelationship/%s/%s",
                myId, theirId);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                        DBHandler.getNewContact(temp_context, theirId);
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
                                int res = PersonContract.addContact(context, jsonToPerson(response));
                                if (res > 0)
                                    ContactsFragment.update(context);
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

    static void saveProfile(Person person, Context context) {
        Person localPerson = PersonContract.getProfile(context);
        if (localPerson == null) {
            createNewPerson(person, context);
        } else {
            updateExistingPerson(localPerson, person, context);
        }

    }

    private static void createNewPerson(Person person, Context context) {
        final Person temp_person = person;
        final Context temp_context = context;
        // if no local ID
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format("http://api.a16_sd206.studev.groept.be/createPerson/%s/%s/%s/%s/%s/%s",
                person.getFirstName(), person.getLastName(), person.getEmail(), person.getPhone(), person.getAddress(), person.getPicture());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                        DBHandler.assignNewlyCreatedId(temp_person, temp_context);
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

    private static void assignNewlyCreatedId(Person person, Context context) {
        final Person temp_person = person;
        final Context temp_context = context;
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
                            temp_person.setId(createdId);
                            PersonContract.saveProfile(temp_context, temp_person);
                            Toast.makeText(temp_context, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(temp_context, "Can't update profile", Toast.LENGTH_SHORT).show();
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

    private static void updateExistingPerson(Person oldPerson, Person newPerson, Context context) {
        final Person temp_old_person = oldPerson;
        final Person temp_new_person = newPerson;
        final Context temp_context = context;
        // if no local ID
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format("http://api.a16_sd206.studev.groept.be/updatePerson/%s/%s/%s/%s/%s/%s/%d",
                newPerson.getFirstName(), newPerson.getLastName(), newPerson.getEmail(), newPerson.getPhone(), newPerson.getAddress(), newPerson.getPicture(), oldPerson.getId());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyApp", response);
                        temp_new_person.setId(temp_old_person.getId());
                        PersonContract.saveProfile(temp_context, temp_new_person);
                        Toast.makeText(temp_context, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyApp", error.getMessage());
                        Toast.makeText(temp_context, "Can't update profile", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(stringRequest);
    }
}
