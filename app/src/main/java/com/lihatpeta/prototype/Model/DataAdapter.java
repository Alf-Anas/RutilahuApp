package com.lihatpeta.prototype.Model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lihatpeta.prototype.R;
import com.lihatpeta.prototype.RTLHShow;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private Context mCtx;
    private List<Data> dataList;
    private MediaPlayer soundClick;

    public DataAdapter(Context mCtx, List<Data> dataList) {
        this.mCtx = mCtx;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.data_list, null);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataViewHolder holder, int position) {
        final Data data = dataList.get(position);

        holder.textNama.setText(data.getNamartlh());
        holder.textDukungan.setText(data.getDukungan());
        holder.textJarak.setText(data.getJarak());
        holder.textKoordinat.setText(data.getKoordinat());
        holder.textOleh.setText(data.getOleh());
        Picasso.get().load(data.getFotortlh()).error(R.drawable.errorimage).fit().centerCrop().into(holder.imageFoto);
        holder.linearDataList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadRTLH(data.getIdrtlh());
                soundClick.start();
            }
        });
    }

    private void loadRTLH(String id_rtlh) {
        Intent detailIntent=new Intent(mCtx, RTLHShow.class);
        SharedPreferences.Editor editor = mCtx.getSharedPreferences("KOORDINAT", MODE_PRIVATE).edit();
        editor.putString("id_rtlh", id_rtlh);
        editor.apply();
        mCtx.startActivity(detailIntent);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        TextView textNama, textDukungan, textJarak, textKoordinat, textOleh;
        ImageView imageFoto;
        LinearLayout linearDataList;

        DataViewHolder(View itemView) {
            super(itemView);
            textNama = itemView.findViewById(R.id.data_nama);
            textDukungan = itemView.findViewById(R.id.data_dukungan);
            textJarak = itemView.findViewById(R.id.data_jarak);
            textKoordinat= itemView.findViewById(R.id.data_koordinat);
            textOleh = itemView.findViewById(R.id.data_oleh);
            imageFoto = itemView.findViewById(R.id.image_data);
            linearDataList = itemView.findViewById(R.id.linearLayoutDataList);
            soundClick = MediaPlayer.create(mCtx, R.raw.click);
        }
    }
}
