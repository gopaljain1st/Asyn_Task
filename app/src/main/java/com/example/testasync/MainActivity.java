package com.example.testasync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    EditText et;
    Button submit;
    LinearLayoutManager manager;
    RecyclerView.Adapter<MyAdapter.MyAdapterViewHolder>adapter;
    ArrayList<contact>al;
    BroadcastReceiver broadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv=findViewById(R.id.rv);
        et=findViewById(R.id.ed);
        submit=findViewById(R.id.submit);
        manager=new LinearLayoutManager(this);
        rv.setLayoutManager(manager);
        al=new ArrayList<>();
        adapter=new MyAdapter(this,al);
        rv.setAdapter(adapter);
        readFromLocalStorage();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromLocalStorage();
            }
        };
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToServer(et.getText().toString());
                et.setText("");
            }
        });
        registerReceiver(new NetworkMonitor(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void saveToLocalStorage(String name,int syncStatus)
    {
        DatabaseHelper helper=new DatabaseHelper(this);
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("name",name);
        values.put("sync_status",syncStatus);
        db.insert("contact",null,values);
        db.close();
        readFromLocalStorage();
        helper.close();
    }

    private void readFromLocalStorage()
    {
        al.clear();
        DatabaseHelper helper=new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase=helper.getReadableDatabase();
        Cursor c=sqLiteDatabase.rawQuery("select * from contact",null);
        while(c.moveToNext())
        {
            String name = c.getString(1);
            int syncStatus = c.getInt(2);
            al.add(new contact(name,syncStatus));
        }
        adapter.notifyDataSetChanged();
        c.close();
        helper.close();
    }
    private void saveToServer(final String name)
    {
         if(checkNetworkConnection())
        {
            String url="https://inventivepartner.com/Inventive_fruits/addSyncContact.php";
            StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                     if(response.equals("item Inserted"))
                     {
                         saveToLocalStorage(name,1);
                     }
                     else saveToLocalStorage(name,0);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                      saveToLocalStorage(name,0);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String>map=new HashMap<>();
                    map.put("name",name);
                    return map;
                }
            };
            MySingleton.getInstance(this).addToRequestQue(stringRequest);
        }
        else
        {
            saveToLocalStorage(name,0);
        }
    }
    public boolean checkNetworkConnection()
    {
        ConnectivityManager connectivityManager =(ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!= null && networkInfo.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver,new IntentFilter(NetworkMonitor.UI_UPDATE_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
