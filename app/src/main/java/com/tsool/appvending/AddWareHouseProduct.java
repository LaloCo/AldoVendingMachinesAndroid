package com.tsool.appvending;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AddWareHouseProduct extends AppCompatActivity {

    MobileServiceClient mClient;
    private MobileServiceSyncTable<Bodega> mBodegaTable;

    MobileService objMobileService = new MobileService(this);

    EditText etxNameProduct, etxAvailability, etxUnitName, etxAvailabilityAlert, etxPriceSnack,etxPurchasePrice;
    TextInputLayout tilPrecioSnack;
    MenuItem miSave;


    int kindProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ware_house_product);
        setTitle("Agregar producto");

        Spinner spKindProduct = (Spinner) findViewById(R.id.spKindProduct);
        etxNameProduct = (EditText) findViewById(R.id.etxNameProduct);
        etxAvailability = (EditText) findViewById(R.id.etxAvailability);
        etxUnitName = (EditText) findViewById(R.id.etxUnitName);
        etxAvailabilityAlert = (EditText) findViewById(R.id.etxAvailabilityAlert);
        etxPriceSnack = (EditText) findViewById(R.id.etxPrecioSnack);
        etxPurchasePrice = (EditText) findViewById(R.id.etxPurchasePrice);
        tilPrecioSnack = (TextInputLayout) findViewById(R.id.tilPrecioSnack);

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

        spKindProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        tilPrecioSnack.setVisibility(View.VISIBLE);
                        kindProduct = 1;
                        break;
                    case 1:
                        tilPrecioSnack.setVisibility(View.INVISIBLE);
                        kindProduct = 2;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ready_action, menu);
        miSave = menu.findItem(R.id.actionReady);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        MobileService objMobileService = new MobileService(this);
        switch (item.getItemId()) {
            case R.id.actionReady:
                if (etxNameProduct.getText().toString().equals("") || etxAvailability.getText().toString().equals("") || etxUnitName.getText().toString().equals("") || etxAvailabilityAlert.getText().toString().equals("") || etxPurchasePrice.getText().toString().equals("")) {
                    if (etxNameProduct.getText().toString().equals(""))
                        etxNameProduct.setError("Campo Requerido");

                    if (etxUnitName.getText().toString().equals(""))
                        etxUnitName.setError("Campo requerido");

                    if (etxAvailability.getText().toString().equals(""))
                        etxAvailability.setError("Campo Requerido");

                    if (etxAvailabilityAlert.getText().toString().equals(""))
                        etxAvailabilityAlert.setError("Campo Requerido");

                    if (etxPurchasePrice.getText().toString().equals(""))
                        etxPurchasePrice.setError("Campo Requerido");

                } else if (!(kindProduct == 1)) {
                    miSave.setVisible(false);
                    Bodega bodega = new Bodega();
                    bodega.Name = etxNameProduct.getText().toString();
                    bodega.Availability = Float.valueOf(etxAvailability.getText().toString());
                    bodega.AvailabilityAlert = Float.valueOf(etxAvailabilityAlert.getText().toString());
                    bodega.Type = kindProduct;
                    bodega.UnitName = etxUnitName.getText().toString();
                    bodega.PurchasePrice = Float.valueOf(etxPurchasePrice.getText().toString());
                    InsertWarehouseProduct(bodega);
                } else {
                    if (etxPriceSnack.getText().toString().equals("")) {
                        etxPriceSnack.setError("Campo Requerido");
                    } else {
                        miSave.setVisible(false);
                        Bodega bodega = new Bodega();
                        bodega.Name = etxNameProduct.getText().toString();
                        bodega.Availability = Float.valueOf(etxAvailability.getText().toString());
                        bodega.AvailabilityAlert = Float.valueOf(etxAvailabilityAlert.getText().toString());
                        bodega.Type = kindProduct;
                        bodega.UnitName = etxUnitName.getText().toString();
                        bodega.Price = Float.valueOf(etxPriceSnack.getText().toString());
                        bodega.PurchasePrice = Float.valueOf(etxPurchasePrice.getText().toString());
                        InsertWarehouseProduct(bodega);


                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private AsyncTask<Void,Void,Void> InsertWarehouseProduct(final Bodega bodega){
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mBodegaTable.insert(bodega).get();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(AddWareHouseProduct.this, Warehouse.class));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
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
