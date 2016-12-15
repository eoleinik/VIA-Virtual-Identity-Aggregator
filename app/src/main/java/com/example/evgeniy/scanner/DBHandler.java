package com.example.evgeniy.scanner;

import android.content.Context;
import android.util.JsonReader;
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

public class DBHandler {

    public static void saveProfile(Person person, Context context) {
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

    protected static void assignNewlyCreatedId(Person person, Context context) {
        final Person temp_person = person;
        final Context temp_context = context;
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format("http://api.a16_sd206.studev.groept.be/getPersonByAttributes/%s/%s/%s/%s/%s/%s",
                person.getFirstName(), person.getLastName(), person.getEmail(), person.getPhone(), person.getAddress(), "");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            int createdId = -1;
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
