package com.lihatpeta.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class FragmentTutor extends Fragment {

    TextView textJudul, textDeskripsi;
    ImageView imgTRTLH, imgTShow, imgTDaftar, imgTSwipe, imgTAkun, imgRTLH1, imgRTLH2, imgRTLH3, imgBG;
    Button btnLink, btnUserManual, btnLanjut, btnKembali;
    int tutor=-1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tutorial,container,false);

        textJudul=view.findViewById(R.id.textTutorialJudul);
        textDeskripsi=view.findViewById(R.id.textTutorialDeskripsi);
        btnLink=view.findViewById(R.id.tutotBtnLink);
        btnUserManual=view.findViewById(R.id.tutotBtnUserManual);
        btnLanjut=view.findViewById(R.id.tutotBtnNext);
        btnKembali=view.findViewById(R.id.tutotBtnBack);
        imgBG=view.findViewById(R.id.imageTutorialBG);
        imgRTLH1=view.findViewById(R.id.imageTutorialRTLH);
        imgRTLH2=view.findViewById(R.id.imageTutorialRTLH2);
        imgRTLH3=view.findViewById(R.id.imageTutorialRTLH3);
        imgTAkun=view.findViewById(R.id.imageTutorialTouchAkun);
        imgTDaftar=view.findViewById(R.id.imageTutorialTouchDaftar);
        imgTRTLH=view.findViewById(R.id.imageTutorialTouchRTLH);
        imgTShow=view.findViewById(R.id.imageTutorialTouchShow);
        imgTSwipe=view.findViewById(R.id.imageTutorialTouchSwipe);

        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getResources().getString(R.string.tutor_LinkModul));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        btnUserManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getResources().getString(R.string.link_usermanual));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutor++;
                showTutorial(tutor);
            }
        });
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutor--;
                showTutorial(tutor);
            }
        });
        return view;
    }

    private void showTutorial(int tutor) {
        imgBG.setVisibility(View.GONE);
        imgRTLH1.setVisibility(View.GONE);
        imgRTLH2.setVisibility(View.GONE);
        imgRTLH3.setVisibility(View.GONE);
        imgTAkun.setVisibility(View.GONE);
        imgTDaftar.setVisibility(View.GONE);
        imgTSwipe.setVisibility(View.GONE);
        imgTShow.setVisibility(View.GONE);
        imgTRTLH.setVisibility(View.GONE);
        if (tutor==0){
            btnLink.setVisibility(View.GONE);
            btnKembali.setVisibility(View.GONE);
            textJudul.setText("1. Menampilkan Data RTLH");
            textDeskripsi.setText("Tekan pada Objek RTLH yang ada pada Tampilan Peta untuk menampilkan Nama Penghuni RTLH tersebut");
            imgRTLH1.setVisibility(View.VISIBLE);
            imgRTLH2.setVisibility(View.VISIBLE);
            imgRTLH3.setVisibility(View.VISIBLE);
            imgTRTLH.setVisibility(View.VISIBLE);
        } else if (tutor==1){
            btnKembali.setVisibility(View.VISIBLE);
            textJudul.setText("1. Menampilkan Data RTLH");
            textDeskripsi.setText("Nama Penghuni RTLH akan ditampilkan, sentuh pada nama penghuni untuk melihat detail RTLH");
            imgRTLH1.setVisibility(View.VISIBLE);
            imgRTLH2.setVisibility(View.VISIBLE);
            imgRTLH3.setVisibility(View.VISIBLE);
            imgRTLH2.setImageDrawable(getResources().getDrawable(R.drawable.bm_rtlh));
            imgTShow.setVisibility(View.VISIBLE);
            imgBG.setVisibility(View.VISIBLE);
        } else if (tutor==2){
            textJudul.setText("2. Menambahkan Data RTLH");
            textDeskripsi.setText("Tekan dan Tahan pada Tampilan Peta sesuai dengan lokasi RTLH, untuk menmpilkan titik lokasi RTLH pada peta dan menampilkan Menu menambahkan Data");
            imgRTLH2.setVisibility(View.VISIBLE);
            imgRTLH2.setImageDrawable(getResources().getDrawable(R.drawable.bm_point));
            imgTRTLH.setVisibility(View.VISIBLE);
        } else if (tutor==3){
            textJudul.setText("2. Menambahkan Data RTLH");
            textDeskripsi.setText("Menu untuk menambahkan Data akan ditampilkan, sentuh pada simbol RTLH untuk membuka formulir dan memulai menambahkan data RTLH");
            imgTShow.setVisibility(View.VISIBLE);
            imgRTLH2.setVisibility(View.VISIBLE);
            imgBG.setVisibility(View.VISIBLE);
        } else if (tutor==4){
            textJudul.setText("3. Mengganti Tampilan Peta");
            textDeskripsi.setText("Geser ke atas untuk memunculkan menu mengganti Tampilan Peta, melihat tutorial kembali, memberikan Rating, serta melihat informasi tentang aplikasi ini");
            imgTSwipe.setVisibility(View.VISIBLE);
        } else if (tutor==5){
            textJudul.setText("4. Menampilkan Lokasi Anda");
            textDeskripsi.setText("Tampilan Peta dapat langsung di arahkan ke lokasi perangkat Anda dengan menekan 1 kali pada Menu Peta dan tekan 2 kali secara cepat untuk zoom otomatis");
            imgTSwipe.setVisibility(View.VISIBLE);
        } else if (tutor==6){
            textJudul.setText("5. Mencari Data RTLH di Sekitaran");
            textDeskripsi.setText("Sentuh Menu Data untuk mencari Data RTLH di Sekitaran. Pada Menu ini, Anda bisa mencari data di sekitar lokasi Anda, atau di lokasi tertentu. Data yang ditampilkan dapat Anda simpan dalam format .csv pada folder Dokumen di Penyimpanan Internal");
            imgTDaftar.setVisibility(View.VISIBLE);
        } else if (tutor==7){
            textJudul.setText("6. Melihat Akun");
            textDeskripsi.setText("Sentuh Menu Akun untuk melihat pengaturan Akun. Pada Menu ini, Anda dapat mengganti foto profil dan melihat kontribusi Anda");
            imgTAkun.setVisibility(View.VISIBLE);
            btnLanjut.setText(getResources().getString(R.string.text_lanjut));
        } else if (tutor==8){
            textJudul.setText("Tutorial Selesai");
            textDeskripsi.setText("Anda dapat kembali melihat Tutorial melalui Menu mengganti Tampilan Peta");
            imgTAkun.setVisibility(View.VISIBLE);
            btnLanjut.setText(getResources().getString(R.string.text_selesai));
        } else {
            tutorialEnd();
        }
    }

    private void tutorialEnd() {
        tutor=-1;
        MainActivity myAct = (MainActivity) getActivity();
        BottomNavigationView navigation = Objects.requireNonNull(myAct).findViewById(R.id.navigation);
        NestedScrollView mainNested = myAct.findViewById(R.id.mainNested);
        navigation.getMenu().getItem(0).setEnabled(true);
        navigation.getMenu().getItem(1).setEnabled(true);
        navigation.getMenu().getItem(2).setEnabled(true);
        mainNested.setNestedScrollingEnabled(true);
        mainNested.setVisibility(View.VISIBLE);
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("FIRSTINSTALL", MODE_PRIVATE).edit();
        editor.putBoolean("tutorial", false).apply();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

}
