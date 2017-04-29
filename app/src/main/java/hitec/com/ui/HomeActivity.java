package hitec.com.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hitec.com.ApplicationContext;
import hitec.com.R;
import hitec.com.adapter.RecentStatusAdapter;
import hitec.com.adapter.UserAdapter;
import hitec.com.event.GetRecentStatusEvent;
import hitec.com.event.GetUsersEvent;
import hitec.com.model.RecentStatusItem;
import hitec.com.model.UserItem;
import hitec.com.notification.TrackingService;
import hitec.com.proxy.BaseProxy;
import hitec.com.task.GetRecentStatusTask;
import hitec.com.task.GetUsersTask;
import hitec.com.task.SendLocationTask;
import hitec.com.task.SendNotificationTask;
import hitec.com.util.SharedPrefManager;
import hitec.com.vo.GetRecentStatusResponseVO;
import hitec.com.vo.GetUsersResponseVO;

public class HomeActivity extends AppCompatActivity {

    //defining views
    @Bind(R.id.user_list)
    RecyclerView userList;

    private ProgressDialog progressDialog;
    private RecentStatusAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        userList.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        userList.setLayoutManager(mLinearLayoutManager);
        userList.addItemDecoration(new DividerItemDecoration(HomeActivity.this, DividerItemDecoration.VERTICAL_LIST));

        adapter = new RecentStatusAdapter(HomeActivity.this);
        userList.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        startService(new Intent(HomeActivity.this, TrackingService.class));
    }

    @Subscribe
    public void onGetRecentStatusEvent(GetRecentStatusEvent event) {
        hideProgressDialog();
        GetRecentStatusResponseVO responseVo = event.getResponse();
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

        getRecentStatus();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.btn_view_users)
    void onBtnClickViewUsers() {
        Intent intent = new Intent(HomeActivity.this, UserListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_view_my_messages)
    void onBtnClickMyMessages() {
        String username = SharedPrefManager.getInstance(HomeActivity.this).getUsername();
        Intent intent = new Intent(HomeActivity.this, UserDetailActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    @OnClick(R.id.btn_post_status)
    void onBtnClickPostStatus() {
        Intent intent = new Intent(HomeActivity.this, PostStatusActivity.class);
        PostStatusActivity.mphoto = null;
        startActivity(intent);
    }

    private void getRecentStatus() {
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
        GetRecentStatusTask task = new GetRecentStatusTask();
        String username = SharedPrefManager.getInstance(this).getUsername();

        task.execute(username);
    }

    private void refreshList(String messages) {
        ArrayList<RecentStatusItem> items = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(messages);
            int count = jsonArray.length();
            for(int i = 0; i < count; i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);
                String fromUser = json.getString("from_user");
                String toUser = json.getString("to_user");
                String curUserName = SharedPrefManager.getInstance(this).getUsername();

                RecentStatusItem item = new RecentStatusItem();
                if(fromUser.equals(curUserName)) {
                    item.setUserName("To " + toUser);
                }
                else {
                    item.setUserName("From " + fromUser);
                }
                item.setMessage(json.getString("message"));
                item.setImageURL(json.getString("image"));
                item.setTime(json.getString("time"));
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
        ApplicationContext.showToastMessage(HomeActivity.this, getResources().getString(R.string.network_error));
    }

    public void sendNotification(final String receiver) {
        //Show Tag Request Dialog
        LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dlg_input_tag, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText edtTag = (EditText) promptView.findViewById(R.id.edt_tag);

        alertDialogBuilder.setCancelable(false)
                .setTitle(R.string.input_tag)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sender = SharedPrefManager.getInstance(HomeActivity.this).getUsername();
                        String message = edtTag.getText().toString();
                        SendNotificationTask task = new SendNotificationTask();
                        task.execute(sender, receiver, message);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}