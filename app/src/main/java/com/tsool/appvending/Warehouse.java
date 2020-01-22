package com.tsool.appvending;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Warehouse extends AppCompatActivity {

    MobileServiceClient mClient;
    private MobileServiceSyncTable<Bodega> mBodegaTable;

    MobileServiceTable<Bodega> mTableBodega;
    MobileServiceList<Bodega> listBodega;
    ProductsAdapter mAdapter;

    Adapter adapter;

    android.widget.SearchView etxFindWarehouse;

    private static String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);
        if (getIntent().getBooleanExtra("NameSent",false)) {
            title = getIntent().getStringExtra("Name");
        }
        setTitle(title);


        etxFindWarehouse = (android.widget.SearchView)findViewById(R.id.svFindWarehouse);
        final ListView lvProducts = (ListView) findViewById(R.id.lvProducts);

        try
        {
            mClient = new MobileServiceClient(
                    "https://vendingmachines.azurewebsites.net/",
                    this
            );
        }catch (Exception ex){}

        try {
            SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "AppVending", null, 1);
            SimpleSyncHandler handler = new SimpleSyncHandler();
            MobileServiceSyncContext syncContext = mClient.getSyncContext();

            Map<String, ColumnDataType> bodegaTableDefinition = new HashMap<String, ColumnDataType>();
            bodegaTableDefinition.put("id", ColumnDataType.String);
            bodegaTableDefinition.put("Name", ColumnDataType.String);
            bodegaTableDefinition.put("UnitName", ColumnDataType.String);
            bodegaTableDefinition.put("Availability", ColumnDataType.Real);
            bodegaTableDefinition.put("AvailabilityAlert", ColumnDataType.Real);
            bodegaTableDefinition.put("Price", ColumnDataType.Real);
            bodegaTableDefinition.put("Type", ColumnDataType.Integer);
            bodegaTableDefinition.put("PurchasePrice", ColumnDataType.Real);

            localStore.defineTable("Bodega", bodegaTableDefinition);

            syncContext.initialize(localStore, handler).get();
        }catch (Exception e){
            Throwable t = e;
            while (t.getCause() != null) {
                t = t.getCause();
            }
            createAndShowDialog(new Exception("Unknown error: " + t.getMessage()), "Error");
        }

        // Get the Mobile Service Table instance to use
        mBodegaTable = mClient.getSyncTable(Bodega.class);

        syncAsync();

        etxFindWarehouse.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!(adapter==null))
                    adapter.getFilter().filter(newText);
                return false;
            }
        });

        MobileService objMobileService = new MobileService(this);
        final ArrayList<Bodega> b = new ArrayList<Bodega>();

        try{
            mTableBodega = objMobileService.connection(this).getTable(Bodega.class);
            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        //listBodega = mTableBodega.execute().get();
                        listBodega = mBodegaTable.read(QueryOperations.tableName("Bodega")).get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                for(Bodega warehouse : listBodega){
                                    b.add(new Bodega(warehouse.Name));
                                }

                                adapter = new Adapter(Warehouse.this,b);
                                lvProducts.setAdapter(adapter);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }catch (Exception ex){}




        ImageButton ibAddWarehouseProduct = (ImageButton) findViewById(R.id.ibAddWarehouseProduct);

        ibAddWarehouseProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Warehouse.this, AddWareHouseProduct.class));
            }
        });

        lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bodega b = (Bodega) lvProducts.getItemAtPosition(position);
                for (Bodega bodega:listBodega){
                    if (b.Name.toString().equals(bodega.Name)){
                        Toast.makeText(Warehouse.this, bodega.getName(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Warehouse.this,EditWareHouseProduct.class);
                        i.putExtra("id",bodega.id);
                        i.putExtra("Name",bodega.Name);
                        i.putExtra("Availability",String.valueOf(bodega.Availability));
                        i.putExtra("AvailabilityAlert",String.valueOf(bodega.AvailabilityAlert));
                        i.putExtra("UnitName",bodega.UnitName);
                        i.putExtra("Price",String.valueOf(bodega.Price));
                        i.putExtra("Type",bodega.Type);
                        i.putExtra("PurchasePrice",String.valueOf(bodega.PurchasePrice));
                        startActivity(i);
                    }
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Warehouse.this, MainActivity.class));
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(exception, title);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void syncAsync(){
        if (isNetworkAvailable()) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        mClient.getSyncContext().push().get();
                        mBodegaTable.pull(null).get();

                    } catch (Exception exception) {
                        createAndShowDialog(exception, "Error");
                    }
                    return null;
                }
            }.execute();
        }
    }
}
