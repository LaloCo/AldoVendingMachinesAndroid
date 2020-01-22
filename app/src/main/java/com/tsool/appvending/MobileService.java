package com.tsool.appvending;

import android.content.Context;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

/**
 * Created by Diego on 21/01/2016.
 */
public class MobileService {

    MobileServiceClient mClient;

    public MobileService() {
    }

    public MobileService(Context context)
    {
        connection(context);
    }

    public MobileServiceClient connection(Context context)
    {
        try
        {
            mClient = new MobileServiceClient(
                    "https://vendingmachines.azurewebsites.net/",
                    context
            );
        }catch (Exception ex){}
        return mClient;
    }

    public void InsertMachine(Maquina maquina){
        mClient.getTable(Maquina.class).insert(maquina);
    }

    public void InsertWarehouse(Bodega bodega){
        mClient.getTable(Bodega.class).insert(bodega);
    }

    public void insertLecture(Lectura lectura){
        mClient.getTable(Lectura.class).insert(lectura);
    }

    public void insertLectureDetail(Detalle_Lectura detalle_lectura){
        mClient.getTable(Detalle_Lectura.class).insert(detalle_lectura);
    }

    public void UpdateWarehouse(Bodega bodega){
        mClient.getTable(Bodega.class).update(bodega);
    }

}
