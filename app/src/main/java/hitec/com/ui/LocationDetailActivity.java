package hitec.com.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.RunnableFuture;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import hitec.com.ApplicationContext;
import hitec.com.R;
import hitec.com.adapter.LocationAdapter;
import hitec.com.adapter.MessageAdapter;
import hitec.com.event.GetLocationsEvent;
import hitec.com.event.GetUserMessagesEvent;
import hitec.com.model.LocationItem;
import hitec.com.model.MessageItem;
import hitec.com.proxy.BaseProxy;
import hitec.com.task.GetLocationsTask;
import hitec.com.task.GetUserMessagesTask;
import hitec.com.util.SharedPrefManager;
import hitec.com.vo.GetLocationsResponseVO;
import hitec.com.vo.GetUserMessagesResponseVO;

public class LocationDetailActivity extends AppCompatActivity {

    //defining views
    @Bind(R.id.location_list)
    RecyclerView locationList;
    @Bind(R.id.edt_date)
    EditText edtDate;

    private ProgressDialog progressDialog;
    private LocationAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    private String username;
    private String date;

    private int year;
    private int month;
    private int day;

    private final int DATE_DIALOG_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = getIntent().getStringExtra("username");

        locationList.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(LocationDetailActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        locationList.setLayoutManager(mLinearLayoutManager);
        locationList.addItemDecoration(new DividerItemDecoration(LocationDetailActivity.this, DividerItemDecoration.VERTICAL_LIST));

        adapter = new LocationAdapter(LocationDetailActivity.this);
        locationList.setAdapter(adapter);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();

        edtDate.setKeyListener(null);

        progressDialog = new ProgressDialog(LocationDetailActivity.this);
        getLocations();
    }

    @Subscribe
    public void onGetLocationssEvent(GetLocationsEvent event) {
        hideProgressDialog();
        GetLocationsResponseVO responseVo = event.getResponse();
        if (responseVo != null) {
            if(responseVo.success == BaseProxy.RESPONSE_SUCCESS) {
                String datas = responseVo.datas;
                refreshList(datas);
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
                    getLocations();
                }
            });
        }
    };

    private void updateDisplay() {
        date = new StringBuilder().append(year).append("/").append(month + 1).append("/").append(day).toString();
        edtDate.setText(date);
    }

    @Override
    protected Dialog onCreateDialog(int id){
        switch(id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(LocationDetailActivity.this, mDateSetListener, year, month, day);
        }

        return null;
    }

    @OnTouch(R.id.edt_date)
    boolean onTouchDate() {
        showDialog(DATE_DIALOG_ID);

        return false;
    }

    private void getLocations() {
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
        GetLocationsTask task = new GetLocationsTask();

        task.execute(username, date);
    }

    private void refreshList(String datas) {
        ArrayList<LocationItem> items = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(datas);
            int count = jsonArray.length();
            for(int i = 0; i < count; i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);
                String address = json.getString("address");
                String latitude = json.getString("latitude");
                String longitude = json.getString("longitude");
                String time = json.getString("time");

                LocationItem item = new LocationItem();
                item.setAddress(address);
                item.setLatitude(latitude);
                item.setLongitude(longitude);
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
        ApplicationContext.showToastMessage(LocationDetailActivity.this, getResources().getString(R.string.network_error));
    }
}