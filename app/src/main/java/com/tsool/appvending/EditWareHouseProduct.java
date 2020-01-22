package com.tsool.appvending;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class EditWareHouseProduct extends AppCompatActivity {

    MobileServiceClient mClient;
    private MobileServiceSyncTable<Bodega> mBodegaTable;

    MobileService objMobileService = new MobileService(this);

    EditText etxEditAvailability, etxEditUnitName, etxEditAvailabilityAlert, etxEditPriceSnack, etxEditPurchasePrice;
    MenuItem miSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ware_house_product);
        setTitle(getIntent().getStringExtra("Name"));

        TextInputLayout tilPrecioSnack = (TextInputLayout)findViewById(R.id.tilPrecioSnack);
        etxEditAvailability = (EditText)findViewById(R.id.etxEditAvailability);
        etxEditUnitName = (EditText)findViewById(R.id.etxEditUnitName);
        etxEditAvailabilityAlert = (EditText)findViewById(R.id.etxEditAvailabilityAlert);
        etxEditPriceSnack = (EditText)findViewById(R.id.etxEditPrecioSnack);
        etxEditPurchasePrice = (EditText)findViewById(R.id.etxEditPurchasePrice);

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

        if(getIntent().getIntExtra("Type",0) == 2){
            tilPrecioSnack.setVisibility(View.INVISIBLE);
        }
        etxEditAvailability.setText(getIntent().getStringExtra("Availability"));
        etxEditAvailabilityAlert.setText(getIntent().getStringExtra("AvailabilityAlert"));
        etxEditPriceSnack.setText(getIntent().getStringExtra("Price"));
        etxEditUnitName.setText(getIntent().getStringExtra("UnitName"));
        etxEditPurchasePrice.setText(getIntent().getStringExtra("PurchasePrice"));
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
                    if (etxEditAvailability.getText().toString().equals("") || etxEditAvailabilityAlert.getText().toString().equals("") || etxEditUnitName.getText().toString().equals("") || etxEditPriceSnack.getText().toString().equals("") || etxEditPurchasePrice.getText().toString().equals("")) {
                        if (etxEditUnitName.getText().toString().equals(""))
                            etxEditUnitName.setError("Este campo no puede quedar vacío");

                        if (etxEditAvailabilityAlert.getText().toString().equals(""))
                            etxEditAvailabilityAlert.setError("Este campo no puede quedar vacío");

                        if (etxEditAvailability.getText().toString().equals(""))
                            etxEditAvailability.setError("Este campo no puede quedar vacío");

                        if (etxEditPriceSnack.getText().toString().equals(""))
                            etxEditPriceSnack.setError("Este campo no puede quedar vacío");

                        if (etxEditPurchasePrice.getText().toString().equals(""))
                            etxEditPurchasePrice.setError("Este campo no puede quedar vacío");
                    } else {
                        miSave.setVisible(false);
                        Bodega bodega = new Bodega();
                        bodega.id = getIntent().getStringExtra("id");
                        bodega.Name = getIntent().getStringExtra("Name");
                        bodega.Availability = Float.valueOf(etxEditAvailability.getText().toString());
                        bodega.AvailabilityAlert = Float.valueOf(etxEditAvailabilityAlert.getText().toString());
                        bodega.UnitName = etxEditUnitName.getText().toString();
                        bodega.Price = Float.valueOf(etxEditPriceSnack.getText().toString());
                        bodega.PurchasePrice = Float.valueOf(etxEditPurchasePrice.getText().toString());
                        UpdateWarehouseProduct(bodega);
                    }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    AsyncTask<Void,Void,Void> UpdateWarehouseProduct(final Bodega bodega){
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //objMobileService.connection(EditWareHouseProduct.this).getTable(Bodega.class).update(bodega).get();
                    mBodegaTable.update(bodega).get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(EditWareHouseProduct.this, Warehouse.class));
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
