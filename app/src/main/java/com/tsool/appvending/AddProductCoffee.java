package com.tsool.appvending;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddProductCoffee extends AppCompatActivity {

    MobileServiceClient mClient;
    private MobileServiceSyncTable<Bodega> mBodegaTable;

    MobileServiceTable<Bodega> mTableBodega;
    MobileServiceList<Bodega> listBodega;

    Spinner spCoffeeProducts;
    EditText etxQuantity;
    TextView txvUnit;
    
    int productSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_coffee);
        setTitle("Agregar producto");

        MobileService objMobileService = new MobileService(this);

        try {
            mTableBodega = objMobileService.connection(this).getTable(Bodega.class);
        }catch(Exception ex){}

        spCoffeeProducts = (Spinner) findViewById(R.id.spCoffeeProduct);
        etxQuantity = (EditText)findViewById(R.id.etxQuantityCoffeeProduct);
        txvUnit = (TextView)findViewById(R.id.txvUnit);

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

        
        spCoffeeProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productSelected = position;
                txvUnit.setText(listBodega.get(position).UnitName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getProductList();

    }

    private AsyncTask<Void,Void,Void> getProductList(){
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    //listBodega = mTableBodega.where().field("Type").eq(2).execute().get();
                    listBodega = mBodegaTable.read(QueryOperations.tableName("Bodega").field("Type").eq(2)).get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<String> list = new ArrayList<String>();
                            for(Bodega bodega : listBodega){
                                list.add(bodega.Name);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddProductCoffee.this,android.R.layout.simple_spinner_item,list);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spCoffeeProducts.setAdapter(adapter);
                        }
                    });
                }catch (Exception ex){}
                return null;
            }
        };
        return runAsyncTask(task);
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ready_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionReady:
                if (etxQuantity.getText().toString().equals("")){
                    etxQuantity.setError("Ingrese la cantidad del producto seleccionado");
                }else {
                    if(Float.valueOf(etxQuantity.getText().toString()) == 0){
                        etxQuantity.setError("Esta cantidad no esta ");
                    }else {
                        Intent i = new Intent(AddProductCoffee.this, Coffee.class);
                        i.putExtra("ProductSelected", listBodega.get(productSelected).id);
                        i.putExtra("NameProduct", listBodega.get(productSelected).Name);
                        i.putExtra("Quantity", etxQuantity.getText().toString());
                        i.putExtra("productRegistered", true);
                        startActivity(i);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
