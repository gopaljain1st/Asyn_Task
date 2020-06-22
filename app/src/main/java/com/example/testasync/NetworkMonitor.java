package com.example.testasync;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class NetworkMonitor extends BroadcastReceiver
{
    public static final String UI_UPDATE_BROADCAST="com.example.testasync.uiupdatebroadcast";
    @Override
    public void onReceive(final Context context, Intent intent)
    {

        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable())
        {

            Log.d("Network Available ", "Flag No 1");
        }
        if(checkNetworkConnection(context))
        {
            Toast.makeText(context, "in network", Toast.LENGTH_SHORT).show();
            final DatabaseHelper helper=new DatabaseHelper(context);
            SQLiteDatabase sqLiteDatabase=helper.getReadableDatabase();
            final Cursor c=sqLiteDatabase.rawQuery("select * from contact where sync_status=0",null);
            //Log.d("cursor",c.getString(1));
            while(c.moveToNext())
            {

                final String name=c.getString(c.getColumnIndex("name"));
                final int id=c.getInt(c.getColumnIndex("id"));
                Log.d("c",name);
                String url="https://inventivepartner.com/Inventive_fruits/addSyncContact.php";
                StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        if(response.equals("item Inserted"))
                        {
                            SQLiteDatabase db=helper.getWritableDatabase();
                            ContentValues values=new ContentValues();
                            values.put("name",name);
                            values.put("sync_status",1);
                            db.update("contact",values,"id="+id,null);
                            db.close();
                            context.sendBroadcast(new Intent(NetworkMonitor.UI_UPDATE_BROADCAST));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String>map=new HashMap<>();
                        map.put("name",name);
                        return map;
                    }
                };
                MySingleton.getInstance(context).addToRequestQue(stringRequest);
            }
            helper.close();
        }

    }
    public boolean checkNetworkConnection(Context context)
    {
        ConnectivityManager connectivityManager =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!= null && networkInfo.isConnected());
    }
}
