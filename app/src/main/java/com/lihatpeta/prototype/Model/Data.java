package com.lihatpeta.prototype.Model;

public class Data {

    private String id_rtlh, namartlh, dukungan, jarak, koordinat, oleh, fotortlh;

    public Data(String id_rtlh, String namartlh, String dukungan, String jarak, String koordinat, String oleh, String fotortlh) {
        this.id_rtlh = id_rtlh;
        this.namartlh = namartlh;
        this.dukungan = dukungan;
        this.jarak = jarak;
        this.koordinat = koordinat;
        this.oleh = oleh;
        this.fotortlh = fotortlh;
    }

    public String getIdrtlh() {
        return id_rtlh;
    }
    public String getNamartlh() {
        return namartlh;
    }
    public String getDukungan() {
        return dukungan;
    }
    public String getJarak() {
        return jarak;
    }
    public String getKoordinat() {
        return koordinat;
    }
    public String getOleh() {
        return oleh;
    }
    public String getFotortlh() {
        return fotortlh;
    }
}
