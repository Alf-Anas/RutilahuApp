package com.lihatpeta.prototype;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.chrisbanes.photoview.PhotoView;
import com.lihatpeta.prototype.Model.Review;
import com.lihatpeta.prototype.Model.ReviewAdapter;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textFont;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textHaloColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textHaloWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class RTLHShow extends AppCompatActivity implements View.OnClickListener {
    MapView mapView;
    private MapboxMap mapboxMap;
    SymbolLayer pointlayer;
    AlertDialog Editdialog;
    String editLat, editLon, strlihatfoto="Lihat Foto", strClassname, strMyUname;
    Button btnSimpanLoc;
    ProgressBar editProgress;
    boolean FirstLoad=true;
    boolean EditData=false;
    boolean firstMapLoad=true;
    static final int REQUEST_TAKE_PHOTO = 1, REQUEST_IMAGE_CAPTURE = 1;
    String imageFileName, currentPhotoPath, strLinkFotoPicasso="";
    String dataid, my_akun, userSubmit, userSubmitId, FotoTag;
    Bitmap bitmaps;
    ArrayList<String> FotoPathList = new ArrayList<>();
    ArrayList<String> NamePathList = new ArrayList<>();
    ArrayList<String> idFotoList = new ArrayList<>();
    int JumlahFoto, FotoUploaded=0,  errorLoad=0, volleyNumber, errorDukung=0, errorUname=0;
    double latLoc, lonLoc;
    ArrayList<String> linkFotoPicasso = new ArrayList<>();

    LinearLayout layoutImage, opsionalLayout, RTLHOpsional;
    HorizontalScrollView hrScroll;
    BottomNavigationView submitnavigation;

    TextView uploadGambarText, uploadDataText, simpanDataText;

    JSONObject showdata, datafoto;
    AlertDialog dialog;
    List<Review> reviewList;
    RecyclerView recyclerView;
    ArrayList<String> noArray = new ArrayList<>();
    ArrayList<String> id_akunArray = new ArrayList<>();
    ArrayList<String> usernameArray = new ArrayList<>();
    ArrayList<String> tanggalArray = new ArrayList<>();
    ArrayList<String> reviewArray = new ArrayList<>();
    ArrayList<String> dukunganArray = new ArrayList<>();
    ArrayList<String> fotoArray = new ArrayList<>();
    ArrayList<String> FotoTagList = new ArrayList<>();

    ArrayList<String> id_DifferArray = new ArrayList<>();
    ArrayList<String> unameDifferArray = new ArrayList<>();
    ArrayList<String> fotoDifferArray = new ArrayList<>();
    int jumlahUser=0, popupImageInt;
    Boolean PremiumAccess;

    ProgressBar progressBar, progressBarDukungan;
    TextView textWajib, textOpsional, txtKoordinat, txtOleh, textDukungan, textPenolakan, textValidasi, textNoData, textEditOleh, textHideIdentitas;
    Button Ambil_Foto, Edit_Loc, Hapus_Data;
    Button foto_ktp, foto_kondisikolom, foto_konstruksiatap, foto_kamarmandi, foto_airminum, foto_listrik, foto_materialatap, foto_kondisiatap, foto_materialdinding, foto_kondisidinding, foto_materiallantai, foto_kondisilantai;
    Integer link_foto_ktp, link_foto_kondisikolom, link_foto_konstruksiatap, link_foto_kamarmandi, link_foto_airminum, link_foto_listrik, link_foto_materialatap, link_foto_kondisiatap, link_foto_materialdinding, link_foto_kondisidinding, link_foto_materiallantai, link_foto_kondisilantai;
    TableLayout tabelIdentitas;
    EditText Nama_Penghuni, Desa_Kel, Kecamatan, Kab_Kota, Provinsi, Nama_Lengkap, Usia, Alamat_Lengkap, Nomor_KTP,
            Jumlah_KK, Luas_Rumah, Jumlah_Penghuni, KodePos, Deskripsi;
    RadioGroup Pend_Terakhir, Jenis_Kelamin, Peker_Utama, Penghasilan, Status_Tanah, Status_Rumah, Aset_Rumah, Aset_Tanah, Bantuan_Perumahan, Jenis_Kawasan,
            Pondasi, Kondisi_Kolom, Konstruksi_Atap, Jendela, Ventilasi, Kamar_Mandi, Jarak_TPA, Air_Minum, Listrik, Material_Atap, Kondisi_Atap,
            Material_Dinding, Kondisi_Dinding, Material_Lantai, Kondisi_Lantai;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_back:
                    if (EditData){ backButton(); }
                    else { finish(); }
                    return true;
                case R.id.navigation_refresh:
                    if (FirstLoad){ FirstLoad=false; }
                    else { DukunganMasyarakat(); }
                    return true;
                case R.id.navigation_submit:
                    if (!EditData){ editData(); }
                    else { kirimDataBaru(); }
                    return true;
            }
            return false;
        }
    };

    private void backButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Semua Data yang telah Anda Masukan Akan Hilang")
                .setTitle("KEMBALI");
        builder.setPositiveButton(R.string.text_kembali, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.text_batal, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void kirimDataBaru() {
        if (!Nama_Penghuni.getText().toString().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Anda Yakin Ingin Mengirim Data Pembaharuan?")
                    .setTitle("KIRIM DATA TERBARU");
            builder.setPositiveButton(R.string.title_submit, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    JumlahFoto = FotoPathList.size();
                    upLoading();
                }
            });
            builder.setNegativeButton(R.string.text_batal, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            Toast.makeText(RTLHShow.this, "Lengkapi Isian Wajib", Toast.LENGTH_LONG).show(); }
    }

    private void errorVolley() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.no_internet_connection);

        builder.setPositiveButton(R.string.text_cobalagi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                errorLoad=0;
                if (volleyNumber==1){
                    kirimDataPembaharuan();
                } else if (volleyNumber==2){
                    uploadImage();
                }
            }
        });
        builder.setNegativeButton(R.string.text_keluar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor editor = getSharedPreferences("FIRSTINSTALL", MODE_PRIVATE).edit();
                editor.putBoolean("reloadrtlh", true).apply();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    private void upLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View uploadView=getLayoutInflater().inflate(R.layout.upload_layout,null);
        uploadGambarText=uploadView.findViewById(R.id.textUploadGambar);
        uploadDataText=uploadView.findViewById(R.id.textPostRTLH);
        simpanDataText=uploadView.findViewById(R.id.textSetRTLH);

        uploadDataText.setVisibility(View.GONE);

        builder.setView(uploadView);

        final AlertDialog dialog=builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        kirimDataPembaharuan();
    }

    private void kirimDataPembaharuan() {
        String UploadURL="https://rtlh-poi.000webhostapp.com/Android-Conn/Set_RTLH.php";
        StringRequest stringRequest =new StringRequest(Request.Method.POST, UploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(RTLHShow.this,Response,Toast.LENGTH_SHORT).show();
                    simpanDataText.setText("Menyimpan Data : OK");
                    uploadFoto();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RTLHShow.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                volleyNumber=1;
                errorLoad++;
                Handler loadHandler = new Handler();
                Runnable loadRunnable=new Runnable() {
                    @Override
                    public void run() {
                        if (errorLoad>=5){
                            errorVolley();
                        } else {
                            kirimDataPembaharuan();
                        }
                    }
                };
                loadHandler.postDelayed(loadRunnable, 3000);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();

                params.put("id_akun", my_akun);
                params.put("id_rtlh",dataid);
                params.put("nama_penghuni",Nama_Penghuni.getText().toString());
                params.put("desa_kel",Desa_Kel.getText().toString());
                params.put("kecamatan",Kecamatan.getText().toString());
                params.put("kab_kota",Kab_Kota.getText().toString());
                params.put("provinsi",Provinsi.getText().toString());
                params.put("kodepos",KodePos.getText().toString());
                params.put("deskripsi",Deskripsi.getText().toString());
                params.put("nama_lengkap",Nama_Lengkap.getText().toString());
                params.put("usia",Usia.getText().toString());
                params.put("alamat_lengkap",Alamat_Lengkap.getText().toString());
                params.put("nomor_ktp",Nomor_KTP.getText().toString());
                params.put("jumlah_kk",Jumlah_KK.getText().toString());
                params.put("luas_rumah",Luas_Rumah.getText().toString());
                params.put("jumlah_penghuni",Jumlah_Penghuni.getText().toString());
                params.put("disabled_edit","0");
                params.put("pengedit",my_akun);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdf.format(c.getTime());
                params.put("tanggal_edit",strDate);

                int PendidikanId = Pend_Terakhir.getCheckedRadioButtonId();
                int JenisKelaminId = Jenis_Kelamin.getCheckedRadioButtonId();
                int PekerjaanId = Peker_Utama.getCheckedRadioButtonId();
                int PenghasilanId = Penghasilan.getCheckedRadioButtonId();
                int StatusTanahId = Status_Tanah.getCheckedRadioButtonId();
                int StatusRumahId = Status_Rumah.getCheckedRadioButtonId();
                int AsetRumahId = Aset_Rumah.getCheckedRadioButtonId();
                int AsetTanahId = Aset_Tanah.getCheckedRadioButtonId();
                int BantuanPerumahanId = Bantuan_Perumahan.getCheckedRadioButtonId();
                int JenisKawasanId = Jenis_Kawasan.getCheckedRadioButtonId();
                int PondasiId = Pondasi.getCheckedRadioButtonId();
                int KondisiKolomId = Kondisi_Kolom.getCheckedRadioButtonId();
                int KonstruksiAtapId = Konstruksi_Atap.getCheckedRadioButtonId();
                int JendelaId = Jendela.getCheckedRadioButtonId();
                int VentilasiId = Ventilasi.getCheckedRadioButtonId();
                int KamarMandiId = Kamar_Mandi.getCheckedRadioButtonId();
                int JarakTPAId = Jarak_TPA.getCheckedRadioButtonId();
                int AirMinumId = Air_Minum.getCheckedRadioButtonId();
                int ListrikId = Listrik.getCheckedRadioButtonId();
                int MaterialAtapId = Material_Atap.getCheckedRadioButtonId();
                int KondisiAtapId = Kondisi_Atap.getCheckedRadioButtonId();
                int MaterialDindingId = Material_Dinding.getCheckedRadioButtonId();
                int KondisiDindingId = Kondisi_Dinding.getCheckedRadioButtonId();
                int MaterialLantaiId = Material_Lantai.getCheckedRadioButtonId();
                int KondisiLantaiId = Kondisi_Lantai.getCheckedRadioButtonId();

                if (PendidikanId!=-1){
                    RadioButton PendidikanRadio=findViewById(PendidikanId);
                    params.put("pendidikan_terakhir",PendidikanRadio.getText().toString());}
                else { params.put("pendidikan_terakhir",""); }
                if (JenisKelaminId!=-1){
                    RadioButton JenisKelaminRadio=findViewById(JenisKelaminId);
                    params.put("jenis_kelamin",JenisKelaminRadio.getText().toString());}
                else { params.put("jenis_kelamin",""); }
                if (PekerjaanId!=-1){
                    RadioButton PekerjaanRadio=findViewById(PekerjaanId);
                    params.put("pekerjaan_utama",PekerjaanRadio.getText().toString());}
                else { params.put("pekerjaan_utama",""); }
                if (PenghasilanId!=-1){
                    RadioButton PenghasilanRadio=findViewById(PenghasilanId);
                    params.put("penghasilan_bulan",PenghasilanRadio.getText().toString());}
                else { params.put("penghasilan_bulan",""); }
                if (StatusTanahId!=-1){
                    RadioButton StatusTanahRadio=findViewById(StatusTanahId);
                    params.put("status_tanah",StatusTanahRadio.getText().toString());}
                else { params.put("status_tanah",""); }
                if (StatusRumahId!=-1){
                    RadioButton StatusRumahRadio=findViewById(StatusRumahId);
                    params.put("status_rumah",StatusRumahRadio.getText().toString());}
                else { params.put("status_rumah",""); }
                if (AsetRumahId!=-1){
                    RadioButton AsetRumahRadio=findViewById(AsetRumahId);
                    params.put("aset_rumah_lain",AsetRumahRadio.getText().toString());}
                else { params.put("aset_rumah_lain",""); }
                if (AsetTanahId!=-1){
                    RadioButton AsetTanahRadio=findViewById(AsetTanahId);
                    params.put("aset_tanah_lain",AsetTanahRadio.getText().toString());}
                else { params.put("aset_tanah_lain",""); }
                if (BantuanPerumahanId!=-1){
                    RadioButton BantuanPerumahanRadio=findViewById(BantuanPerumahanId);
                    params.put("bantuan_perumahan",BantuanPerumahanRadio.getText().toString());}
                else { params.put("bantuan_perumahan",""); }
                if (JenisKawasanId!=-1){
                    RadioButton JenisKawasanRadio=findViewById(JenisKawasanId);
                    params.put("jenis_kawasan",JenisKawasanRadio.getText().toString());}
                else { params.put("jenis_kawasan",""); }
                if (PondasiId!=-1){
                    RadioButton PondasiRadio=findViewById(PondasiId);
                    params.put("pondasi",PondasiRadio.getText().toString()); }
                else { params.put("pondasi",""); }
                if (KondisiKolomId!=-1){
                    RadioButton KondisiKolomRadio=findViewById(KondisiKolomId);
                    params.put("kondisi_kolom",KondisiKolomRadio.getText().toString());}
                else { params.put("kondisi_kolom",""); }
                if (KonstruksiAtapId!=-1){
                    RadioButton KonstruksiAtapRadio=findViewById(KonstruksiAtapId);
                    params.put("kondisi_konstruksi_atap",KonstruksiAtapRadio.getText().toString());}
                else { params.put("kondisi_konstruksi_atap",""); }
                if (JendelaId!=-1){
                    RadioButton JendelaRadio=findViewById(JendelaId);
                    params.put("jendela",JendelaRadio.getText().toString());}
                else { params.put("jendela",""); }
                if (VentilasiId!=-1){
                    RadioButton VentilasiRadio=findViewById(VentilasiId);
                    params.put("ventilasi",VentilasiRadio.getText().toString());}
                else { params.put("ventilasi",""); }
                if (KamarMandiId!=-1){
                    RadioButton KamarMandiRadio=findViewById(KamarMandiId);
                    params.put("kamar_mandi",KamarMandiRadio.getText().toString());}
                else { params.put("kamar_mandi",""); }
                if (JarakTPAId!=-1){
                    RadioButton JarakTPARadio=findViewById(JarakTPAId);
                    params.put("sumber_air",JarakTPARadio.getText().toString());}
                else { params.put("sumber_air",""); }
                if (AirMinumId!=-1){
                    RadioButton AirMinumRadio=findViewById(AirMinumId);
                    params.put("air_minum",AirMinumRadio.getText().toString());}
                else { params.put("air_minum",""); }
                if (ListrikId!=-1){
                    RadioButton ListrikRadio=findViewById(ListrikId);
                    params.put("listrik",ListrikRadio.getText().toString());}
                else { params.put("listrik",""); }
                if (MaterialAtapId!=-1){
                    RadioButton MaterialAtapRadio=findViewById(MaterialAtapId);
                    params.put("material_atap",MaterialAtapRadio.getText().toString());}
                else { params.put("material_atap",""); }
                if (KondisiAtapId!=-1){
                    RadioButton KondisiAtapRadio=findViewById(KondisiAtapId);
                    params.put("kondisi_atap",KondisiAtapRadio.getText().toString());}
                else { params.put("kondisi_atap",""); }
                if (MaterialDindingId!=-1){
                    RadioButton MaterialDindingRadio=findViewById(MaterialDindingId);
                    params.put("material_dinding",MaterialDindingRadio.getText().toString());}
                else { params.put("material_dinding",""); }
                if (KondisiDindingId!=-1){
                    RadioButton KondisiDindingRadio=findViewById(KondisiDindingId);
                    params.put("kondisi_dinding",KondisiDindingRadio.getText().toString());}
                else { params.put("kondisi_dinding",""); }
                if (MaterialLantaiId!=-1){
                    RadioButton MaterialLantaiRadio=findViewById(MaterialLantaiId);
                    params.put("material_lantai",MaterialLantaiRadio.getText().toString());}
                else { params.put("material_lantai",""); }
                if (KondisiLantaiId!=-1){
                    RadioButton KondisiLantaiRadio=findViewById(KondisiLantaiId);
                    params.put("kondisi_lantai",KondisiLantaiRadio.getText().toString());}
                else { params.put("kondisi_lantai",""); }

                return params;
            }
        };
        ProfileUpload.getInstance(RTLHShow.this).addToRequestQueue(stringRequest);
    }

    private void uploadFoto() {
        uploadGambarText.setText("Mengunggah Gambar : "+FotoUploaded+"/"+JumlahFoto);
        if (FotoUploaded<JumlahFoto){
            currentPhotoPath=NamePathList.get(FotoUploaded);
            bitmaps = BitmapFactory.decodeFile(FotoPathList.get(FotoUploaded));
            uploadImage();
        } else {
            Toast.makeText(RTLHShow.this,R.string.text_submit_success,Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = getSharedPreferences("FIRSTINSTALL", MODE_PRIVATE).edit();
            editor.putBoolean("reloadrtlh", true).apply();
            finish();
        }
    }

    private void uploadImage() {
        String UploadURL="https://rtlh-poi.000webhostapp.com/Android-Conn/Post_Foto.php";
        StringRequest stringRequest =new StringRequest(Request.Method.POST, UploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(RTLHShow.this,Response,Toast.LENGTH_SHORT).show();
                    FotoUploaded++;
                    uploadFoto();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RTLHShow.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                volleyNumber=2;
                errorLoad++;
                Handler loadHandler = new Handler();
                Runnable loadRunnable=new Runnable() {
                    @Override
                    public void run() {
                        if (errorLoad>=5){
                            errorVolley();
                        }else {
                            uploadImage();
                        }
                    }
                };
                loadHandler.postDelayed(loadRunnable, 3000);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdf.format(c.getTime());

                int no_foto=Integer.parseInt(dataid.replaceAll("[\\D]", ""));
                String strno_foto=String.valueOf(no_foto);
                String timeStamp=idFotoList.get(FotoUploaded);
                String id_foto ="FOTO"+strno_foto+"_"+timeStamp;
                String myfototag=FotoTagList.get(FotoUploaded);

                params.put("id_foto",id_foto);
                params.put("id_rtlh",dataid);
                params.put("id_akun",my_akun);
                params.put("tanggal",strDate);
                params.put("image",imageToString(bitmaps));
                params.put("tag",myfototag);
                return params;
            }
        };
        ProfileUpload.getInstance(RTLHShow.this).addToRequestQueue(stringRequest);
    }

    private String imageToString(Bitmap bitmaps){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmaps.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] imgBytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }

    private void editData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apa Anda Ingin Mengedit Data RTLH ini?")
                .setTitle("EDIT DATA");

        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditData=true;
                editDataEnabled();
                Edit_Loc.setVisibility(View.VISIBLE);
                Ambil_Foto.setVisibility(View.VISIBLE);
                foto_ktp.setVisibility(View.VISIBLE);
                foto_kondisikolom.setVisibility(View.VISIBLE);
                foto_konstruksiatap.setVisibility(View.VISIBLE);
                foto_kamarmandi.setVisibility(View.VISIBLE);
                foto_airminum.setVisibility(View.VISIBLE);
                foto_listrik.setVisibility(View.VISIBLE);
                foto_materialatap.setVisibility(View.VISIBLE);
                foto_kondisiatap.setVisibility(View.VISIBLE);
                foto_materialdinding.setVisibility(View.VISIBLE);
                foto_kondisidinding.setVisibility(View.VISIBLE);
                foto_materiallantai.setVisibility(View.VISIBLE);
                foto_kondisilantai.setVisibility(View.VISIBLE);
                strlihatfoto=" Lihat Foto ";
            }
        });
        builder.setNegativeButton(R.string.text_batal, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void editDataEnabled() {
        submitnavigation.getMenu().getItem(2).setIcon(R.drawable.ic_forward_black_24dp).setTitle(R.string.title_submit);

        Nama_Penghuni.setEnabled(true);
        Desa_Kel.setEnabled(true);
        Kecamatan.setEnabled(true);
        Kab_Kota.setEnabled(true);
        Provinsi.setEnabled(true);
        KodePos.setEnabled(true);
        Deskripsi.setEnabled(true);
        Nama_Lengkap.setEnabled(true);
        Usia.setEnabled(true);
        Alamat_Lengkap.setEnabled(true);
        Nomor_KTP.setEnabled(true);
        Jumlah_KK.setEnabled(true);
        Luas_Rumah.setEnabled(true);
        Jumlah_Penghuni.setEnabled(true);

        for(int i = 0; i < Pend_Terakhir.getChildCount(); i++){ Pend_Terakhir.getChildAt(i).setEnabled(true); }
        for(int i = 0; i < Jenis_Kelamin.getChildCount(); i++){ Jenis_Kelamin.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Peker_Utama.getChildCount(); i++){Peker_Utama.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Penghasilan.getChildCount(); i++){Penghasilan.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Status_Tanah.getChildCount(); i++){Status_Tanah.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Status_Rumah.getChildCount(); i++){Status_Rumah.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Aset_Rumah.getChildCount(); i++){Aset_Rumah.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Aset_Tanah.getChildCount(); i++){Aset_Tanah.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Bantuan_Perumahan.getChildCount(); i++){Bantuan_Perumahan.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Jenis_Kawasan.getChildCount(); i++){Jenis_Kawasan.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Pondasi.getChildCount(); i++){Pondasi.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Kondisi_Kolom.getChildCount(); i++){Kondisi_Kolom.getChildAt(i).setEnabled(true); }
        for(int i = 0; i < Konstruksi_Atap.getChildCount(); i++){Konstruksi_Atap.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Jendela.getChildCount(); i++){Jendela.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Ventilasi.getChildCount(); i++){Ventilasi.getChildAt(i).setEnabled(true); }
        for(int i = 0; i < Kamar_Mandi.getChildCount(); i++){Kamar_Mandi.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Jarak_TPA.getChildCount(); i++){Jarak_TPA.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Air_Minum.getChildCount(); i++){Air_Minum.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Listrik.getChildCount(); i++){Listrik.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Material_Atap.getChildCount(); i++){Material_Atap.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Kondisi_Atap.getChildCount(); i++){Kondisi_Atap.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Material_Dinding.getChildCount(); i++){Material_Dinding.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Kondisi_Dinding.getChildCount(); i++){Kondisi_Dinding.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Material_Lantai.getChildCount(); i++){Material_Lantai.getChildAt(i).setEnabled(true);}
        for(int i = 0; i < Kondisi_Lantai.getChildCount(); i++){Kondisi_Lantai.getChildAt(i).setEnabled(true);}
    }

    private void DukunganMasyarakat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        noArray= new ArrayList<>();
        id_akunArray=new ArrayList<>();
        usernameArray=new ArrayList<>();
        tanggalArray=new ArrayList<>();
        reviewArray=new ArrayList<>();
        dukunganArray=new ArrayList<>();
        fotoArray=new ArrayList<>();

        id_DifferArray = new ArrayList<>();
        unameDifferArray = new ArrayList<>();
        fotoDifferArray = new ArrayList<>();

        final View dukunganView=getLayoutInflater().inflate(R.layout.dukungan_layout,null);
        textDukungan=dukunganView.findViewById(R.id.textDukungan);
        textPenolakan=dukunganView.findViewById(R.id.textPenolakan);
        textValidasi=dukunganView.findViewById(R.id.textValidasi);
        textNoData=dukunganView.findViewById(R.id.textDukunganNoData);
        progressBarDukungan=dukunganView.findViewById(R.id.progressBarDukungan);
        progressBarDukungan.setVisibility(View.VISIBLE);
        final EditText dukunganEditText=dukunganView.findViewById(R.id.dukungan);
        final RadioGroup dukungRadio=dukunganView.findViewById(R.id.radioDukungan);
        RadioButton radioValid=dukunganView.findViewById(R.id.radioValid);
        if (PremiumAccess){
            radioValid.setVisibility(View.VISIBLE); }

        recyclerView = dukunganView.findViewById(R.id.recyclerDukungan);
        reviewList = new ArrayList<>();

        Button buttonDukung = dukunganView.findViewById(R.id.buttonDukungan);
        buttonDukung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int dukungId=dukungRadio.getCheckedRadioButtonId();
                final RadioButton radioDukung=dukunganView.findViewById(dukungId);
                if (dukungId!=-1 && !dukunganEditText.getText().toString().isEmpty()){
                    kirimDukungan(radioDukung.getText().toString(), dukunganEditText.getText().toString());
                } else {
                    Toast.makeText(RTLHShow.this,"Berikan Dukungan dan Pendapat Anda",Toast.LENGTH_SHORT).show(); }
            }
        });
        builder.setView(dukunganView);
        builder.setNeutralButton(R.string.text_tutup, null);

        dialog=builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button neutralbutton = (dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                neutralbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        LoadDukunganMasyarakat();
    }

    private void LoadDukunganMasyarakat() {
        final String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Review.php?dataid="+dataid;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            int jumlahDukungan=0;
                            int jumlahPenolakan=0;
                            int jumlahValidasi=0;
                            for(int i = 0; i < array.length(); i++){
                                JSONObject dataReview = array.getJSONObject(i);

                                noArray.add(dataReview.getString("no"));
                                id_akunArray.add(dataReview.getString("id_akun"));
                                id_DifferArray.add(dataReview.getString("id_akun"));
                                tanggalArray.add(dataReview.getString("tanggal"));
                                reviewArray.add(dataReview.getString("review"));
                                dukunganArray.add(dataReview.getString("dukungan"));

                                if (dataReview.getString("dukungan").equals("TOLAK")){
                                    jumlahPenolakan++;
                                } else if (dataReview.getString("dukungan").equals("DUKUNG")){
                                    jumlahDukungan++;
                                } else if (dataReview.getString("dukungan").equals("VALID")){
                                    jumlahValidasi++;
                                }
                            }

                            String strJumlahDukungan="Dukungan : "+ jumlahDukungan;
                            String strJumlahPenolakan="Penolakan : "+ jumlahPenolakan;
                            String strJumlahValidasi="Validasi : "+ jumlahValidasi;
                            textDukungan.setText(strJumlahDukungan);
                            textPenolakan.setText(strJumlahPenolakan);
                            textValidasi.setText(strJumlahValidasi);
                            if (jumlahDukungan!=0 || jumlahPenolakan!=0 || jumlahValidasi!=0){
                                HashSet<String> clearId = new HashSet<>(id_DifferArray);
                                id_DifferArray.clear();
                                id_DifferArray.addAll(clearId);
                                loadUserReview();
                            } else { textNoData.setVisibility(View.VISIBLE);
                                    progressBarDukungan.setVisibility(View.GONE);
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
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void kirimDukungan(final String strDukungan, final String strPendapat) {
        progressBarDukungan.setVisibility(View.VISIBLE);
        String UploadURL="https://rtlh-poi.000webhostapp.com/Android-Conn/Post_Review.php";
        StringRequest stringRequest =new StringRequest(Request.Method.POST, UploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(RTLHShow.this,Response,Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    DukunganMasyarakat();
                    progressBarDukungan.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RTLHShow.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                errorDukung++;
                Handler loadHandler = new Handler();
                Runnable loadRunnable=new Runnable() {
                    @Override
                    public void run() {
                        if (errorDukung>=3){
                            errorDukung=0;
                            Toast.makeText(RTLHShow.this,"Gagal Mengirim Dukungan",Toast.LENGTH_LONG).show();
                        } else {
                            kirimDukungan(strDukungan, strPendapat);
                        }
                    }
                };
                loadHandler.postDelayed(loadRunnable, 3000);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdf.format(c.getTime());
                String theReview = strClassname + strPendapat;

                params.put("id_akun", my_akun);
                params.put("class_name",strClassname+strMyUname);
                params.put("id_rtlh",dataid);
                params.put("dukungan",strDukungan);
                params.put("review",theReview);
                params.put("tanggal",strDate);
                return params;
            }
        };
        ProfileUpload.getInstance(RTLHShow.this).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_rtlhsubmit);

        createEditDialog(savedInstanceState);

        submitnavigation =findViewById(R.id.navigation_rtlhsubmit);
        submitnavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        submitnavigation.getMenu().getItem(1).setIcon(R.drawable.ic_thumbs_up_down_black_24dp).setTitle(R.string.title_dukung).setEnabled(false);
        submitnavigation.getMenu().getItem(2).setIcon(R.drawable.ic_edit_black_24dp).setTitle(R.string.title_edit).setEnabled(false);

        hrScroll=findViewById(R.id.horizontal_scroll);
        layoutImage = findViewById(R.id.linearImageRTLH);
        progressBar=findViewById(R.id.progressBarRTLH);

        RTLHOpsional=findViewById(R.id.linearRTLHOpsional);
        final ImageView btnShow=findViewById(R.id.rtlh_img_show);
        opsionalLayout=findViewById(R.id.rtlhLayoutOpsional);
        RTLHOpsional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnShow.getRotation()==180){
                    opsionalLayout.setVisibility(View.VISIBLE);
                    btnShow.setRotation(0);
                }
                else {
                    opsionalLayout.setVisibility(View.GONE);
                    btnShow.setRotation(180);
                }
            }
        });

        textWajib=findViewById(R.id.textWajib);
        textOpsional=findViewById(R.id.textOpsional);
        txtOleh=findViewById(R.id.rtlhtextOleh);
        textWajib.setText("Informasi Dasar");
        textOpsional.setText("Informasi Lainnya");
        txtOleh.setVisibility(View.VISIBLE);
        txtOleh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAccount(userSubmit);
            }
        });


        tabelIdentitas=findViewById(R.id.tableIdentitasPenghuni);
        textHideIdentitas=findViewById(R.id.txtHideIdentitasPenghuni);

        txtKoordinat=findViewById(R.id.rtlhtextKoordinat);
        SharedPreferences prefs = getSharedPreferences("KOORDINAT", MODE_PRIVATE);
        String restoredText = prefs.getString("id_rtlh", null);
        if (restoredText != null) {
            dataid=prefs.getString("id_rtlh", null);
        }
        SharedPreferences prefsAkun = getSharedPreferences("AKUN_SETTING", MODE_PRIVATE);
        String restoredTextAkun = prefsAkun.getString("id_akun", null);
        if (restoredTextAkun != null) {
            my_akun=prefsAkun.getString("id_akun", null);
            PremiumAccess=prefsAkun.getBoolean("PremiumAccess",false);
            strClassname=prefsAkun.getString("class_name","[Pengguna Biasa]");
            strMyUname=prefsAkun.getString("username","");
        }

        Edit_Loc=findViewById(R.id.btnEditLoc);
        Edit_Loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editdialog.show();
                if (firstMapLoad){
                    refreshMap();
                }
            }
        });
        Ambil_Foto=findViewById(R.id.rtlh_btn_foto);
        Ambil_Foto.setVisibility(View.GONE);
        Ambil_Foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FotoTag="0";
                TakePicture();
            }
        });
        Hapus_Data=findViewById(R.id.rtlh_hapus_data);
        Hapus_Data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDataRTLH();
            }
        });

        textEditOleh=findViewById(R.id.rtlhtextEditOleh);
        textEditOleh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PremiumAccess || my_akun.equals(userSubmitId)){
                    viewEditHistory();
                }
            }
        });

        Nama_Penghuni=findViewById(R.id.rtlh_edit_nama_penghuni);
        Desa_Kel=findViewById(R.id.rtlheditDesa);
        Kecamatan=findViewById(R.id.rtlheditKec);
        Kab_Kota=findViewById(R.id.rtlheditKab);
        Provinsi =findViewById(R.id.rtlheditProv);
        KodePos=findViewById(R.id.rtlheditKodePos);
        Deskripsi =findViewById(R.id.rtlh_edit_deskripsi_tambahan);
        Nama_Lengkap=findViewById(R.id.rtlheditNamaLengkap);
        Usia=findViewById(R.id.rtlheditUsia);
        Alamat_Lengkap=findViewById(R.id.rtlheditAlamat);
        Nomor_KTP=findViewById(R.id.rtlheditNomorKTP);
        Jumlah_KK=findViewById(R.id.rtlheditJumlahKK);
        Luas_Rumah=findViewById(R.id.rtlheditLuas);
        Jumlah_Penghuni=findViewById(R.id.rtlheditJumlahPenghuni);

        Nama_Penghuni.setEnabled(false);
        Desa_Kel.setEnabled(false);
        Kecamatan.setEnabled(false);
        Kab_Kota.setEnabled(false);
        Provinsi.setEnabled(false);
        KodePos.setEnabled(false);
        Deskripsi.setEnabled(false);
        Nama_Lengkap.setEnabled(false);
        Usia.setEnabled(false);
        Alamat_Lengkap.setEnabled(false);
        Nomor_KTP.setEnabled(false);
        Jumlah_KK.setEnabled(false);
        Luas_Rumah.setEnabled(false);
        Jumlah_Penghuni.setEnabled(false);

        Pend_Terakhir=findViewById(R.id.radioPendidikan);
        Jenis_Kelamin=findViewById(R.id.radioJenisKelamin);
        Peker_Utama=findViewById(R.id.radioPekerjaan);
        Penghasilan=findViewById(R.id.radioPenghasilan);
        Status_Tanah=findViewById(R.id.radioStatusTanah);
        Status_Rumah=findViewById(R.id.radioStatusRumah);
        Aset_Rumah=findViewById(R.id.radioAsetRumah);
        Aset_Tanah=findViewById(R.id.radioAsetTanah);
        Bantuan_Perumahan=findViewById(R.id.radioBantuan);
        Jenis_Kawasan=findViewById(R.id.radioKawasan);
        Pondasi=findViewById(R.id.radioPondasi);
        Kondisi_Kolom=findViewById(R.id.radioKolom);
        Konstruksi_Atap=findViewById(R.id.radioKonstruksi);
        Jendela=findViewById(R.id.radioJendela);
        Ventilasi=findViewById(R.id.radioVentilasi);
        Kamar_Mandi=findViewById(R.id.radioKamarMandi);
        Jarak_TPA=findViewById(R.id.radioJarakTPA);
        Air_Minum=findViewById(R.id.radioAirMinum);
        Listrik=findViewById(R.id.radioListrik);
        Material_Atap=findViewById(R.id.radioMaterialAtap);
        Kondisi_Atap=findViewById(R.id.radioKondisiAtap);
        Material_Dinding=findViewById(R.id.radioMaterialDinding);
        Kondisi_Dinding=findViewById(R.id.radioKondisiDinding);
        Material_Lantai=findViewById(R.id.radioMaterialLantai);
        Kondisi_Lantai=findViewById(R.id.radioKondisiLantai);

        foto_ktp=findViewById(R.id.rtlh_btn_foto_ktp);
        foto_kondisikolom=findViewById(R.id.rtlh_btn_foto_kolom);
        foto_konstruksiatap=findViewById(R.id.rtlh_btn_foto_konstruksi);
        foto_kamarmandi=findViewById(R.id.rtlh_btn_foto_kamarmandi);
        foto_airminum=findViewById(R.id.rtlh_btn_foto_airminum);
        foto_listrik=findViewById(R.id.rtlh_btn_foto_listrik);
        foto_materialatap=findViewById(R.id.rtlh_btn_foto_materialatap);
        foto_kondisiatap=findViewById(R.id.rtlh_btn_foto_kondisiatap);
        foto_materialdinding=findViewById(R.id.rtlh_btn_foto_materialdinding);
        foto_kondisidinding=findViewById(R.id.rtlh_btn_foto_kondisidinding);
        foto_materiallantai=findViewById(R.id.rtlh_btn_foto_materiallantai);
        foto_kondisilantai=findViewById(R.id.rtlh_btn_foto_kondisilantai);

        foto_ktp.setOnClickListener(this);
        foto_kondisikolom.setOnClickListener(this);
        foto_konstruksiatap.setOnClickListener(this);
        foto_kamarmandi.setOnClickListener(this);
        foto_airminum.setOnClickListener(this);
        foto_listrik.setOnClickListener(this);
        foto_materialatap.setOnClickListener(this);
        foto_kondisiatap.setOnClickListener(this);
        foto_materialdinding.setOnClickListener(this);
        foto_kondisidinding.setOnClickListener(this);
        foto_materiallantai.setOnClickListener(this);
        foto_kondisilantai.setOnClickListener(this);

        foto_ktp.setVisibility(View.GONE);
        foto_kondisikolom.setVisibility(View.GONE);
        foto_konstruksiatap.setVisibility(View.GONE);
        foto_kamarmandi.setVisibility(View.GONE);
        foto_airminum.setVisibility(View.GONE);
        foto_listrik.setVisibility(View.GONE);
        foto_materialatap.setVisibility(View.GONE);
        foto_kondisiatap.setVisibility(View.GONE);
        foto_materialdinding.setVisibility(View.GONE);
        foto_kondisidinding.setVisibility(View.GONE);
        foto_materiallantai.setVisibility(View.GONE);
        foto_kondisilantai.setVisibility(View.GONE);

        getDataRTLH();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rtlh_btn_foto_ktp:
                if (foto_ktp.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_ktp;
                    popupImage();
                } else if (foto_ktp.getText().toString().equals(" Lihat Foto ")){
                    popupImageInt=link_foto_ktp;
                    popupImageLocal();
                } else {
                    FotoTag="10";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_kolom:
                if (foto_kondisikolom.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_kondisikolom;
                    popupImage();
                } else if (foto_kondisikolom.getText().toString().equals(" Lihat Foto ")){
                    popupImageInt=link_foto_kondisikolom;
                    popupImageLocal();
                } else {
                    FotoTag="21";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_konstruksi:
                if (foto_konstruksiatap.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_konstruksiatap;
                    popupImage();
                } else if (foto_konstruksiatap.getText().toString().equals(" Lihat Foto ")){
                    popupImageInt=link_foto_konstruksiatap;
                    popupImageLocal();
                } else {
                    FotoTag="22";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_kamarmandi:
                if (foto_kamarmandi.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_kamarmandi;
                    popupImage();
                } else if (foto_kamarmandi.getText().toString().equals(" Lihat Foto ")){
                    popupImageInt=link_foto_kamarmandi;
                    popupImageLocal();
                } else {
                    FotoTag="31";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_airminum:
                if (foto_airminum.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_airminum;
                    popupImage();
                } else if (foto_airminum.getText().toString().equals(" Lihat Foto ")){
                    popupImageInt=link_foto_airminum;
                    popupImageLocal();
                } else {
                    FotoTag="32";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_listrik:
                if (foto_listrik.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_listrik;
                    popupImage();
                } else if (foto_listrik.getText().toString().equals(" Lihat Foto ")){
                    popupImageInt=link_foto_listrik;
                    popupImageLocal();
                } else {
                    FotoTag="33";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_materialatap:
                if (foto_materialatap.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_materialatap;
                    popupImage();
                } else if (foto_materialatap.getText().toString().equals(" Lihat Foto ")){
                    popupImageInt=link_foto_materialatap;
                    popupImageLocal();
                } else {
                    FotoTag="41";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_kondisiatap:
                if (foto_kondisiatap.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_kondisiatap;
                    popupImage();
                } else if (foto_kondisiatap.getText().toString().equals(" Lihat Foto ")){
                    popupImageInt=link_foto_kondisiatap;
                    popupImageLocal();
                } else {
                    FotoTag="42";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_materialdinding:
                if (foto_materialdinding.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_materialdinding;
                    popupImage();
                } else if (foto_materialdinding.getText().toString().equals(" Lihat Foto ")){
                    popupImageInt=link_foto_materialdinding;
                    popupImageLocal();
                } else {
                    FotoTag="43";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_kondisidinding:
                if (foto_kondisidinding.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_kondisidinding;
                    popupImage();
                } else if (foto_kondisidinding.getText().toString().equals(" Lihat Foto ")){
                    popupImageInt=link_foto_kondisidinding;
                    popupImageLocal();
                } else {
                    FotoTag="44";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_materiallantai:
                if (foto_materiallantai.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_materiallantai;
                    popupImage();
                } else if (foto_materiallantai.getText().toString().equals(" Lihat Foto ")){
                    popupImageInt=link_foto_materiallantai;
                    popupImageLocal();
                } else {
                    FotoTag="45";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_kondisilantai:
                if (foto_kondisilantai.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_kondisilantai;
                    popupImage();
                } else if (foto_kondisilantai.getText().toString().equals(" Lihat Foto ")){
                    popupImageInt=link_foto_kondisilantai;
                    popupImageLocal();
                } else {
                    FotoTag="46";
                    TakePicture();
                }
                break;
            default:
                break;
        }
    }


    private void viewEditHistory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View historyView=getLayoutInflater().inflate(R.layout.edit_history,null);
        final LinearLayout LLEdit01=historyView.findViewById(R.id.LLEdit01);
        final LinearLayout LLEdit02=historyView.findViewById(R.id.LLEdit02);
        final LinearLayout LLEdit03=historyView.findViewById(R.id.LLEdit03);
        final LinearLayout LLEdit04=historyView.findViewById(R.id.LLEdit04);
        final LinearLayout LLEdit05=historyView.findViewById(R.id.LLEdit05);
        final LinearLayout LLEdit06=historyView.findViewById(R.id.LLEdit06);
        final LinearLayout LLEdit07=historyView.findViewById(R.id.LLEdit07);
        final LinearLayout LLEdit08=historyView.findViewById(R.id.LLEdit08);
        final LinearLayout LLEdit09=historyView.findViewById(R.id.LLEdit09);
        final LinearLayout LLEdit10=historyView.findViewById(R.id.LLEdit10);
        final LinearLayout LLEdit11=historyView.findViewById(R.id.LLEdit11);
        final LinearLayout LLEdit12=historyView.findViewById(R.id.LLEdit12);
        final LinearLayout LLEdit13=historyView.findViewById(R.id.LLEdit13);
        final LinearLayout LLEdit14=historyView.findViewById(R.id.LLEdit14);
        final LinearLayout LLEdit15=historyView.findViewById(R.id.LLEdit15);
        final LinearLayout LLEdit16=historyView.findViewById(R.id.LLEdit16);
        final LinearLayout LLEdit17=historyView.findViewById(R.id.LLEdit17);
        final LinearLayout LLEdit18=historyView.findViewById(R.id.LLEdit18);
        final LinearLayout LLEdit19=historyView.findViewById(R.id.LLEdit19);
        final LinearLayout LLEdit20=historyView.findViewById(R.id.LLEdit20);
        final LinearLayout LLEdit21=historyView.findViewById(R.id.LLEdit21);
        final LinearLayout LLEdit22=historyView.findViewById(R.id.LLEdit22);
        final LinearLayout LLEdit23=historyView.findViewById(R.id.LLEdit23);
        final LinearLayout LLEdit24=historyView.findViewById(R.id.LLEdit24);
        final LinearLayout LLEdit25=historyView.findViewById(R.id.LLEdit25);
        final LinearLayout LLEdit26=historyView.findViewById(R.id.LLEdit26);
        final LinearLayout LLEdit27=historyView.findViewById(R.id.LLEdit27);
        final LinearLayout LLEdit28=historyView.findViewById(R.id.LLEdit28);
        final LinearLayout LLEdit29=historyView.findViewById(R.id.LLEdit29);
        final LinearLayout LLEdit30=historyView.findViewById(R.id.LLEdit30);
        final LinearLayout LLEdit31=historyView.findViewById(R.id.LLEdit31);
        final LinearLayout LLEdit32=historyView.findViewById(R.id.LLEdit32);
        final LinearLayout LLEdit33=historyView.findViewById(R.id.LLEdit33);
        final LinearLayout LLEdit34=historyView.findViewById(R.id.LLEdit34);
        final LinearLayout LLEdit35=historyView.findViewById(R.id.LLEdit35);
        final LinearLayout LLEdit36=historyView.findViewById(R.id.LLEdit36);
        final LinearLayout LLEdit37=historyView.findViewById(R.id.LLEdit37);
        final LinearLayout LLEdit38=historyView.findViewById(R.id.LLEdit38);
        final LinearLayout LLEdit39=historyView.findViewById(R.id.LLEdit39);
        final LinearLayout LLEdit40=historyView.findViewById(R.id.LLEdit40);
        final LinearLayout LLEdit41=historyView.findViewById(R.id.LLEdit41);
        final LinearLayout LLEdit42=historyView.findViewById(R.id.LLEdit42);
        final LinearLayout LLEdit43=historyView.findViewById(R.id.LLEdit43);
        final LinearLayout LLEdit44=historyView.findViewById(R.id.LLEdit44);

        final int[] iUser = {1};
        final ArrayList<String> userId=new ArrayList<>();
        final ArrayList<String> onlyDifferentId =new ArrayList<>();
        final ArrayList<String> onlyUnameId= new ArrayList<>();
        onlyUnameId.add("..");

        String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_History.php?dataid="+dataid;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject history= array.getJSONObject(i);

                                TextView TextEdit01=new TextView(RTLHShow.this);
                                TextEdit01.setText(String.valueOf(i));
                                LLEdit01.addView(TextEdit01);

                                userId.add(history.getString("pengedit"));

                                TextView TextEdit03=new TextView(RTLHShow.this);
                                TextEdit03.setText(history.getString("tanggal_edit"));
                                LLEdit03.addView(TextEdit03);

                                TextView TextEdit04=new TextView(RTLHShow.this);
                                TextEdit04.setText(history.getString("lat"));
                                LLEdit04.addView(TextEdit04);

                                TextView TextEdit05=new TextView(RTLHShow.this);
                                TextEdit05.setText(history.getString("lon"));
                                LLEdit05.addView(TextEdit05);

                                TextView TextEdit06=new TextView(RTLHShow.this);
                                TextEdit06.setText(history.getString("nama_penghuni"));
                                LLEdit06.addView(TextEdit06);

                                TextView TextEdit07=new TextView(RTLHShow.this);
                                TextEdit07.setText(history.getString("desa_kel"));
                                LLEdit07.addView(TextEdit07);

                                TextView TextEdit08=new TextView(RTLHShow.this);
                                TextEdit08.setText(history.getString("kecamatan"));
                                LLEdit08.addView(TextEdit08);

                                TextView TextEdit09=new TextView(RTLHShow.this);
                                TextEdit09.setText(history.getString("kab_kota"));
                                LLEdit09.addView(TextEdit09);

                                TextView TextEdit10=new TextView(RTLHShow.this);
                                TextEdit10.setText(history.getString("provinsi"));
                                LLEdit10.addView(TextEdit10);

                                TextView TextEdit11=new TextView(RTLHShow.this);
                                TextEdit11.setText(history.getString("kodepos"));
                                LLEdit11.addView(TextEdit11);

                                TextView TextEdit12=new TextView(RTLHShow.this);
                                TextEdit12.setText(history.getString("deskripsi"));
                                LLEdit12.addView(TextEdit12);

                                TextView TextEdit13=new TextView(RTLHShow.this);
                                TextEdit13.setText(history.getString("nama_lengkap"));
                                LLEdit13.addView(TextEdit13);

                                TextView TextEdit14=new TextView(RTLHShow.this);
                                TextEdit14.setText(history.getString("usia"));
                                LLEdit14.addView(TextEdit14);

                                TextView TextEdit15=new TextView(RTLHShow.this);
                                TextEdit15.setText(history.getString("pendidikan_terakhir"));
                                LLEdit15.addView(TextEdit15);

                                TextView TextEdit16=new TextView(RTLHShow.this);
                                TextEdit16.setText(history.getString("jenis_kelamin"));
                                LLEdit16.addView(TextEdit16);

                                TextView TextEdit17=new TextView(RTLHShow.this);
                                TextEdit17.setText(history.getString("alamat_lengkap"));
                                LLEdit17.addView(TextEdit17);

                                TextView TextEdit18=new TextView(RTLHShow.this);
                                TextEdit18.setText(history.getString("nomor_ktp"));
                                LLEdit18.addView(TextEdit18);

                                TextView TextEdit19=new TextView(RTLHShow.this);
                                TextEdit19.setText(history.getString("jumlah_kk"));
                                LLEdit19.addView(TextEdit19);

                                TextView TextEdit20=new TextView(RTLHShow.this);
                                TextEdit20.setText(history.getString("pekerjaan_utama"));
                                LLEdit20.addView(TextEdit20);

                                TextView TextEdit21=new TextView(RTLHShow.this);
                                TextEdit21.setText(history.getString("penghasilan_bulan"));
                                LLEdit21.addView(TextEdit21);

                                TextView TextEdit22=new TextView(RTLHShow.this);
                                TextEdit22.setText(history.getString("status_tanah"));
                                LLEdit22.addView(TextEdit22);

                                TextView TextEdit23=new TextView(RTLHShow.this);
                                TextEdit23.setText(history.getString("status_rumah"));
                                LLEdit23.addView(TextEdit23);

                                TextView TextEdit24=new TextView(RTLHShow.this);
                                TextEdit24.setText(history.getString("aset_rumah_lain"));
                                LLEdit24.addView(TextEdit24);

                                TextView TextEdit25=new TextView(RTLHShow.this);
                                TextEdit25.setText(history.getString("aset_tanah_lain"));
                                LLEdit25.addView(TextEdit25);

                                TextView TextEdit26=new TextView(RTLHShow.this);
                                TextEdit26.setText(history.getString("bantuan_perumahan"));
                                LLEdit26.addView(TextEdit26);

                                TextView TextEdit27=new TextView(RTLHShow.this);
                                TextEdit27.setText(history.getString("jenis_kawasan"));
                                LLEdit27.addView(TextEdit27);

                                TextView TextEdit28=new TextView(RTLHShow.this);
                                TextEdit28.setText(history.getString("pondasi"));
                                LLEdit28.addView(TextEdit28);

                                TextView TextEdit29=new TextView(RTLHShow.this);
                                TextEdit29.setText(history.getString("kondisi_kolom"));
                                LLEdit29.addView(TextEdit29);

                                TextView TextEdit30=new TextView(RTLHShow.this);
                                TextEdit30.setText(history.getString("kondisi_konstruksi_atap"));
                                LLEdit30.addView(TextEdit30);

                                TextView TextEdit31=new TextView(RTLHShow.this);
                                TextEdit31.setText(history.getString("jendela"));
                                LLEdit31.addView(TextEdit31);

                                TextView TextEdit32=new TextView(RTLHShow.this);
                                TextEdit32.setText(history.getString("ventilasi"));
                                LLEdit32.addView(TextEdit32);

                                TextView TextEdit33=new TextView(RTLHShow.this);
                                TextEdit33.setText(history.getString("kamar_mandi"));
                                LLEdit33.addView(TextEdit33);

                                TextView TextEdit34=new TextView(RTLHShow.this);
                                TextEdit34.setText(history.getString("sumber_air"));
                                LLEdit34.addView(TextEdit34);

                                TextView TextEdit35=new TextView(RTLHShow.this);
                                TextEdit35.setText(history.getString("air_minum"));
                                LLEdit35.addView(TextEdit35);

                                TextView TextEdit36=new TextView(RTLHShow.this);
                                TextEdit36.setText(history.getString("listrik"));
                                LLEdit36.addView(TextEdit36);

                                TextView TextEdit37=new TextView(RTLHShow.this);
                                TextEdit37.setText(history.getString("luas_rumah"));
                                LLEdit37.addView(TextEdit37);

                                TextView TextEdit38=new TextView(RTLHShow.this);
                                TextEdit38.setText(history.getString("jumlah_penghuni"));
                                LLEdit38.addView(TextEdit38);

                                TextView TextEdit39=new TextView(RTLHShow.this);
                                TextEdit39.setText(history.getString("material_atap"));
                                LLEdit39.addView(TextEdit39);

                                TextView TextEdit40=new TextView(RTLHShow.this);
                                TextEdit40.setText(history.getString("kondisi_atap"));
                                LLEdit40.addView(TextEdit40);

                                TextView TextEdit41=new TextView(RTLHShow.this);
                                TextEdit41.setText(history.getString("material_dinding"));
                                LLEdit41.addView(TextEdit41);

                                TextView TextEdit42=new TextView(RTLHShow.this);
                                TextEdit42.setText(history.getString("kondisi_dinding"));
                                LLEdit42.addView(TextEdit42);

                                TextView TextEdit43=new TextView(RTLHShow.this);
                                TextEdit43.setText(history.getString("material_lantai"));
                                LLEdit43.addView(TextEdit43);

                                TextView TextEdit44=new TextView(RTLHShow.this);
                                TextEdit44.setText(history.getString("kondisi_lantai"));
                                LLEdit44.addView(TextEdit44);
                            }
                            HashSet<String> clearId = new HashSet<>();
                            clearId.addAll(userId);
                            onlyDifferentId.addAll(clearId);

                            if (onlyDifferentId.size()>1){
                                loadEditor(array.length());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    private void loadEditor(final int length) {
                        String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Akun.php?id_akun="+onlyDifferentId.get(iUser[0]);
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONArray array = new JSONArray(response);
                                            JSONObject akunData = array.getJSONObject(0);
                                            onlyUnameId.add(akunData.getString("username"));
                                            iUser[0]++;
                                            if (iUser[0] <onlyDifferentId.size()){
                                                loadEditor(length);
                                            } else {
                                                for (int ir = 0; ir < length; ir++) {
                                                    TextView TextEdit02=new TextView(RTLHShow.this);
                                                    for (int irl = 0; irl < onlyDifferentId.size(); irl++) {
                                                        if (userId.get(ir).equals(onlyDifferentId.get(irl))){
                                                            TextEdit02.setText(onlyUnameId.get(irl));
                                                        } else {
                                                            TextEdit02.setText("...");
                                                        }
                                                    }
                                                    LLEdit02.addView(TextEdit02);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(RTLHShow.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                                    }
                                });
                        Volley.newRequestQueue(RTLHShow.this).add(stringRequest);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        Volley.newRequestQueue(this).add(stringRequest);

        builder.setView(historyView);
        builder.setNeutralButton(R.string.text_tutup,null);
        final AlertDialog dialog=builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = (dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void deleteDataRTLH() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Anda Yakin Ingin Menghapus Data RTLH ini? \n" +
                "Data yang sudah dihapus tidak dapat dikembalikan lagi")
                .setTitle("HAPUS DATA RTLH");
        builder.setPositiveButton(R.string.text_hapus_data, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String UploadURL="https://rtlh-poi.000webhostapp.com/Android-Conn/Delete_Data.php";
                StringRequest stringRequest =new StringRequest(Request.Method.POST, UploadURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            String Response=jsonObject.getString("response");
                            Toast.makeText(RTLHShow.this,Response,Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = getSharedPreferences("FIRSTINSTALL", MODE_PRIVATE).edit();
                            editor.putBoolean("reloadrtlh", true).apply();
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RTLHShow.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params=new HashMap<>();
                        params.put("id_rtlh", dataid);
                        params.put("removefoto", strLinkFotoPicasso);
                        return params;
                    }
                };
                ProfileUpload.getInstance(RTLHShow.this).addToRequestQueue(stringRequest);
            }
        });
        builder.setNegativeButton(R.string.text_batal, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createEditDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder Editbuilder = new AlertDialog.Builder(RTLHShow.this);
        View editView=getLayoutInflater().inflate(R.layout.edit_loc,null);

        mapView=editView.findViewById(R.id.mapViewEdit);
        mapView.onCreate(savedInstanceState);
        RadioButton radioBasic=editView.findViewById(R.id.radioBasic);
        RadioButton radioSattelite=editView.findViewById(R.id.radioSattelite);
        btnSimpanLoc=editView.findViewById(R.id.btnSimpanLoc);
        editProgress=editView.findViewById(R.id.progressEditLoc);
        radioBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadMap(Style.MAPBOX_STREETS);
            }
        });
        radioSattelite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadMap(Style.SATELLITE);
            }
        });
        btnSimpanLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProgress.setVisibility(View.VISIBLE);
                sendNewLoc();
            }
        });

        Editbuilder.setView(editView);
        Editbuilder.setNeutralButton(R.string.text_tutup,null);
        Editdialog=Editbuilder.create();
        Editdialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = (Editdialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Editdialog.dismiss();
                    }
                });
            }
        });
    }

    private void sendNewLoc() {
        String UploadURL="https://rtlh-poi.000webhostapp.com/Android-Conn/Edit_Loc.php";
        StringRequest stringRequest =new StringRequest(Request.Method.POST, UploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(RTLHShow.this,Response,Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = getSharedPreferences("FIRSTINSTALL", MODE_PRIVATE).edit();
                    editor.putBoolean("reloadrtlh", true).apply();

                    latLoc= Double.parseDouble(editLat);
                    lonLoc= Double.parseDouble(editLon);
                    double dbLatitude = Double.parseDouble(editLat);
                    double dbLongitude = Double.parseDouble(editLon);
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
                    txtKoordinat.setText(strKoordinat);
                    reloadMap(Style.MAPBOX_STREETS);
                    editProgress.setVisibility(View.GONE);
                    btnSimpanLoc.setVisibility(View.GONE);
                    Editdialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RTLHShow.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("id_rtlh", dataid);
                params.put("lat", editLat);
                params.put("lon", editLon);
                return params;
            }
        };
        ProfileUpload.getInstance(RTLHShow.this).addToRequestQueue(stringRequest);
    }

    private void refreshMap() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                RTLHShow.this.mapboxMap = mapboxMap;
                reloadMap(Style.MAPBOX_STREETS);
            }
        });
    }

    private void reloadMap(String basemap) {
        mapboxMap.setStyle(basemap, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull final Style style) {
                double LatAtas = latLoc-0.1*(1 / 110.57);
                double LonAtas = lonLoc-0.1*(1 / 111.32);
                double LatBawah = latLoc+0.1*(1 / 110.57);
                double LonBawah = lonLoc+0.1*(1 / 111.32);
                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .include(new LatLng(LatAtas, LonAtas))
                        .include(new LatLng(LatBawah, LonBawah))
                        .build();
                mapboxMap.setLatLngBoundsForCameraTarget(latLngBounds);
                mapboxMap.setMinZoomPreference(16);

                CameraPosition position = new CameraPosition.Builder()
                        .zoom(18).target(new LatLng(latLoc,lonLoc)).bearing(0).tilt(0).build();
                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position));

                SymbolLayer selectedLayer=new SymbolLayer("layerselected","select_id");
                style.addSource(new GeoJsonSource("select_id", Feature.fromGeometry(Point.fromLngLat(lonLoc,latLoc))));
                style.addImage("bm_rtlh", BitmapFactory.decodeResource(getResources(),R.drawable.bm_rtlh));
                style.addLayer(selectedLayer);
                selectedLayer.setProperties(
                        iconImage("bm_rtlh"),
                        iconAllowOverlap(true),
                        iconIgnorePlacement(true),
                        textField("Lokasi Sebelum"),
                        textColor(Color.DKGRAY),
                        textAnchor(Property.TEXT_ANCHOR_TOP),
                        textFont(new String[] {"Ubuntu Medium", "Arial Unicode MS Regular"}),
                        textHaloColor(Color.WHITE),
                        textHaloWidth(1f),
                        textAllowOverlap(true)
                );
                pointlayer=new SymbolLayer("layerpoint","point_id");
                style.addSource(new GeoJsonSource("point_id", Feature.fromGeometry(Point.fromLngLat(0,0))));
                style.addImage("bm_point", BitmapFactory.decodeResource(getResources(),R.drawable.bm_point));
                style.addLayer(pointlayer);
                pointlayer.setProperties(
                        visibility(NONE),
                        iconImage("bm_point"),
                        iconAllowOverlap(true),
                        iconIgnorePlacement(true));

                if (firstMapLoad){
                    firstMapLoad=false;
                    mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                        @Override
                        public boolean onMapLongClick(@NonNull LatLng point) {
                            String content = String.format(Locale.US,point.toString());
                            content = content.replace("LatLng [", "");
                            content = content.replace("]", "");
                            content = content.replace(" ", "");
                            content = content.replace("latitude=", "");
                            content = content.replace("longitude=", "");
                            content = content.replace("altitude=", "");
                            String[] separated = content.split(",");
                            String latitude = separated[0];
                            String longitude = separated[1];
                            String altitude = separated[2];

                            final Double LAT=Double.parseDouble(latitude);
                            final Double LON=Double.parseDouble(longitude);
                            Double ALT=Double.parseDouble(altitude);

                            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    GeoJsonSource NewLatLon=style.getSourceAs("point_id");
                                    Objects.requireNonNull(NewLatLon).setGeoJson(Feature.fromGeometry(Point.fromLngLat(LON,LAT)));
                                    pointlayer.setProperties(visibility(VISIBLE));
                                }
                            });
                            editLat=latitude;
                            editLon=longitude;
                            btnSimpanLoc.setVisibility(View.VISIBLE);
                            return true;
                        }
                    });
                }
            }
        });
    }

    private void TakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(RTLHShow.this,"Gagal Mengambil Foto",Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(RTLHShow.this,
                        "com.lihatpeta.prototype.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = my_akun + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            int targetW = hrScroll.getWidth()/2;
            int targetH = hrScroll.getHeight();

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            bitmaps = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            FotoPathList.add(currentPhotoPath);
            NamePathList.add(imageFileName);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            idFotoList.add(timeStamp);
            FotoTagList.add(FotoTag);
            SetBTNFoto(FotoTag, linkFotoPicasso.size()+FotoPathList.size()-1);

            final ImageView newFoto = new ImageView(this);
            newFoto.setId(FotoPathList.size());
            newFoto.setTag(linkFotoPicasso.size()+FotoPathList.size()-1);
            newFoto.setPadding(2, 2, 2, 2);
            newFoto.setImageBitmap(bitmaps);
            newFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupImageInt= (int) newFoto.getTag();
                    popupImageLocal();
                }
            });
            layoutImage.addView(newFoto);
        }
    }

    private void popupImageLocal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView=getLayoutInflater().inflate(R.layout.popup_image,null);
        final PhotoView popPhoto=popupView.findViewById(R.id.photoPopup);
        final Button btnNext=popupView.findViewById(R.id.popupBtnNext);
        final Button btnBack=popupView.findViewById(R.id.popupBtnBack);
        if (popupImageInt==linkFotoPicasso.size()){
            btnBack.setVisibility(View.GONE); }
        if (linkFotoPicasso.size()-1+FotoPathList.size()==popupImageInt){
            btnNext.setVisibility(View.GONE);}
        Picasso.get().load("file://"+FotoPathList.get(popupImageInt-linkFotoPicasso.size())).error(R.drawable.errorimage).into(popPhoto);
        if (FotoPathList.size()==1){
            btnNext.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE); }
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupImageInt++;
                Picasso.get().load("file://"+FotoPathList.get(popupImageInt-linkFotoPicasso.size())).error(R.drawable.errorimage).into(popPhoto);
                if (popupImageInt==linkFotoPicasso.size()+1){
                    btnBack.setVisibility(View.VISIBLE);
                }
                if (popupImageInt==linkFotoPicasso.size()-1+FotoPathList.size()){
                    btnNext.setVisibility(View.GONE);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupImageInt--;
                Picasso.get().load("file://"+FotoPathList.get(popupImageInt-linkFotoPicasso.size())).error(R.drawable.errorimage).into(popPhoto);
                if (popupImageInt==linkFotoPicasso.size()){
                    btnBack.setVisibility(View.GONE);
                }
                if (popupImageInt==linkFotoPicasso.size()+FotoPathList.size()-2){
                    btnNext.setVisibility(View.VISIBLE);
                }
            }
        });

        builder.setView(popupView);
        builder.setNeutralButton(R.string.text_tutup,null);
        final AlertDialog dialog=builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = (dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void getDataRTLH() {
        progressBar.setVisibility(View.VISIBLE);
        String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_RTLH.php?dataid="+dataid;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            showdata = array.getJSONObject(0);
                            loadArrayData();
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
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void loadArrayData() {
        try {
            latLoc= showdata.getDouble("lat");
            lonLoc= showdata.getDouble("lon");
            double dbLatitude = showdata.getDouble("lat");
            double dbLongitude = showdata.getDouble("lon");
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
            txtKoordinat.setText(strKoordinat);
            loadUser(showdata.getString("id_akun"), showdata.getString("tanggal"));
            userSubmitId=showdata.getString("id_akun");
            if (my_akun.equals(userSubmitId) || PremiumAccess){
                Hapus_Data.setVisibility(View.VISIBLE);
            } else {
                tabelIdentitas.setVisibility(View.GONE);
                textHideIdentitas.setVisibility(View.VISIBLE); }

            Nama_Penghuni.setText(showdata.getString("nama_penghuni"));
            Desa_Kel.setText(showdata.getString("desa_kel"));
            Kecamatan.setText(showdata.getString("kecamatan"));
            Kab_Kota.setText(showdata.getString("kab_kota"));
            Provinsi.setText(showdata.getString("provinsi"));
            KodePos.setText(showdata.getString("kodepos"));
            if (showdata.getString("deskripsi").equals("")){
                Deskripsi.setText("*Tidak Ada Deskripsi*");
            } else {
                Deskripsi.setText(showdata.getString("deskripsi")); }
            Nama_Lengkap.setText(showdata.getString("nama_lengkap"));
            Usia.setText(showdata.getString("usia"));
            Alamat_Lengkap.setText(showdata.getString("alamat_lengkap"));
            Nomor_KTP.setText(showdata.getString("nomor_ktp"));
            Jumlah_KK.setText(showdata.getString("jumlah_kk"));
            Luas_Rumah.setText(showdata.getString("luas_rumah"));
            Jumlah_Penghuni.setText(showdata.getString("jumlah_penghuni"));
            if (!showdata.getString("pengedit").equals("")){
                loadUserEdit(showdata.getString("pengedit"), showdata.getString("tanggal_edit")); }

            for(int i = 0; i < Pend_Terakhir.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Pend_Terakhir.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("pendidikan_terakhir"))){
                    dataRadio.setChecked(true);
                } else { Pend_Terakhir.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Jenis_Kelamin.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Jenis_Kelamin.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("jenis_kelamin"))){
                    dataRadio.setChecked(true);
                } else { Jenis_Kelamin.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Peker_Utama.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Peker_Utama.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("pekerjaan_utama"))){
                    dataRadio.setChecked(true);
                } else { Peker_Utama.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Penghasilan.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Penghasilan.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("penghasilan_bulan"))){
                    dataRadio.setChecked(true);
                } else { Penghasilan.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Status_Tanah.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Status_Tanah.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("status_tanah"))){
                    dataRadio.setChecked(true);
                } else { Status_Tanah.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Status_Rumah.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Status_Rumah.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("status_rumah"))){
                    dataRadio.setChecked(true);
                } else { Status_Rumah.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Aset_Rumah.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Aset_Rumah.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("aset_rumah_lain"))){
                    dataRadio.setChecked(true);
                } else { Aset_Rumah.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Aset_Tanah.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Aset_Tanah.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("aset_tanah_lain"))){
                    dataRadio.setChecked(true);
                } else { Aset_Tanah.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Bantuan_Perumahan.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Bantuan_Perumahan.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("bantuan_perumahan"))){
                    dataRadio.setChecked(true);
                } else { Bantuan_Perumahan.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Jenis_Kawasan.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Jenis_Kawasan.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("jenis_kawasan"))){
                    dataRadio.setChecked(true);
                } else { Jenis_Kawasan.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Pondasi.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Pondasi.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("pondasi"))){
                    dataRadio.setChecked(true);
                } else { Pondasi.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Kondisi_Kolom.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Kondisi_Kolom.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("kondisi_kolom"))){
                    dataRadio.setChecked(true);
                } else { Kondisi_Kolom.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Konstruksi_Atap.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Konstruksi_Atap.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("kondisi_konstruksi_atap"))){
                    dataRadio.setChecked(true);
                } else { Konstruksi_Atap.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Jendela.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Jendela.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("jendela"))){
                    dataRadio.setChecked(true);
                } else { Jendela.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Ventilasi.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Ventilasi.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("ventilasi"))){
                    dataRadio.setChecked(true);
                } else { Ventilasi.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Kamar_Mandi.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Kamar_Mandi.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("kamar_mandi"))){
                    dataRadio.setChecked(true);
                } else { Kamar_Mandi.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Jarak_TPA.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Jarak_TPA.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("sumber_air"))){
                    dataRadio.setChecked(true);
                } else { Jarak_TPA.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Air_Minum.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Air_Minum.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("air_minum"))){
                    dataRadio.setChecked(true);
                } else { Air_Minum.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Listrik.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Listrik.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("listrik"))){
                    dataRadio.setChecked(true);
                } else { Listrik.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Material_Atap.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Material_Atap.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("material_atap"))){
                    dataRadio.setChecked(true);
                } else { Material_Atap.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Kondisi_Atap.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Kondisi_Atap.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("kondisi_atap"))){
                    dataRadio.setChecked(true);
                } else { Kondisi_Atap.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Material_Dinding.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Material_Dinding.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("material_dinding"))){
                    dataRadio.setChecked(true);
                } else { Material_Dinding.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Kondisi_Dinding.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Kondisi_Dinding.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("kondisi_dinding"))){
                    dataRadio.setChecked(true);
                } else { Kondisi_Dinding.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Material_Lantai.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Material_Lantai.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("material_lantai"))){
                    dataRadio.setChecked(true);
                } else { Material_Lantai.getChildAt(i).setEnabled(false); }
            }
            for(int i = 0; i < Kondisi_Lantai.getChildCount(); i++){
                RadioButton dataRadio= (RadioButton) Kondisi_Lantai.getChildAt(i);
                if (dataRadio.getText().toString().equals(showdata.getString("kondisi_lantai"))){
                    dataRadio.setChecked(true);
                } else { Kondisi_Lantai.getChildAt(i).setEnabled(false); }
            }

            LoadFoto();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadUserEdit(String pengedit, final String tanggal_edit) {
        String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Akun.php?id_akun="+pengedit;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject akunData = array.getJSONObject(0);
                            String strOleh = "Diedit oleh : @"+akunData.getString("username")+", Pada : "+tanggal_edit;
                            textEditOleh.setText(strOleh);
                            textEditOleh.setVisibility(View.VISIBLE);
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
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void LoadFoto() {
        final String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Foto.php?dataid="+dataid;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            for(int i = 0; i < array.length(); i++){
                                datafoto = array.getJSONObject(i);
                                final ImageView newFoto = new ImageView(RTLHShow.this);
                                newFoto.setId(i);
                                newFoto.setTag(i);
                                newFoto.setPadding(2, 2, 2, 2);
                                newFoto.setLayoutParams(new LinearLayout.LayoutParams(hrScroll.getWidth()/2,hrScroll.getHeight()));

                                if (datafoto.getString("tag").equals("10")){
                                    if (my_akun.equals(userSubmitId) || PremiumAccess){
                                        Picasso.get().load(datafoto.getString("link_foto")).error(R.drawable.errorimage).fit().into(newFoto);
                                        linkFotoPicasso.add(datafoto.getString("link_foto"));
                                    } else {
                                        Picasso.get().load("https://rtlh-poi.000webhostapp.com/Android-Conn/Foto_RTLH/errorimage.png").error(R.drawable.errorimage).fit().into(newFoto);
                                        linkFotoPicasso.add("https://rtlh-poi.000webhostapp.com/Android-Conn/Foto_RTLH/errorimage.png");
                                    }
                                } else {
                                    Picasso.get().load(datafoto.getString("link_foto")).error(R.drawable.errorimage).fit().into(newFoto);
                                    linkFotoPicasso.add(datafoto.getString("link_foto"));
                                }

                                String fotolink=datafoto.getString("link_foto").replace("https://rtlh-poi.000webhostapp.com/Android-Conn/Foto_RTLH/","");
                                strLinkFotoPicasso=strLinkFotoPicasso+","+fotolink;
                                newFoto.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        popupImageInt= (int) newFoto.getTag();
                                        popupImage();
                                    }
                                });
                                layoutImage.addView(newFoto);
                                SetBTNFoto(datafoto.getString("tag"), linkFotoPicasso.size()-1);
                            }
                            progressBar.setVisibility(View.GONE);
                            submitnavigation.getMenu().getItem(1).setEnabled(true);
                            if (my_akun.equals(userSubmitId) || PremiumAccess){
                                submitnavigation.getMenu().getItem(2).setEnabled(true);
                            } else {
                                submitnavigation.getMenu().getItem(2).setEnabled(false); }
                            submitnavigation.setSelectedItemId(R.id.navigation_refresh);
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
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void SetBTNFoto(String fotoTag, int photoPath) {
        if (fotoTag.equals("10")){
            foto_ktp.setText(strlihatfoto);
            foto_ktp.setVisibility(View.VISIBLE);
            link_foto_ktp=photoPath;
        }else if (fotoTag.equals("21")){
            foto_kondisikolom.setText(strlihatfoto);
            foto_kondisikolom.setVisibility(View.VISIBLE);
            link_foto_kondisikolom=photoPath;
        }else if (fotoTag.equals("22")){
            foto_konstruksiatap.setText(strlihatfoto);
            foto_konstruksiatap.setVisibility(View.VISIBLE);
            link_foto_konstruksiatap=photoPath;
        }else if (fotoTag.equals("31")){
            foto_kamarmandi.setText(strlihatfoto);
            foto_kamarmandi.setVisibility(View.VISIBLE);
            link_foto_kamarmandi=photoPath;
        }else if (fotoTag.equals("32")){
            foto_airminum.setText(strlihatfoto);
            foto_airminum.setVisibility(View.VISIBLE);
            link_foto_airminum=photoPath;
        }else if (fotoTag.equals("33")){
            foto_listrik.setText(strlihatfoto);
            foto_listrik.setVisibility(View.VISIBLE);
            link_foto_listrik=photoPath;
        }else if (fotoTag.equals("41")){
            foto_materialatap.setText(strlihatfoto);
            foto_materialatap.setVisibility(View.VISIBLE);
            link_foto_materialatap=photoPath;
        }else if (fotoTag.equals("42")){
            foto_kondisiatap.setText(strlihatfoto);
            foto_kondisiatap.setVisibility(View.VISIBLE);
            link_foto_kondisiatap=photoPath;
        }else if (fotoTag.equals("43")){
            foto_materialdinding.setText(strlihatfoto);
            foto_materialdinding.setVisibility(View.VISIBLE);
            link_foto_materialdinding=photoPath;
        }else if (fotoTag.equals("44")){
            foto_kondisidinding.setText(strlihatfoto);
            foto_kondisidinding.setVisibility(View.VISIBLE);
            link_foto_kondisidinding=photoPath;
        }else if (fotoTag.equals("45")){
            foto_materiallantai.setText(strlihatfoto);
            foto_materiallantai.setVisibility(View.VISIBLE);
            link_foto_materiallantai=photoPath;
        }else if (fotoTag.equals("46")){
            foto_kondisilantai.setText(strlihatfoto);
            foto_kondisilantai.setVisibility(View.VISIBLE);
            link_foto_kondisilantai=photoPath;
        }
    }

    private void popupImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView=getLayoutInflater().inflate(R.layout.popup_image,null);
        final PhotoView popPhoto=popupView.findViewById(R.id.photoPopup);
        final Button btnNext=popupView.findViewById(R.id.popupBtnNext);
        final Button btnBack=popupView.findViewById(R.id.popupBtnBack);
        if (popupImageInt==0){
            btnBack.setVisibility(View.GONE); }
        if (linkFotoPicasso.size()-1==popupImageInt){
            btnNext.setVisibility(View.GONE);}
        Picasso.get().load(linkFotoPicasso.get(popupImageInt)).error(R.drawable.errorimage).into(popPhoto);
        if (linkFotoPicasso.size()==1){
            btnNext.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE); }
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupImageInt++;
                Picasso.get().load(linkFotoPicasso.get(popupImageInt)).error(R.drawable.errorimage).into(popPhoto);
                if (popupImageInt==1){
                    btnBack.setVisibility(View.VISIBLE);
                }
                if (popupImageInt==(linkFotoPicasso.size()-1)){
                    btnNext.setVisibility(View.GONE);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupImageInt--;
                Picasso.get().load(linkFotoPicasso.get(popupImageInt)).error(R.drawable.errorimage).into(popPhoto);
                if (popupImageInt==0){
                    btnBack.setVisibility(View.GONE);
                }
                if (popupImageInt==(linkFotoPicasso.size()-2)){
                    btnNext.setVisibility(View.VISIBLE);
                }
            }
        });

        builder.setView(popupView);
        builder.setNeutralButton(R.string.text_tutup,null);
        final AlertDialog dialog=builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = (dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void loadUser(String id_akun, final String tanggal) {
        String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Akun.php?id_akun="+id_akun;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject akunData = array.getJSONObject(0);
                            String strOleh = "Oleh : @"+akunData.getString("username")+", Pada : "+tanggal;
                            txtOleh.setText(strOleh);
                            userSubmit=akunData.getString("username");
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
        Volley.newRequestQueue(this).add(stringRequest);
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
                            fotoDifferArray.add(akunData.getString("foto_profil"));

                            jumlahUser++;
                            if (jumlahUser<id_DifferArray.size()){
                                loadUserReview();
                            } else {
                                jumlahUser=0;
                                LoadAdapter();
                                progressBarDukungan.setVisibility(View.GONE);
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
        Volley.newRequestQueue(RTLHShow.this).add(stringRequest);
    }

    private void LoadAdapter() {
        for (int i = 0; i < id_akunArray.size(); i++) {
            for (int j = 0; j < id_DifferArray.size(); j++) {
                if (id_akunArray.get(i).equals(id_DifferArray.get(j))){
                    usernameArray.add(unameDifferArray.get(j));
                    fotoArray.add(fotoDifferArray.get(j));
                }
            }
        }
        for(int i = 0; i < id_akunArray.size(); i++){
            reviewList.add(new Review(
                    Integer.parseInt(noArray.get(i)),
                    usernameArray.get(i),
                    tanggalArray.get(i),
                    reviewArray.get(i),
                    dukunganArray.get(i),
                    fotoArray.get(i)
            ));
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ReviewAdapter adapter = new ReviewAdapter(RTLHShow.this, reviewList);
        recyclerView.setAdapter(adapter);
    }

    private void ShowAccount(String txtusername, String txtemail, String txtclass, String txttanggal_registrasi, String txtakses_terakhir, String txtfoto_profil, String txttentang, String txtdatabaru, String txteditdata, String txtdukungan, String txtpenolakan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View unameView= LayoutInflater.from(this).inflate(R.layout.fragment_account,null);
        TextView username=unameView.findViewById(R.id.akun_uname);
        TextView email=unameView.findViewById(R.id.akun_email);
        TextView classname=unameView.findViewById(R.id.akun_class);
        TextView tanggal_registrasi=unameView.findViewById(R.id.akun_tgl_regis);
        TextView akses_terakhir=unameView.findViewById(R.id.akun_aks_trk);
        ImageView foto_profil=unameView.findViewById(R.id.image_profile);
        EditText tentang=unameView.findViewById(R.id.akun_aboutme);
        Button btnFoto=unameView.findViewById(R.id.ubah_foto_btn);
        Button btnSimpan=unameView.findViewById(R.id.simpan_tentang_btn);
        ProgressBar circLoading=unameView.findViewById(R.id.progressBarAccount);
        TextView databaru=unameView.findViewById(R.id.textDataBaru);
        TextView editdata=unameView.findViewById(R.id.textEditData);
        TextView dukungan=unameView.findViewById(R.id.textDukunganAccount);
        TextView penolakan=unameView.findViewById(R.id.textPenolakanAccount);

        btnFoto.setVisibility(View.GONE);
        btnSimpan.setVisibility(View.GONE);
        circLoading.setVisibility(View.GONE);

        String[] databaruJumlah=txtdatabaru.split(",");
        String[] editdataJumlah=txteditdata.split(",");
        String[] dukunganJumlah=txtdukungan.split(",");
        String[] penolakanJumlah=txtpenolakan.split(",");

        username.setText("@"+txtusername);
        email.setText(txtemail);
        if (txtclass.equals("null")){
            classname.setText("[Pengguna Biasa]");
        } else {
            classname.setText("["+txtclass+"]");
        }
        tanggal_registrasi.setText(txttanggal_registrasi);
        akses_terakhir.setText(txtakses_terakhir);
        databaru.setText(String.valueOf(databaruJumlah.length-1));
        editdata.setText(String.valueOf(editdataJumlah.length-1));
        dukungan.setText(String.valueOf(dukunganJumlah.length-1));
        penolakan.setText(String.valueOf(penolakanJumlah.length-1));

        if (!txttentang.equals("null")){
            tentang.setText(txttentang);
        }
        if (!txtfoto_profil.equals("null")){
            Picasso.get().load(txtfoto_profil).error(R.drawable.errorimage).into(foto_profil);
        }
        builder.setView(unameView);
        final AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void loadAccount(final String uname) {
        String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Akun.php?unameUser="+uname;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    JSONObject unameUser = array.getJSONObject(0);
                    if (unameUser.getString("username").equals(uname)){
                        String txtusername=unameUser.getString("username");
                        String txtemail=unameUser.getString("email");
                        String txtclass=unameUser.getString("class_name");
                        String txttanggal_registrasi=unameUser.getString("tanggal_registrasi")+"(Tgl Registrasi)";
                        String txtakses_terakhir=unameUser.getString("akses_terakhir")+"(Akses Terakhir)";
                        String txtfoto_profil=unameUser.getString("foto_profil");
                        String txttentang=unameUser.getString("tentang");
                        String txtdatabaru=unameUser.getString("rtlh_submit");
                        String txteditdata=unameUser.getString("rtlh_edit");
                        String txtdukungan=unameUser.getString("rtlh_dukung");
                        String txtpenolakan=unameUser.getString("rtlh_tolak");

                        ShowAccount(txtusername, txtemail, txtclass, txttanggal_registrasi, txtakses_terakhir, txtfoto_profil, txttentang, txtdatabaru, txteditdata, txtdukungan, txtpenolakan);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RTLHShow.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                errorUname++;
                if (errorUname>=3){
                    errorUname=0;
                    Toast.makeText(RTLHShow.this, "Gagal Mendapatkan Nama Pengirim Data", Toast.LENGTH_SHORT).show();
                }else {
                    loadAccount(uname);
                }
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.on_back_pressed, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
