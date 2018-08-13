package com.seg2.edudata.lists;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

public class Country implements Parcelable {
    String country;
    String code;
    String capital;
    String longitude;
    String latitude;
    boolean isSelected;

    public Country(JSONObject data) {
        try {
            country = (String) data.get("name");
            code = (String) data.get("iso2Code");
            capital = (String) data.get("capitalCity");
            longitude = (String) data.get("longitude");
            latitude = (String) data.get("latitude");
            isSelected = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String toString() {
        return country;
    }

    public String getCountryCode() {
        return code;
    }

    public String getCapital() {
        return capital;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setSelected(boolean b) {
        isSelected = b;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public static Comparator<Country> CountryCompare = new Comparator<Country>() {

        public int compare(Country c1, Country c2) {
            return c1.toString().toUpperCase().compareTo(c2.toString().toUpperCase());
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(country);
        out.writeString(code);
        out.writeString(capital);
        out.writeString(longitude);
        out.writeString(latitude);

    }

    public static final Parcelable.Creator<Country> CREATOR
            = new Parcelable.Creator<Country>() {
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    private Country(Parcel in) {
        country = in.readString();
        code = in.readString();
        capital = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        isSelected = true;
    }
}