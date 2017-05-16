package hitec.com.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import hitec.com.ApplicationContext;
import hitec.com.R;
import hitec.com.adapter.UserAdapter;
import hitec.com.db.UserDB;
import hitec.com.event.GetUsersEvent;
import hitec.com.model.UserItem;
import hitec.com.proxy.BaseProxy;
import hitec.com.task.GetUsersTask;
import hitec.com.task.SendNotificationTask;
import hitec.com.ui.DividerItemDecoration;
import hitec.com.util.SharedPrefManager;
import hitec.com.vo.GetUsersResponseVO;

/**
 * Created by Caesar on 5/7/2017.
 */

public class UserFragment extends Fragment{

    @Bind(R.id.user_list)
    RecyclerView userList;

    private ProgressDialog progressDialog;
    private UserAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        ButterKnife.bind(this, view);

        userList.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        userList.setLayoutManager(mLinearLayoutManager);
        userList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        adapter = new UserAdapter(UserFragment.this);
        userList.setAdapter(adapter);

        progressDialog = new ProgressDialog(getActivity());
//        startService(new Intent(UserListActivity.this, TrackingService.class));
        getUsers();

        return view;
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
                refreshInLocal();
            }
        } else {
            refreshInLocal();
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

    private void getUsers() {
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
        GetUsersTask task = new GetUsersTask();
        String username = SharedPrefManager.getInstance(getActivity()).getUsername();
        String customerID = SharedPrefManager.getInstance(getActivity()).getCustomerID();
        String usertype = String.valueOf(SharedPrefManager.getInstance(getActivity()).getUserType());

        task.execute(username, customerID, usertype);
    }

    private void refreshList(String users) {
        ArrayList<UserItem> items = new ArrayList<>();
        UserDB userDB = new UserDB(getActivity());
        String lastUpdate = SharedPrefManager.getInstance(getActivity()).getUserUpdateTime();
        String tempTime = lastUpdate;
        try {
            JSONArray jsonArray = new JSONArray(users);
            int count = jsonArray.length();
            for(int i = 0; i < count; i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);
                String username = json.getString("username");
                String createdAt = json.getString("created_at");

                UserItem item = new UserItem();
                item.setUserName(username);
                item.setCreatedAt(createdAt);
                items.add(item);


                if(createdAt.compareTo(lastUpdate) > 0) {
                    userDB.addUser(item);
                    if(createdAt.compareTo(tempTime) > 0)
                        tempTime = createdAt;
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        SharedPrefManager.getInstance(getActivity()).saveUserUpdateTime(tempTime);
        adapter.addItems(items);
        adapter.notifyDataSetChanged();
    }

    private void refreshInLocal() {
        UserDB userDB = new UserDB(getActivity());
        ArrayList<UserItem> items = new ArrayList<>();
        items = userDB.fetchAllUsers();
        adapter.addItems(items);
        adapter.notifyDataSetChanged();
    }

    private void hideProgressDialog() {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void networkError() {
        ApplicationContext.showToastMessage(getActivity(), getResources().getString(R.string.network_error));
    }

    public void sendNotification(final String receiver) {
        //Show Tag Request Dialog
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.dlg_input_tag, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final EditText edtTag = (EditText) promptView.findViewById(R.id.edt_tag);

        alertDialogBuilder.setCancelable(false)
                .setTitle(R.string.input_tag)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sender = SharedPrefManager.getInstance(getActivity()).getUsername();
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
