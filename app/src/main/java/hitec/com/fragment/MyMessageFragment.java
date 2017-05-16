package hitec.com.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import hitec.com.adapter.MessageAdapter;
import hitec.com.adapter.RecentStatusAdapter;
import hitec.com.db.MessageDB;
import hitec.com.event.GetUserMessagesEvent;
import hitec.com.model.MessageItem;
import hitec.com.proxy.BaseProxy;
import hitec.com.task.GetUserMessagesTask;
import hitec.com.ui.DividerItemDecoration;
import hitec.com.ui.HomeActivity;
import hitec.com.ui.UserDetailActivity;
import hitec.com.util.SharedPrefManager;
import hitec.com.vo.GetUserMessagesResponseVO;

/**
 * Created by Caesar on 5/7/2017.
 */

public class MyMessageFragment extends Fragment{

    //defining views
    @Bind(R.id.message_list)
    RecyclerView messageList;

    private ProgressDialog progressDialog;
    private MessageAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    private String username;

    public static MyMessageFragment newInstance() {
        MyMessageFragment fragment = new MyMessageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_message, container, false);
        ButterKnife.bind(this, view);

        username = SharedPrefManager.getInstance(getActivity()).getUsername();

        messageList.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messageList.setLayoutManager(mLinearLayoutManager);
        messageList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        adapter = new MessageAdapter(getActivity(), username);
        messageList.setAdapter(adapter);

        progressDialog = new ProgressDialog(getActivity());

        return view;
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
        getUserMessages();
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    private void getUserMessages() {
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
        GetUserMessagesTask task = new GetUserMessagesTask();

        task.execute(username);
    }

    private void refreshInLocal() {
        ArrayList<MessageItem> items = new ArrayList<>();
        MessageDB messageDB = new MessageDB(getActivity());
        items = messageDB.fetchUserMessage(username);
        adapter.addItems(items);
        adapter.notifyDataSetChanged();
    }

    private void refreshList(String messages) {
        ArrayList<MessageItem> items = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(messages);
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
        ApplicationContext.showToastMessage(getActivity(), getResources().getString(R.string.network_error));
    }
}
