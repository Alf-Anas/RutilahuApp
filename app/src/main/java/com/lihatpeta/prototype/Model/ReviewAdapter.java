package com.lihatpeta.prototype.Model;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lihatpeta.prototype.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context mCtx;
    private List<Review> reviewList;
    private MediaPlayer soundClick;

    public ReviewAdapter(Context mCtx, List<Review> reviewList) {
        this.mCtx = mCtx;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.review_list, null);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewViewHolder holder, int position) {
        final Review review = reviewList.get(position);

        holder.textUname.setText("@"+review.getUsername());
        holder.textTanggal.setText(review.getTanggal());
        holder.editReview.setText(review.getPendapat());
        Picasso.get().load(review.getFotoprofil()).error(R.drawable.profile).fit().centerCrop().into(holder.imageFoto);
        holder.radioDukungan.setText(review.getDukungan());
        holder.imageFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAccount(review.getUsername());
                soundClick.start();
            }
        });
    }

    private void ShowAccount(String txtusername, String txtemail, String txtclass, String txttanggal_registrasi, String txtakses_terakhir, String txtfoto_profil, String txttentang, String txtdatabaru, String txteditdata, String txtdukungan, String txtpenolakan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);

        View unameView= LayoutInflater.from(mCtx).inflate(R.layout.fragment_account,null);
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
            Picasso.get().load(txtfoto_profil).fit().centerCrop().into(foto_profil);
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
                Toast.makeText(mCtx, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                loadAccount(uname);
            }
        });
        Volley.newRequestQueue(mCtx).add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView textUname, textTanggal;
        EditText editReview;
        ImageView imageFoto;
        RadioButton radioDukungan;

        ReviewViewHolder(View itemView) {
            super(itemView);
            textUname = itemView.findViewById(R.id.textAdapterUname);
            textTanggal = itemView.findViewById(R.id.textAdapterTanggal);
            editReview = itemView.findViewById(R.id.editAdapter);
            imageFoto = itemView.findViewById(R.id.imageAdapter);
            radioDukungan = itemView.findViewById(R.id.radioAdapter);
            soundClick = MediaPlayer.create(mCtx, R.raw.click);
        }
    }
}
