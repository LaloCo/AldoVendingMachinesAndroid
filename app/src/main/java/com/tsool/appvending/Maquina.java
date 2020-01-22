package com.tsool.appvending;

/**
 * Created by Diego on 22/01/2016.
 */
public class Maquina {
    public String id;
    public String Name;
    public int Kind;
    public float FirstRead;
    public float PrecioCafe;
    public int TipoParticipacion;
    public float ValorParticipacion;

    public Maquina() {
    }

    public Maquina(String name, int kind, int firstRead, int precioCafe) {
        this.Name = name;
        this.Kind = kind;
        this.FirstRead = firstRead;
        this.PrecioCafe = precioCafe;
    }

    public Maquina(String name, int kind) {
        Name = name;
        Kind = kind;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getKind() {
        return Kind;
    }

    public void setKind(int kind) {
        Kind = kind;
    }
}
