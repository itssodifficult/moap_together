package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.model.GroundOverlayOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap mMap;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소

    Location mCurrentLocation;
    LatLng currentPosition;
    Location target_mCurrentLocation;
    LatLng target_currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    private Location target_location;

    // 파이어베이스 변수들
    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference myRef = fd.getReference();

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String uid, target;

    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요

    //경북대 근처 gs25 표시를 위한 변수
    GroundOverlayOptions groundOverlayOptions;
    LatLng[] gs;

    //광명시 편의점 변수
    private static RequestQueue requestQueue;
    private String URL = "https://api.odcloud.kr/api/15113231/v1/uddi:66cbcba0-3dd1-4ad6-adda-8f594dd5797f?page=1&perPage=600&returnType=JSON&serviceKey=Y3CpHUcQ58P6XcqQ%2B63ssDZecOmqfwHIs0tI%2FuNQ3%2BBoEbdbiDI0JcUQGAQm%2BaHozElPP9oqfA%2BGtCa45SKuJQ%3D%3D";
    private Gson gson;
    private ConvenienceStoreResponse csr;


    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private StringRequest request;
    private Polyline currentPolyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_map);

        mLayout = findViewById(R.id.layout_main);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (user != null) {
            uid = user.getUid();
            Log.d(TAG, "User UID: " + uid);
        } else {
            Log.d(TAG, "No user signed in.");
            // 로그인되어 있지 않은 경우, 로그인 화면으로 이동하거나 다른 처리를 수행할 수 있습니다.
        }
        target=getIntent().getStringExtra("otherUid");

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        Request();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //gs25 표시
        gs = new LatLng[40];
        gs[0] = new LatLng(35.895812322493484,128.60542303208527);
        gs[1] = new LatLng(35.89598525480742,128.61269174804565);
        gs[2] = new LatLng(35.89480145848144,128.61494915407462);
        gs[3] = new LatLng(35.89297920773362,128.60905366702002);
        gs[4] = new LatLng(35.89179607432953,128.6112667605069);
        gs[5] = new LatLng(35.89145153871419,128.61276589623844);
        gs[6] = new LatLng(35.891314564108086,128.61491154671197);
        gs[7] = new LatLng(35.89073505027637,128.60705917106915);
        gs[8] = new LatLng(35.888474936393195,128.60355840398785);
        gs[9] = new LatLng(35.88903106406622,128.61451079704094);
        gs[10] = new LatLng(35.886902435184254,128.6092850550675);
        gs[11] = new LatLng(35.886097775912845,128.61015466326953);
        gs[12] = new LatLng(35.885853985137295,128.60951853050196);
        gs[13] = new LatLng(35.885276522419204,128.6095732840364);
        gs[14] = new LatLng(35.884744159531174,128.61096883924978);
        gs[15] = new LatLng(35.89485868176753,128.6029451479523);
        gs[16] = new LatLng(35.89591687001675,128.61275127329623);
        gs[17] = new LatLng(35.87861031813363,128.60856641174692);
        gs[18] = new LatLng(35.879165295748244,128.60581499773247);
        gs[19] = new LatLng(35.895268233251386,128.61759446851426);
        gs[20] = new LatLng(35.893919446769694,128.61838101615362);
        gs[21] = new LatLng(35.895135401159976,128.622763742976);
        gs[22] = new LatLng(35.89502598262951,128.6236917990632);
        gs[23] = new LatLng(35.89280155511254,128.6222565086826);
        gs[24] = new LatLng(35.89125927733047,128.61935124327914);
        gs[25] = new LatLng(35.89041704539395,128.61966077931382);
        gs[26] = new LatLng(35.8851888612022,128.60450813624072);
        gs[27] = new LatLng(35.882623973995436,128.60829878172277);
        gs[28] = new LatLng(35.88484818929191,128.61446186025802);
        gs[29] = new LatLng(35.88108847804093,128.6172866518344);
        gs[30] = new LatLng(35.88002113403262,128.61054375775606);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    public void Request(){
        request = new StringRequest(
                Request.Method.GET,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("태그","리스폰"+response);
                        gson = new Gson();
                        csr = gson.fromJson(response, ConvenienceStoreResponse.class);
                        if (csr == null) {
                            Toast.makeText(getApplicationContext(), "parse 실패", Toast.LENGTH_LONG).show();
                        } else if (csr.getData() == null) {
                            Toast.makeText(getApplicationContext(), "rowList가 null", Toast.LENGTH_LONG).show();
                        }
                        Map();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("태그","에러"+error.getMessage());
                    }
                }) {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<String,String>();
                return param;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    public void Map(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.googleMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");
        mMap = googleMap;
        getCurrentLocation();
        setDefaultLocation();

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            // 2. 이미 퍼미션을 가지고 있다면
            startLocationUpdates(); // 3. 위치 업데이트 시작
        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MapActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 현재 오동작을 해서 주석처리
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d( TAG, "onMapClick :");
            }
        });
        for(int i=0;i<=30;i++) {
            LatLng point = gs[i];
            Marker targetMarker;
            double lat = gs[i].latitude;
            double lon = gs[i].longitude;
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(gs[i]);
            markerOptions.title("gs25");
            markerOptions.snippet("경북대 근처 gs25");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

            targetMarker = mMap.addMarker(markerOptions);
        }

        if (csr != null && csr.getData() != null) {
            List<Item> rowList = csr.getData();
            for (int i = 0; i < rowList.size(); i++) {
                Item item = rowList.get(i);

                // 위도와 경도가 null이 아닌지 확인
                if (item.get위도() != null && item.get경도() != null) {
                    try {
                        // 문자열을 double로 변환
                        double latitude = item.getLatitude();
                        double longitude = item.getLongitude();

                        // LatLng 객체 생성
                        LatLng conv = new LatLng(latitude, longitude);

                        // MarkerOptions 객체 생성 및 위치 설정
                        MarkerOptions markerOptions = new MarkerOptions().position(conv);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                        // 지도에 마커 추가 (mMap이 초기화되어 있는지 확인)
                        if (mMap != null) {
                            mMap.addMarker(markerOptions);
                        } else {
                            // mMap이 null인 경우 처리 (예: 로그 출력)
                            Toast.makeText(this, "mMap이 null", Toast.LENGTH_LONG).show();
                        }
                    } catch (NumberFormatException e) {
                        // 숫자 형식이 잘못된 경우 예외 처리
                        Toast.makeText(this, "숫자형식 문제", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    // 위도나 경도가 null인 경우 처리 (예: 로그 출력)
                    Toast.makeText(this, "위도나 경도가 null", Toast.LENGTH_LONG).show();
                }
            }
        }
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (currentLocation != null) {
                    displayNewRoute(currentLocation, marker.getPosition());
                } else {
                    Toast.makeText(MapActivity.this, "현재 위치를 가져오는 중입니다.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new com.google.android.gms.tasks.OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (!locationList.isEmpty()) {
                location = locationList.get(locationList.size() - 1);

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());


                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocation = location;

                //데이터베이스에 현재 위도, 경도 쓰기
                myRef.child(uid).child("latitude").setValue(location.getLatitude(), new DatabaseReference.CompletionListener(){
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            Log.d(TAG, "Data could not be saved: " + error.getMessage());
                        } else {
                            Log.d(TAG, "Data saved successfully.");
                        }
                    }
                });

                myRef.child(uid).child("longitude").setValue(location.getLongitude(), new DatabaseReference.CompletionListener(){
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            Log.d(TAG, "Data could not be saved: " + error.getMessage());
                        } else {
                            Log.d(TAG, "Data saved successfully.");
                        }
                    }
                });
            }

            myRef.addValueEventListener(new ValueEventListener() {
                Marker targetMarker;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        DataSnapshot targetSnapshot = snapshot.child(target);

                        if (targetSnapshot.exists()) {
                            // 하위 노드에서 latitude와 longitude 값을 직접 가져오기
                            Double latitude = targetSnapshot.child("latitude").getValue(Double.class);
                            Double longitude = targetSnapshot.child("longitude").getValue(Double.class);

                            if (latitude != null && longitude != null) {
                                LatLng latLng = new LatLng(latitude, longitude);
                                if(targetMarker!=null){
                                    targetMarker.remove();
                                }

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title("현재 위치");
                                markerOptions.snippet("위도: " + latitude + ", 경도: " + longitude);
                                targetMarker = mMap.addMarker(markerOptions);
                            } else {
                                Log.e(TAG, "위도 또는 경도 값이 null입니다.");
                                Toast.makeText(MapActivity.this, "위도 또는 경도 값이 null입니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else{
                            Log.e(TAG, "snapshot이 null입니다.");
                            Toast.makeText(MapActivity.this, "상대방이 지도를 실행할때까지 기다리는 중입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "데이터가 존재하지 않습니다.");
                        Toast.makeText(MapActivity.this, "데이터가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "데이터를 가져오는 데 실패했습니다: " + error.getMessage());
                    Toast.makeText(MapActivity.this, "데이터를 가져오는 데 실패했습니다: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    };

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public String getCurrentAddress(LatLng latlng) {

        if (latlng == null) {
            Toast.makeText(this, "LatLng 객체가 null입니다", Toast.LENGTH_LONG).show();
            return "LatLng 객체가 null입니다";
        }

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude,1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            if (address != null && address.getAddressLine(0) != null) {
                return address.getAddressLine(0);
            } else {
                Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
                return "주소 미발견";
            }
        }

    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);

        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        //mMap.moveCamera(cameraUpdate);

    }

    public void setDefaultLocation() {

        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(35.89, 128.61);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }
        return false;

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {

                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                        needRequest = true;
                        return;
                    }
                }

                break;
        }
    }
    private void getDirections(LatLng origin, LatLng destination) {
        String originStr = origin.latitude + "," + origin.longitude;
        String destinationStr = destination.latitude + "," + destination.longitude;
        String key = "AIzaSyDHQEIQgFC5vjnEjNCtyKvyfpVoYBd4yiQ";

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + originStr + "&destination=" + destinationStr + "&mode=transit&key=" + key;

        Log.d("DirectionsAPI", "URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DirectionsAPI", "Response: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray routes = jsonResponse.getJSONArray("routes");
                            if (routes.length() > 0) {
                                JSONObject route = routes.getJSONObject(0);
                                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                                String points = overviewPolyline.getString("points");
                                List<LatLng> decodedPath = PolyUtil.decode(points);

                                currentPolyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.BLUE));
                            } else {
                                Toast.makeText(MapActivity.this, "경로를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapActivity.this, "경로 요청 실패", Toast.LENGTH_SHORT).show();
                Log.e("DirectionsAPI", "Error: " + error.getMessage());
            }
        });

        queue.add(stringRequest);
    }
    private void removePolyline() {
        if (currentPolyline != null) {
            currentPolyline.remove(); // Polyline 객체 삭제
            currentPolyline = null; // 객체 참조 해제
        }
    }
    private void displayNewRoute(LatLng origin, LatLng destination) {
        removePolyline(); // 기존의 Polyline 삭제
        getDirections(origin, destination); // 새로운 경로 요청
    }

}