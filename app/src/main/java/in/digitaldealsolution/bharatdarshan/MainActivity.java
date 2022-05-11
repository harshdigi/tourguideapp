package in.digitaldealsolution.bharatdarshan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.digitaldealsolution.bharatdarshan.Models.Places;
import in.digitaldealsolution.bharatdarshan.Utils.PlacesCallback;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    private DatabaseReference places = FirebaseDatabase.getInstance("https://tour-guide-mit-default-rtdb.firebaseio.com/").getReference("Places");
    private HashMap<String,Places> placesList ;
    private BottomSheetBehavior bottomSheetBehavior ;
    private LinearLayout menu, camera, list,info;

    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    Uri imageuri;
    private Bitmap bitmap;
    private List<String> labels;

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

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(camera.getVisibility() == View.GONE){
                    camera.setVisibility(View.VISIBLE);
                    list.setVisibility(View.VISIBLE);
                    info.setVisibility(View.VISIBLE);
                }
                else{
                    camera.setVisibility(View.GONE);
                    list.setVisibility(View.GONE);
                    info.setVisibility(View.GONE);
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
    public void getPlacesList(PlacesCallback placesCallback){
        placesList = new HashMap<>();
        places.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Double lat = (double) snapshot.child("lat").getValue();
                    Double lng = (double) snapshot.child("lng").getValue();
                    String type = snapshot.child("type").getValue(String.class);
                    String name = snapshot.getKey();
                    String summary = snapshot.child("summary").getValue(String.class);
                    String about = snapshot.child("about").getValue(String.class);
                    String mission = snapshot.child("mission").getValue(String.class);
                    String vision = snapshot.child("vision").getValue(String.class);
                    String brochure = snapshot.child("brochure").getValue(String.class);
                    Places place = new Places(name,lng,lat,type,about,summary,brochure,mission,vision);
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
        getPlacesList(new PlacesCallback() {
            @Override
            public void onResponse(HashMap<String, Places> placeList) {
                Iterator<HashMap.Entry<String, Places>> itr = placeList.entrySet().iterator();
                while (itr.hasNext()){
                    Places places = itr.next().getValue();
                    LatLng latLng = new LatLng(places.getlat(),places.getlng());
                    Marker m = googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Marker in "+ places.getName()));
                    m.setTag(places);
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            try {
                                updateBottomSheetContent(marker);
                            }
                            catch (Exception e) {
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
        int height = getResources().getDisplayMetrics().heightPixels ;

        int padding = (int) (width*0.20);

        googleMap.setLatLngBoundsForCameraTarget(bounds);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        googleMap.setMinZoomPreference(googleMap.getCameraPosition().zoom);

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng dome = new LatLng(18.492858977762122, 74.02552368092631);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(dome));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
    }
    private void updateBottomSheetContent(Marker marker) {
        TextView name  = findViewById(R.id.place_name);
        TextView summary = findViewById(R.id.place_summary);
        TextView type  = findViewById( R.id.place_type);
        Button btnInfo = findViewById(R.id.more_info);
        Button btnDirection = findViewById(R.id.get_direction);

        Places place = (Places) marker.getTag();
        name.setText(place.getName());
        SpannableString summary_text = new SpannableString("Summary: " + place.getSummary() );
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        summary_text.setSpan(boldSpan, 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        summary.setText(summary_text);
        SpannableString type_text = new SpannableString("Place Type:" + place.getType());;
        type_text.setSpan(boldSpan, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        type.setText(type_text);

        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", place.getlat(),place.getlng(), "Where the party is at");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( MainActivity.this, PlaceActivity.class);
                intent.putExtra("name", place.getName());
                startActivity(intent);
            }
        });


    }

}
