package hitec.com.adapter;

import android.content.Context;
import android.content.Intent;
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
import hitec.com.model.MessageItem;
import hitec.com.ui.UserDetailActivity;
import hitec.com.util.DateUtil;
import hitec.com.util.URLManager;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context parent;
    private List<MessageItem> items = new ArrayList<>();
    private String username;

    public MessageAdapter(Context parent, String username) {
        this.parent = parent;
        this.username = username;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_messages, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        MessageItem item = items.get(position);

        if(username.equals(item.getFromUser())) {
            holder.tvUserName.setText(item.getToUser());
            holder.ivStatus.setBackground(parent.getResources().getDrawable(R.drawable.ic_send));
        }
        else {
            holder.tvUserName.setText(item.getFromUser());
            holder.ivStatus.setBackground(parent.getResources().getDrawable(R.drawable.ic_receive));
        }
        holder.tvMessage.setText(item.getMessage());
        holder.tvMessage.setSelected(true);
        holder.tvTime.setText(DateUtil.getSimpleFormat(item.getTime()));

        holder.tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parent, UserDetailActivity.class);
                intent.putExtra("username", holder.tvUserName.getText().toString());
                parent.startActivity(intent);
            }
        });

        if(!item.getImageURL().isEmpty() && item.getImageURL() != null) {
            ImageLoader.getInstance().displayImage(URLManager.getImageURL() + item.getImageURL(), holder.ivImage);
            holder.ivImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivImage.setVisibility(View.GONE);
        }
    }

    public MessageItem getItem(int pos) {
        return items.get(pos);
    }

    public void clearItems() {
        items.clear();
    }

    public void addItem(MessageItem item) {
        items.add(item);
    }

    public void addItems(ArrayList<MessageItem> items) {
        this.items.clear();
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public final View view;

        @Bind(R.id.tv_username)
        TextView tvUserName;
        @Bind(R.id.tv_message)
        TextView tvMessage;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.iv_image)
        ImageView ivImage;
        @Bind(R.id.iv_status)
        ImageView ivStatus;

        public MessageViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
