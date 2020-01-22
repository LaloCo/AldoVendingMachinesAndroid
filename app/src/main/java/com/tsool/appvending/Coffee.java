package com.tsool.appvending;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Coffee extends AppCompatActivity {

    MobileServiceClient mClient;
    private MobileServiceSyncTable<Lectura> mLecturaTable;

    MaquinaDetails objMaquinaDetails = new MaquinaDetails();

    MobileServiceTable<Lectura> mTableLectura;
    MobileServiceList<Lectura> listLectura;

    EditText etxLecturaCoffee, etxAjustesCoffee;
    MenuItem miSave;

    private static ArrayList<CoffeeProduct> mList;

    private static String title;
    private static boolean products,indexObtained,objectDuplicated,objectSimilar;

    private static float lectura,ajustes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee);
        if (getIntent().getBooleanExtra("NameSent",false)) {
            title = getIntent().getStringExtra("Name");
        }
        setTitle(title);



        MobileService objMobileService = new MobileService(this);

        products = getIntent().getBooleanExtra("productRegistered", false);


        ImageButton ibAddCoffeeProduct = (ImageButton)findViewById(R.id.ibAddCoffeeProduct);
        ListView lvListCoffeeProducts = (ListView)findViewById(R.id.lvListCoffeeProducts);
        etxLecturaCoffee = (EditText)findViewById(R.id.etxLecturaCoffee);
        etxAjustesCoffee = (EditText)findViewById(R.id.etxAjustesCoffee);

        etxAjustesCoffee.setText("0");

        try {
            mTableLectura = objMobileService.connection(this).getTable(Lectura.class);
        }catch (Exception ex){}

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

            Map<String, ColumnDataType> lecturaTableDefinition = new HashMap<String, ColumnDataType>();
            lecturaTableDefinition.put("id", ColumnDataType.String);
            lecturaTableDefinition.put("Valor", ColumnDataType.Real);
            lecturaTableDefinition.put("Reposiciones", ColumnDataType.Real);
            lecturaTableDefinition.put("Id_Maquina", ColumnDataType.String);
            lecturaTableDefinition.put("NombreMaquina", ColumnDataType.String);
            lecturaTableDefinition.put("ValorMonetario", ColumnDataType.Real);
            lecturaTableDefinition.put("FechaEntrada", ColumnDataType.Date);

            localStore.defineTable("Lectura", lecturaTableDefinition);

            syncContext.initialize(localStore, handler).get();
        }catch (Exception e){
            Throwable t = e;
            while (t.getCause() != null) {
                t = t.getCause();
            }
            createAndShowDialog(new Exception("Unknown error: " + t.getMessage()), "Error");
        }

        // Get the Mobile Service Table instance to use
        mLecturaTable = mClient.getSyncTable(Lectura.class);

        syncAsync();

        ibAddCoffeeProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etxLecturaCoffee.getText().toString().equals("")) {
                    lectura = Float.valueOf(etxLecturaCoffee.getText().toString());
                }else{
                    lectura = 0;
                }
                if(!etxAjustesCoffee.getText().toString().equals("")) {
                    ajustes = Float.valueOf(etxAjustesCoffee.getText().toString());
                }else{
                    ajustes = 0;
                }
                startActivity(new Intent(Coffee.this, AddProductCoffee.class));
            }
        });

        if(products == true){
            if(!(lectura == 0)) {
                etxLecturaCoffee.setText(String.valueOf(lectura));
            }
            if(!(ajustes == 0)){
                etxAjustesCoffee.setText(String.valueOf(ajustes));
            }
            GetIndex();
            if(!indexObtained) {
                mList.add(new CoffeeProduct(getIntent().getStringExtra("ProductSelected"), getIntent().getStringExtra("NameProduct"), Float.valueOf(getIntent().getStringExtra("Quantity"))));
            }
            else{
                Toast.makeText(Coffee.this, "Este producto ya esta agregado en la lista", Toast.LENGTH_SHORT).show();
            }
            int _id = 0;
            String[] columnas = new String[] {"_id","name", "quantity"};
            MatrixCursor cursor = new MatrixCursor(columnas);

            for(CoffeeProduct coffeeProduct : mList){
                cursor.addRow(new Object[]{_id, coffeeProduct.Name, coffeeProduct.Quantity});
                _id++;
            }
            String[] from = {"name","quantity"};
            int[] to = {R.id.txvCoffeeProductName,R.id.txvCoffeeProductQuantity};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.coffee_product_item,cursor,from,to,0);
            lvListCoffeeProducts.setAdapter(adapter);
        }else{
            mList = new ArrayList<CoffeeProduct>();
        }
    }

    private void GetIndex(){
        indexObtained = false;
        for(CoffeeProduct coffeeProduct : mList){
            if(getIntent().getStringExtra("NameProduct").equals(coffeeProduct.Name)){
                indexObtained = true;
            }
        }
    }

    private AsyncTask<Void,Void,Void> getLectureList(final Lectura lectura){
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    //listLectura = mTableLectura.where().field("Id_Maquina").eq(objMaquinaDetails.getId_Maquina()).and().field("FechaEntrada").eq(lectura.FechaEntrada).execute().get();
                    listLectura = mLecturaTable.read(QueryOperations.tableName("Lectura").field("Id_Maquina").eq(objMaquinaDetails.getId_Maquina()).and().field("FechaEntrada").eq(lectura.FechaEntrada)).get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MobileService objMobileService = new MobileService(Coffee.this);
                            for (CoffeeProduct coffeeProduct : mList){
                                Detalle_Lectura detalle_lectura = new Detalle_Lectura();
                                detalle_lectura.Id_Lectura = listLectura.get(0).id;
                                detalle_lectura.Id_Item = coffeeProduct.id;
                                detalle_lectura.CantidadAgregada = Float.valueOf(coffeeProduct.Quantity);
                                objMobileService.insertLectureDetail(detalle_lectura);

                            }
                            startActivity(new Intent(Coffee.this, MainActivity.class));
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
    public void onBackPressed() {
        startActivity(new Intent(Coffee.this, MainActivity.class));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_machine_actions, menu);
        miSave = menu.findItem(R.id.actionSave);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int n = 0;
        MobileService objMobileService = new MobileService(this);
        switch (item.getItemId()) {
            case R.id.actionSave:
                for (CoffeeProduct coffeeProduct : mList){
                        n++;
                }
                if (etxLecturaCoffee.getText().toString().equals("")||etxAjustesCoffee.getText().toString().equals("")) {
                    if (etxLecturaCoffee.getText().toString().equals("")) {
                        etxLecturaCoffee.setError("Ingresar el total de la lectura");
                    }
                    if (etxAjustesCoffee.getText().toString().equals("")) {
                        etxAjustesCoffee.setError("Ingresar el total de los ajustes");
                    }
                }else {
                        miSave.setVisible(false);
                        final Lectura lectura = new Lectura();
                        lectura.Id_Maquina = objMaquinaDetails.getId_Maquina();
                        lectura.NombreMaquina = objMaquinaDetails.getNombreMaquina();
                        lectura.Valor = Float.valueOf(etxLecturaCoffee.getText().toString());
                        lectura.Reposiciones = Float.valueOf(etxAjustesCoffee.getText().toString());
                        lectura.FechaEntrada = getDate();
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                try {
                                    mLecturaTable.insert(lectura).get();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getLectureList(lectura);
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
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Date getDate(){
        Date date = null;
        String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            date = sdf.parse(sdf.format(c.getTime()));
            String dates = date.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
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
                        mLecturaTable.pull(null).get();

                    } catch (Exception exception) {
                        createAndShowDialog(exception, "Error");
                    }
                    return null;
                }
            }.execute();
        }
    }
}
