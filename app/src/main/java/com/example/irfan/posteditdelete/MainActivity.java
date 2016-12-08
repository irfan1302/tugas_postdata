package com.example.irfan.posteditdelete;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
public class MainActivity extends ListActivity implements View.OnClickListener {
    // URL to get contacts JSON
    private static String url = "http://dthan.net/json_barang.php";
    // JSON Node names
    private static final String TAG_BARANGINFO = "data";
    private static final String TAG_KODE = "kode_barang";
    private static final String TAG_NAMA = "nama_barang";
    private static final String TAG_HARGA = "harga";
    // private ListAdapter daftar;
    private String item;
    private ListView listView;
    Button btnTambah;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTambah = (Button)findViewById(R.id.btnTambahBarang);
        btnTambah.setOnClickListener(this);
// Calling async task to get json
        new GetBarang().execute();

    }
    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, AddBarang.class);
        startActivity(i);
    }
    /**
     * Async task class to get json by making HTTP call
     */
    private class GetBarang extends AsyncTask<Void, Void, Void> {
        // Hashmap for ListView
        ArrayList<HashMap<String, String>> studentList;
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
// Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
// Creating service handler class instance
            Koneksi webreq = new Koneksi();
// Making a request to url and getting response
            String jsonStr = webreq.makeWebServiceCall(url, Koneksi.GET);
            Log.d("Response: ", "> " + jsonStr);
            studentList = ParseJSON(jsonStr);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
// Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
/**
 * Updating parsed JSON data into ListView
 * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, studentList,
                    R.layout.list_item, new String[]{TAG_NAMA, TAG_KODE,
                    TAG_HARGA}, new int[]{R.id.nama,
                    R.id.kodeBarang, R.id.harga});
            setListAdapter(adapter);
            listView = getListView();
            listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
// item = (String) parent.getItemAtPosition(position);
                    Intent detail = new Intent(getApplicationContext(), EditBarang.class);
                    startActivity(detail);

                }
            });
            ;
        }
    }
    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
// Hashmap for ListView
                ArrayList<HashMap<String, String>> barangList = new
                        ArrayList<HashMap<String, String>>();
                JSONObject jsonObj = new JSONObject(json);
// Getting JSON Array node
                JSONArray dataBarang = jsonObj.getJSONArray(TAG_BARANGINFO);
// looping through All Students
                for (int i = 0; i < dataBarang.length(); i++) {
                    JSONObject c = dataBarang.getJSONObject(i);
                    String nama = "Nama Barang : " + c.getString(TAG_NAMA);
                    String kode = "Kode : " + c.getString(TAG_KODE);
                    String harga = "Harga : Rp. " + c.getString(TAG_HARGA);
// tmp hashmap for single student
                    HashMap<String, String> barang = new HashMap<String, String>();
// adding each child node to HashMap key => value
                    barang.put(TAG_NAMA, nama);
                    barang.put(TAG_KODE, kode);
                    barang.put(TAG_HARGA, harga);
                    barangList.add(barang);
                }
                return barangList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
            return null;
        }
    }
}