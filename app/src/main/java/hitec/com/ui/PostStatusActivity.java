package hitec.com.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import hitec.com.ApplicationContext;
import hitec.com.R;
import hitec.com.event.SendAdminNotificationEvent;
import hitec.com.event.SendNotificationEvent;
import hitec.com.proxy.SendAdminNotificationProxy;
import hitec.com.proxy.SendNotificationProxy;
import hitec.com.task.SendAdminNotificationTask;
import hitec.com.task.SendNotificationTask;
import hitec.com.util.SharedPrefManager;
import hitec.com.util.StringUtil;
import hitec.com.util.URLManager;
import hitec.com.vo.SendAdminNotificationResponseVO;
import hitec.com.vo.SendNotificationResponseVO;

import static android.R.attr.bitmap;
import static android.R.attr.data;
import static android.R.attr.maxItemsPerRow;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static android.util.Log.v;

public class PostStatusActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Bind(R.id.edt_users)
    TextInputEditText edtUsers;
    @Bind(R.id.edt_message)
    TextInputEditText edtMessage;
    @Bind(R.id.iv_image)
    ImageView imgPreview;

    private Animation shake;

    private static final int REQUEST_SELECT_USER = 1;
    private String selectedUsers = "";

    private static final int REQUEST_CAMERA = 1888;
    private static final int SELECT_FILE = 1988;
    public static Bitmap mphoto;
    private String filename = "";

    RequestParams params = new RequestParams();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_status);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mphoto = null;

        edtUsers.setKeyListener(null);
        edtUsers.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Intent intent = new Intent(PostStatusActivity.this, SelectUserActivity.class);
                        startActivityForResult(intent, REQUEST_SELECT_USER);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        shake = AnimationUtils.loadAnimation(PostStatusActivity.this, R.anim.edittext_shake);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v("orientation", "landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.v("orientation", "portrait");
        }
    }

    @Subscribe
    public void onSendNotificationEvent(SendNotificationEvent event) {
        hideProgressDialog();
        SendNotificationResponseVO responseVO = event.getResponse();
        if(responseVO != null && responseVO.success == SendNotificationProxy.RESPONSE_SUCCESS) {
            postSuccess();
        } else {
            networkError();
        }
    }

    @Subscribe
    public void onSendAdminNotificationEvent(SendAdminNotificationEvent event) {
        hideProgressDialog();
        SendAdminNotificationResponseVO responseVO = event.getResponse();
        if(responseVO != null && responseVO.success == SendAdminNotificationProxy.RESPONSE_SUCCESS) {
            postSuccess();
        } else {
            networkError();
        }
    }

    private boolean checkMessage() {
        if (StringUtil.isEmpty(edtMessage.getText().toString())) {
            showInfoNotice(edtMessage);
            return false;
        }

        return true;
    }

    private void showInfoNotice(TextInputEditText target) {
        target.startAnimation(shake);
        if (target.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        showPhoto();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    private void hideProgressDialog() {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @OnClick(R.id.btn_add_image)
    void onClickBtnAddImage() {
        selectImage();
    }

    @OnClick(R.id.btn_post)
    void onClickBtnPost() {
        if(!checkMessage())
            return;

        if(mphoto != null) {
            uploadImage();
        } else {
            postStatus();
        }
    }

    private void postStatus() {
        progressDialog.setMessage(getResources().getString(R.string.posting));
        progressDialog.show();
        if(selectedUsers.isEmpty()) {
            //Send to Admins.
            String sender = SharedPrefManager.getInstance(PostStatusActivity.this).getUsername();
            String customerID = SharedPrefManager.getInstance(PostStatusActivity.this).getCustomerID();
            String message = edtMessage.getText().toString();

            SendAdminNotificationTask task = new SendAdminNotificationTask();
            task.execute(sender, customerID, message, filename);
        } else {
            //Send to Users
            String sender = SharedPrefManager.getInstance(PostStatusActivity.this).getUsername();
            String message = edtMessage.getText().toString();
            SendNotificationTask task = new SendNotificationTask();
            task.execute(sender, selectedUsers, message, filename);
        }
    }

    private void uploadImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mphoto.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        filename = SharedPrefManager.getInstance(this).getUsername() + "_" + System.currentTimeMillis() + ".jpg";

        params.put("filename", filename);
        params.put("image", encodedImage);

        makeHTTPCall();
    }

    public void makeHTTPCall() {
        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as well.
        client.post(URLManager.getUploadImageURL(),
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        postStatus();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured n Most Common Error: n1. Device not connected to Internetn2. Web App is not deployed in App servern3. App server is not runningn HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    private void postSuccess() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PostStatusActivity.this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.status_posted));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void networkError() {
        ApplicationContext.showToastMessage(PostStatusActivity.this, getResources().getString(R.string.network_error));
    }

    private void showPhoto() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
//                        wait(2000);
                        // runOnUiThread method used to do UI task in main thread.

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mphoto != null)
                                    imgPreview.setImageBitmap(mphoto);
                            }
                        });
                    }
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(PostStatusActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,SELECT_FILE);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mphoto = bm;
        showPhoto();
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mphoto = thumbnail;
        showPhoto();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_SELECT_USER && resultCode == RESULT_OK) {
            selectedUsers = intent.getStringExtra("users");
            edtUsers.setText(selectedUsers);
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(intent);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(intent);
        }
    }
}