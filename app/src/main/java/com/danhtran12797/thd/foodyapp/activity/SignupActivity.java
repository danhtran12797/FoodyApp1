package com.danhtran12797.thd.foodyapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.model.User;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.victor.loading.rotate.RotateLoading;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, ILoading {

    private static final String TAG = "SignupActivity";

    private LinearLayout layout_change_image;
    private CircleImageView img_close, img_user;
    private EditText edt_username, edt_pass, edt_confirm_pass;
    private Button btn_next;
    private TextView txt_link;
    private RotateLoading rotateLoading;
    private FrameLayout layout_container;
    private ConstraintLayout layout_signup;

    private Uri resultUri=null;

    private String id_user, name, url, login_to;
    private String confirm_pass, username, pass;
    private Product product=null;

    ILoading mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mListener=this;

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
            id_user=bundle.getString("id_user");
            name=bundle.getString("name");
            url=bundle.getString("url");
            login_to=bundle.getString("login_to","");
            product= (Product) bundle.getSerializable("product");

            Log.d(TAG, "login_to: "+login_to);
        }

        initView();
        setImage();

    }

    private void setImage() {
        Picasso.get().load(url)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(img_user);
    }

    private void initView() {
        layout_change_image=findViewById(R.id.layout_change_image);
        img_close=findViewById(R.id.img_close);
        img_user=findViewById(R.id.img_user);
        edt_username=findViewById(R.id.edt_username);
        edt_pass=findViewById(R.id.edt_pass);
        edt_confirm_pass=findViewById(R.id.edt_confirm_pass);
        btn_next=findViewById(R.id.btn_next);
        txt_link=findViewById(R.id.txt_link);
        txt_link.setMovementMethod(LinkMovementMethod.getInstance());
        rotateLoading=findViewById(R.id.rotateLoading);
        layout_container=findViewById(R.id.layout_container);
        layout_container.setVisibility(View.GONE);
        layout_signup=findViewById(R.id.layout_signup);

        btn_next.setOnClickListener(this);
        layout_change_image.setOnClickListener(this);
        img_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                event_next();
                break;
            case R.id.layout_change_image:
                BringImagePicker();
                break;
            case R.id.img_close:
                finish();
                break;
        }
    }

    private void event_next() {
        username=edt_username.getText().toString().trim();
        pass=edt_pass.getText().toString().trim();
        confirm_pass=edt_confirm_pass.getText().toString().trim();
        if (validateForm(username, pass)) {
            if (validateForm1(pass, confirm_pass)) {
                uploadDataUser(id_user, name, username, "", "", pass, id_user+".png","");
            }
        }
    }

    private void uploadDataUser(String id_user, String name, String username, String email, String phone, String pass, String name_image, String address) {
        mListener.start_loading();

        DataService dataService = APIService.getService();
        Call<String> callback = dataService.Register(id_user, name, username, pass, email, address, phone, name_image);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mListener.stop_loading(true);

                String message = response.body();
                Log.d(TAG, "message: " + message);
                if (message.equals("success")) {
                    if(resultUri != null){
                        uploadImage(id_user);
                    }else{
                        download_avatar(id_user, url);
                    }
                } else if (message.equals("failed")) {
                    Ultil.showDialog(SignupActivity.this, null, "Sự cố thêm tài khoản thất bại, chúng tôi sẽ khắc phục ngay.", null, null);
                } else {
                    if(message.equals("username"))
                        message="Tên đăng nhập";
                    else if(message.equals("email"))
                        message="Email";
                    else
                        message="Số điện thoại";
                    Ultil.showDialog(SignupActivity.this, null, message+" này đã tồn tại. Vui lòng nhập lại!", null, null);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mListener.stop_loading(false);
                Log.d(TAG, "error register: " + t.getMessage());
            }
        });
    }

    private void uploadImage(String id_user) {
        File file = new File(resultUri.getPath());

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload_file", id_user+".png", requestBody);

        mListener.start_loading();
        DataService service = APIService.getService();
        Call<String> callback = service.UploadImage(body);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mListener.stop_loading(true);
                String message = response.body();
                Log.d(TAG, "uploadImage: "+message);
                if(message.equals(id_user+".png")){
                    Toast.makeText(SignupActivity.this, "Đăng ký thành công", Toast.LENGTH_LONG).show();
                    open_activity();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                mListener.stop_loading(false);
            }
        });
    }

    private void download_avatar(String id_user, String url) {
        mListener.start_loading();
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.DownloadSocial(id_user, url);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mListener.stop_loading(true);
                String message = response.body();
                Log.d(TAG, "onResponse: " + message);
                if (message.equals("success")) {
                    Toast.makeText(SignupActivity.this, "Đăng ký thành công", Toast.LENGTH_LONG).show();
                    open_activity();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: download_avatar : " + t.getMessage());
                mListener.stop_loading(false);
            }
        });
    }

    private void open_activity(){
        Ultil.user = new User(id_user, name, username, "", pass, "", "", id_user + ".png");
        Ultil.setUserPreference(SignupActivity.this);

        Intent intent = Ultil.create_intent(login_to, this, product);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                img_user.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "Error: "+error.getMessage());
            }
        }
    }

    private boolean validateForm(String username, String pass) {
        boolean valid = true;

        //kiểm tra pass
        if (pass.isEmpty()) {
            edt_pass.setError("Không được bỏ trống");
            valid = false;
        } else if (pass.length() < 6) {
            edt_pass.setError("Mật khẩu ít nhất phải 6 ký tự");
            valid = false;
        } else {
            edt_pass.setError(null);
        }

        // kiểm tra username
        if (username.isEmpty()) {
            edt_username.setError("Không được bỏ trống");
            valid = false;
        } else if (username.length() < 6) {
            edt_username.setError("Tên đăng nhập ít nhất phải 6 ký tự");
            valid = false;
        } else {
            edt_username.setError(null);
        }

        return valid;
    }

    private boolean validateForm1(String pass, String confirm) {
        boolean valid = true;

        if (!pass.equals(confirm)) {
            edt_confirm_pass.setError("Xác nhận mật khẩu không đúng!");
            valid = false;
        } else {
            edt_confirm_pass.setError(null);
        }

        return valid;
    }

    @Override
    public void start_loading() {
        rotateLoading.start();
        layout_container.setVisibility(View.VISIBLE);
        img_close.setVisibility(View.INVISIBLE);
    }

    @Override
    public void stop_loading(boolean isConnect) {
        rotateLoading.stop();
        layout_container.setVisibility(View.GONE);
        img_close.setVisibility(View.VISIBLE);
        if(!isConnect){
            Ultil.show_snackbar(layout_signup, null);
        }
    }
}
