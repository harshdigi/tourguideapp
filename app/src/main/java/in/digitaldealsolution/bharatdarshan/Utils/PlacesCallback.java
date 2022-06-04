package in.digitaldealsolution.bharatdarshan.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import in.digitaldealsolution.bharatdarshan.Models.Places;

public interface PlacesCallback {
    void onResponse(HashMap<String, Places> placeList);

    void onResponseData(ArrayList<Places> placesList);
}
