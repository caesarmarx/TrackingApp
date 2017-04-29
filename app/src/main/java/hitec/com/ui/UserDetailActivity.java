package hitec.com.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hitec.com.ApplicationContext;
import hitec.com.R;
import hitec.com.adapter.MessageAdapter;
import hitec.com.event.GetUserMessagesEvent;
import hitec.com.model.MessageItem;
import hitec.com.proxy.BaseProxy;
import hitec.com.task.GetUserMessagesTask;
import hitec.com.util.SharedPrefManager;
import hitec.com.vo.GetUserMessagesResponseVO;

public class UserDetailActivity extends AppCompatActivity {

    //defining views
    @Bind(R.id.message_list)
    RecyclerView messageList;
    @Bind(R.id.btn_track_location)
    Button btnTrackLocation;

    private ProgressDialog progressDialog;
    private MessageAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    private String username;
    private int usertype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = getIntent().getStringExtra("username");

        messageList.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(UserDetailActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messageList.setLayoutManager(mLinearLayoutManager);
        messageList.addItemDecoration(new DividerItemDecoration(UserDetailActivity.this, DividerItemDecoration.VERTICAL_LIST));

        usertype = SharedPrefManager.getInstance(this).getUserType();
        if(usertype == 0) {
            btnTrackLocation.setVisibility(View.VISIBLE);
        } else {
            btnTrackLocation.setVisibility(View.GONE);
        }

        adapter = new MessageAdapter(UserDetailActivity.this, username);
        messageList.setAdapter(adapter);

        progressDialog = new ProgressDialog(UserDetailActivity.this);
        getUserMessages();
    }

    @Subscribe
    public void onGetUserMessagesEvent(GetUserMessagesEvent event) {
        hideProgressDialog();
        GetUserMessagesResponseVO responseVo = event.getResponse();
        if (responseVo != null) {
            if(responseVo.success == BaseProxy.RESPONSE_SUCCESS) {
                String messages = responseVo.messages;
                refreshList(messages);
            } else {
                networkError();
            }
        } else {
            networkError();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_track_location)
    void onBtnTrackLocation() {
        Intent intent = new Intent(UserDetailActivity.this, LocationDetailActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void getUserMessages() {
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
        GetUserMessagesTask task = new GetUserMessagesTask();

        task.execute(username);
    }

    private void refreshList(String users) {
        ArrayList<MessageItem> items = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(users);
            int count = jsonArray.length();
            for(int i = 0; i < count; i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);
                String fromUser = json.getString("from_user");
                String toUser = json.getString("to_user");
                String message = json.getString("message");
                String imageURL = json.getString("image");
                String time = json.getString("time");

                MessageItem item = new MessageItem();
                item.setFromUser(fromUser);
                item.setToUser(toUser);
                item.setMessage(message);
                item.setImageURL(imageURL);
                item.setTime(time);

                items.add(item);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        adapter.addItems(items);
        adapter.notifyDataSetChanged();
    }

    private void hideProgressDialog() {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void networkError() {
        ApplicationContext.showToastMessage(UserDetailActivity.this, getResources().getString(R.string.network_error));
    }
}