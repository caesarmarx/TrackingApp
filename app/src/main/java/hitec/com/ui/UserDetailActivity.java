package hitec.com.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import hitec.com.ApplicationContext;
import hitec.com.R;
import hitec.com.adapter.MessageAdapter;
import hitec.com.db.MessageDB;
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
    @Bind(R.id.edt_date)
    EditText edtDate;

    private ProgressDialog progressDialog;
    private MessageAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    private String username;
    private int usertype;

    private String date;

    private int year;
    private int month;
    private int day;

    private final int DATE_DIALOG_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        ButterKnife.bind(this);


        username = getIntent().getStringExtra("username");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(username);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
//        updateDisplay();

        edtDate.setKeyListener(null);

        messageList.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(UserDetailActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messageList.setLayoutManager(mLinearLayoutManager);
        messageList.addItemDecoration(new DividerItemDecoration(UserDetailActivity.this, DividerItemDecoration.VERTICAL_LIST));

        usertype = SharedPrefManager.getInstance(this).getUserType();
        if(usertype == 0) {
            edtDate.setVisibility(View.VISIBLE);
            btnTrackLocation.setVisibility(View.VISIBLE);
        } else {
            edtDate.setVisibility(View.GONE);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            year = i;
            month = i1;
            day = i2;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateDisplay();
                    getUserMessageByDate(date);
                }
            });
        }
    };

    private void updateDisplay() {
        String strYear = "";
        String strMonth = "";
        String strDay = "";
        if(month < 10)
            strMonth = "0" + String.valueOf(month + 1);
        else
            strMonth = String.valueOf(month);

        if(day < 10)
            strDay = "0" + day;
        else
            strDay = String.valueOf(day);
        date = new StringBuilder().append(year).append("-").append(strMonth).append("-").append(strDay).toString();
        edtDate.setText(date);
    }

    @Override
    protected Dialog onCreateDialog(int id){
        switch(id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(UserDetailActivity.this, mDateSetListener, year, month, day);
        }

        return null;
    }

    @OnTouch(R.id.edt_date)
    boolean onTouchDate() {
        showDialog(DATE_DIALOG_ID);

        return false;
    }

    @OnClick(R.id.btn_track_location)
    void onBtnTrackLocation() {
        Intent intent = new Intent(UserDetailActivity.this, LocationDetailActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void getUserMessageByDate(String date) {
        MessageDB messageDB = new MessageDB(UserDetailActivity.this);
        ArrayList<MessageItem> items = messageDB.fetchUserMessageByDate(username, date);

        adapter.addItems(items);
        adapter.notifyDataSetChanged();
    }

    private void getUserMessages() {
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
        GetUserMessagesTask task = new GetUserMessagesTask();

        task.execute(username);
    }

    private void refreshList(String users) {
        ArrayList<MessageItem> items = new ArrayList<>();
        String lastUpdateTime = SharedPrefManager.getInstance(UserDetailActivity.this).getUserMessageUpdateTime(username);
        String tempTime = lastUpdateTime;
        try {
            JSONArray jsonArray = new JSONArray(users);
            int count = jsonArray.length();
            MessageDB messageDB = new MessageDB(UserDetailActivity.this);
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
        SharedPrefManager.getInstance(UserDetailActivity.this).saveUserMessageUpdateTime(username, tempTime);

        adapter.addItems(items);
        adapter.notifyDataSetChanged();
    }

    private void refreshInLocal() {
        MessageDB messageDB = new MessageDB(UserDetailActivity.this);
        ArrayList<MessageItem> items = new ArrayList<>();
        items = messageDB.fetchUserMessage(username);
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