package com.example.evgeniy.scanner;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class DBHandler {

    public static void saveProfileRemote(Person person, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format("http://api.a16_sd206.studev.groept.be/createPerson/%s/%s/%s/%s/%s/%s",
                person.getFirstName(), person.getLastName(), person.getEmail(), person.getPhone(), person.getAddress(), "");
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

        queue.add(stringRequest);
    }
}
