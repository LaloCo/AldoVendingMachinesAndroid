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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Snacks1 extends AppCompatActivity {

    MobileServiceClient mClient;
    private MobileServiceSyncTable<Bodega> mBodegaTable;
    private MobileServiceSyncTable<Lectura> mLecturaTable;
    private MobileServiceSyncTable<Detalle_Lectura> mDetalleLecturaTable;

    MaquinaDetails objMaquinaDetails = new MaquinaDetails();

    TextView txvProduct1,txvProduct2,txvProduct3,txvProduct4,txvProduct5,txvProduct6,txvProduct7,txvProduct8,txvProduct9,txvProduct10,
            txvProduct11,txvProduct12,txvProduct13,txvProduct14,txvProduct15,txvProduct16,txvProduct17,txvProduct18,txvProduct19,txvProduct20,
            txvProduct21,txvProduct22,txvProduct23,txvProduct24,txvProduct25,txvProduct26,txvProduct27,txvProduct28,txvProduct29,txvProduct30;

    EditText etxAjustesSnacks1,etxLecturaSnacks1;
    MenuItem miSave;

    MobileServiceTable<Lectura> mTableLectura;
    MobileServiceList<Lectura> listLectura;

    MobileServiceTable<Detalle_Lectura> mTableDetalleLectura;
    MobileServiceList<Detalle_Lectura> listDetalleLectura;

    MobileServiceTable<Bodega> mTableBodega;
    MobileServiceList<Bodega> listBodega;

    private static ArrayList<Product> mProductList;

    private static boolean productRegistered,indexObtained;
    private static boolean productRegistered1,productRegistered2,productRegistered3,productRegistered4,productRegistered5,productRegistered6,productRegistered7,productRegistered8,productRegistered9,productRegistered10,
            productRegistered11,productRegistered12,productRegistered13,productRegistered14,productRegistered15,productRegistered16,productRegistered17,productRegistered18,productRegistered19,productRegistered20,
            productRegistered21,productRegistered22,productRegistered23,productRegistered24,productRegistered25,productRegistered26,productRegistered27,productRegistered28,productRegistered29,productRegistered30;
    private static int posicionSeleccionada,index;
    private static float lectura,ajustes;

    private static String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snacks1);
        if (getIntent().getBooleanExtra("NameSent",false)) {
            title = getIntent().getStringExtra("Name");
        }
        setTitle(title);



        txvProduct1 = (TextView) findViewById(R.id.txvSnacks1Product1);
        txvProduct2 = (TextView) findViewById(R.id.txvSnacks1Product2);
        txvProduct3 = (TextView) findViewById(R.id.txvSnacks1Product3);
        txvProduct4 = (TextView) findViewById(R.id.txvSnacks1Product4);
        txvProduct5 = (TextView) findViewById(R.id.txvSnacks1Product5);
        txvProduct6 = (TextView) findViewById(R.id.txvSnacks1Product6);
        txvProduct7 = (TextView) findViewById(R.id.txvSnacks1Product7);
        txvProduct8 = (TextView) findViewById(R.id.txvSnacks1Product8);
        txvProduct9 = (TextView) findViewById(R.id.txvSnacks1Product9);
        txvProduct10 = (TextView) findViewById(R.id.txvSnacks1Product10);
        txvProduct11 = (TextView) findViewById(R.id.txvSnacks1Product11);
        txvProduct12 = (TextView) findViewById(R.id.txvSnacks1Product12);
        txvProduct13 = (TextView) findViewById(R.id.txvSnacks1Product13);
        txvProduct14 = (TextView) findViewById(R.id.txvSnacks1Product14);
        txvProduct15 = (TextView) findViewById(R.id.txvSnacks1Product15);
        txvProduct16 = (TextView) findViewById(R.id.txvSnacks1Product16);
        txvProduct17 = (TextView) findViewById(R.id.txvSnacks1Product17);
        txvProduct18 = (TextView) findViewById(R.id.txvSnacks1Product18);
        txvProduct19 = (TextView) findViewById(R.id.txvSnacks1Product19);
        txvProduct20 = (TextView) findViewById(R.id.txvSnacks1Product20);
        txvProduct21 = (TextView) findViewById(R.id.txvSnacks1Product21);
        txvProduct22 = (TextView) findViewById(R.id.txvSnacks1Product22);
        txvProduct23 = (TextView) findViewById(R.id.txvSnacks1Product23);
        txvProduct24 = (TextView) findViewById(R.id.txvSnacks1Product24);
        txvProduct25 = (TextView) findViewById(R.id.txvSnacks1Product25);
        txvProduct26 = (TextView) findViewById(R.id.txvSnacks1Product26);
        txvProduct27 = (TextView) findViewById(R.id.txvSnacks1Product27);
        txvProduct28 = (TextView) findViewById(R.id.txvSnacks1Product28);
        txvProduct29 = (TextView) findViewById(R.id.txvSnacks1Product29);
        txvProduct30 = (TextView) findViewById(R.id.txvSnacks1Product30);

        etxLecturaSnacks1 = (EditText)findViewById(R.id.etxLecturaSnacks1);
        etxAjustesSnacks1 = (EditText)findViewById(R.id.etxAjustesSnacks1);

        etxAjustesSnacks1.setText("0");

        MobileService objMobileService = new MobileService(this);

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
            detalleLecturaTableDefinition.put("__createdAt", ColumnDataType.Date);

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
        mBodegaTable = mClient.getSyncTable(Bodega.class);
        mLecturaTable = mClient.getSyncTable(Lectura.class);
        mDetalleLecturaTable = mClient.getSyncTable(Detalle_Lectura.class);

        syncAsync();

        productRegistered = getIntent().getBooleanExtra("productRegistered",false);

        try {
            mTableLectura = objMobileService.connection(this).getTable(Lectura.class);
            mTableBodega = objMobileService.connection(this).getTable(Bodega.class);
            mTableDetalleLectura = objMobileService.connection(this).getTable(Detalle_Lectura.class);
        }catch(Exception ex){}

        if(productRegistered == true){
            switch (posicionSeleccionada){
                case 1:
                    productRegistered1 = true;
                    break;
                case 2:
                    productRegistered2 = true;
                    break;
                case 3:
                    productRegistered3 = true;
                    break;
                case 4:
                    productRegistered4 = true;
                    break;
                case 5:
                    productRegistered5 = true;
                    break;
                case 6:
                    productRegistered6 = true;
                    break;
                case 7:
                    productRegistered7 = true;
                    break;
                case 8:
                    productRegistered8 = true;
                    break;
                case 9:
                    productRegistered9 = true;
                    break;
                case 10:
                    productRegistered10 = true;
                    break;
                case 11:
                    productRegistered11 = true;
                    break;
                case 12:
                    productRegistered12 = true;
                    break;
                case 13:
                    productRegistered13 = true;
                    break;
                case 14:
                    productRegistered14 = true;
                    break;
                case 15:
                    productRegistered15 = true;
                    break;
                case 16:
                    productRegistered16 = true;
                    break;
                case 17:
                    productRegistered17 = true;
                    break;
                case 18:
                    productRegistered18 = true;
                    break;
                case 19:
                    productRegistered19 = true;
                    break;
                case 20:
                    productRegistered20 = true;
                    break;
                case 21:
                    productRegistered21 = true;
                    break;
                case 22:
                    productRegistered22 = true;
                    break;
                case 23:
                    productRegistered23 = true;
                    break;
                case 24:
                    productRegistered24 = true;
                    break;
                case 25:
                    productRegistered25 = true;
                    break;
                case 26:
                    productRegistered26 = true;
                    break;
                case 27:
                    productRegistered27 = true;
                    break;
                case 28:
                    productRegistered28 = true;
                    break;
                case 29:
                    productRegistered29 = true;
                    break;
                case 30:
                    productRegistered30 = true;
                    break;
                default:
                    break;
            }
            changeSelectedProducts();
            if(!(lectura == 0)) {
                etxLecturaSnacks1.setText(String.valueOf(lectura));
            }
            if(!(ajustes == 0)){
                etxAjustesSnacks1.setText(String.valueOf(ajustes));
            }
            if(indexObtained == true) {
                mProductList.set(index, new Product(posicionSeleccionada, getIntent().getStringExtra("idProducto"), getIntent().getStringExtra("nombreProducto"), Float.valueOf(getIntent().getStringExtra("PrecioProducto")), Integer.valueOf(getIntent().getStringExtra("CantidadProducto")), Integer.valueOf(getIntent().getStringExtra("CantidadDisponibleProducto"))));
            }else if(indexObtained == false) {
                mProductList.add(new Product(posicionSeleccionada, getIntent().getStringExtra("idProducto"), getIntent().getStringExtra("nombreProducto"), Float.valueOf(getIntent().getStringExtra("PrecioProducto")), Integer.valueOf(getIntent().getStringExtra("CantidadProducto")),Integer.valueOf(getIntent().getStringExtra("CantidadDisponibleProducto"))));
            }
            AsignarValores();
        }else if(productRegistered == false){
            mProductList = new ArrayList<Product>();
            ListProducts();
        }


        txvProduct1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this, AddProduct.class));
                posicionSeleccionada = 1;
                GetIndex();
            }
        });
        txvProduct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 2;
                GetIndex();
            }
        });
        txvProduct3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 3;
                GetIndex();
            }
        });
        txvProduct4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 4;
                GetIndex();
            }
        });
        txvProduct5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 5;
                GetIndex();
            }
        });
        txvProduct6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 6;
                GetIndex();
            }
        });
        txvProduct7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 7;
                GetIndex();
            }
        });
        txvProduct8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 8;
                GetIndex();
            }
        });
        txvProduct9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 9;
                GetIndex();
            }
        });
        txvProduct10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this, AddProduct.class));
                posicionSeleccionada = 10;
                GetIndex();
            }
        });
        txvProduct11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this, AddProduct.class));
                posicionSeleccionada = 11;
                GetIndex();
            }
        });
        txvProduct12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 12;
                GetIndex();
            }
        });
        txvProduct13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 13;
                GetIndex();
            }
        });
        txvProduct14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 14;
                GetIndex();
            }
        });
        txvProduct15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 15;
                GetIndex();
            }
        });
        txvProduct16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 16;
                GetIndex();
            }
        });
        txvProduct17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 17;
                GetIndex();
            }
        });
        txvProduct18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 18;
                GetIndex();
            }
        });
        txvProduct19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 19;
                GetIndex();
            }
        });
        txvProduct20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 20;
                GetIndex();
            }
        });
        txvProduct21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this, AddProduct.class));
                posicionSeleccionada = 21;
                GetIndex();
            }
        });
        txvProduct22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 22;
                GetIndex();
            }
        });
        txvProduct23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 23;
                GetIndex();
            }
        });
        txvProduct24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 24;
                GetIndex();
            }
        });
        txvProduct25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 25;
                GetIndex();
            }
        });
        txvProduct26.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 26;
                GetIndex();
            }
        });
        txvProduct27.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 27;
                GetIndex();
            }
        });
        txvProduct28.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 28;
                GetIndex();
            }
        });
        txvProduct29.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 29;
                GetIndex();
            }
        });
        txvProduct30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Snacks1.this,AddProduct.class));
                posicionSeleccionada = 30;
                GetIndex();
            }
        });
    }

    private void GetIndex(){
        indexObtained = false;
        if(!etxLecturaSnacks1.getText().toString().equals("")) {
            lectura = Float.valueOf(etxLecturaSnacks1.getText().toString());
        }else{
            lectura = 0;
        }
        if(!etxAjustesSnacks1.getText().toString().equals("")) {
            ajustes = Float.valueOf(etxAjustesSnacks1.getText().toString());
        }else{
            ajustes = 0;
        }
        for(Product product : mProductList){
            if(posicionSeleccionada == product.Posicion){
                index = mProductList.indexOf(product);
                indexObtained = true;
            }
        }
    }

    private void CleanVariables(){
        productRegistered = false;
        productRegistered1 = false;
        productRegistered2 = false;
        productRegistered3 = false;
        productRegistered4 = false;
        productRegistered5 = false;
        productRegistered6 = false;
        productRegistered7 = false;
        productRegistered8 = false;
        productRegistered9 = false;
        productRegistered10 = false;
        productRegistered11 = false;
        productRegistered12 = false;
        productRegistered13 = false;
        productRegistered14 = false;
        productRegistered15 = false;
        productRegistered16 = false;
        productRegistered17 = false;
        productRegistered18 = false;
        productRegistered19 = false;
        productRegistered20 = false;
        productRegistered21 = false;
        productRegistered22 = false;
        productRegistered23 = false;
        productRegistered24 = false;
        productRegistered25 = false;
        productRegistered26 = false;
        productRegistered27 = false;
        productRegistered28 = false;
        productRegistered29 = false;
        productRegistered30 = false;
        lectura = 0;
        ajustes = 0;


    }

    private AsyncTask<Void,Void,Void> ListProducts(){
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    //listLectura = mTableLectura.where().field("id_Maquina").eq(objMaquinaDetails.getId_Maquina()).execute().get();
                    //listBodega = mTableBodega.execute().get();
                    //listDetalleLectura = mTableDetalleLectura.where().field("id_Lectura").eq(listLectura.get(0).id).orderBy("posicion", QueryOrder.Ascending).execute().get();
                    listLectura = mLecturaTable.read(QueryOperations.tableName("Lectura").field("id_Maquina").eq(objMaquinaDetails.getId_Maquina())).get();
                    listBodega = mBodegaTable.read(QueryOperations.tableName("Bodega")).get();
                    listDetalleLectura = mDetalleLecturaTable.read(QueryOperations.tableName("Detalle_Lectura").field("id_Lectura").eq(listLectura.get(0).id).orderBy("posicion",QueryOrder.Ascending)).get();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            for (Detalle_Lectura detalle : listDetalleLectura) {
                                for (Bodega item : listBodega) {
                                    if (detalle.Id_Item.equals(item.id)) {
                                        mProductList.add(new Product(Integer.valueOf(detalle.Posicion),detalle.Id_Item , item.Name, Float.valueOf(detalle.Precio), 0,0));
                                    }
                                }
                            }

                            AsignarValores();
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

    private void AsignarValores(){
        for (Product product : mProductList) {
            switch (product.Posicion) {
                case 1:
                    txvProduct1.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 2:
                    txvProduct2.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 3:
                    txvProduct3.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 4:
                    txvProduct4.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 5:
                    txvProduct5.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 6:
                    txvProduct6.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 7:
                    txvProduct7.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 8:
                    txvProduct8.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 9:
                    txvProduct9.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 10:
                    txvProduct10.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 11:
                    txvProduct11.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 12:
                    txvProduct12.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 13:
                    txvProduct13.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 14:
                    txvProduct14.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 15:
                    txvProduct15.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 16:
                    txvProduct16.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 17:
                    txvProduct17.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 18:
                    txvProduct18.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 19:
                    txvProduct19.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 20:
                    txvProduct20.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 21:
                    txvProduct21.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 22:
                    txvProduct22.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 23:
                    txvProduct23.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 24:
                    txvProduct24.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 25:
                    txvProduct25.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 26:
                    txvProduct26.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 27:
                    txvProduct27.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 28:
                    txvProduct28.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 29:
                    txvProduct29.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                case 30:
                    txvProduct30.setText(product.Name + "\n" + "$" + String.valueOf(product.Price));
                    break;
                default:
                    break;
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
                            MobileService objMobileService = new MobileService(Snacks1.this);

                            for (Product product : mProductList){
                                if(!(product.Quatity == 0 && product.Availability == 0)) {
                                    Detalle_Lectura detalle_lectura = new Detalle_Lectura();
                                    detalle_lectura.Id_Lectura = listLectura.get(0).id;
                                    detalle_lectura.Id_Item = product.id;
                                    detalle_lectura.CantidadDisponible = product.Availability;
                                    detalle_lectura.CantidadAgregada = Float.valueOf(product.Quatity);
                                    detalle_lectura.Posicion = product.Posicion;
                                    detalle_lectura.Precio = product.Price;
                                    //objMobileService.insertLectureDetail(detalle_lectura);
                                    try {
                                        mDetalleLecturaTable.insert(detalle_lectura).get();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            startActivity(new Intent(Snacks1.this, MainActivity.class));
                            CleanVariables();
                        }
                    });
                }catch (Exception ex){}
                return null;
            }
        };
        return runAsyncTask(task);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Snacks1.this,MainActivity.class));
        productRegistered = false;
        CleanVariables();
        mProductList.clear();
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
        int n=0;
        MobileService objMobileService = new MobileService(this);
        switch (item.getItemId()){
            case R.id.actionSave:
                for (Product product : mProductList){
                    if(!(product.Quatity == 0 && product.Availability == 0)){
                        n++;
                    }
                }
                if (etxLecturaSnacks1.getText().toString().equals("")||etxAjustesSnacks1.getText().toString().equals("")) {
                    if (etxLecturaSnacks1.getText().toString().equals("")) {
                        etxLecturaSnacks1.setError("Ingresar el total de la lectura");
                    }
                    if (etxAjustesSnacks1.getText().toString().equals("")) {
                        etxAjustesSnacks1.setError("Ingresar el total de los ajustes");
                    }
                }else {
                        miSave.setVisible(false);
                        final Lectura lectura = new Lectura();
                        lectura.Id_Maquina = objMaquinaDetails.getId_Maquina();
                        lectura.NombreMaquina = objMaquinaDetails.getNombreMaquina();
                        lectura.Valor = Float.valueOf(etxLecturaSnacks1.getText().toString());
                        lectura.Reposiciones = Float.valueOf(etxAjustesSnacks1.getText().toString());
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

    private void changeSelectedProducts(){
        if(productRegistered1==true){
            txvProduct1.setTextColor(txvProduct1.getContext().getResources().getColor(R.color.forest));
            txvProduct1.setBackground(txvProduct1.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered2==true){
            txvProduct2.setTextColor(txvProduct2.getContext().getResources().getColor(R.color.forest));
            txvProduct2.setBackground(txvProduct2.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered3==true){
            txvProduct3.setTextColor(txvProduct3.getContext().getResources().getColor(R.color.forest));
            txvProduct3.setBackground(txvProduct3.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered4==true){
            txvProduct4.setTextColor(txvProduct4.getContext().getResources().getColor(R.color.forest));
            txvProduct4.setBackground(txvProduct4.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered5==true){
            txvProduct5.setTextColor(txvProduct5.getContext().getResources().getColor(R.color.forest));
            txvProduct5.setBackground(txvProduct5.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered6==true){
            txvProduct6.setTextColor(txvProduct6.getContext().getResources().getColor(R.color.forest));
            txvProduct6.setBackground(txvProduct6.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered7==true){
            txvProduct7.setTextColor(txvProduct7.getContext().getResources().getColor(R.color.forest));
            txvProduct7.setBackground(txvProduct7.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }if(productRegistered8==true){
            txvProduct8.setTextColor(txvProduct8.getContext().getResources().getColor(R.color.forest));
            txvProduct8.setBackground(txvProduct8.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }if(productRegistered9==true){
            txvProduct9.setTextColor(txvProduct9.getContext().getResources().getColor(R.color.forest));
            txvProduct9.setBackground(txvProduct9.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered10==true){
            txvProduct10.setTextColor(txvProduct10.getContext().getResources().getColor(R.color.forest));
            txvProduct10.setBackground(txvProduct10.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered11==true){
            txvProduct11.setTextColor(txvProduct11.getContext().getResources().getColor(R.color.forest));
            txvProduct11.setBackground(txvProduct11.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered12==true){
            txvProduct12.setTextColor(txvProduct12.getContext().getResources().getColor(R.color.forest));
            txvProduct12.setBackground(txvProduct12.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered13==true){
            txvProduct13.setTextColor(txvProduct13.getContext().getResources().getColor(R.color.forest));
            txvProduct13.setBackground(txvProduct13.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered14==true){
            txvProduct14.setTextColor(txvProduct14.getContext().getResources().getColor(R.color.forest));
            txvProduct14.setBackground(txvProduct14.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered15==true){
            txvProduct15.setTextColor(txvProduct15.getContext().getResources().getColor(R.color.forest));
            txvProduct15.setBackground(txvProduct15.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered16==true){
            txvProduct16.setTextColor(txvProduct16.getContext().getResources().getColor(R.color.forest));
            txvProduct16.setBackground(txvProduct16.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered17==true){
            txvProduct17.setTextColor(txvProduct17.getContext().getResources().getColor(R.color.forest));
            txvProduct17.setBackground(txvProduct17.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered18==true){
            txvProduct18.setTextColor(txvProduct18.getContext().getResources().getColor(R.color.forest));
            txvProduct18.setBackground(txvProduct18.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered19==true){
            txvProduct19.setTextColor(txvProduct19.getContext().getResources().getColor(R.color.forest));
            txvProduct19.setBackground(txvProduct19.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered20==true){
            txvProduct20.setTextColor(txvProduct20.getContext().getResources().getColor(R.color.forest));
            txvProduct20.setBackground(txvProduct20.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered21==true){
            txvProduct21.setTextColor(txvProduct21.getContext().getResources().getColor(R.color.forest));
            txvProduct21.setBackground(txvProduct21.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered22==true){
            txvProduct22.setTextColor(txvProduct22.getContext().getResources().getColor(R.color.forest));
            txvProduct22.setBackground(txvProduct22.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered23==true){
            txvProduct23.setTextColor(txvProduct23.getContext().getResources().getColor(R.color.forest));
            txvProduct23.setBackground(txvProduct23.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered24==true){
            txvProduct24.setTextColor(txvProduct24.getContext().getResources().getColor(R.color.forest));
            txvProduct24.setBackground(txvProduct24.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered25==true){
            txvProduct25.setTextColor(txvProduct25.getContext().getResources().getColor(R.color.forest));
            txvProduct25.setBackground(txvProduct25.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered26==true){
            txvProduct26.setTextColor(txvProduct26.getContext().getResources().getColor(R.color.forest));
            txvProduct26.setBackground(txvProduct26.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered27==true){
            txvProduct27.setTextColor(txvProduct27.getContext().getResources().getColor(R.color.forest));
            txvProduct27.setBackground(txvProduct27.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered28==true){
            txvProduct28.setTextColor(txvProduct28.getContext().getResources().getColor(R.color.forest));
            txvProduct28.setBackground(txvProduct28.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered29==true){
            txvProduct29.setTextColor(txvProduct29.getContext().getResources().getColor(R.color.forest));
            txvProduct29.setBackground(txvProduct29.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
        }
        if(productRegistered30==true){
            txvProduct30.setTextColor(txvProduct30.getContext().getResources().getColor(R.color.forest));
            txvProduct30.setBackground(txvProduct30.getContext().getResources().getDrawable(R.drawable.background_listview_maquinas));
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
