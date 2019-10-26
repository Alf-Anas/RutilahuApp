package com.lihatpeta.prototype.Model;

public class Review {

    private int no;
    private String username, tanggal, pendapat, dukungan, fotoprofil;

    public Review(int no, String username, String tanggal, String pendapat, String dukungan, String fotoprofil) {
        this.no = no;
        this.username = username;
        this.tanggal = tanggal;
        this.pendapat = pendapat;
        this.dukungan = dukungan;
        this.fotoprofil = fotoprofil;
    }

    public int getNo() {
        return no;
    }
    public String getUsername() {
        return username;
    }
    public String getTanggal() {
        return tanggal;
    }
    public String getPendapat() {
        return pendapat;
    }
    public String getDukungan() {
        return dukungan;
    }
    public String getFotoprofil() {
        return fotoprofil;
    }
}
