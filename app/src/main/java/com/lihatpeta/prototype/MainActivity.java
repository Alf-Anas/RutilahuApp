package com.lihatpeta.prototype;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.VectorSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.step;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOutlineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textFont;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textHaloColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textHaloWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, MapboxMap.OnMapLongClickListener {
    private PermissionsManager permissionsManager;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private BuildingPlugin buildingPlugin;
    boolean doubleBackToExitPressedOnce = false, doubleZoom=false, lockLoc=false, inZoom=false;
    double cameraLat, cameraLon, originlat, originlon;
    JSONArray arrayRTLH;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    Point destinationPoint, originPoint;

    BottomNavigationView navigation;
    SymbolLayer pointlayer, rtlhLayerIcon, selectedLayer;
    FillLayer adminLayer, selectedAdminLayer;
    MediaPlayer soundClick;
    ProgressBar circLoading;
    NestedScrollView mainNested;
    Button btnTentang, btnTutorial, btnRating;

    Fragment fragmentMenu = new FragmentMenu();
    Fragment fragmentAccount = new FragmentAccount();
    FragmentManager fm = getSupportFragmentManager();

    boolean FirstLoad = true, LayerLoad = false, locationEnabled=false, updateAkun=false, loadFragmentAccount=false, loadFragmentMenu=false;
    String dataid, dataEmail;

    int map = 1, errorLoad=0, errorRating=0;
    boolean showBatasAdmin=false;

    Double LAT, LON, ALT, SUDUT= 30d;

    ConstraintLayout SlideNew, SlideShow;

    ImageView basicMap, trafficMap, outdoorMap, nightMap, satteliteMap, streetMap, rtlhLayer, bataswilayahLayer, newRTLH, slideShowBG;
    TextView basicText, trafficText, outdoorText, nighttext, satteliteText, streetText, rtlhText, bataswilayahText, textName, MyLocText;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_list:
                    if (!loadFragmentMenu){
                        fm.beginTransaction().add(R.id.container, fragmentMenu, "0").show(fragmentMenu).commit();
                        loadFragmentMenu=true; }
                    fm.beginTransaction().hide(fragmentAccount).show(fragmentMenu).commit();
                    navigation.getMenu().getItem(1).setIcon(R.drawable.ic_location_searching_black_24dp);
                    break;
                case R.id.navigation_map:
                    if (locationEnabled){
                        item.setIcon(R.drawable.ic_my_location_black_24dp);
                        lockLoc=true;
                        if (doubleZoom) {
                            getZoom();
                            lockLoc=false;}
                        doubleZoom=true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doubleZoom=false;
                                if (lockLoc){
                                    if (!inZoom){
                                        getLoc();
                                    } else {
                                        resetZoom(); }
                                }
                            }
                        }, 500);
                    } else {
                        if (!FirstLoad){
                            getLoc();
                        } }
                    fm.beginTransaction().hide(fragmentMenu).hide(fragmentAccount).commit();
                    break;
                case R.id.navigation_account:
                    if (!loadFragmentAccount){
                        fm.beginTransaction().remove(fragmentAccount).commit();
                        fragmentAccount = new FragmentAccount();
                        fm.beginTransaction().add(R.id.container, fragmentAccount, "2").show(fragmentAccount).commit();
                        loadFragmentAccount=true; }
                    fm.beginTransaction().hide(fragmentMenu).show(fragmentAccount).commit();
                    navigation.getMenu().getItem(1).setIcon(R.drawable.ic_location_searching_black_24dp);
                    break;
            }
            return true;
        }
    };

    private void resetZoom() {
        CameraPosition position = new CameraPosition.Builder()
                .zoom(15).bearing(0).tilt(0).build();
        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position));
        inZoom=false;
    }

    private void getZoom() {
        CameraPosition position = new CameraPosition.Builder()
                .zoom(19).bearing(SUDUT).tilt(45).build();
        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position),2000);
        inZoom=true;
    }

    private void getLoc() {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
            }
        });
    }

    private void loadDataRTLH() {
        loadFragmentAccount=false;
        updateAkun=false;
        String URL_RTLH = "https://rtlh-poi.000webhostapp.com/Android-Conn/Load_RTLH.php";
        StringRequest RTLHstringRequest = new StringRequest(Request.Method.GET, URL_RTLH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            arrayRTLH = new JSONArray(response);
                            SharedPreferences.Editor editor = getSharedPreferences("FIRSTINSTALL", MODE_PRIVATE).edit();
                            editor.putBoolean("reloadrtlh", false).apply();
                            reloadmap();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                        errorLoad++;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (errorLoad>=3){
                                    errorVolley();
                                } else {
                                    loadDataRTLH();
                                }
                            }
                        }, 2000);
                    }
                });
        Volley.newRequestQueue(MainActivity.this).add(RTLHstringRequest);
    }

    private void errorVolley() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.no_internet_connection);

        builder.setPositiveButton(R.string.text_cobalagi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                errorLoad=0;
                loadDataRTLH();
            }
        });
        builder.setNegativeButton(R.string.text_tutup, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    public String getEmailData() {
        return dataEmail; }
    public double getPickLat(){
        LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;
        return mapTargetLatLng.getLatitude(); }
    public double getPickLon(){
        LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;
        return mapTargetLatLng.getLongitude(); }
    public double getMyLastLat(){
        return originlat; }
    public double getMyLastLon(){
        return originlon; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);

        mainNested = findViewById(R.id.mainNested);

        soundClick = MediaPlayer.create(this, R.raw.click);
        circLoading = findViewById(R.id.progressBarMain);
        circLoading.setVisibility(View.GONE);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        SlideShow = findViewById(R.id.slideShow);
        SlideShow.setVisibility(View.GONE);
        textName = findViewById(R.id.textShowName);
        slideShowBG=findViewById(R.id.slideShowBG);
        slideShowBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundClick.start();
                Intent detailIntent = new Intent(MainActivity.this, RTLHShow.class);
                SharedPreferences.Editor editor = getSharedPreferences("KOORDINAT", MODE_PRIVATE).edit();
                editor.putString("id_rtlh", dataid);
                editor.apply();
                startActivity(detailIntent);
            }
        });
        slideShowBG.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                originPoint=Point.fromLngLat(originlon,originlat);
                getRoute(originPoint,destinationPoint);
                return true;
            }
        });

        SharedPreferences prefs = getSharedPreferences("AKUN_SETTING", MODE_PRIVATE);
        dataEmail = prefs.getString("email", null);

        SharedPreferences prefs2 = getSharedPreferences("FIRSTINSTALL", MODE_PRIVATE);
        boolean tutorial = prefs2.getBoolean("tutorial", true);
        if (tutorial){
            Fragment tutorFragment=new FragmentTutor();
            fm.beginTransaction().add(R.id.container, tutorFragment, "1").commit();
            navigation.getMenu().getItem(0).setEnabled(false);
            navigation.getMenu().getItem(1).setEnabled(false);
            navigation.getMenu().getItem(2).setEnabled(false);
            mainNested.setNestedScrollingEnabled(false);
        }

        newRTLH = findViewById(R.id.newRTLH);
        SlideNew = findViewById(R.id.slideNew);
        SlideNew.setVisibility(View.GONE);

        newRTLH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundClick.start();
                Intent RTLHsubmitIntent = new Intent(MainActivity.this, RTLHSubmit.class);
                startActivity(RTLHsubmitIntent);
                SlideNew.setVisibility(View.GONE);
            }
        });
        newRTLH.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                originPoint=Point.fromLngLat(originlon,originlat);
                getRoute(originPoint,destinationPoint);
                return true;
            }
        });

        MyLocText=findViewById(R.id.MainMyLoc);

        basicMap = findViewById(R.id.basic_image);
        basicMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map = 1;
                reloadmap();
            }
        });
        trafficMap = findViewById(R.id.traffic_image);
        trafficMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map = 2;
                reloadmap();
            }
        });
        outdoorMap = findViewById(R.id.outdoor_image);
        outdoorMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map = 3;
                reloadmap();
            }
        });
        nightMap = findViewById(R.id.night_image);
        nightMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map = 4;
                reloadmap();
            }
        });
        satteliteMap = findViewById(R.id.sattelite_image);
        satteliteMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map = 5;
                reloadmap();
            }
        });
        streetMap = findViewById(R.id.sattelite_st_image);
        streetMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map = 6;
                reloadmap();
            }
        });

        basicText = findViewById(R.id.basic_text);
        trafficText = findViewById(R.id.traffic_text);
        outdoorText = findViewById(R.id.outdoor_text);
        nighttext = findViewById(R.id.night_text);
        satteliteText = findViewById(R.id.sattelite_text);
        streetText = findViewById(R.id.street_text);

        rtlhText = findViewById(R.id.rtlh_text);
        bataswilayahText = findViewById(R.id.admin_text);

        rtlhLayer = findViewById(R.id.rtlh_image);
        rtlhLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Layer layer = Objects.requireNonNull(mapboxMap.getStyle()).getLayer("layerrtlh");
                soundClick.start();
                if (layer != null) {
                    if (VISIBLE.equals(layer.getVisibility().getValue())) {
                        layer.setProperties(visibility(NONE));
                        rtlhText.setTextColor(getResources().getColor(R.color.colorPrimary));
                        rtlhLayer.setBackgroundColor(getResources().getColor(R.color.colorLight));
                    } else {
                        loadDataRTLH();
                        layer.setProperties(visibility(VISIBLE));
                        rtlhText.setTextColor(getResources().getColor(R.color.colorSecondaryDark));
                        rtlhLayer.setBackgroundColor(getResources().getColor(R.color.colorSecondaryDark));
                    }
                }
            }
        });
        bataswilayahLayer=findViewById(R.id.admin_image);
        bataswilayahLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Layer btsLayer= Objects.requireNonNull(mapboxMap.getStyle()).getLayer("layeradmin");
                Layer slcLayer = mapboxMap.getStyle().getLayer("selectedAdmin");
                Layer lineLayer = mapboxMap.getStyle().getLayer("linelayer");
                soundClick.start();
                if (btsLayer != null) {
                    if(showBatasAdmin){
                        btsLayer.setProperties(fillOpacity(0f));
                        bataswilayahText.setTextColor(getResources().getColor(R.color.colorPrimary));
                        bataswilayahLayer.setBackgroundColor(getResources().getColor(R.color.colorLight));
                        Objects.requireNonNull(slcLayer).setProperties(visibility(NONE));
                        Objects.requireNonNull(lineLayer).setProperties(visibility(NONE));
                        showBatasAdmin=false;
                    } else {
                        btsLayer.setProperties(fillOpacity(0.6f));
                        bataswilayahText.setTextColor(getResources().getColor(R.color.colorSecondaryDark));
                        bataswilayahLayer.setBackgroundColor(getResources().getColor(R.color.colorSecondaryDark));
                        Objects.requireNonNull(lineLayer).setProperties(visibility(VISIBLE));
                        showBatasAdmin=true;
                    }
                }
            }
        });

        btnTutorial=findViewById(R.id.buttonTutorial);
        btnRating=findViewById(R.id.buttonRating);
        btnTentang=findViewById(R.id.buttonTentang);

        btnTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment tutorFragment=new FragmentTutor();
                fm.beginTransaction().add(R.id.container, tutorFragment, "1").commit();
                navigation.getMenu().getItem(0).setEnabled(false);
                navigation.getMenu().getItem(1).setEnabled(false);
                navigation.getMenu().getItem(2).setEnabled(false);
                mainNested.setNestedScrollingEnabled(false);
                mainNested.setVisibility(View.GONE);
            }
        });
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beriRating();
            }
        });
        btnTentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View ratingView=getLayoutInflater().inflate(R.layout.about_layout,null);
                ImageView imgItenas=ratingView.findViewById(R.id.imgAbtItenas);
                Picasso.get().load(R.drawable.logoitenas).into(imgItenas);
                TextView textUserManual=ratingView.findViewById(R.id.linkUserManual);
                TextView textModul=ratingView.findViewById(R.id.aboutLink);
                TextView textEmail=ratingView.findViewById(R.id.linkEmailDeveloper);
                textModul.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse(getResources().getString(R.string.tutor_LinkModul));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                textUserManual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse(getResources().getString(R.string.link_usermanual));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                textEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, getResources().getString(R.string.text_emaildeveloper));
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Rutilahu App : ");
                        emailIntent.setType("plain/text");
                        startActivity(emailIntent);
                    }
                });
                builder.setView(ratingView);
                builder.setPositiveButton(R.string.text_tutup,null);
                final AlertDialog dialog=builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
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
        });
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(getString(R.string.access_token))
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, retrofit2.Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Toast.makeText(MainActivity.this,"Gagal Menemukan Rute",Toast.LENGTH_SHORT).show();
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Toast.makeText(MainActivity.this,"Gagal Menemukan Rute",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        currentRoute = response.body().routes().get(0);
                        if (navigationMapRoute != null) {
                            navigationMapRoute.updateRouteVisibilityTo(false);
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }
                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this,"Error Mencari Rute",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void beriRating() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View ratingView=getLayoutInflater().inflate(R.layout.rating_layout,null);
        final EditText perangkatEditText=ratingView.findViewById(R.id.editRatingPerangkat);
        final EditText pendapatEditText=ratingView.findViewById(R.id.editRatingPendapat);
        final EditText saranEditText=ratingView.findViewById(R.id.editRatingSaran);
        final EditText laporEditText=ratingView.findViewById(R.id.editRatingLaporkan);
        final LinearLayout linearShowKuesioner=ratingView.findViewById(R.id.linearLayoutRatingShow);
        final ImageView imgShowRating=ratingView.findViewById(R.id.ratingImgShow);
        final LinearLayout linearKuesioner=ratingView.findViewById(R.id.ratingLayoutKuesioner);

        final RadioGroup RQ1=ratingView.findViewById(R.id.radioR1);
        final RadioGroup RQ2=ratingView.findViewById(R.id.radioR2);
        final RadioGroup RQ3=ratingView.findViewById(R.id.radioR3);
        final RadioGroup RQ4=ratingView.findViewById(R.id.radioR4);
        final RadioGroup RQ5=ratingView.findViewById(R.id.radioR5);
        final RadioGroup RQ6=ratingView.findViewById(R.id.radioR6);
        final RadioGroup RQ7=ratingView.findViewById(R.id.radioR7);
        final RadioGroup RQ8=ratingView.findViewById(R.id.radioR8);
        final RadioGroup RQ9=ratingView.findViewById(R.id.radioR9);
        final RadioGroup RQ10=ratingView.findViewById(R.id.radioR10);
        final RadioGroup RQ11=ratingView.findViewById(R.id.radioR11);
        final RadioGroup RQ12=ratingView.findViewById(R.id.radioR12);
        final RadioGroup RQ13=ratingView.findViewById(R.id.radioR13);
        final RadioGroup RQ14=ratingView.findViewById(R.id.radioR14);
        final RadioGroup RQ15=ratingView.findViewById(R.id.radioR15);

        linearShowKuesioner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgShowRating.getRotation()==180){
                    linearKuesioner.setVisibility(View.VISIBLE);
                    imgShowRating.setRotation(0);
                }
                else {
                    linearKuesioner.setVisibility(View.GONE);
                    imgShowRating.setRotation(180);
                }
            }
        });

        builder.setView(ratingView);
        builder.setPositiveButton(R.string.title_submit,null);
        builder.setNegativeButton(R.string.text_batal, null);
        final AlertDialog dialog=builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!perangkatEditText.getText().toString().isEmpty() && !pendapatEditText.getText().toString().isEmpty()){
                            String ePerangkat=perangkatEditText.getText().toString();
                            String ePendapat=pendapatEditText.getText().toString();
                            String eSaran=saranEditText.getText().toString();
                            String eLapor=laporEditText.getText().toString();
                            String SQ1="0";
                            String SQ2="0";
                            String SQ3="0";
                            String SQ4="0";
                            String SQ5="0";
                            String SQ6="0";
                            String SQ7="0";
                            String SQ8="0";
                            String SQ9="0";
                            String SQ10="0";
                            String SQ11="0";
                            String SQ12="0";
                            String SQ13="0";
                            String SQ14="0";
                            String SQ15="0";

                            if (RQ1.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ1.getCheckedRadioButtonId());
                                SQ1=RB1.getText().toString(); }
                            if (RQ2.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ2.getCheckedRadioButtonId());
                                SQ2=RB1.getText().toString(); }
                            if (RQ3.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ3.getCheckedRadioButtonId());
                                SQ3=RB1.getText().toString(); }
                            if (RQ4.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ4.getCheckedRadioButtonId());
                                SQ4=RB1.getText().toString(); }
                            if (RQ5.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ5.getCheckedRadioButtonId());
                                SQ5=RB1.getText().toString(); }
                            if (RQ6.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ6.getCheckedRadioButtonId());
                                SQ6=RB1.getText().toString(); }
                            if (RQ7.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ7.getCheckedRadioButtonId());
                                SQ7=RB1.getText().toString(); }
                            if (RQ8.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ8.getCheckedRadioButtonId());
                                SQ8=RB1.getText().toString(); }
                            if (RQ9.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ9.getCheckedRadioButtonId());
                                SQ9=RB1.getText().toString(); }
                            if (RQ10.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ10.getCheckedRadioButtonId());
                                SQ10=RB1.getText().toString(); }
                            if (RQ11.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ11.getCheckedRadioButtonId());
                                SQ11=RB1.getText().toString(); }
                            if (RQ12.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ12.getCheckedRadioButtonId());
                                SQ12=RB1.getText().toString(); }
                            if (RQ13.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ13.getCheckedRadioButtonId());
                                SQ13=RB1.getText().toString(); }
                            if (RQ14.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ14.getCheckedRadioButtonId());
                                SQ14=RB1.getText().toString(); }
                            if (RQ15.getCheckedRadioButtonId()!=-1){
                                RadioButton RB1=ratingView.findViewById(RQ15.getCheckedRadioButtonId());
                                SQ15=RB1.getText().toString(); }

                            uploadRating(ePerangkat, ePendapat, eSaran, eLapor, SQ1, SQ2, SQ3, SQ4, SQ5, SQ6, SQ7, SQ8, SQ9, SQ10, SQ11, SQ12, SQ13, SQ14, SQ15);
                            Toast.makeText(MainActivity.this, "Mengirim Rating...", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }else {
                            perangkatEditText.setHintTextColor(getResources().getColor(R.color.colorRed));
                            pendapatEditText.setHintTextColor(getResources().getColor(R.color.colorRed));
                            Toast.makeText(MainActivity.this, "Masukan Tipe Smartphone dan Pendapat Anda", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                Button backbutton = (dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                backbutton.setOnClickListener(new View.OnClickListener() {
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

    private void uploadRating(final String ePerangkat, final String ePendapat, final String eSaran, final String eLapor, final String toString, final String toString1, final String toString2, final String toString3, final String toString4, final String toString5, final String toString6, final String toString7, final String toString8, final String toString9, final String toString10, final String toString11, final String toString12, final String toString13, final String toString14) {
        String UploadURL="https://rtlh-poi.000webhostapp.com/Android-Conn/Post_Rating.php";
        StringRequest stringRequest =new StringRequest(Request.Method.POST, UploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    Toast.makeText(MainActivity.this,Response,Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                errorRating++;
                Handler loadHandler = new Handler();
                Runnable loadRunnable=new Runnable() {
                    @Override
                    public void run() {
                        if (errorRating>=4){
                            errorRating=0;
                            Toast.makeText(MainActivity.this,"Gagal Mengirim Rating",Toast.LENGTH_LONG).show();
                        } else {
                            uploadRating(ePerangkat, ePendapat,eSaran,eLapor, toString,toString1,toString2,toString3,toString4,toString5,toString6,toString7,toString8,toString9,toString10,toString11,toString12,toString13,toString14);
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

                params.put("email", dataEmail);
                params.put("perangkat",ePerangkat);
                params.put("tanggal",strDate);
                params.put("pendapat",ePendapat);
                params.put("saran",eSaran);
                params.put("laporan",eLapor);
                params.put("q1",toString);
                params.put("q2",toString1);
                params.put("q3",toString2);
                params.put("q4",toString3);
                params.put("q5",toString4);
                params.put("q6",toString5);
                params.put("q7",toString6);
                params.put("q8",toString7);
                params.put("q9",toString8);
                params.put("q10",toString9);
                params.put("q11",toString10);
                params.put("q12",toString11);
                params.put("q13",toString12);
                params.put("q14",toString13);
                params.put("q15",toString14);
                return params;
            }
        };
        ProfileUpload.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
    }

    private void reloadmap() {
        circLoading.setVisibility(View.VISIBLE);
        soundClick.start();
        String BaseMap = "";
        LayerLoad=true;

        basicMap.setBackgroundColor(getResources().getColor(R.color.colorLight));
        trafficMap.setBackgroundColor(getResources().getColor(R.color.colorLight));
        outdoorMap.setBackgroundColor(getResources().getColor(R.color.colorLight));
        nightMap.setBackgroundColor(getResources().getColor(R.color.colorLight));
        satteliteMap.setBackgroundColor(getResources().getColor(R.color.colorLight));
        streetMap.setBackgroundColor(getResources().getColor(R.color.colorLight));

        basicText.setTextColor(getResources().getColor(R.color.colorPrimary));
        trafficText.setTextColor(getResources().getColor(R.color.colorPrimary));
        outdoorText.setTextColor(getResources().getColor(R.color.colorPrimary));
        nighttext.setTextColor(getResources().getColor(R.color.colorPrimary));
        satteliteText.setTextColor(getResources().getColor(R.color.colorPrimary));
        streetText.setTextColor(getResources().getColor(R.color.colorPrimary));

        if (map==1){
            BaseMap=Style.MAPBOX_STREETS;
            basicMap.setBackgroundColor(getResources().getColor(R.color.colorSecondaryDark));
            basicText.setTextColor(getResources().getColor(R.color.colorSecondaryDark));
        }
        else if (map==2){
            BaseMap=Style.TRAFFIC_DAY;
            trafficMap.setBackgroundColor(getResources().getColor(R.color.colorSecondaryDark));
            trafficText.setTextColor(getResources().getColor(R.color.colorSecondaryDark));
        }
        else if (map==3){
            BaseMap=Style.OUTDOORS;
            outdoorMap.setBackgroundColor(getResources().getColor(R.color.colorSecondaryDark));
            outdoorText.setTextColor(getResources().getColor(R.color.colorSecondaryDark));
        }
        else if (map==4){
            BaseMap=Style.TRAFFIC_NIGHT;
            nightMap.setBackgroundColor(getResources().getColor(R.color.colorSecondaryDark));
            nighttext.setTextColor(getResources().getColor(R.color.colorSecondaryDark));
        }
        else if (map==5){
            BaseMap=Style.SATELLITE;
            satteliteMap.setBackgroundColor(getResources().getColor(R.color.colorSecondaryDark));
            satteliteText.setTextColor(getResources().getColor(R.color.colorSecondaryDark));
        }
        else if (map==6){
            BaseMap=Style.SATELLITE_STREETS;
            streetMap.setBackgroundColor(getResources().getColor(R.color.colorSecondaryDark));
            streetText.setTextColor(getResources().getColor(R.color.colorSecondaryDark));
        }
        mapboxMap.setStyle(BaseMap, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull final Style style) {
                final List<Feature> Data_RTLH = new ArrayList<>();
                try {
                    for (int i = 0; i < arrayRTLH.length(); i++) {
                        JSONObject datajson = arrayRTLH.getJSONObject(i);
                        JsonObject Coor_RTLH = new JsonObject();
                        Coor_RTLH.addProperty("nama", datajson.getString("nama_penghuni"));
                        Data_RTLH.add(Feature.fromGeometry(
                                Point.fromLngLat(datajson.getDouble("lon"), datajson.getDouble("lat")), Coor_RTLH, datajson.getString("id_rtlh")));
                    }

                    style.addSource(new VectorSource("Administrasi", "mapbox://lihatmap.a4l4iasn"));
                    adminLayer=new FillLayer("layeradmin","Administrasi");
                    adminLayer.setSourceLayer("newAdmin-5wq2n4");
                    adminLayer.setProperties(
                            fillColor(Color.YELLOW),
                            fillOutlineColor(Color.MAGENTA),
                            fillOpacity(0f)
                    );
                    style.addLayer(adminLayer);

                    style.addSource(new GeoJsonSource("selectedFeatureAdmin"));
                    selectedAdminLayer=new FillLayer("selectedAdmin","selectedFeatureAdmin");
                    selectedAdminLayer.setProperties(
                            visibility(NONE),
                            fillColor(Color.RED),
                            fillOpacity(0.6f)
                    );
                    style.addLayer(selectedAdminLayer);

                    LineLayer adminLine=new LineLayer("linelayer","Administrasi");
                    adminLine.setSourceLayer("newAdmin-5wq2n4");
                    adminLine.setProperties(
                            visibility(NONE),
                            lineColor(Color.MAGENTA),
                            lineWidth(3f)
                    );
                    style.addLayer(adminLine);

                    style.addSource(new GeoJsonSource("rtlh_id",
                            FeatureCollection.fromFeatures(Data_RTLH)));
                    style.addImage("bm_rtlh", BitmapFactory.decodeResource(getResources(),R.drawable.bm_rtlh));
                    rtlhLayerIcon=new SymbolLayer("layerrtlh","rtlh_id");
                    rtlhLayerIcon.setProperties(
                            visibility(VISIBLE),
                            iconImage("bm_rtlh"),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true),
                            textField("{nama}"),
                            textColor(Color.DKGRAY),
                            textAnchor(Property.TEXT_ANCHOR_TOP),
                            textFont(new String[] {"Ubuntu Medium", "Arial Unicode MS Regular"}),
                            textHaloColor(Color.WHITE),
                            textHaloWidth(1f),
                            textSize(step(zoom(), literal(0f), stop(16f, 15f))),
                            textAllowOverlap(true)
                    );
                    style.addLayer(rtlhLayerIcon);

                    selectedLayer=new SymbolLayer("layerselected","select_id");
                    style.addSource(new GeoJsonSource("select_id", Feature.fromGeometry(Point.fromLngLat(0,0))));
                    style.addImage("bm_point", BitmapFactory.decodeResource(getResources(),R.drawable.bm_point));
                    selectedLayer.setProperties(
                            visibility(NONE),
                            iconImage("bm_point"),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true)
                    );
                    style.addLayer(selectedLayer);

                    pointlayer=new SymbolLayer("layerpoint","point_id");
                    style.addSource(new GeoJsonSource("point_id", Feature.fromGeometry(Point.fromLngLat(0,0))));
                    pointlayer.setProperties(
                            visibility(NONE),
                            iconImage("bm_point"),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true)
                    );
                    style.addLayer(pointlayer);

                    LayerLoad=false;
                    circLoading.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (FirstLoad){
                    enableLocationComponent(style);
                    mapboxMap.addOnMapClickListener(MainActivity.this);
                    mapboxMap.addOnMapLongClickListener(MainActivity.this);
                    buildingPlugin = new BuildingPlugin(mapView, mapboxMap, style);
                    buildingPlugin.setVisibility(true);
                    buildingPlugin.setOpacity(0.5f);
                    FirstLoad=false; }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;
        loadDataRTLH();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            originlat=location.getLatitude();
            originlon=location.getLongitude();
            double dbLatitude = location.getLatitude();
            double dbLongitude = location.getLongitude();
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
            float akurasi = location.getAccuracy();
            String strKoordinat = "Lokasi Anda : ["+dbLongitude +strBujur+", "+ dbLatitude +strLintang+"] \n Akurasi GPS : "+akurasi +" Meter";
            MyLocText.setText(strKoordinat);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationManager locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            enableWriteStorage();
            boolean gps_enabled = false;
            boolean network_enabled = false;
            try {
                gps_enabled = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ignored) {}
            try {
                network_enabled = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ignored) {}
            if(!gps_enabled && !network_enabled) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Aktifkan GPS Anda untuk mendapatkan Posisi yang Akurat")
                        .setTitle("Aktifkan GPS");
                builder.setPositiveButton("Aktifkan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                locationEnabled=true;}

            final LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            if (locationComponent.getLastKnownLocation() != null) {
                originlat=locationComponent.getLastKnownLocation().getLatitude();
                originlon=locationComponent.getLastKnownLocation().getLongitude();
                double dbLatitude = locationComponent.getLastKnownLocation().getLatitude();
                double dbLongitude = locationComponent.getLastKnownLocation().getLongitude();
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
                float akurasi = locationComponent.getLastKnownLocation().getAccuracy();
                String strKoordinat = "Lokasi Anda : ["+dbLongitude +strBujur+", "+ dbLatitude +strLintang+"] \n Akurasi GPS : "+akurasi +" Meter";
                SUDUT= (double) Objects.requireNonNull(locationComponent.getCompassEngine()).getLastHeading();
                MyLocText.setText(strKoordinat);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;
                    cameraLat=mapTargetLatLng.getLatitude();
                    cameraLon=mapTargetLatLng.getLongitude();
                    if (!updateAkun){
                        setAkun();
                        updateAkun=true; }
                }
            }, 2000);
        } else {
            permissionsManager = new PermissionsManager(MainActivity.this);
            permissionsManager.requestLocationPermissions(MainActivity.this);
        }
    }

    private void enableWriteStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 99);
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onMapClick(@NonNull final LatLng point) {
        destinationPoint= Point.fromLngLat(point.getLongitude(), point.getLatitude());

        SharedPreferences prefs = getSharedPreferences("FIRSTINSTALL", MODE_PRIVATE);
        boolean reload = prefs.getBoolean("reloadrtlh", false);
        if (reload){
            loadDataRTLH(); }
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                pointlayer.setProperties(visibility(NONE));
                SlideNew.setVisibility(View.GONE);

                PointF pointf = mapboxMap.getProjection().toScreenLocation(point);
                RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);

                List<Feature> pointList = mapboxMap.queryRenderedFeatures(rectF, "layerrtlh");
                if (pointList.size() > 0) {
                    for (Feature feature : pointList) {
                        dataid = pointList.get(0).id();
                        SlideShow.setVisibility(View.VISIBLE);
                        textName.setText(pointList.get(0).getStringProperty("nama"));
                    }
                    GeoJsonSource NewSelected=style.getSourceAs("select_id");
                    Objects.requireNonNull(NewSelected).setGeoJson(Feature.fromGeometry(pointList.get(0).geometry()));
                    selectedLayer.setProperties(visibility(VISIBLE));
                    soundClick.start();
                }else {
                    selectedLayer.setProperties(visibility(NONE));
                    SlideShow.setVisibility(View.GONE);
                    if (navigationMapRoute != null) {
                        navigationMapRoute.updateRouteVisibilityTo(false);}
                }

                if (showBatasAdmin){
                    List<Feature> adminList = mapboxMap.queryRenderedFeatures(rectF, "layeradmin");
                    if (adminList.size() > 0) {
                        for (Feature adminfeature : adminList) {
                            GeoJsonSource source = style.getSourceAs("selectedFeatureAdmin");
                            String showAdmin="Koordinat : "+ point.getLatitude() +", " + point.getLongitude() + "\n" +
                                    "Desa/Kel : " + adminfeature.getStringProperty("Kel_Desa") + "\n" +
                                    "Kecamatan : " + adminfeature.getStringProperty("Kecamatan") + "\n" +
                                    "Kab/Kota : " + adminfeature.getStringProperty("Kab_Kota") + "\n" +
                                    "Provinsi : " + adminfeature.getStringProperty("Provinsi");
                            Toast.makeText(MainActivity.this,showAdmin,Toast.LENGTH_SHORT).show();
                            if (source != null) {
                                source.setGeoJson(adminfeature);
                                selectedAdminLayer.setProperties(visibility(VISIBLE));
                            }
                        }
                    }
                }
            }
        });
        return false;
    }

    @Override
    public boolean onMapLongClick(@NonNull final LatLng point) {
        destinationPoint= Point.fromLngLat(point.getLongitude(), point.getLatitude());
        soundClick.start();

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

        LAT=Double.parseDouble(latitude);
        LON=Double.parseDouble(longitude);
        ALT=Double.parseDouble(altitude);

        SharedPreferences.Editor editor = getSharedPreferences("KOORDINAT", MODE_PRIVATE).edit();
        editor.putString("latitude", String.valueOf(LAT));
        editor.putString("longitude", String.valueOf(LON));
        editor.putString("desa_kel", "");
        editor.putString("kecamatan", "");
        editor.putString("kab_kota", "");
        editor.putString("provinsi", "");
        editor.putString("kodepos", "");
        editor.apply();

        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                GeoJsonSource NewLatLon=style.getSourceAs("point_id");
                Objects.requireNonNull(NewLatLon).setGeoJson(Feature.fromGeometry(Point.fromLngLat(LON,LAT)));
                pointlayer.setProperties(visibility(VISIBLE));

                PointF pointf = mapboxMap.getProjection().toScreenLocation(point);
                RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);

                List<Feature> adminList = mapboxMap.queryRenderedFeatures(rectF, "layeradmin");
                if (adminList.size() > 0) {
                    for (Feature feature : adminList) {
                        SharedPreferences.Editor editor = getSharedPreferences("KOORDINAT", MODE_PRIVATE).edit();
                        editor.putString("desa_kel", adminList.get(0).getStringProperty("Kel_Desa"));
                        editor.putString("kecamatan", adminList.get(0).getStringProperty("Kecamatan"));
                        editor.putString("kab_kota", adminList.get(0).getStringProperty("Kab_Kota"));
                        editor.putString("provinsi", adminList.get(0).getStringProperty("Provinsi"));
                        editor.putString("kodepos", adminList.get(0).getStringProperty("Kode_Pos"));
                        editor.apply();
                    }
                }
            }
        });
        selectedLayer.setProperties(visibility(NONE));
        SlideNew.setVisibility(View.VISIBLE);
        SlideShow.setVisibility(View.GONE);
        return false;
    }

    private void setAkun() {
        SharedPreferences prefs = getSharedPreferences("AKUN_SETTING", MODE_PRIVATE);
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
                Toast.makeText(MainActivity.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("id_akun",strId_Akun);
                params.put("akses_terakhir",strDate);
                params.put("lat", String.valueOf(cameraLat));
                params.put("lon", String.valueOf(cameraLon));
                return params;
            }
        };
        ProfileUpload.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
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
