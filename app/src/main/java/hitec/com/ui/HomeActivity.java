package hitec.com.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import hitec.com.adapter.RecentStatusAdapter;
import hitec.com.adapter.UserAdapter;
import hitec.com.event.GetRecentStatusEvent;
import hitec.com.event.GetUsersEvent;
import hitec.com.fragment.HomeFragment;
import hitec.com.fragment.MyMessageFragment;
import hitec.com.fragment.UserFragment;
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

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Bind(R.id.toolbar)
    Toolbar toolBar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.nav_view)
    NavigationView navigationView;

    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);
        setSupportActionBar(toolBar);

        toggle = new ActionBarDrawerToggle(HomeActivity.this, drawerLayout, toolBar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerOpened(View drawerView) {
            }
        };

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(HomeActivity.this);
        navigationView.getMenu().getItem(0).setChecked(true);
        startService(new Intent(HomeActivity.this, TrackingService.class));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.main_frame, HomeFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int selectedIndex = -1;

        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = HomeFragment.newInstance();
                getSupportActionBar().setTitle(R.string.app_name);
                break;
            case R.id.nav_users:
                fragment = UserFragment.newInstance();
                getSupportActionBar().setTitle(R.string.hint_users);
                break;
            case R.id.nav_my_message:
                fragment = MyMessageFragment.newInstance();
                getSupportActionBar().setTitle(R.string.view_my_messages);
                break;
            case R.id.nav_post_status:
                Intent intent = new Intent(HomeActivity.this, PostStatusActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        invalidateOptionsMenu();

        if (fragment != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_frame, fragment)
                    .commit();
        }

        drawerLayout.closeDrawers();

        return true;
    }
}