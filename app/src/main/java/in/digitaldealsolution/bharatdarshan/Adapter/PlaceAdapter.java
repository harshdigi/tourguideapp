package in.digitaldealsolution.bharatdarshan.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import in.digitaldealsolution.bharatdarshan.Models.Places;
import in.digitaldealsolution.bharatdarshan.PlaceActivity;
import in.digitaldealsolution.bharatdarshan.R;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private Context context;
    private List<Places> placeList;

    public PlaceAdapter(Context context, List<Places> placeList) {
        this.context = context;
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public PlaceAdapter.PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        v = layoutInflater.inflate(R.layout.listview_card, parent, false);
        return new PlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceAdapter.PlaceViewHolder holder, int position) {
        holder.placeName.setText(placeList.get(position).getName());
        holder.placeType.setText(": " + placeList.get(position).getType().toUpperCase());
        holder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlaceActivity.class);
                intent.putExtra("name", placeList.get(holder.getAdapterPosition()).getName());
                context.startActivity(intent);
            }
        });
        holder.getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", placeList.get(holder.getAdapterPosition()).getlat(), placeList.get(holder.getAdapterPosition()).getlng(), "Where the party is at");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.buttonsHolder.getVisibility() == View.GONE) {
                    holder.buttonsHolder.setVisibility(View.VISIBLE);
                } else {
                    holder.buttonsHolder.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView placeName, placeType;
        Button getDirection, moreInfo;
        LinearLayout buttonsHolder;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.card_place_name);
            placeType = itemView.findViewById(R.id.card_place_type);
            getDirection = itemView.findViewById(R.id.get_direction_card);
            moreInfo = itemView.findViewById(R.id.more_info_card);
            buttonsHolder = itemView.findViewById(R.id.bottom_card_btn);
        }
    }

    public void filterlist(List<Places> filterList) {
        placeList = filterList;
        notifyDataSetChanged();
    }
}
