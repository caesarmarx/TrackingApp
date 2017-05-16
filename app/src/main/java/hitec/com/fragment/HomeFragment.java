package hitec.com.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import hitec.com.db.MessageDB;
import hitec.com.event.GetRecentStatusEvent;
import hitec.com.model.MessageItem;
import hitec.com.proxy.BaseProxy;
import hitec.com.task.GetRecentStatusTask;
import hitec.com.ui.DividerItemDecoration;
import hitec.com.util.SharedPrefManager;
import hitec.com.vo.GetRecentStatusResponseVO;

/**
 * Created by Caesar on 5/7/2017.
 */

public class HomeFragment extends Fragment {

    @Bind(R.id.status_list)
    RecyclerView recentList;

    private ProgressDialog progressDialog;

    private MessageAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        recentList.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recentList.setLayoutManager(mLinearLayoutManager);
        recentList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new MessageAdapter(getActivity(), SharedPrefManager.getInstance(getActivity()).getUsername());
        recentList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        getRecentStatus();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
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
                refreshInLocal();
            }
        } else {
            refreshInLocal();
        }
    }

    private void getRecentStatus() {
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
        GetRecentStatusTask task = new GetRecentStatusTask();
        String username = SharedPrefManager.getInstance(getActivity()).getUsername();

        task.execute(username);
    }

    private void refreshList(String messages) {
        ArrayList<MessageItem> items = new ArrayList<>();
        MessageDB messageDB = new MessageDB(getActivity());
        String curUserName = SharedPrefManager.getInstance(getActivity()).getUsername();
        String lastUpdateTime = SharedPrefManager.getInstance(getActivity()).getUserMessageUpdateTime(curUserName);
        String tempTime = lastUpdateTime;
        try {
            JSONArray jsonArray = new JSONArray(messages);
            int count = jsonArray.length();
            for(int i = 0; i < count; i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);
                String fromUser = json.getString("from_user");
                String toUser = json.getString("to_user");
//                RecentStatusItem item = new RecentStatusItem();
                MessageItem item = new MessageItem();
                item.setFromUser(fromUser);
                item.setToUser(toUser);
                item.setMessage(json.getString("message"));
                item.setImageURL(json.getString("image"));
                item.setTime(json.getString("time"));

                if(item.getTime().compareTo(lastUpdateTime) > 0) {
                    messageDB.addMessage(item);
                    if(item.getTime().compareTo(tempTime) > 0)
                        tempTime = item.getTime();
                }

                items.add(item);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        SharedPrefManager.getInstance(getActivity()).saveUserMessageUpdateTime(curUserName, tempTime);

        adapter.addItems(items);
        adapter.notifyDataSetChanged();
    }

    private void refreshInLocal() {
        String username = SharedPrefManager.getInstance(getActivity()).getUsername();
        MessageDB messageDB = new MessageDB(getActivity());
        ArrayList<MessageItem> items = messageDB.fetchRecentMessage(username);
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
