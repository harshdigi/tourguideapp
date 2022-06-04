package in.digitaldealsolution.bharatdarshan;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PlaceActivity extends AppCompatActivity {
    String name, type, summary, about, mission, vision, brochure;
    Double lat, lng;
    TextView textName, textSummary, textAbout, textMission, textVision, textEvents;
    List eventsList;
    Button getDirection, getBrochure;
    DownloadManager manager;
    ImageView summaryImg, aboutImg, missionImg, visionImg, eventsImg;


    private DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://tour-guide-mit-default-rtdb.firebaseio.com/").getReference("Places");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        name = getIntent().getStringExtra("name");
        textName = findViewById(R.id.name);
        textSummary = findViewById(R.id.summary);
        textAbout = findViewById(R.id.about);
        textMission = findViewById(R.id.mission);
        textVision = findViewById(R.id.vision);
        textEvents = findViewById(R.id.events);
        getDirection = findViewById(R.id.direction);
        getBrochure = findViewById(R.id.brochure);
        summaryImg = findViewById(R.id.summary_arrow);
        aboutImg = findViewById(R.id.about_arrow);
        missionImg = findViewById(R.id.mission_arrow);
        visionImg = findViewById(R.id.vision_arrow);
        eventsImg = findViewById(R.id.events_arrow);
        databaseReference.child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()   ) {
                    lng = (double) snapshot.child("lng").getValue();
                    lat = (double) snapshot.child("lat").getValue();
                    summary = snapshot.child("summary").getValue(String.class);
                    about = snapshot.child("about").getValue(String.class);
                    mission = snapshot.child("mission").getValue(String.class);
                    vision = snapshot.child("vision").getValue(String.class);
                    brochure = snapshot.child("brochure").getValue(String.class);

                    GenericTypeIndicator<List<HashMap<String, String>>> t = new GenericTypeIndicator<List<HashMap<String, String>>>() {
                    };
                    if (snapshot.child("events").exists()) {
                        eventsList = snapshot.child("events").getValue(t);
                    } else {
                        eventsList = new ArrayList();
                    }
                    getDirection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", lat, lng, "Where the party is at");
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setPackage("com.google.android.apps.maps");
                            startActivity(intent);
                        }

                    });

                    //Setting Name
                    type = snapshot.child("type").getValue(String.class);
                    name = snapshot.getKey();
                    textName.setText(name);
                    StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                    // Setting Summary
                    if (!summary.equals("")) {
                        SpannableString summary_text = new SpannableString("Summary: " + summary);
                        summary_text.setSpan(boldSpan, 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textSummary.setText(summary_text);
                    } else {
                        findViewById(R.id.summary_btn).setVisibility(View.GONE);
                    }
                    //setting about
                    if (!about.equals("")) {
                        SpannableString about_text = new SpannableString("About: " + about);
                        about_text.setSpan(boldSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textAbout.setText(about_text);
                    } else {
                        findViewById(R.id.about_btn).setVisibility(View.GONE);
                    }

                    //Setting mission
                    if (!mission.equals("")) {
                        SpannableString mission_text = new SpannableString("Mission: " + mission);
                        mission_text.setSpan(boldSpan, 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textMission.setText(mission_text);
                    } else {
                        findViewById(R.id.mission_btn).setVisibility(View.GONE);
                    }

                    // Setting vision
                    if (!vision.equals("")) {

                        SpannableString vision_text = new SpannableString("Vision: " + vision);
                        vision_text.setSpan(boldSpan, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textVision.setText(vision_text);
                    } else {
                        findViewById(R.id.vision_btn).setVisibility(View.GONE);
                    }

                    // setting brochure
                    if (!brochure.equals("")) {
                        getBrochure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                Uri uri = Uri.parse(brochure);
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                long reference = manager.enqueue(request);
                                Toast.makeText(PlaceActivity.this, "File added for Downloading", Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        getBrochure.setVisibility(View.GONE);
                    }

                    if (!eventsList.isEmpty()) {
                        String text = "";
                        for (int i = 0; i < eventsList.size(); i++) {
                            String status = null;
                            Date date = null;
                            Date newDate = null;
                            HashMap<String, String> temp = (HashMap<String, String>) eventsList.get(i);
                            DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            try {
                                date = format.parse(temp.get("datetime"));
                                newDate = new Date(date.getTime() + TimeUnit.HOURS.toMillis(Long.valueOf(temp.get("durationhr"))));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date currentTime = Calendar.getInstance().getTime();
                            if (newDate.after(currentTime) && date.before(currentTime)) {
                                status = "Ongoing";
                            }
                            if (date.after(currentTime)) {
                                status = "Upcoming";
                            }
                            if (newDate.before(currentTime)) {
                                status = "Expired";
                            }
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            text += "<b>" + String.valueOf(i + 1) + "</b>. " + "<b> Event Name: </b> " + temp.get("name") + "<br>" + "<b> Date: </b>" + cal.get(Calendar.DATE) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + "<br>" + "<b>Time:</b> " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + "<br>" + "<b>Status:</b> " + status + "<br><br>";


                        }
                        textEvents.setText(Html.fromHtml(text));
                    } else {
                        findViewById(R.id.events_btn).setVisibility(View.GONE);
                    }

                }
                else{
                    Toast.makeText(PlaceActivity.this, "No such Place", Toast.LENGTH_LONG );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void unHide(View v) {
        switch (v.getId()) {
            case R.id.summary_btn:
                if (textSummary.getVisibility() == View.GONE) {
                    textSummary.setVisibility(View.VISIBLE);
                    summaryImg.setImageDrawable(getDrawable(R.drawable.ic_down));
                } else {
                    textSummary.setVisibility(View.GONE);
                    summaryImg.setImageDrawable(getDrawable(R.drawable.ic_forward));
                }
                break;
            case R.id.about_btn:
                if (textAbout.getVisibility() == View.GONE) {
                    textAbout.setVisibility(View.VISIBLE);
                    aboutImg.setImageDrawable(getDrawable(R.drawable.ic_down));
                } else {
                    textAbout.setVisibility(View.GONE);
                    aboutImg.setImageDrawable(getDrawable(R.drawable.ic_forward));
                }
                break;
            case R.id.mission_btn:
                if (textMission.getVisibility() == View.GONE) {
                    textMission.setVisibility(View.VISIBLE);
                    missionImg.setImageDrawable(getDrawable(R.drawable.ic_down));
                } else {
                    textMission.setVisibility(View.GONE);
                    missionImg.setImageDrawable(getDrawable(R.drawable.ic_forward));
                }
                break;
            case R.id.vision_btn:
                if (textVision.getVisibility() == View.GONE) {
                    textVision.setVisibility(View.VISIBLE);
                    visionImg.setImageDrawable(getDrawable(R.drawable.ic_down));
                } else {
                    textVision.setVisibility(View.GONE);
                    visionImg.setImageDrawable(getDrawable(R.drawable.ic_forward));
                }
                break;
            case R.id.events_btn:
                if (textEvents.getVisibility() == View.GONE) {
                    textEvents.setVisibility(View.VISIBLE);
                    eventsImg.setImageDrawable(getDrawable(R.drawable.ic_down));
                } else {
                    textEvents.setVisibility(View.GONE);
                    eventsImg.setImageDrawable(getDrawable(R.drawable.ic_forward));
                }
                break;

        }
    }
}