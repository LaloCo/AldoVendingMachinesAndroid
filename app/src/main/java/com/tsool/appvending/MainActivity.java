package com.tsool.appvending;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.Query;
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

public class MainActivity extends AppCompatActivity {

    MobileServiceClient mClient;

    MobileServiceTable<Maquina> mTableMachine;
    MobileServiceList<Maquina> listMachine;
    MaquinasAdapter mAdapter;



    Adapter adapter;

    private MobileServiceSyncTable<Maquina> mMaquinaTable;
    private MobileServiceSyncTable<Bodega> mBodegaTable;
    private MobileServiceSyncTable<Lectura> mLecturaTable;
    private MobileServiceSyncTable<Detalle_Lectura> mDetalleLecturaTable;

    MobileServiceTable<Detalle_Lectura> mTableDetalle;
    MobileServiceList<Detalle_Lectura> listDetalle;

    MobileService objMobileService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Máquinas");

        objMobileService = new MobileService(this);

        try
        {
            mClient = new MobileServiceClient("https://vendingmachines.azurewebsites.net/",this);
        }catch (Exception ex){}

        try {
            SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "AppVending", null, 1);
            SimpleSyncHandler handler = new SimpleSyncHandler();
            MobileServiceSyncContext syncContext = mClient.getSyncContext();

            Map<String, ColumnDataType> maquinaTableDefinition = new HashMap<String, ColumnDataType>();
            maquinaTableDefinition.put("id", ColumnDataType.String);
            maquinaTableDefinition.put("Name", ColumnDataType.String);
            maquinaTableDefinition.put("Kind", ColumnDataType.Integer);
            maquinaTableDefinition.put("FirstRead", ColumnDataType.Real);
            maquinaTableDefinition.put("PrecioCafe", ColumnDataType.Real);
            maquinaTableDefinition.put("TipoParticipacion", ColumnDataType.Integer);
            maquinaTableDefinition.put("ValorParticipacion", ColumnDataType.Real);

            Map<String, ColumnDataType> bodegaTableDefinition = new HashMap<String, ColumnDataType>();
            bodegaTableDefinition.put("id", ColumnDataType.String);
            bodegaTableDefinition.put("Name", ColumnDataType.String);
            bodegaTableDefinition.put("UnitName", ColumnDataType.String);
            bodegaTableDefinition.put("Availability", ColumnDataType.Real);
            bodegaTableDefinition.put("AvailabilityAlert", ColumnDataType.Real);
            bodegaTableDefinition.put("Price", ColumnDataType.Real);
            bodegaTableDefinition.put("Type", ColumnDataType.Integer);

            Map<String, ColumnDataType> lecturaTableDefinition = new HashMap<String, ColumnDataType>();
            lecturaTableDefinition.put("id", ColumnDataType.String);
            lecturaTableDefinition.put("Valor", ColumnDataType.Real);
            lecturaTableDefinition.put("Reposiciones", ColumnDataType.Real);
            lecturaTableDefinition.put("Id_Maquina", ColumnDataType.String);
            lecturaTableDefinition.put("NombreMaquina", ColumnDataType.String);
            lecturaTableDefinition.put("ValorMonetario", ColumnDataType.Real);
            lecturaTableDefinition.put("FechaEntrada", ColumnDataType.Date);

            Map<String, ColumnDataType> detalleLecturaTableDefinition = new HashMap<String, ColumnDataType>();
            detalleLecturaTableDefinition.put("id", ColumnDataType.String);
            detalleLecturaTableDefinition.put("Id_Lectura", ColumnDataType.String);
            detalleLecturaTableDefinition.put("Id_Item", ColumnDataType.String);
            detalleLecturaTableDefinition.put("CantidadAgregada", ColumnDataType.Real);
            detalleLecturaTableDefinition.put("Precio", ColumnDataType.Real);
            detalleLecturaTableDefinition.put("Posicion", ColumnDataType.Integer);
            detalleLecturaTableDefinition.put("CantidadDisponible", ColumnDataType.Integer);
            detalleLecturaTableDefinition.put("__createdAt",ColumnDataType.Date);

            localStore.defineTable("Maquina", maquinaTableDefinition);
            localStore.defineTable("Bodega", bodegaTableDefinition);
            localStore.defineTable("Lectura", lecturaTableDefinition);
            localStore.defineTable("Detalle_Lectura", detalleLecturaTableDefinition);

            syncContext.initialize(localStore, handler).get();
        }catch (Exception e){
            Throwable t = e;
            while (t.getCause() != null) {
                t = t.getCause();
            }
            createAndShowDialog(new Exception("Unknown error: " + t.getMessage()), "Error");
        }

        // Get the Mobile Service Table instance to use
        mMaquinaTable = mClient.getSyncTable(Maquina.class);
        mBodegaTable = mClient.getSyncTable(Bodega.class);
        mLecturaTable = mClient.getSyncTable(Lectura.class);
        mDetalleLecturaTable = mClient.getSyncTable(Detalle_Lectura.class);

        syncAsync();

        final MaquinaDetails objMaquinaDetails = new MaquinaDetails();
        final ArrayList<Maquina> m = new ArrayList<Maquina>();

        final ListView lvMachine = (ListView) findViewById(R.id.lvMachine);

        try{
            mTableDetalle = objMobileService.connection(this).getTable(Detalle_Lectura.class);
            mTableMachine = objMobileService.connection(this).getTable(Maquina.class);

            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        //listDetalle = mTableDetalle.where().orderBy("__createdAt",QueryOrder.Ascending).execute().get();
                        //listMachine = mTableMachine.where().orderBy("Kind", QueryOrder.Ascending).execute().get();
                        listDetalle = mDetalleLecturaTable.read(QueryOperations.tableName("Detalle_Lectura").orderBy("__createdAt",QueryOrder.Ascending)).get();
                        listMachine = mMaquinaTable.read(QueryOperations.tableName("Maquina").orderBy("Kind",QueryOrder.Ascending)).get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                for(Maquina machine : listMachine){
                                    m.add(new Maquina(machine.getName(),machine.getKind()));
                                }
                                mAdapter = new MaquinasAdapter(MainActivity.this,m);
                                lvMachine.setAdapter(mAdapter);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }catch (Exception ex){}






        lvMachine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Maquina m = (Maquina) lvMachine.getItemAtPosition(position);
                for (Maquina maquina : listMachine) {
                    if (m.Name.toString().equals(maquina.Name)&&(m.Kind==maquina.Kind)) {
                        Intent i;
                        switch (maquina.Kind) {
                            case 0:
                                i = new Intent(MainActivity.this, Warehouse.class);
                                i.putExtra("Name", maquina.Name);
                                i.putExtra("NameSent", true);
                                startActivity(i);
                                break;
                            case 1:
                                i = new Intent(MainActivity.this, Snacks1.class);
                                i.putExtra("Name", maquina.Name);
                                i.putExtra("NameSent", true);
                                startActivity(i);
                                objMaquinaDetails.setId_Maquina(maquina.id);
                                objMaquinaDetails.setNombreMaquina(maquina.Name);
                                objMaquinaDetails.setTipoMaquina(maquina.Kind);
                                break;
                            case 2:
                                i = new Intent(MainActivity.this, Snacks2.class);
                                i.putExtra("Name", maquina.Name);
                                i.putExtra("NameSent", true);
                                startActivity(i);
                                objMaquinaDetails.setId_Maquina(maquina.id);
                                objMaquinaDetails.setNombreMaquina(maquina.Name);
                                objMaquinaDetails.setTipoMaquina(maquina.Kind);
                                break;
                            case 3:
                                i = new Intent(MainActivity.this, Coffee.class);
                                i.putExtra("Name", maquina.Name);
                                i.putExtra("NameSent", true);
                                startActivity(i);
                                objMaquinaDetails.setId_Maquina(maquina.id);
                                objMaquinaDetails.setNombreMaquina(maquina.Name);
                                objMaquinaDetails.setTipoMaquina(maquina.Kind);
                                break;
                            default:
                                Toast.makeText(MainActivity.this, listMachine.get(position).Name, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

        FloatingActionButton fabAddMachine = (FloatingActionButton) findViewById(R.id.fabAddMachine);



        fabAddMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddMachine.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_action, menu);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                if (!(mAdapter == null))
                    mAdapter.getFilter().filter(text);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
                        mMaquinaTable.pull(null).get();
                        mBodegaTable.pull(null).get();
                        mLecturaTable.pull(null).get();
                        mDetalleLecturaTable.pull(null).get();

                    } catch (Exception exception) {
                        createAndShowDialog(exception, "Error");
                    }
                    return null;
                }
            }.execute();
        }
    }
}
