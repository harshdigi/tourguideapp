package in.digitaldealsolution.bharatdarshan;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.digitaldealsolution.bharatdarshan.Adapter.PlaceAdapter;
import in.digitaldealsolution.bharatdarshan.Models.Places;

public class ListViewActivity extends AppCompatActivity {
    private DatabaseReference places = FirebaseDatabase.getInstance("https://tour-guide-mit-default-rtdb.firebaseio.com/").getReference("Places");
    private RecyclerView recyclerView;
    private ArrayList<Places> placesArrayList;
    private EditText placeSearch;
    private PlaceAdapter placeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        recyclerView = findViewById(R.id.place_recyclerView);
        placeSearch = findViewById(R.id.search_place);
        getPlacesList();
        placeSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });


    }

    public void getPlacesList() {
        placesArrayList = new ArrayList<>();
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
                    placesArrayList.add(place);
                }
                PutDataIntoRecyclerView(placesArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filter(String text) {
        List<Places> filterlist = new ArrayList<>();
        for (Places places : placesArrayList) {
            if (places.getName().toLowerCase().contains(text.toLowerCase())) {
                filterlist.add(places);
            } else if (places.getType().toLowerCase().contains(text.toLowerCase())) {
                filterlist.add(places);
            }

            placeAdapter.filterlist(filterlist);
        }
    }

    private void PutDataIntoRecyclerView(ArrayList<Places> placesList) {
        placeAdapter = new PlaceAdapter(ListViewActivity.this, placesList);
        LinearLayoutManager manager = new LinearLayoutManager(ListViewActivity.this) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
            }
        };
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(placeAdapter);

    }
}