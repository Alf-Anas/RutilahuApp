package com.lihatpeta.prototype;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Splash extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    boolean sendUname=true;
    ProgressBar circLoading;
    Double versiDouble=0.0901;
    String email, code, uname;
    int errorLoad=0, volleyNumber;

    private void errorVolley(final int volleyNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.no_internet_connection);

        builder.setPositiveButton(R.string.text_cobalagi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                errorLoad=0;
                if (volleyNumber==1){
                    checkVersion();
                } else if (volleyNumber==2){
                    sendVerif();
                } else if (volleyNumber==3){
                    checkEmail();
                } else if (volleyNumber==4){
                    checkUname();
                }
            }
        });
        builder.setNegativeButton(R.string.text_keluar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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
        setContentView(R.layout.activity_splash);

        circLoading=findViewById(R.id.progressBar);
        circLoading.setVisibility(View.GONE);

        SharedPreferences prefs = getSharedPreferences("FIRSTINSTALL", MODE_PRIVATE);
        boolean agreement = prefs.getBoolean("agreement", false);
        if (agreement){
            checkVersion(); }
        else {
            licenseAgreement(); }
    }

    private void licenseAgreement() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.text_license_agreement)+"\n \n"+
                getString(R.string.text_license_agreement1)+"\n"+
                getString(R.string.text_license_agreement2)+"\n"+
                getString(R.string.text_license_agreement3)+"\n"+
                getString(R.string.text_license_agreement4))
                .setTitle(R.string.text_license);

        builder.setPositiveButton(R.string.text_setuju, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor editor = getSharedPreferences("FIRSTINSTALL", MODE_PRIVATE).edit();
                editor.putBoolean("agreement", true).apply();
                checkVersion();
            }
        });
        builder.setNegativeButton(R.string.text_tidak_setuju, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    private void checkVersion() {
        String URL_DATA="https://rtlh-poi.000webhostapp.com/Android-Conn/Load_Version.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject api_version = array.getJSONObject(0);

                            Double versi=api_version.getDouble("versi");
                            String pesan=api_version.getString("pesan");
                            String deskripsi=api_version.getString("deskripsi");
                            String link_gplay=api_version.getString("link_gplay");
                            int wajib=api_version.getInt("wajib");
                            String tanggal_pembaharuan=api_version.getString("tanggal_pembaharuan");

                            if (versi.equals(versiDouble)){
                                newInstalls();
                            }
                            else {
                                updateAps(versi, pesan, deskripsi, link_gplay, wajib, tanggal_pembaharuan);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Splash.this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                        volleyNumber=1;
                        errorLoad++;
                        Handler loadHandler = new Handler();
                        Runnable loadRunnable=new Runnable() {
                            @Override
                            public void run() {
                                if (errorLoad>=3){
                                    errorVolley(volleyNumber);
                                } else {
                                    checkVersion();
                                }
                            }
                        };
                        loadHandler.postDelayed(loadRunnable, 3000);
                    }
                });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void newInstalls() {
        SharedPreferences prefs = getSharedPreferences("AKUN_SETTING", MODE_PRIVATE);
        String restoredText = prefs.getString("id_akun", null);
        if (restoredText != null) {
            String username = prefs.getString("username", "");
            String accountclass = prefs.getString("class", "");
            String time_limit = prefs.getString("time_limit", "");

            Calendar c=Calendar.getInstance();
            int waktu=c.get(Calendar.HOUR_OF_DAY);
            String salam= "";
            if (waktu>=0 && waktu<6){
                salam="Selamat Dini Hari";
            } else if (waktu>=6 && waktu<12){
                salam="Selamat Pagi";
            } else if (waktu>=12 && waktu<15){
                salam="Selamat Siang";
            } else if (waktu>=15 && waktu<18){
                salam="Selamat Sore";
            } else if (waktu>=18 && waktu<24){
                salam="Selamat Malam";
            }
            Toast.makeText(Splash.this, salam+" @"+username, Toast.LENGTH_LONG).show();

            SharedPreferences.Editor editor = getSharedPreferences("AKUN_SETTING", MODE_PRIVATE).edit();

            if (Objects.requireNonNull(accountclass).equals("1")){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = dateFormat.format(c.getTime());
                try {
                    Date Surveyor_time_limit = dateFormat.parse(time_limit);
                    Date time_now = dateFormat.parse(strDate);
                    if (Surveyor_time_limit.compareTo(time_now)<0){
                        editor.putBoolean("PremiumAccess", false).apply();
                        resetSurveyor(username);
                    } else {
                        editor.putBoolean("PremiumAccess", true).apply();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (accountclass.equals("3") || accountclass.equals("2")){
                editor.putBoolean("PremiumAccess", true).apply();
            } else {
                editor.putBoolean("PremiumAccess", false).apply();
            }

            mainMenu();
        }
        else{
            emailDialog();
        }
    }

    private void resetSurveyor(final String username) {
        String ServerURL = "https://rtlh-poi.000webhostapp.com/Android-Conn/Reset_Class.php" ;
        StringRequest stringRequest =new StringRequest(Request.Method.POST, ServerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(Splash.this, Response, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Splash.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("username",username);
                return params;
            }
        };
        ProfileUpload.getInstance(Splash.this).addToRequestQueue(stringRequest);
    }

    private void emailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View emailView=getLayoutInflater().inflate(R.layout.email_layout,null);
        final EditText emailEditText=emailView.findViewById(R.id.email);

        builder.setView(emailView);
        builder.setPositiveButton(R.string.text_lanjut,null);

        final AlertDialog dialog=builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (emailEditText.getText().toString().isEmpty()){
                            Toast.makeText(Splash.this, "Masukan Email Anda", Toast.LENGTH_SHORT).show();
                        }
                        else if (!isValidEmail(emailEditText.getText().toString())){
                            Toast.makeText(Splash.this, "Email yang Anda masukan tidak benar", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            email=emailEditText.getText().toString();
                            Random r = new Random();
                            int rCode = r.nextInt(8999) + 1000;
                            code=String.valueOf(rCode);
                            sendVerif();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void sendVerif() {
        circLoading.setVisibility(View.VISIBLE);
        String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Email_Sender.php?email="+email+"&code="+code;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(Splash.this, Response, Toast.LENGTH_SHORT).show();
                    circLoading.setVisibility(View.GONE);
                    verifDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyNumber=2;
                Toast.makeText(Splash.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                errorVolley(volleyNumber);
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void verifDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View verifView=getLayoutInflater().inflate(R.layout.verif_layout,null);
        final EditText verifEditText=verifView.findViewById(R.id.verif);

        builder.setView(verifView);
        builder.setPositiveButton(R.string.text_lanjut,null);
        builder.setNegativeButton(R.string.text_kembali, null);

        final AlertDialog dialog=builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (verifEditText.getText().toString().isEmpty()){
                            Toast.makeText(Splash.this, "Masukan Kode Verifikasi Anda", Toast.LENGTH_SHORT).show();
                        }
                        else if (verifEditText.getText().toString().equals(code)){
                            Toast.makeText(Splash.this, "Kode Verifikasi diterima", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            checkEmail();
                        }
                        else {
                            Toast.makeText(Splash.this, "Kode Verifikasi Salah", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Button backbutton = (dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                backbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        emailDialog();
                    }
                });
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    private void checkEmail() {
        circLoading.setVisibility(View.VISIBLE);
        String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Akun.php?emailUser="+email;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    JSONObject emailUser = array.getJSONObject(0);
                    String Response=emailUser.getString("email");
                    if (Response.equals(email)){
                        String id_akun=emailUser.getString("id_akun");
                        String newemail=emailUser.getString("email");
                        String username=emailUser.getString("username");
                        int nomor_acak=emailUser.getInt("nomor_acak");
                        String lastLat=emailUser.getString("lat");
                        String lastLon=emailUser.getString("lon");
                        String accountclass=emailUser.getString("class");
                        String time_limit=emailUser.getString("time_limit");
                        String classname=emailUser.getString("class_name");
                        if (emailUser.getString("class_name").equals("null")){
                            classname="Pengguna Biasa"; }

                        saveAccount(id_akun, newemail, username, nomor_acak, lastLat, lastLon, accountclass, classname, time_limit);

                        newInstalls();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    unameDialog();
                    circLoading.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyNumber=3;
                Toast.makeText(Splash.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                errorVolley(volleyNumber);
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void unameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View unameView=getLayoutInflater().inflate(R.layout.uname_layout,null);
        final EditText unameEditText=unameView.findViewById(R.id.uname);

        builder.setView(unameView);
        builder.setPositiveButton(R.string.text_lanjut,null);
        builder.setNegativeButton(R.string.text_kembali, null);

        final AlertDialog dialog=builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (unameEditText.getText().toString().isEmpty()){
                            Toast.makeText(Splash.this, "Masukan Nama Pengguna Anda", Toast.LENGTH_SHORT).show();
                        }
                        else if (unameEditText.getText().toString().length()<=3){
                            Toast.makeText(Splash.this, "Nama Pengguna Anda Terlalu Singkat", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (sendUname){
                                uname=unameEditText.getText().toString();
                                checkUname();
                                sendUname=false;
                            }
                        }
                    }
                });
                Button backbutton = (dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                backbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        verifDialog();
                    }
                });
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    private void checkUname() {
        circLoading.setVisibility(View.VISIBLE);
        String URL_DATA = "https://rtlh-poi.000webhostapp.com/Android-Conn/Get_Akun.php?unameUser="+uname;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    JSONObject unameUser = array.getJSONObject(0);
                    String Response=unameUser.getString("username");
                    if (Response.equals(uname) || Response.toLowerCase().equals(uname.toLowerCase())){
                        Toast.makeText(Splash.this, "Nama Pengguna Telah digunakan", Toast.LENGTH_LONG).show();
                        circLoading.setVisibility(View.GONE);
                        sendUname=true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    newAccount();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyNumber=4;
                Toast.makeText(Splash.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                errorVolley(volleyNumber);
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public void newAccount() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        Random r = new Random();
        int noAcak = r.nextInt(89999999) + 10000000;

        String TempEmail = email;
        String TempUname = uname;
        String TempNoAcak = String.valueOf(noAcak);

        InsertAccount(TempEmail, TempUname, TempNoAcak, strDate);
    }

    public void InsertAccount(final String tempEmail, final String tempUname, final String tempNoAcak, final String tempTanggal) {
        String ServerURL = "https://rtlh-poi.000webhostapp.com/Android-Conn/Post_Akun.php" ;
        StringRequest stringRequest =new StringRequest(Request.Method.POST, ServerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(Splash.this,Response,Toast.LENGTH_LONG).show();
                    checkEmail();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Splash.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("email",tempEmail);
                params.put("username",tempUname);
                params.put("nomor_acak",tempNoAcak);
                params.put("tanggal_registrasi",tempTanggal);
                return params;
            }
        };
        ProfileUpload.getInstance(Splash.this).addToRequestQueue(stringRequest);
    }

    private void saveAccount(String id_akun, String newemail, String username, int nomor_acak, String lastLat, String lastLon, String accountclass, String classname, String time_limit) {
        SharedPreferences.Editor editor = getSharedPreferences("AKUN_SETTING", MODE_PRIVATE).edit();
        editor.putString("id_akun", id_akun);
        editor.putString("email", newemail);
        editor.putString("username", username);
        editor.putInt("nomor_acak", nomor_acak);
        editor.putString("lastLat",lastLat);
        editor.putString("lastLon",lastLon);
        editor.putString("class",accountclass);
        editor.putString("class_name",classname);
        editor.putString("time_limit",time_limit);
        editor.apply();
    }

    private void mainMenu() {
        final Intent mainIntent=new Intent(Splash.this,MainActivity.class);
        Handler loadHandler = new Handler();
        Runnable loadRunnable=new Runnable() {
            @Override
            public void run() {
                startActivity(mainIntent);
                finish();
            }
        };
        loadHandler.postDelayed(loadRunnable, 1000);
    }

    private void updateAps(Double versi, String pesan, String deskripsi, final String link_gplay, final int wajib, String tanggal_pembaharuan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setMessage(deskripsi+"\nPada : "+tanggal_pembaharuan)
                .setTitle(pesan+" Versi : "+versi);

        builder.setPositiveButton(R.string.text_perbarui, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Uri uri = Uri.parse(link_gplay);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        if (wajib!=1){
            builder.setNegativeButton(R.string.text_batal, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    newInstalls();
                }
            });
        }
         AlertDialog dialog = builder.create();
         dialog.show();
         dialog.setCanceledOnTouchOutside(false);
         dialog.setCancelable(false);
    }

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

