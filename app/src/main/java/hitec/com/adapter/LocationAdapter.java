package hitec.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hitec.com.R;
import hitec.com.model.LocationItem;
import hitec.com.model.MessageItem;
import hitec.com.ui.LocationDetailActivity;
import hitec.com.ui.UserDetailActivity;
import hitec.com.util.SharedPrefManager;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private LocationDetailActivity parent;
    private List<LocationItem> items = new ArrayList<>();

    public LocationAdapter(LocationDetailActivity parent) {
        this.parent = parent;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_locations, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocationViewHolder holder, int position) {
        LocationItem item = items.get(position);

        holder.tvAddress.setText(item.getAddress());
        holder.tvLatitude.setText(item.getLatitude());
        holder.tvLongitude.setText(item.getLongitude());
        holder.tvTime.setText(item.getTime().split(" ")[1]);
    }

    public LocationItem getItem(int pos) {
        return items.get(pos);
    }

    public void clearItems() {
        items.clear();
    }

    public void addItem(LocationItem item) {
        items.add(item);
    }

    public void addItems(ArrayList<LocationItem> items) {
        this.items.clear();
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        public final View view;

        @Bind(R.id.tv_address)
        TextView tvAddress;
        @Bind(R.id.tv_latitude)
        TextView tvLatitude;
        @Bind(R.id.tv_longitude)
        TextView tvLongitude;
        @Bind(R.id.tv_time)
        TextView tvTime;

        public LocationViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
