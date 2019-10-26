package com.lihatpeta.prototype;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class FragmentAccount extends Fragment {
    private final int IMG_REQUEST=1;
    private Bitmap bitmaps;
    ProgressBar circLoading;
    TextView username, email, tanggal_registrasi, akses_terakhir, databaru, editdata, dukungan, penolakan, accountclass;
    EditText tentang;
    String txtid_akun, txtusername, txtclass, txtclassname, txttimelimit, txtemail, txttanggal_registrasi, txtakses_terakhir, txtfoto_profil, txttentang, txtdatabaru, txteditdata, txtdukungan, txtpenolakan;
    ImageView foto_profil;
    Button btnFoto, btnSimpan, btnAngkat;
    Boolean tentangBtn=false;
    int errorLoad=0;
    String strNamaKegiatan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_account,container,false);
        username=view.findViewById(R.id.akun_uname);
        accountclass=view.findViewById(R.id.akun_class);
        email=view.findViewById(R.id.akun_email);
        tanggal_registrasi=view.findViewById(R.id.akun_tgl_regis);
        akses_terakhir=view.findViewById(R.id.akun_aks_trk);
        foto_profil=view.findViewById(R.id.image_profile);
        tentang=view.findViewById(R.id.akun_aboutme);
        btnFoto=view.findViewById(R.id.ubah_foto_btn);
        btnSimpan=view.findViewById(R.id.simpan_tentang_btn);
        circLoading=view.findViewById(R.id.progressBarAccount);
        databaru=view.findViewById(R.id.textDataBaru);
        editdata=view.findViewById(R.id.textEditData);
        dukungan=view.findViewById(R.id.textDukunganAccount);
        penolakan=view.findViewById(R.id.textPenolakanAccount);
        btnAngkat=view.findViewById(R.id.buttonSurveyor);

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UbahFoto();
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tentangBtn){
                    btnSimpan.setText("Ubah");
                    tentang.setEnabled(false);
                    tentangBtn=false;
                    SimpanTentang();
                } else {
                    btnSimpan.setText("Simpan");
                    tentang.setEnabled(true);
                    tentang.setFocusable(true);
                    tentangBtn=true;
                }
            }
        });
        btnAngkat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                angkatsurveyor();
            }
        });

        MainActivity myAct = (MainActivity) getActivity();
        txtemail= Objects.requireNonNull(myAct).getEmailData();
        email.setText(txtemail);

        loadAccount();
        return view;
    }

    private void angkatsurveyor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        View angkatView=getLayoutInflater().inflate(R.layout.angkat_layout,null);
        final EditText angkatEditText=angkatView.findViewById(R.id.edit_angkatsurveyor);
        final EditText namakegiatanEditText=angkatView.findViewById(R.id.editTextNamaKegiatan);
        final EditText timelimit=angkatView.findViewById(R.id.editTextTimeLimit);
        builder.setView(angkatView);
        builder.setPositiveButton(R.string.account_angkat,null);
        builder.setNegativeButton(R.string.text_batal,null);

        final AlertDialog dialog=builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button buttonBack = (dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (angkatEditText.getText().toString().isEmpty() || namakegiatanEditText.getText().toString().isEmpty() || timelimit.getText().toString().isEmpty() || Integer.parseInt(timelimit.getText().toString())>30){
                            Toast.makeText(getActivity(), "Masukan Nama Pengguna yang ingin diangkat, Nama Kegiatan Pendataan dan Maximum Pengangkatan tidak lebih dari 30 Hari", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String newsurveyor=angkatEditText.getText().toString();
                            strNamaKegiatan=namakegiatanEditText.getText().toString();
                            String[] separated = newsurveyor.split(",");
                            int time_limit=Integer.parseInt(timelimit.getText().toString());
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String strDate = sdf.format(calendar.getTime());
                            inputKegiatan(txtid_akun, strDate, strNamaKegiatan, time_limit, newsurveyor);
                            try {
                                Date time_now = sdf.parse(strDate);
                                calendar.setTime(time_now);
                                calendar.add(Calendar.DAY_OF_YEAR, +time_limit);
                                String strTimeLimit = sdf.format(calendar.getTime());
                                pengangkatan(separated, separated.length, 0, strTimeLimit);
                                dialog.dismiss();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                buttonBack.setOnClickListener(new View.OnClickListener() {
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
    }

    private void inputKegiatan(String txtid_akun, String strDate, String strNamaKegiatan, int time_limit, String newsurveyor) {
        String ServerURL = "https://rtlh-poi.000webhostapp.com/Android-Conn/Post_Kegiatan.php" ;
        StringRequest stringRequest =new StringRequest(Request.Method.POST, ServerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(getActivity(),Response,Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),R.string.no_internet_connection,Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("id_akun",txtid_akun);
                params.put("tanggal",strDate);
                params.put("nama_kegiatan",strNamaKegiatan);
                params.put("waktu", String.valueOf(time_limit));
                params.put("peserta",newsurveyor);
                return params;
            }
        };
        ProfileUpload.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void pengangkatan(final String[] newsurveyor, final int length, final int i, final String strTimeLimit) {
        final String newusername=newsurveyor[i];
        String ServerURL = "https://rtlh-poi.000webhostapp.com/Android-Conn/Set_Class.php" ;
        StringRequest stringRequest =new StringRequest(Request.Method.POST, ServerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(getActivity(),Response,Toast.LENGTH_SHORT).show();
                    if ((i+1)<length){
                        pengangkatan(newsurveyor,length,i+1,strTimeLimit);
                    } else {
                        Toast.makeText(getActivity(),"Surveyor Telah Selesai Diangkat",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),R.string.no_internet_connection,Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("username",newusername);
                params.put("time_limit",strTimeLimit);
                params.put("nama_kegiatan",strNamaKegiatan);
                return params;
            }
        };
        ProfileUpload.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void loadAccount() {
        String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Akun.php?emailUser="+txtemail;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    JSONObject emailUser = array.getJSONObject(0);
                    String Response=emailUser.getString("email");
                    if (Response.equals(txtemail)){
                        txtid_akun=emailUser.getString("id_akun");
                        txtusername=emailUser.getString("username");
                        txtclass=emailUser.getString("class");
                        txtclassname=emailUser.getString("class_name");
                        txttimelimit=emailUser.getString("time_limit");
                        txttanggal_registrasi=emailUser.getString("tanggal_registrasi")+"(Tgl Registrasi)";
                        txtakses_terakhir=emailUser.getString("akses_terakhir")+"(Akses Terakhir)";
                        txtfoto_profil=emailUser.getString("foto_profil");
                        txttentang=emailUser.getString("tentang");
                        txtdatabaru=emailUser.getString("rtlh_submit");
                        txteditdata=emailUser.getString("rtlh_edit");
                        txtdukungan=emailUser.getString("rtlh_dukung");
                        txtpenolakan=emailUser.getString("rtlh_tolak");

                        String[] databaruJumlah=txtdatabaru.split(",");
                        String[] editdataJumlah=txteditdata.split(",");
                        String[] dukunganJumlah=txtdukungan.split(",");
                        String[] penolakanJumlah=txtpenolakan.split(",");

                        SharedPreferences.Editor editor = Objects.requireNonNull(getActivity()).getSharedPreferences("AKUN_SETTING", MODE_PRIVATE).edit();
                        username.setText("@"+ txtusername);
                        editor.putString("class",txtclass);
                        editor.putString("time_limit",txttimelimit);
                        if (!txtclass.equals("null")){
                            accountclass.setText("["+txtclassname+"]");
                            editor.putString("class_name","["+txtclassname+"]");
                            editor.putBoolean("PremiumAccess", true);
                            editor.apply();
                        } else {
                            accountclass.setText("[Pengguna Biasa]");
                            editor.putString("class_name","[Pengguna Biasa]");
                            editor.putBoolean("PremiumAccess", false);
                            editor.apply();
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
                            Picasso.get().load(txtfoto_profil).error(R.drawable.errorimage).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).fit().centerCrop().into(foto_profil);
                        }
                        if (txtclass.equals("2") || txtclass.equals("3")){
                            btnAngkat.setVisibility(View.VISIBLE);
                        }
                        circLoading.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                errorLoad++;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (errorLoad>=3){
                            errorVolley();
                        } else {
                            loadAccount();
                        }
                    }
                }, 2000);
            }
        });
        Volley.newRequestQueue(Objects.requireNonNull(getContext())).add(stringRequest);
    }

    private void errorVolley() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle(R.string.no_internet_connection);
        builder.setPositiveButton(R.string.text_cobalagi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                errorLoad=0;
                loadAccount();
            }
        });
        builder.setNegativeButton(R.string.text_tutup, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                circLoading.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    private void UbahFoto() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    private void SimpanTentang() {
        circLoading.setVisibility(View.VISIBLE);
        txttentang=tentang.getText().toString();

        String UploadURL="https://rtlh-poi.000webhostapp.com/Android-Conn/Set_About.php";
        StringRequest stringRequest =new StringRequest(Request.Method.POST, UploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(getActivity(),Response,Toast.LENGTH_LONG).show();
                    circLoading.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                circLoading.setVisibility(View.GONE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("id_akun",txtid_akun);
                params.put("tentang",txttentang);
                return params;
            }
        };
        ProfileUpload.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==IMG_REQUEST && resultCode==RESULT_OK && data!=null)
        {
            Uri path=data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getApplicationContext().getContentResolver().openInputStream(Objects.requireNonNull(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmaps = BitmapFactory.decodeStream(imageStream);
            foto_profil.setImageBitmap(bitmaps);
            uploadProfile();
        }
    }

    private void uploadProfile() {
        circLoading.setVisibility(View.VISIBLE);
        String UploadURL="https://rtlh-poi.000webhostapp.com/Android-Conn/Upload_Profile.php";
        StringRequest stringRequest =new StringRequest(Request.Method.POST, UploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(getActivity(),Response,Toast.LENGTH_LONG).show();
                    loadAccount();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                circLoading.setVisibility(View.GONE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("id_akun",txtid_akun);
                params.put("image",imageToString(bitmaps));
                return params;
            }
        };
        ProfileUpload.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private String imageToString(Bitmap bitmaps){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmaps.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] imgBytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }

}
