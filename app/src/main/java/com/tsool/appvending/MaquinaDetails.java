package com.tsool.appvending;

/**
 * Created by Diego on 26/01/2016.
 */
public class MaquinaDetails {
    private static String id_Maquina;
    private static String nombreMaquina;
    private static String id_LecturaActual;
    private static int tipoMaquina;

    public static String getId_Maquina() {
        return id_Maquina;
    }

    public static void setId_Maquina(String id_Maquina) {
        MaquinaDetails.id_Maquina = id_Maquina;
    }

    public static String getNombreMaquina() {
        return nombreMaquina;
    }

    public static void setNombreMaquina(String nombreMaquina) {
        MaquinaDetails.nombreMaquina = nombreMaquina;
    }

    public static String getId_LecturaActual() {
        return id_LecturaActual;
    }

    public static void setId_LecturaActual(String id_LecturaActual) {
        MaquinaDetails.id_LecturaActual = id_LecturaActual;
    }

    public static int getTipoMaquina() {
        return tipoMaquina;
    }

    public static void setTipoMaquina(int tipoMaquina) {
        MaquinaDetails.tipoMaquina = tipoMaquina;
    }
}
