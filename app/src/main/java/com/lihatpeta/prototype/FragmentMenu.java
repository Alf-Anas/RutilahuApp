package com.lihatpeta.prototype;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lihatpeta.prototype.Model.Data;
import com.lihatpeta.prototype.Model.DataAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class FragmentMenu extends Fragment {

    double myLat, myLon, pickLat, pickLon;
    boolean loading=false, PremiumAccess;
    ConstraintLayout constraintLayout;
    ScrollView scrollMenu;

    EditText editRadius;
    ImageView imgMidPoint;
    Button btnTampilkan, btnUnduh, btnPicker;
    TextView textNoData, textKoordinat, textGeser;
    double LatKM = (1 / 110.57);
    double LonKM = (1 / 111.32);
    int jumlahUser = 0, jumlahID=0, errorLoad=0;
    ProgressBar progressBar;
    JSONArray array = null;
    RadioButton radioLokasiSaya, radioPilihLokasi;

    ArrayList<String> noArray = new ArrayList<>();
    ArrayList<String> id_rtlhArray = new ArrayList<>();
    ArrayList<String> id_akunArray = new ArrayList<>();
    ArrayList<String> koordinatArray = new ArrayList<>();
    ArrayList<String> dukunganArray = new ArrayList<>();
    ArrayList<String> jarakArray = new ArrayList<>();
    ArrayList<String> fotoArray = new ArrayList<>();
    ArrayList<String> namaArray = new ArrayList<>();
    ArrayList<String> olehArray = new ArrayList<>();
    ArrayList<String> tanggalArray = new ArrayList<>();

    ArrayList<String> unameDifferArray = new ArrayList<>();
    ArrayList<String> id_DifferArray = new ArrayList<>();
    ArrayList<String> userSubmitArray = new ArrayList<>();
    ArrayList<Double> sortJarakArray = new ArrayList<>();
    ArrayList<Double> newJarakArray = new ArrayList<>();
    ArrayList<Integer> sortIntArray = new ArrayList<>();

    ArrayList<String> dataValidasiArray = new ArrayList<>();

    List<Data> dataList = new ArrayList<>();
    RecyclerView recyclerView;
    String DataFileName;
    int banyakFoto, fotoDownloaded;
    ArrayList<String> linkDownloadFoto = new ArrayList<>();
    ArrayList<String> namaFoto = new ArrayList<>();
    Runnable loadRunnable;

    private void errorVolley() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle(R.string.no_internet_connection);

        builder.setNegativeButton(R.string.text_tutup, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                errorLoad=0;
                loading=false;
                progressBar.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        radioLokasiSaya=view.findViewById(R.id.radioLokasiSaya);
        radioPilihLokasi=view.findViewById(R.id.radioPilihLokasi);
        editRadius=view.findViewById(R.id.editDataRadius);
        textNoData=view.findViewById(R.id.textDataNoData);
        textKoordinat=view.findViewById(R.id.textMenuKoordinat);
        btnTampilkan=view.findViewById(R.id.buttonDataTampilkan);
        btnUnduh=view.findViewById(R.id.buttonDataUnduh);
        recyclerView=view.findViewById(R.id.recyclerData);
        progressBar=view.findViewById(R.id.progressBarData);
        imgMidPoint=view.findViewById(R.id.imgMidPoint);
        btnPicker=view.findViewById(R.id.btnPicker);
        textGeser=view.findViewById(R.id.textGeserPeta);
        constraintLayout=view.findViewById(R.id.constraintMenu);
        scrollMenu=view.findViewById(R.id.scrollMenu);

        final MainActivity myAct = (MainActivity) getActivity();
        final BottomNavigationView navigation=Objects.requireNonNull(myAct).findViewById(R.id.navigation);
        radioLokasiSaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myLat= myAct.getMyLastLat();
                myLon= myAct.getMyLastLon();
                double dbLatitude = myLat;
                double dbLongitude = myLon;
                String strBujur, strLintang;
                if (dbLongitude>0){
                    strBujur=" BT";
                } else {
                    strBujur=" BB";
                    dbLongitude=dbLongitude*(-1); }
                if (dbLatitude>0){
                    strLintang=" LU";
                } else {
                    strLintang=" LS";
                    dbLatitude=dbLatitude*(-1); }
                String strKoordinat = dbLongitude +strBujur+", "+ dbLatitude +strLintang;
                textKoordinat.setText(strKoordinat);
            }
        });
        radioPilihLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollMenu.setVisibility(View.GONE);
                constraintLayout.setClickable(false);
                imgMidPoint.setVisibility(View.VISIBLE);
                btnPicker.setVisibility(View.VISIBLE);
                textGeser.setVisibility(View.VISIBLE);

                navigation.getMenu().getItem(1).setEnabled(false);
                navigation.getMenu().getItem(2).setEnabled(false);
            }
        });

        btnPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollMenu.setVisibility(View.VISIBLE);
                constraintLayout.setClickable(true);
                imgMidPoint.setVisibility(View.GONE);
                btnPicker.setVisibility(View.GONE);
                textGeser.setVisibility(View.GONE);

                pickLat= Objects.requireNonNull(myAct).getPickLat();
                pickLon= Objects.requireNonNull(myAct).getPickLon();
                navigation.getMenu().getItem(1).setEnabled(true);
                navigation.getMenu().getItem(2).setEnabled(true);

                double dbLatitude = pickLat;
                double dbLongitude = pickLon;
                String strBujur, strLintang;
                if (dbLongitude>0){
                    strBujur=" BT";
                } else {
                    strBujur=" BB";
                    dbLongitude=dbLongitude*(-1); }
                if (dbLatitude>0){
                    strLintang=" LU";
                } else {
                    strLintang=" LS";
                    dbLatitude=dbLatitude*(-1); }
                String strKoordinat = dbLongitude +strBujur+", "+ dbLatitude +strLintang;
                textKoordinat.setText(strKoordinat);
            }
        });

        btnTampilkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editRadius.getText().toString().equals("") && !loading){
                    noArray.clear();
                    id_rtlhArray.clear();
                    namaArray.clear();
                    dukunganArray.clear();
                    jarakArray.clear();
                    koordinatArray.clear();
                    id_akunArray.clear();
                    fotoArray.clear();
                    olehArray.clear();
                    tanggalArray.clear();
                    unameDifferArray.clear();
                    userSubmitArray.clear();
                    id_DifferArray.clear();
                    sortIntArray.clear();
                    sortJarakArray.clear();
                    newJarakArray.clear();
                    dataList.clear();
                    dataValidasiArray.clear();

                    String radius=editRadius.getText().toString();
                    textNoData.setText("Mencari Data...");
                    btnUnduh.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    if (radioPilihLokasi.isChecked()){
                        progressBar.setVisibility(View.VISIBLE);
                        loading=true;
                        loadData(radius, pickLat, pickLon);
                    }else if (radioLokasiSaya.isChecked()){
                        progressBar.setVisibility(View.VISIBLE);
                        loading=true;
                        loadData(radius, myLat, myLon);
                        setAkun();
                    }else {
                        Toast.makeText(getActivity(),"Gagal Mendapatkan Lokasi Perangkat",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(),"Masukan Radius",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnUnduh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    DownloadPhotos();
                    }
                else {
                    Toast.makeText(getActivity(),"Anda Tidak Mengizinkan Akses Penyimpanan",Toast.LENGTH_LONG).show(); }
            }
        });
        return view;
    }

    private void saveFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        DataFileName = "DataRTLH_"+timeStamp;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "DataRTLH" + File.separator + DataFileName + File.separator);
        if (!storageDir.exists()){
            storageDir.mkdirs(); }
        try {
            File DataCSV = new File(storageDir, DataFileName+".csv");
            FileOutputStream fOut=new FileOutputStream(DataCSV);
            String entryHeader="Nomor,Id_RTLH,Oleh,Lintang,Bujur,Tanggal,Link_Foto,Nama_Penghuni,Desa/Kel,Kecamatan,Kab/Kota,Provinsi,KodePos,Deskripsi," +
                    "Nama_Lengkap,Usia(Tahun),Pendidikan_Terakhir,Jenis_Kelamin,Alamat_Lengkap,Nomor_KTP,Jumlah_KK,Pekerjaan_Utama,Penghasilan/Pengeluaran_Perbulan,Status_Kepemilikan_Tanah,Status_Kepemilikan_Rumah,Aset_Rumah_Ditempat_Lain,Aset_Tanah_Ditempat_Lain,Pernah_Mendapatkan_Bantuan_Perumahan,Jenis_Kawasan_Loksai_Rumah_yang_Ditempati," +
                    "Pondasi,Kondisi_Kolom_dan_Balok,Kondisi_Konstruksi_Atap,Jendela/Lubang_Cahaya,Ventilasi,Kepemilikan_Kamar_Mandi_dan_Jamban,Jarak_Sumber_Air_Minum_ke_TPA_Tinja,Sumber_Air_Minum,Sumber_Listrik,Luas_Rumah(M2),Jumlah_Penghuni(Orang),Material_Atap_Terluas,Kondisi_Atap,Material_Dinding_Terluas,Kondisi_Dinding,Material_Lantai_Terluas,Kondisi_Lantai,Dukungan_Masyarakat,Di_Validasi_Oleh"+"\n";
            int Nomor=0;
            if (PremiumAccess){
                fOut.write(entryHeader.getBytes());

                for(int i = 0; i < array.length(); i++) {
                    JSONObject dataRTLH = array.getJSONObject(i);
                    Nomor++;
                    String entryRTLH=Nomor+","+
                            dataRTLH.getString("id_rtlh")+","+
                            userSubmitArray.get(i)+","+
                            dataRTLH.getString("lat")+","+
                            dataRTLH.getString("lon")+","+
                            dataRTLH.getString("tanggal")+","+
                            dataRTLH.getString("foto")+",\""+
                            dataRTLH.getString("nama_penghuni")+"\",\""+
                            dataRTLH.getString("desa_kel")+"\",\""+
                            dataRTLH.getString("kecamatan")+"\",\""+
                            dataRTLH.getString("kab_kota")+"\",\""+
                            dataRTLH.getString("provinsi")+"\",\""+
                            dataRTLH.getString("kodepos")+"\",\""+
                            dataRTLH.getString("deskripsi")+"\",\""+
                            dataRTLH.getString("nama_lengkap")+"\","+
                            dataRTLH.getString("usia")+","+
                            dataRTLH.getString("pendidikan_terakhir")+","+
                            dataRTLH.getString("jenis_kelamin")+",\""+
                            dataRTLH.getString("alamat_lengkap")+"\","+
                            dataRTLH.getString("nomor_ktp")+","+
                            dataRTLH.getString("jumlah_kk")+","+
                            dataRTLH.getString("pekerjaan_utama")+","+
                            dataRTLH.getString("penghasilan_bulan")+","+
                            dataRTLH.getString("status_tanah")+","+
                            dataRTLH.getString("status_rumah")+","+
                            dataRTLH.getString("aset_rumah_lain")+","+
                            dataRTLH.getString("aset_tanah_lain")+","+
                            dataRTLH.getString("bantuan_perumahan")+","+
                            dataRTLH.getString("jenis_kawasan")+","+
                            dataRTLH.getString("pondasi")+","+
                            dataRTLH.getString("kondisi_kolom")+","+
                            dataRTLH.getString("kondisi_konstruksi_atap")+","+
                            dataRTLH.getString("jendela")+","+
                            dataRTLH.getString("ventilasi")+","+
                            dataRTLH.getString("kamar_mandi")+","+
                            dataRTLH.getString("sumber_air")+","+
                            dataRTLH.getString("air_minum")+","+
                            dataRTLH.getString("listrik")+","+
                            dataRTLH.getString("luas_rumah")+","+
                            dataRTLH.getString("jumlah_penghuni")+","+
                            dataRTLH.getString("material_atap")+","+
                            dataRTLH.getString("kondisi_atap")+","+
                            dataRTLH.getString("material_dinding")+","+
                            dataRTLH.getString("kondisi_dinding")+","+
                            dataRTLH.getString("material_lantai")+","+
                            dataRTLH.getString("kondisi_lantai")+","+
                            dukunganArray.get(i)+","+
                            dataValidasiArray.get(i)+","+
                            "\n";
                    fOut.write(entryRTLH.getBytes());
                }
            } else {
                fOut.write(entryHeader.getBytes());
                for(int i = 0; i < array.length(); i++) {
                    JSONObject dataRTLH = array.getJSONObject(i);
                    Nomor++;
                    String entryRTLH=Nomor+","+
                            dataRTLH.getString("id_rtlh")+","+
                            userSubmitArray.get(i)+","+
                            dataRTLH.getString("lat")+","+
                            dataRTLH.getString("lon")+","+
                            dataRTLH.getString("tanggal")+","+
                            dataRTLH.getString("foto")+",\""+
                            dataRTLH.getString("nama_penghuni")+"\",\""+
                            dataRTLH.getString("desa_kel")+"\",\""+
                            dataRTLH.getString("kecamatan")+"\",\""+
                            dataRTLH.getString("kab_kota")+"\",\""+
                            dataRTLH.getString("provinsi")+"\",\""+
                            dataRTLH.getString("kodepos")+"\",\""+
                            dataRTLH.getString("deskripsi")+"\",\""+
                            "*"+"\","+
                            "*"+","+
                            "*"+","+
                            "*"+",\""+
                            "*"+"\","+
                            "*"+","+
                            "*"+","+
                            "*"+","+
                            "*"+","+
                            "*"+","+
                            "*"+","+
                            "*"+","+
                            "*"+","+
                            "*"+","+
                            "*"+","+
                            dataRTLH.getString("pondasi")+","+
                            dataRTLH.getString("kondisi_kolom")+","+
                            dataRTLH.getString("kondisi_konstruksi_atap")+","+
                            dataRTLH.getString("jendela")+","+
                            dataRTLH.getString("ventilasi")+","+
                            dataRTLH.getString("kamar_mandi")+","+
                            dataRTLH.getString("sumber_air")+","+
                            dataRTLH.getString("air_minum")+","+
                            dataRTLH.getString("listrik")+","+
                            dataRTLH.getString("luas_rumah")+","+
                            dataRTLH.getString("jumlah_penghuni")+","+
                            dataRTLH.getString("material_atap")+","+
                            dataRTLH.getString("kondisi_atap")+","+
                            dataRTLH.getString("material_dinding")+","+
                            dataRTLH.getString("kondisi_dinding")+","+
                            dataRTLH.getString("material_lantai")+","+
                            dataRTLH.getString("kondisi_lantai")+","+
                            dukunganArray.get(i)+","+
                            dataValidasiArray.get(i)+","+
                            "\n";
                    fOut.write(entryRTLH.getBytes());
                }
            }
            fOut.close();
            Toast.makeText(getActivity(),"Data RTLH Berhasil disimpan di Folder Download",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void DownloadPhotos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle("Mengunduh Data")
        .setMessage("Data RTLH akan disimpan dalam Format .csv di Folder Download pada Penyimpanan Internal \n" +
                "\n" +
                "Direkomendasikan untuk menggunakan Jaringan Wi-Fi");

        builder.setPositiveButton("Data CSV & Foto", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveFile();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"Mengunduh Foto...",Toast.LENGTH_LONG).show();
                        banyakFoto=0;
                        linkDownloadFoto.clear();
                        namaFoto.clear();
                        saveFotoFromURL();
                    }
                }, 500);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Hanya Data CSV", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveFile();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveFotoFromURL() {
        final String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Foto.php?dataid="+id_rtlhArray.get(banyakFoto);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            for(int i = 0; i < array.length(); i++){
                                JSONObject datafoto = array.getJSONObject(i);
                                if (datafoto.getString("tag").equals("10") && !PremiumAccess){
                                    linkDownloadFoto.add("https://rtlh-poi.000webhostapp.com/Android-Conn/Foto_RTLH/errorimage.png");
                                } else {
                                    linkDownloadFoto.add(datafoto.getString("link_foto"));
                                }
                                namaFoto.add(id_rtlhArray.get(banyakFoto)+"__"+ i);
                            }
                            banyakFoto++;
                            if (banyakFoto<id_rtlhArray.size()){
                                saveFotoFromURL();
                            } else {
                                fotoDownloaded=0;
                                saveFotoToInternal();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(stringRequest);
    }

    private void saveFotoToInternal() {
        Picasso.get().load(linkDownloadFoto.get(fotoDownloaded)).error(R.drawable.errorimage).into(target);
        final int oldFotoDownloaded=fotoDownloaded;

        final Handler loadHandler = new Handler();
        loadRunnable=new Runnable() {
            @Override
            public void run() {
                if (fotoDownloaded!=oldFotoDownloaded){
                    Toast.makeText(getActivity()," "+fotoDownloaded+"/"+linkDownloadFoto.size()+" Foto",Toast.LENGTH_SHORT).show();
                    if (fotoDownloaded<linkDownloadFoto.size()){
                        saveFotoToInternal();
                    }else {
                        Toast.makeText(getActivity(),"Foto-foto Berhasil diunduh",Toast.LENGTH_LONG).show(); }
                } else {
                    loadHandler.postDelayed(loadRunnable, 600);
                }
            }
        };
        loadHandler.postDelayed(loadRunnable, 600);
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "DataRTLH" + File.separator + DataFileName + File.separator + "Foto"  + File.separator);
                    if (!storageDir.exists()){
                        storageDir.mkdirs(); }
                    File file = new File(storageDir, namaFoto.get(fotoDownloaded)+".jpg");
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,50,ostream);
                        ostream.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    fotoDownloaded++;
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };

    private void loadData(final String radius, final double lastLat, final double lastLon) {
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("AKUN_SETTING", MODE_PRIVATE);
        PremiumAccess = prefs.getBoolean("PremiumAccess", false);

        double RadiusLat=Double.parseDouble(radius)*LatKM;
        double RadiusLon=Double.parseDouble(radius)*LonKM;

        double minlat=lastLat-RadiusLat;
        double maxlat=lastLat+RadiusLat;
        double minlon=lastLon-RadiusLon;
        double maxlon=lastLon+RadiusLon;

        final String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Data_RTLH.php?minlat="+minlat+"&maxlat="+maxlat+"&minlon="+minlon+"&maxlon="+maxlon;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            array = new JSONArray(response);
                            for(int i = 0; i < array.length(); i++){
                                JSONObject dataRTLH = array.getJSONObject(i);

                                noArray.add(dataRTLH.getString("no"));
                                id_rtlhArray.add(dataRTLH.getString("id_rtlh"));
                                id_akunArray.add(dataRTLH.getString("id_akun"));
                                id_DifferArray.add(dataRTLH.getString("id_akun"));

                                double dbLatitude = dataRTLH.getDouble("lat");
                                double dbLongitude = dataRTLH.getDouble("lon");
                                double xLat=(lastLat-dbLatitude)/LatKM;
                                double xLon=(lastLon-dbLongitude)/LonKM;
                                double jarak=Math.floor(Math.sqrt((xLat*xLat)+(xLon*xLon))*100)/100;
                                double realjarak=Math.sqrt((xLat*xLat)+(xLon*xLon));

                                String strBujur, strLintang;
                                if (dbLongitude>0){
                                    strBujur=" BT";
                                } else {
                                    strBujur=" BB";
                                    dbLongitude=dbLongitude*(-1); }
                                if (dbLatitude>0){
                                    strLintang=" LU";
                                } else {
                                    strLintang=" LS";
                                    dbLatitude=dbLatitude*(-1); }
                                String strKoordinat = dbLongitude +strBujur+", "+ dbLatitude +strLintang;

                                koordinatArray.add(strKoordinat);
                                jarakArray.add("Jarak = "+ jarak +" Km");
                                tanggalArray.add(dataRTLH.getString("tanggal"));
                                fotoArray.add(dataRTLH.getString("foto"));
                                namaArray.add(dataRTLH.getString("nama_penghuni"));
                                sortJarakArray.add(realjarak);
                                newJarakArray.add(realjarak);
                            }
                            if (noArray.size()!=0) {
                                textNoData.setText("*Ditemukan Sebanyak "+String.valueOf(noArray.size()) + " Data RTLH");
                                HashSet<String> clearId=new HashSet<>();
                                clearId.addAll(id_DifferArray);
                                id_DifferArray.clear();
                                id_DifferArray.addAll(clearId);
                                loadUserReview();
                            }else {
                                recyclerView.setVisibility(View.GONE);
                                textNoData.setText("*Tidak Ada Data dalam Radius ini");
                                progressBar.setVisibility(View.GONE);
                                loading=false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                        errorLoad++;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (errorLoad>=5){
                                    errorVolley();
                                }else {
                                    loadData(radius, lastLat, lastLon);
                                }
                            }
                        }, 2000);

                    }
                });
        Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(stringRequest);
    }

    private void loadUserReview() {
        String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Akun.php?id_akun="+id_DifferArray.get(jumlahUser);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject akunData = array.getJSONObject(0);
                            unameDifferArray.add(akunData.getString("username"));
                            jumlahUser++;
                            if (jumlahUser<id_DifferArray.size()){
                                loadUserReview();
                            } else {
                                jumlahUser=0;
                                loadDukungan();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getActivity(),R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                        errorLoad++;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (errorLoad>=5){
                                    errorVolley();
                                }else {
                                    loadUserReview();
                                }
                            }
                        }, 2000);
                    }
                });
        Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(stringRequest);
    }

    private void loadDukungan() {
        final String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Review.php?dataid="+id_rtlhArray.get(jumlahID);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            int jumlahDukungan=0;
                            int jumlahPenolakan=0;
                            int jumlahValidasi=0;
                            String diValidasiOleh="";
                            for(int i = 0; i < array.length(); i++){
                                JSONObject dataReview = array.getJSONObject(i);
                                if (dataReview.getString("dukungan").equals("TOLAK")){
                                    jumlahPenolakan++;
                                } else if (dataReview.getString("dukungan").equals("DUKUNG")){
                                    jumlahDukungan++;
                                } else if (dataReview.getString("dukungan").equals("VALID")){
                                    jumlahValidasi++;
                                    diValidasiOleh=diValidasiOleh + dataReview.getString("class_name")+"-"+dataReview.getString("tanggal")+"_ ";
                                }
                            }
                            String strDukungan="Validasi : "+ jumlahValidasi  + " | Dukungan : "+ jumlahDukungan + " | Penolakan : "+ jumlahPenolakan;
                            dukunganArray.add(strDukungan);
                            dataValidasiArray.add(diValidasiOleh);
                            jumlahID++;
                            if (jumlahID<id_rtlhArray.size()){
                                loadDukungan();}
                            else {
                                loadAdapter();
                                jumlahID=0; }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(stringRequest);
    }

    private void loadAdapter() {
        Collections.sort(sortJarakArray);
        for (int i = 0; i < sortJarakArray.size(); i++) {
            for (int j = 0; j < newJarakArray.size(); j++) {
                if (sortJarakArray.get(i).equals(newJarakArray.get(j))){
                    sortIntArray.add(j);
                }
            }
        }

        for (int i = 0; i < noArray.size(); i++) {
            for (int j = 0; j < id_DifferArray.size(); j++) {
                if (id_akunArray.get(i).equals(id_DifferArray.get(j))){
                    userSubmitArray.add(unameDifferArray.get(j));
                    olehArray.add("Oleh : @"+unameDifferArray.get(j)+", Pada : "+tanggalArray.get(i));
                }
            }
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setVisibility(View.VISIBLE);
        for(int i = 0; i < noArray.size(); i++){
            int j = sortIntArray.get(i);
            dataList.add(new Data(
                    id_rtlhArray.get(j),
                    namaArray.get(j),
                    dukunganArray.get(j),
                    jarakArray.get(j),
                    koordinatArray.get(j),
                    olehArray.get(j),
                    fotoArray.get(j)
            ));
        }
        DataAdapter adapter = new DataAdapter(getActivity(), dataList);
        recyclerView.setAdapter(adapter);
        loading=false;
        btnUnduh.setText("Unduh Data");
        btnUnduh.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void setAkun() {
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("AKUN_SETTING", MODE_PRIVATE);
        final String strId_Akun = prefs.getString("id_akun", "");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String strDate = sdf.format(c.getTime());

        String UploadURL="https://rtlh-poi.000webhostapp.com/Android-Conn/Set_Akun.php";
        StringRequest stringRequest =new StringRequest(Request.Method.POST, UploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("id_akun", Objects.requireNonNull(strId_Akun));
                params.put("akses_terakhir",strDate);
                params.put("lat", String.valueOf(myLat));
                params.put("lon", String.valueOf(myLon));
                return params;
            }
        };
        ProfileUpload.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
