package com.tsool.appvending;

import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
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
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.R.layout;
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
import java.util.List;
import java.util.Map;

public class AddProduct extends AppCompatActivity {

    MobileServiceClient mClient;
    private MobileServiceSyncTable<Bodega> mBodegaTable;

    MaquinaDetails objMaquinaDetails;

    MobileServiceTable<Bodega> mTableBodega;
    MobileServiceList<Bodega> listBodega;

    Spinner spProducts;
    EditText etxCantidad,etxPrecio,etxCantidadDisponible;

    private String idProducto,nombreProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setTitle("Agregar snack");


        MobileService objMobileService = new MobileService(this);
        objMaquinaDetails = new MaquinaDetails();

        try {
            mTableBodega = objMobileService.connection(this).getTable(Bodega.class);
        }catch(Exception ex){}

        spProducts = (Spinner) findViewById(R.id.spProduct);
        etxCantidad = (EditText) findViewById(R.id.etxCantidadProducto);
        etxPrecio = (EditText) findViewById(R.id.etxPrecioProducto);
        etxCantidadDisponible = (EditText)findViewById(R.id.etxCantidadDisponibleProducto);

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

        spProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idProducto = listBodega.get(position).id;
                nombreProducto = listBodega.get(position).Name;
                etxPrecio.setText(String.valueOf(listBodega.get(position).Price));
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
                    //listBodega = mTableBodega.where().field("Type").eq(1).execute().get();
                    listBodega = mBodegaTable.read(QueryOperations.tableName("Bodega").field("Type").eq(1)).get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //int _id = 0;
                            //String[] productColumns = new String[] {"name"};
                            //MatrixCursor cursor = new MatrixCursor(productColumns);
                            ArrayList<String> list = new ArrayList<String>();
                            for(Bodega bodega : listBodega){
                                list.add(bodega.Name);
                               // _id++;
                            }
                            //String[] from = {"message"};
                            //int[] to = {R.id.txvProductName};
                            //SimpleCursorAdapter adapter = new SimpleCursorAdapter(AddProduct.this,R.layout.support_simple_spinner_dropdown_item,cursor,from,to,0);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddProduct.this,android.R.layout.simple_spinner_item,list);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spProducts.setAdapter(adapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ready_action,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionReady:
                if (etxCantidad.getText().toString().equals("")||etxCantidadDisponible.getText().toString().equals("")||etxPrecio.getText().toString().equals("")||idProducto == null){
                    if (idProducto == null)
                        Toast.makeText(AddProduct.this, "Necesita seleccionar el producto que se almacenará", Toast.LENGTH_SHORT).show();

                    if (etxCantidad.getText().toString().equals(""))
                        etxCantidad.setError("Campo requerido");

                    if (etxPrecio.getText().toString().equals(""))
                        etxPrecio.setError("Campo requerido");

                    if (etxCantidadDisponible.getText().toString().equals(""))
                        etxCantidadDisponible.setError("Campo requerido");
                }else {
                    if (Float.valueOf(etxPrecio.getText().toString()) == 0){
                        etxPrecio.setError("Este precio no esta permitido");
                    }else {
                        Intent i = null;
                        if (objMaquinaDetails.getTipoMaquina() == 1) {
                            i = new Intent(AddProduct.this, Snacks1.class);

                        } else if (objMaquinaDetails.getTipoMaquina() == 2) {
                            i = new Intent(AddProduct.this, Snacks2.class);
                        }
                        i.putExtra("idProducto", idProducto);
                        i.putExtra("nombreProducto", nombreProducto);
                        i.putExtra("CantidadDisponibleProducto", etxCantidadDisponible.getText().toString());
                        i.putExtra("CantidadProducto", etxCantidad.getText().toString());
                        i.putExtra("PrecioProducto", etxPrecio.getText().toString());
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
