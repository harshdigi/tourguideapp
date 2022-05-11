package in.digitaldealsolution.bharatdarshan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.digitaldealsolution.bharatdarshan.Models.Places;

public class PlaceActivity extends AppCompatActivity {
    String name;
    Places place;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://tour-guide-mit-default-rtdb.firebaseio.com/").getReference("Places");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        name = getIntent().getStringExtra("name");
        databaseReference.child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        })

    }
}