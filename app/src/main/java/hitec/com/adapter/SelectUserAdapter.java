package hitec.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hitec.com.R;
import hitec.com.model.UserItem;
import hitec.com.ui.SelectUserActivity;
import hitec.com.util.SharedPrefManager;

public class SelectUserAdapter extends RecyclerView.Adapter<SelectUserAdapter.UserViewHolder> {

    private SelectUserActivity parent;
    private int usertype;
    private List<UserItem> items = new ArrayList<>();

    public SelectUserAdapter(SelectUserActivity parent) {
        this.parent = parent;
        this.usertype = SharedPrefManager.getInstance(parent).getUserType();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_select_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        final UserItem item = items.get(position);

        holder.tvUserName.setText(item.getUsername());

        holder.view.setTag(position);
        holder.chkSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    item.setSelected(true);
                else
                    item.setSelected(false);
            }
        });
    }

    public UserItem getItem(int pos) {
        return items.get(pos);
    }

    public void clearItems() {
        items.clear();
    }

    public void addItem(UserItem item) {
        items.add(item);
    }

    public void addItems(ArrayList<UserItem> items) {
        this.items.clear();
        this.items = items;
    }

    public ArrayList<UserItem> getSelectedItems() {
        ArrayList<UserItem> selectedItems = new ArrayList<>();
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getSelected())
                selectedItems.add(items.get(i));
        }

        return selectedItems;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public final View view;

        @Bind(R.id.tv_username)
        TextView tvUserName;
        @Bind(R.id.btn_check)
        CheckBox chkSelect;

        public UserViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
