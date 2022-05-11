package in.digitaldealsolution.bharatdarshan.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Places implements Parcelable, Serializable {
    double lng, lat;
    String name,type, about, summary, brochure,mission,vision;

    public Places(String name, double lng, double lat, String type, String about, String summary, String brochure, String mission, String vision) {
        this.name = name;
        this.lng = lng;
        this.lat = lat;
        this.type = type;
        this.about = about;
        this.summary = summary;
        this.brochure = brochure;
        this.mission = mission;
        this.vision = vision;
    }

    protected Places(Parcel in) {
        lng = in.readDouble();
        lat = in.readDouble();
        name = in.readString();
        type = in.readString();
        about = in.readString();
        summary = in.readString();
        brochure = in.readString();
        mission = in.readString();
        vision = in.readString();
    }

    public static final Creator<Places> CREATOR = new Creator<Places>() {
        @Override
        public Places createFromParcel(Parcel in) {
            return new Places(in);
        }

        @Override
        public Places[] newArray(int size) {
            return new Places[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBrochure() {
        return brochure;
    }

    public void setBrochure(String brochure) {
        this.brochure = brochure;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getVision() {
        return vision;
    }

    public void setVision(String vision) {
        this.vision = vision;
    }
    

    public double getlng() {
        return lng;
    }

    public void setlng(double lng) {
        lng = lng;
    }

    public double getlat() {
        return lat;
    }

    public void setlat(double lat) {
        lat = lat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(lng);
        parcel.writeDouble(lat);
        parcel.writeString(name);
        parcel.writeString(type);
        parcel.writeString(about);
        parcel.writeString(summary);
        parcel.writeString(brochure);
        parcel.writeString(mission);
        parcel.writeString(vision);
    }
}
