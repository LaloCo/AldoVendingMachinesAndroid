package com.tsool.appvending;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

public class AddMachine extends AppCompatActivity {

    MobileServiceClient mClient;
    private MobileServiceSyncTable<Maquina> mMaquinaTable;

    MobileService objMobileService = new MobileService(this);

    EditText etxName,etxFirstRead,etxPrecioCafe,etxValorParticipacion;
    Spinner spKindMachine,spKindParticipacion;
    MenuItem miSave;

    int kindSelected,kindParticipation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_machine);
        setTitle("Agregar máquina");

        spKindMachine = (Spinner) findViewById(R.id.spKindMachine);
        spKindParticipacion = (Spinner) findViewById(R.id.spTipoParticipacion);
        final TextInputLayout tilPrecioCafe = (TextInputLayout) findViewById(R.id.tilPrecioCafe);
        final TextInputLayout tilValorParticipacion = (TextInputLayout)findViewById(R.id.tilValorParticipacion);
        etxName =(EditText) findViewById(R.id.etxNameMachine);
        etxFirstRead = (EditText) findViewById(R.id.etxFirstRead);
        etxPrecioCafe = (EditText)findViewById(R.id.etxPrecioCafe);
        etxValorParticipacion = (EditText)findViewById(R.id.etxValorParticipacion);

        objMobileService = new MobileService(this);

        try
        {
            mClient = new MobileServiceClient(
                    "https://vendingmachines.azurewebsites.net//",
                    this
            );
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

            localStore.defineTable("Maquina", maquinaTableDefinition);

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

        syncAsync();

        spKindMachine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final View v = view;
                switch (position){
                    case 0:
                        tilPrecioCafe.setVisibility(View.INVISIBLE);
                        kindSelected = 1;
                        break;
                    case 1:
                        tilPrecioCafe.setVisibility(View.INVISIBLE);
                        kindSelected = 2;
                        break;
                    case 2:
                        tilPrecioCafe.setVisibility(View.VISIBLE);
                        kindSelected = 3;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spKindParticipacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        kindParticipation = 0;
                        break;
                    case 1:
                        kindParticipation = 1;
                        break;
                    case 2:
                        kindParticipation = 2;
                        break;
                    case 3:
                        kindParticipation = 3;
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
        menuInflater.inflate(R.menu.add_machine_actions, menu);
        miSave = menu.findItem(R.id.actionSave);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MobileService mobileService = new MobileService(AddMachine.this);
        switch (item.getItemId()) {
            case R.id.actionSave:
                    if (etxName.getText().toString().equals("") || etxFirstRead.getText().toString().equals("")||etxValorParticipacion.getText().toString().equals("")) {
                        if (etxName.getText().toString().equals(""))
                            etxName.setError("Ingrese el nombre de la máquina");

                        if (etxFirstRead.getText().toString().equals(""))
                            etxFirstRead.setError("Ingrese la primera lectura");

                        if(etxValorParticipacion.getText().toString().equals(""))
                            etxValorParticipacion.setError("Ingrese el valor por participación");
                    } else if (!(kindSelected == 3)) {
                            miSave.setVisible(false);
                            Maquina maquina = new Maquina();
                            maquina.Name = etxName.getText().toString();
                            maquina.Kind = kindSelected;
                            maquina.FirstRead = Integer.valueOf(etxFirstRead.getText().toString());
                            maquina.TipoParticipacion = kindParticipation;
                            maquina.ValorParticipacion = Float.valueOf(etxValorParticipacion.getText().toString());
                            InsertMachine(maquina);

                    } else {
                        if (etxPrecioCafe.getText().toString().equals("")) {
                                etxPrecioCafe.setError("Ingrese el precio por taza");

                        } else {
                            miSave.setVisible(false);
                            Maquina maquina = new Maquina();
                            maquina.Name = etxName.getText().toString();
                            maquina.Kind = kindSelected;
                            maquina.FirstRead = Float.valueOf(etxFirstRead.getText().toString());
                            maquina.PrecioCafe = Float.valueOf(etxPrecioCafe.getText().toString());
                            maquina.TipoParticipacion = kindParticipation;
                            maquina.ValorParticipacion = Float.valueOf(etxValorParticipacion.getText().toString());
                            InsertMachine(maquina);
                        }

                    }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    AsyncTask<Void,Void,Void> InsertMachine(final Maquina maquina){
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mMaquinaTable.insert(maquina).get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(AddMachine.this, MainActivity.class));
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddMachine.this,MainActivity.class));
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

                    } catch (Exception exception) {
                        createAndShowDialog(exception, "Error");
                    }
                    return null;
                }
            }.execute();
        }
    }
}
