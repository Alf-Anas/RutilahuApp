package com.lihatpeta.prototype;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

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
import java.util.Map;
import java.util.Objects;

public class RTLHSubmit extends AppCompatActivity implements View.OnClickListener {

    boolean FirstLoad=true;
    static final int REQUEST_TAKE_PHOTO = 1, REQUEST_IMAGE_CAPTURE = 1;
    String imageFileName, currentPhotoPath;
    Bitmap bitmaps;
    ArrayList<String> FotoPathList = new ArrayList<>();
    ArrayList<String> NamePathList = new ArrayList<>();
    ArrayList<String> idFotoList = new ArrayList<>();
    ArrayList<String> FotoTagList = new ArrayList<>();
    int JumlahFoto, FotoUploaded=0, popupImageInt, errorLoad=0, volleyNumber;

    TextView txtKoordinat;
    String latitude, longitude, id_akun, newID, FotoTag;
    LinearLayout layoutImage, opsionalLayout;
    TextView uploadGambarText, uploadDataText, simpanDataText;
    HorizontalScrollView hrScroll;

    Button Ambil_Foto;
    Button foto_ktp, foto_kondisikolom, foto_konstruksiatap, foto_kamarmandi, foto_airminum, foto_listrik, foto_materialatap, foto_kondisiatap, foto_materialdinding, foto_kondisidinding, foto_materiallantai, foto_kondisilantai;
    Integer link_foto_ktp, link_foto_kondisikolom, link_foto_konstruksiatap, link_foto_kamarmandi, link_foto_airminum, link_foto_listrik, link_foto_materialatap, link_foto_kondisiatap, link_foto_materialdinding, link_foto_kondisidinding, link_foto_materiallantai, link_foto_kondisilantai;
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
                    kembali();
                    return true;
                case R.id.navigation_refresh:
                    if (FirstLoad){
                        FirstLoad=false;
                    }
                    else {
                        reset();
                    }
                    return true;
                case R.id.navigation_submit:
                    SubmitData();
                    return true;
            }
            return false;
        }
    };

    private void kembali() {
        JumlahFoto=FotoPathList.size();
        if (!Nama_Penghuni.getText().toString().isEmpty() || JumlahFoto!=0){
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
        } else {
            finish();
        }
    }

    private void SubmitData() {
        JumlahFoto=FotoPathList.size();
        if (!Nama_Penghuni.getText().toString().isEmpty() && JumlahFoto!=0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Anda Yakin Ingin Mengirim Data?")
                    .setTitle("KIRIM DATA");
            builder.setPositiveButton(R.string.title_submit, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
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
        } else {
            Nama_Penghuni.setHintTextColor(getResources().getColor(R.color.colorRed));
            Ambil_Foto.setTextColor(getResources().getColor(R.color.colorRed));
            Toast.makeText(RTLHSubmit.this,"Lengkapi Isian Wajib",Toast.LENGTH_LONG).show();
        }
    }

    private void reset() {
        JumlahFoto=FotoPathList.size();
        if (!Nama_Penghuni.getText().toString().isEmpty() || JumlahFoto!=0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Semua Data yang telah Anda Masukan Akan Hilang")
                    .setTitle("RESET DATA");
            builder.setPositiveButton(R.string.title_refresh, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent ResetIntent=new Intent(RTLHSubmit.this,RTLHSubmit.class);
                    startActivity(ResetIntent);
                    Toast.makeText(RTLHSubmit.this,R.string.title_refresh,Toast.LENGTH_LONG).show();
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
        } else {
            Intent ResetIntent=new Intent(RTLHSubmit.this,RTLHSubmit.class);
            startActivity(ResetIntent);
            Toast.makeText(RTLHSubmit.this,R.string.title_refresh,Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void errorVolley() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.no_internet_connection);

        builder.setPositiveButton(R.string.text_cobalagi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                errorLoad=0;
                if (volleyNumber==1){
                    PostRTLH();
                } else if (volleyNumber==2){
                    SetOpsional();
                } else if (volleyNumber==3){
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtlhsubmit);

        BottomNavigationView submitnavigation =findViewById(R.id.navigation_rtlhsubmit);
        submitnavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        submitnavigation.setSelectedItemId(R.id.navigation_refresh);

        hrScroll=findViewById(R.id.horizontal_scroll);
        layoutImage = findViewById(R.id.linearImageRTLH);

        LinearLayout RTLHOpsional=findViewById(R.id.linearRTLHOpsional);
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

        txtKoordinat=findViewById(R.id.rtlhtextKoordinat);
        Ambil_Foto=findViewById(R.id.rtlh_btn_foto);
        Ambil_Foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FotoTag="0";
                TakePicture();
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

        SharedPreferences prefsID = getSharedPreferences("AKUN_SETTING", MODE_PRIVATE);
        id_akun = prefsID.getString("id_akun", "");
        SharedPreferences prefs = getSharedPreferences("KOORDINAT", MODE_PRIVATE);
        String restoredText = prefs.getString("latitude", null);
        if (restoredText != null) {
            latitude = prefs.getString("latitude", "");
            longitude = prefs.getString("longitude", "");

            double dbLatitude = Double.parseDouble(latitude);
            double dbLongitude = Double.parseDouble(longitude);

            String strBujur, strLintang;

            if (dbLongitude>0){
                strBujur=" BT";

            } else {
                strBujur=" BB";
                dbLongitude=dbLongitude*(-1);
            }

            if (dbLatitude>0){
                strLintang=" LU";
            } else {
                strLintang=" LS";
                dbLatitude=dbLatitude*(-1);
            }

            String strKoordinat = dbLongitude +strBujur+", "+ dbLatitude +strLintang;
            txtKoordinat.setText(strKoordinat);
            Desa_Kel.setText(prefs.getString("desa_kel", ""));
            Kecamatan.setText(prefs.getString("kecamatan", ""));
            Kab_Kota.setText(prefs.getString("kab_kota", ""));
            Provinsi.setText(prefs.getString("provinsi", ""));
            KodePos.setText(prefs.getString("kodepos", ""));
        }

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rtlh_btn_foto_ktp:
                if (foto_ktp.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_ktp;
                    popupImage();
                } else {
                    FotoTag="10";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_kolom:
                if (foto_kondisikolom.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_kondisikolom;
                    popupImage();
                } else {
                    FotoTag="21";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_konstruksi:
                if (foto_konstruksiatap.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_konstruksiatap;
                    popupImage();
                } else {
                    FotoTag="22";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_kamarmandi:
                if (foto_kamarmandi.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_kamarmandi;
                    popupImage();
                } else {
                    FotoTag="31";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_airminum:
                if (foto_airminum.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_airminum;
                    popupImage();
                } else {
                    FotoTag="32";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_listrik:
                if (foto_listrik.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_listrik;
                    popupImage();
                } else {
                    FotoTag="33";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_materialatap:
                if (foto_materialatap.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_materialatap;
                    popupImage();
                } else {
                    FotoTag="41";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_kondisiatap:
                if (foto_kondisiatap.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_kondisiatap;
                    popupImage();
                } else {
                    FotoTag="42";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_materialdinding:
                if (foto_materialdinding.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_materialdinding;
                    popupImage();
                } else {
                    FotoTag="43";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_kondisidinding:
                if (foto_kondisidinding.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_kondisidinding;
                    popupImage();
                } else {
                    FotoTag="44";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_materiallantai:
                if (foto_materiallantai.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_materiallantai;
                    popupImage();
                } else {
                    FotoTag="45";
                    TakePicture();
                }
                break;
            case R.id.rtlh_btn_foto_kondisilantai:
                if (foto_kondisilantai.getText().toString().equals("Lihat Foto")){
                    popupImageInt=link_foto_kondisilantai;
                    popupImage();
                } else {
                    FotoTag="46";
                    TakePicture();
                }
                break;
            default:
                break;
        }
    }

    private void TakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(RTLHSubmit.this,"Gagal Mengambil Foto",Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(RTLHSubmit.this,
                        "com.lihatpeta.prototype.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = id_akun + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
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
            SetBTNFoto(FotoTag,FotoPathList.size()-1);

            final ImageView newFoto = new ImageView(this);
            newFoto.setId(FotoPathList.size());
            newFoto.setTag(FotoPathList.size()-1);
            newFoto.setPadding(2, 2, 2, 2);
            newFoto.setImageBitmap(bitmaps);
            newFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupImageInt= (int) newFoto.getTag();
                    popupImage();
                }
            });
            layoutImage.addView(newFoto);
        }
    }

    private void SetBTNFoto(String fotoTag, int photoPath) {
        if (fotoTag.equals("10")){
            foto_ktp.setText("Lihat Foto");
            link_foto_ktp=photoPath;
        }else if (fotoTag.equals("21")){
            foto_kondisikolom.setText("Lihat Foto");
            link_foto_kondisikolom=photoPath;
        }else if (fotoTag.equals("22")){
            foto_konstruksiatap.setText("Lihat Foto");
            link_foto_konstruksiatap=photoPath;
        }else if (fotoTag.equals("31")){
            foto_kamarmandi.setText("Lihat Foto");
            link_foto_kamarmandi=photoPath;
        }else if (fotoTag.equals("32")){
            foto_airminum.setText("Lihat Foto");
            link_foto_airminum=photoPath;
        }else if (fotoTag.equals("33")){
            foto_listrik.setText("Lihat Foto");
            link_foto_listrik=photoPath;
        }else if (fotoTag.equals("41")){
            foto_materialatap.setText("Lihat Foto");
            link_foto_materialatap=photoPath;
        }else if (fotoTag.equals("42")){
            foto_kondisiatap.setText("Lihat Foto");
            link_foto_kondisiatap=photoPath;
        }else if (fotoTag.equals("43")){
            foto_materialdinding.setText("Lihat Foto");
            link_foto_materialdinding=photoPath;
        }else if (fotoTag.equals("44")){
            foto_kondisidinding.setText("Lihat Foto");
            link_foto_kondisidinding=photoPath;
        }else if (fotoTag.equals("45")){
            foto_materiallantai.setText("Lihat Foto");
            link_foto_materiallantai=photoPath;
        }else if (fotoTag.equals("46")){
            foto_kondisilantai.setText("Lihat Foto");
            link_foto_kondisilantai=photoPath;
        }
    }

    private void popupImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView=getLayoutInflater().inflate(R.layout.popup_image,null);
        final PhotoView popPhoto=popupView.findViewById(R.id.photoPopup);
        final Button btnNext=popupView.findViewById(R.id.popupBtnNext);
        final Button btnBack=popupView.findViewById(R.id.popupBtnBack);
        Picasso.get().load("file://"+FotoPathList.get(popupImageInt)).error(R.drawable.errorimage).into(popPhoto);
        if (popupImageInt==0){
            btnBack.setVisibility(View.GONE); }
        if (FotoPathList.size()-1==popupImageInt){
            btnNext.setVisibility(View.GONE);}
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupImageInt++;
                Picasso.get().load("file://"+FotoPathList.get(popupImageInt)).error(R.drawable.errorimage).into(popPhoto);
                if (popupImageInt==1){
                    btnBack.setVisibility(View.VISIBLE);
                }
                if (popupImageInt==(FotoPathList.size()-1)){
                    btnNext.setVisibility(View.GONE);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupImageInt--;
                Picasso.get().load("file://"+FotoPathList.get(popupImageInt)).error(R.drawable.errorimage).into(popPhoto);
                if (popupImageInt==0){
                    btnBack.setVisibility(View.GONE);
                }
                if (popupImageInt==(FotoPathList.size()-2)){
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

    private void uploadImage() {
        String UploadURL="https://rtlh-poi.000webhostapp.com/Android-Conn/Post_Foto.php";
        StringRequest stringRequest =new StringRequest(Request.Method.POST, UploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(RTLHSubmit.this,Response,Toast.LENGTH_SHORT).show();
                    FotoUploaded++;
                    uploadFoto();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RTLHSubmit.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                volleyNumber=3;
                errorLoad++;
                Handler loadHandler = new Handler();
                Runnable loadRunnable=new Runnable() {
                    @Override
                    public void run() {
                        if (errorLoad>=5){
                            errorVolley();
                        } else {
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

                int no_foto=Integer.parseInt(newID.replaceAll("[\\D]", ""));
                String strno_foto=String.valueOf(no_foto);
                String timeStamp=idFotoList.get(FotoUploaded);
                String id_foto ="FOTO"+strno_foto+"_"+timeStamp;
                String myfototag=FotoTagList.get(FotoUploaded);

                params.put("id_foto",id_foto);
                params.put("id_rtlh",newID);
                params.put("id_akun",id_akun);
                params.put("tanggal",strDate);
                params.put("image",imageToString(bitmaps));
                params.put("tag",myfototag);
                return params;
            }
        };
        ProfileUpload.getInstance(RTLHSubmit.this).addToRequestQueue(stringRequest);
    }

    private String imageToString(Bitmap bitmaps){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmaps.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] imgBytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }

    private void uploadFoto() {
        uploadGambarText.setText("Mengunggah Gambar : "+FotoUploaded+"/"+JumlahFoto);
        if (FotoUploaded<JumlahFoto){
            currentPhotoPath=NamePathList.get(FotoUploaded);
            bitmaps = BitmapFactory.decodeFile(FotoPathList.get(FotoUploaded));
            uploadImage();
        } else {
            Toast.makeText(RTLHSubmit.this,R.string.text_submit_success,Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = getSharedPreferences("FIRSTINSTALL", MODE_PRIVATE).edit();
            editor.putBoolean("reloadrtlh", true).apply();

            /*File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (Objects.requireNonNull(dir).isDirectory())
            {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++)
                {
                    new File(dir, children[i]).delete();
                }
            }*/
            finish();
        }
    }

    private void PostRTLH() {
        String UploadURL="https://rtlh-poi.000webhostapp.com/Android-Conn/Post_RTLH.php";
        StringRequest stringRequest =new StringRequest(Request.Method.POST, UploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    newID=jsonObject.getString("newID");
                    Toast.makeText(RTLHSubmit.this,Response,Toast.LENGTH_SHORT).show();
                    uploadDataText.setText("Mengirim Data : OK");
                    SetOpsional();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RTLHSubmit.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                volleyNumber=1;
                errorLoad++;
                Handler loadHandler = new Handler();
                Runnable loadRunnable=new Runnable() {
                    @Override
                    public void run() {
                        if (errorLoad>=5){
                            errorVolley();
                        } else {
                            PostRTLH();
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
                int no_akun=Integer.parseInt(id_akun.replaceAll("[\\D]", ""));

                params.put("no_akun", String.valueOf(no_akun));
                params.put("id_akun",id_akun);
                params.put("lat",latitude);
                params.put("lon",longitude);
                params.put("tanggal",strDate);
                params.put("foto",NamePathList.get(0));
                params.put("nama_penghuni",Nama_Penghuni.getText().toString());
                return params;
            }
        };
        ProfileUpload.getInstance(RTLHSubmit.this).addToRequestQueue(stringRequest);
    }

    private void SetOpsional() {
        String UploadURL="https://rtlh-poi.000webhostapp.com/Android-Conn/Set_RTLH.php";
        StringRequest stringRequest =new StringRequest(Request.Method.POST, UploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(RTLHSubmit.this,Response,Toast.LENGTH_SHORT).show();
                    simpanDataText.setText("Menyimpan Data : OK");
                    uploadFoto();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RTLHSubmit.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                volleyNumber=2;
                errorLoad++;
                Handler loadHandler = new Handler();
                Runnable loadRunnable=new Runnable() {
                    @Override
                    public void run() {
                        if (errorLoad>=5){
                            errorVolley();
                        } else {
                            SetOpsional();
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

                params.put("id_akun",id_akun);
                params.put("id_rtlh",newID);
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
                params.put("pengedit","");
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
        ProfileUpload.getInstance(RTLHSubmit.this).addToRequestQueue(stringRequest);
    }

    private void upLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View uploadView=getLayoutInflater().inflate(R.layout.upload_layout,null);
        uploadGambarText=uploadView.findViewById(R.id.textUploadGambar);
        uploadDataText=uploadView.findViewById(R.id.textPostRTLH);
        simpanDataText=uploadView.findViewById(R.id.textSetRTLH);

        builder.setView(uploadView);

        final AlertDialog dialog=builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        PostRTLH();
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
