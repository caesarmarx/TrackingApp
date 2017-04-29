package hitec.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hitec.com.R;
import hitec.com.model.RecentStatusItem;
import hitec.com.ui.HomeActivity;
import hitec.com.ui.UserDetailActivity;
import hitec.com.util.SharedPrefManager;
import hitec.com.util.URLManager;

public class RecentStatusAdapter extends RecyclerView.Adapter<RecentStatusAdapter.RecentStatusViewHolder> {

    private HomeActivity parent;
    private int usertype;
    private List<RecentStatusItem> items = new ArrayList<>();

    public RecentStatusAdapter(HomeActivity parent) {
        this.parent = parent;
        this.usertype = SharedPrefManager.getInstance(parent).getUserType();
    }

    @Override
    public RecentStatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_messages, parent, false);
        return new RecentStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecentStatusViewHolder holder, int position) {
        RecentStatusItem item = items.get(position);

        holder.tvUserName.setText(item.getUsername());
        holder.tvMessage.setText(item.getMessage());
        holder.tvTime.setText(item.getTIme());

        if(!item.getImageURL().isEmpty() && item.getImageURL() != null) {
            ImageLoader.getInstance().displayImage(URLManager.getImageURL() + item.getImageURL(), holder.ivImage);
        }
    }

    public RecentStatusItem getItem(int pos) {
        return items.get(pos);
    }

    public void clearItems() {
        items.clear();
    }

    public void addItem(RecentStatusItem item) {
        items.add(item);
    }

    public void addItems(ArrayList<RecentStatusItem> items) {
        this.items.clear();
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class RecentStatusViewHolder extends RecyclerView.ViewHolder {
        public final View view;

        @Bind(R.id.tv_username)
        TextView tvUserName;
        @Bind(R.id.tv_message)
        TextView tvMessage;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.iv_image)
        ImageView ivImage;

        public RecentStatusViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
