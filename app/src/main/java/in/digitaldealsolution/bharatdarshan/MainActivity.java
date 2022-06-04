package in.digitaldealsolution.bharatdarshan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import in.digitaldealsolution.bharatdarshan.Models.Places;
import in.digitaldealsolution.bharatdarshan.Utils.PlacesCallback;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private DatabaseReference places = FirebaseDatabase.getInstance("https://tour-guide-mit-default-rtdb.firebaseio.com/").getReference("Places");
    private HashMap<String, Places> placesList;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout menu, camera, list, info, qr_code;

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment;
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        menu = findViewById(R.id.menu);
        camera = findViewById(R.id.camera);
        list = findViewById(R.id.list);
        info = findViewById(R.id.info);
        qr_code = findViewById(R.id.qr_code);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camera.getVisibility() == View.GONE) {
                    camera.setVisibility(View.VISIBLE);
                    list.setVisibility(View.VISIBLE);
                    info.setVisibility(View.VISIBLE);
                    qr_code.setVisibility(View.VISIBLE);
                } else {
                    camera.setVisibility(View.GONE);
                    list.setVisibility(View.GONE);
                    info.setVisibility(View.GONE);
                    qr_code.setVisibility(View.GONE);
                }
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetectPlaceActivity.class);
                startActivity(intent);
            }
        });
        qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QrScannerActivity.class);
                startActivity(intent);
            }
        });
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                intent.putExtra("placesList", placesList);
                startActivity(intent);
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });
        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (response != ConnectionResult.SUCCESS) {
            Log.d("places", "Google Play Service Not Available");
            GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, response, 1).show();
        } else {
            Log.d("places", "Google play service available");
        }

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            String name;
                            name = deepLink.getQueryParameter("name");
                            Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
                            intent.putExtra("name", name);
                            startActivity(intent);
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Place", "getDynamicLink:onFailure", e);
                    }
                });

        Uri placeurl = getIntent().getData();
        if (placeurl != null) {
            String name;
            name = placeurl.getQueryParameter("name");
            Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);

        }
        initComponent();
    }


    private void initComponent() {
        ConstraintLayout llBottomSheet = (ConstraintLayout) findViewById(R.id.bottom_sheet);

        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    public void getPlacesList(PlacesCallback placesCallback) {
        placesList = new HashMap<>();
        places.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double lat = (double) snapshot.child("lat").getValue();
                    Double lng = (double) snapshot.child("lng").getValue();
                    String type = snapshot.child("type").getValue(String.class);
                    String name = snapshot.getKey();
                    String summary = snapshot.child("summary").getValue(String.class);
                    String about = snapshot.child("about").getValue(String.class);
                    String mission = snapshot.child("mission").getValue(String.class);
                    String vision = snapshot.child("vision").getValue(String.class);
                    String brochure = snapshot.child("brochure").getValue(String.class);
                    Places place = new Places(name, lng, lat, type, about, summary, brochure, mission, vision);
                    placesList.put(name, place);
                }
                placesCallback.onResponse(placesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getPlacesList(new PlacesCallback() {
            @Override
            public void onResponse(HashMap<String, Places> placeList) {
                Iterator<HashMap.Entry<String, Places>> itr = placeList.entrySet().iterator();
                while (itr.hasNext()) {
                    Places places = itr.next().getValue();
                    LatLng latLng = new LatLng(places.getlat(), places.getlng());
                    Marker m;
                    switch (places.getType()) {
                        case "college":
                            m = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.school)))
                                    .title("Marker in " + places.getName()));

                            break;
                        case "hostel":
                            m = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.hostel)))
                                    .title("Marker in " + places.getName()));

                            break;
                        case "food":
                            m = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.salad)))
                                    .title("Marker in " + places.getName()));

                            break;
                        case "utility":
                            m = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.utility)))
                                    .title("Marker in " + places.getName()));

                            break;
                        case "attraction":
                            m = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.attraction)))
                                    .title("Marker in " + places.getName()));

                            break;
                        case "event hall":
                            m = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.cinema)))
                                    .title("Marker in " + places.getName()));

                            break;
                        case "lab":
                            m = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.lab)))
                                    .title("Marker in " + places.getName()));

                            break;
                        case "sports":
                            m = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.sports)))
                                    .title("Marker in " + places.getName()));

                            break;
                        case "office":
                            m = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.office)))
                                    .title("Marker in " + places.getName()));

                            break;
                        default:
                            m = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title("Marker in " + places.getName()));
                            break;
                    }

                    m.setTag(places);

                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            try {
                                updateBottomSheetContent(marker);
                            } catch (Exception e) {
                            }
                            return true;
                        }

                    });
                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    });
                }

            }

            @Override
            public void onResponseData(ArrayList<Places> placesList) {

            }
        });
        setFocus(googleMap);
    }

    private void setFocus(GoogleMap googleMap) {
        LatLng one = new LatLng(18.4906622369812, 74.01381064969718);
        LatLng two = new LatLng(18.495677463847706, 74.03215117924236);


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(one);
        builder.include(two);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        int padding = (int) (width * 0.20);

        googleMap.setLatLngBoundsForCameraTarget(bounds);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        googleMap.setMinZoomPreference(googleMap.getCameraPosition().zoom);

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng dome = new LatLng(18.492858977762122, 74.02552368092631);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(dome));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
    }

    private void updateBottomSheetContent(Marker marker) {
        TextView name = findViewById(R.id.place_name);
        TextView summary = findViewById(R.id.place_summary);
        TextView type = findViewById(R.id.place_type);
        Button btnInfo = findViewById(R.id.more_info);
        Button btnDirection = findViewById(R.id.get_direction);

        Places place = (Places) marker.getTag();
        name.setText(place.getName());
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        if (!place.getSummary().equals("")) {
            SpannableString summary_text = new SpannableString("Summary: " + place.getSummary());

            summary_text.setSpan(boldSpan, 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            summary.setText(summary_text);
        } else {
            summary.setVisibility(View.GONE);
        }
        SpannableString type_text = new SpannableString("Place Type: " + place.getType().toUpperCase());
        type_text.setSpan(boldSpan, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        type.setText(type_text);

        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", place.getlat(), place.getlng(), "Where the party is at");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
                intent.putExtra("name", place.getName());
                startActivity(intent);
            }
        });

    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
}
