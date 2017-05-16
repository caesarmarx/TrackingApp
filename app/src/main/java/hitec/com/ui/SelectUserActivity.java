package hitec.com.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import hitec.com.adapter.SelectUserAdapter;
import hitec.com.adapter.UserAdapter;
import hitec.com.event.GetUsersEvent;
import hitec.com.model.UserItem;
import hitec.com.notification.TrackingService;
import hitec.com.proxy.BaseProxy;
import hitec.com.task.GetUsersTask;
import hitec.com.task.SendLocationTask;
import hitec.com.task.SendNotificationTask;
import hitec.com.util.SharedPrefManager;
import hitec.com.vo.GetUsersResponseVO;

public class SelectUserActivity extends AppCompatActivity {

    //defining views
    @Bind(R.id.user_list)
    RecyclerView userList;

    private ProgressDialog progressDialog;
    private SelectUserAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userList.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(SelectUserActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        userList.setLayoutManager(mLinearLayoutManager);
        userList.addItemDecoration(new DividerItemDecoration(SelectUserActivity.this, DividerItemDecoration.VERTICAL_LIST));

        adapter = new SelectUserAdapter(SelectUserActivity.this);
        userList.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        getUsers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onGetUserEvent(GetUsersEvent event) {
        hideProgressDialog();
        GetUsersResponseVO responseVo = event.getResponse();
        if (responseVo != null) {
            if(responseVo.success == BaseProxy.RESPONSE_SUCCESS) {
                String users = responseVo.users;
                refreshList(users);
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

    @OnClick(R.id.btn_select)
    void onClickBtnSelect() {
        ArrayList<UserItem> selectedUsers = adapter.getSelectedItems();

        String users = "";
        for(int i = 0; i < selectedUsers.size(); i++) {
            users = users + selectedUsers.get(i).getUsername() + ",";
        }

        if(!users.isEmpty())
            users = users.substring(0, users.length() - 1);

        Intent intent = new Intent();
        intent.putExtra("users", users);
        setResult(RESULT_OK, intent);

        finish();
    }

    private void getUsers() {
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
        GetUsersTask task = new GetUsersTask();
        String username = SharedPrefManager.getInstance(this).getUsername();
        String customerID = SharedPrefManager.getInstance(this).getCustomerID();
        String usertype = String.valueOf(SharedPrefManager.getInstance(this).getUserType());

        task.execute(username, customerID, usertype);
    }

    private void refreshList(String users) {
        ArrayList<UserItem> items = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(users);
            int count = jsonArray.length();
            for(int i = 0; i < count; i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);
                String username = json.getString("username");
                UserItem item = new UserItem();
                item.setUserName(username);
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
        ApplicationContext.showToastMessage(SelectUserActivity.this, getResources().getString(R.string.network_error));
    }

    public void sendNotification(final String receiver) {
        //Show Tag Request Dialog
        LayoutInflater layoutInflater = LayoutInflater.from(SelectUserActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dlg_input_tag, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectUserActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText edtTag = (EditText) promptView.findViewById(R.id.edt_tag);

        alertDialogBuilder.setCancelable(false)
                .setTitle(R.string.input_tag)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sender = SharedPrefManager.getInstance(SelectUserActivity.this).getUsername();
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