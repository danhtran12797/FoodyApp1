package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ICheckPass;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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

import static com.danhtran12797.thd.foodyapp.ultil.Ultil.removeUserPreference;
import static com.danhtran12797.thd.foodyapp.ultil.Ultil.user;

public class AccountUserActivity extends AppCompatActivity implements View.OnClickListener, ILoading, ICheckPass {

    private static final String TAG = "AccountUserActivity";

    private Toolbar toolbar;

    private CircleImageView imgAvatar;
    private CircleImageView imgCamera;
    private Button btnSave;

    private EditText edtName;
    private EditText edtUsername;
    private EditText edtEmail;
    private EditText edtAddress;
    private EditText edtPhone;

    private RotateLoading rotateLoading;
    private RelativeLayout layout_account_user;
    private FrameLayout layout_container;

    private Uri resultUri = null;

    String name_image = "";
    String name = "";
    String address = "";
    String email = "";
    String phone = "";

    private GoogleSignInClient mGoogleSignInClient;

    private AlertDialog dialog_check_pass, dialog_change_pass;
    private TextView txt_error_check_pass, txt_error_change_pass;
    private EditText edt_pass, edt_pass_new, edt_pass_confirm;
    private ProgressBar progress_check_pass, progress_change_pass;

    ILoading mListenerLoading;
    ICheckPass mListenerPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_user);

        mListenerLoading=this;
        mListenerPass=this;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initActionBar();
        initView();
        setDataView();
        evenView();
    }

    private void create_dialog_change_pass() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView=inflater.inflate(R.layout.dialog_change_pass, null);
        progress_change_pass=dialogView.findViewById(R.id.progress_bar_change_pass);
        edt_pass_new=dialogView.findViewById(R.id.edt_pass_new);
        edt_pass_confirm=dialogView.findViewById(R.id.edt_confirm_pass);
        txt_error_change_pass=dialogView.findViewById(R.id.txt_error_change_pass);

        dialog_change_pass = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_lock_black)
                .setTitle("Đổi mật khẩu")
                .setCancelable(false)
                .setView(dialogView)
                .setPositiveButton("Xác nhận", null)
                .setNegativeButton("Hủy", null).show();
        Button negativeButton = dialog_change_pass.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button positiveButton = dialog_change_pass.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass=edt_pass_new.getText().toString();
                String pass_confirm=edt_pass_confirm.getText().toString();
                Log.d(TAG, "change pass/edt_pass_new: "+pass);
                if(validateForm(pass, pass_confirm)){
                    updatePassword(user.getId(), pass);
                }
            }
        });
    }

    private void create_dialog_check_pass(String status) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_check_pass, null);
        progress_check_pass=dialogView.findViewById(R.id.progress_bar);
        edt_pass=dialogView.findViewById(R.id.edt_pass);
        txt_error_check_pass=dialogView.findViewById(R.id.txt_error);

        dialog_check_pass = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_lock_black)
                .setTitle("Xác nhận mật khẩu")
                .setCancelable(false)
                .setView(dialogView)
                .setPositiveButton("Xác nhận", null)
                .setNegativeButton("Hủy", null).show();
        Button negativeButton = dialog_check_pass.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button positiveButton = dialog_check_pass.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass=edt_pass.getText().toString();
                Log.d(TAG, "update pass/edt_pass: "+pass);
                if(validateForm(pass)){
                    txt_error_check_pass.setVisibility(View.GONE);
                    progress_check_pass.setVisibility(View.VISIBLE);
                    CheckPass(user.getId(), pass, status);
                }
            }
        });
    }

    private boolean validateForm(String pass){
        boolean valid = true;
        //kiểm tra pass
        if (pass.isEmpty()) {
            edt_pass.setError("Không được bỏ trống");
            valid = false;
        } else if (pass.length() < 6) {
            edt_pass.setError("Mật khẩu ít nhất phải 6 ký tự");
            valid = false;
        }

        return valid;
    }

    private boolean validateForm(String pass_new, String pass_confirm){
        boolean valid = true;
        //kiểm tra pass
        if (pass_new.isEmpty()) {
            edt_pass_new.setError("Không được bỏ trống");
            valid = false;
        } else if (pass_new.length() < 6) {
            edt_pass_new.setError("Mật khẩu ít nhất phải 6 ký tự");
            valid = false;
        }else {
            if(!pass_new.equals(pass_confirm)){
                edt_pass_confirm.setError("Xác nhận mật khẩu sai");
                valid = false;
            }
        }

        return valid;
    }

    private void logout_account() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            LoginManager.getInstance().logOut();
            Log.d(TAG, "logout_account: success");
        } else if (mGoogleSignInClient != null) {
            Log.d(TAG, "logout_google: success");
            mGoogleSignInClient.signOut();
            mGoogleSignInClient.revokeAccess();
        }

//        Toast.makeText(this, "Đã đăng xuất '" + user.getEmail() + "' thành công", Toast.LENGTH_SHORT).show();
        removeUserPreference(this);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_update_password:
//                create_dialog_change_pass();
                create_dialog_check_pass("update_pass");
                break;
            case R.id.menu_logout_account:
                logout_account();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbar_accounr_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void evenView() {
        imgCamera.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    private void setDataView() {
        if (user != null) {
            Picasso.get().load(Ultil.url_image_avatar + user.getAvatar())
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.error)
                    .into(imgAvatar);
            edtName.setText(user.getName());
            edtUsername.setText(user.getUsername());
            edtAddress.setText(user.getAddress());
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getPhone());

            name_image = user.getAvatar();
        }
    }

    private void initView() {
        imgAvatar = findViewById(R.id.imgAvatarAccount);
        imgCamera = findViewById(R.id.imgCameraAccount);
        btnSave = findViewById(R.id.btn_save_change_user);
        edtName = findViewById(R.id.edtNameAccount);
        edtUsername = findViewById(R.id.edtUserNameAccount);
        edtAddress = findViewById(R.id.edtAddressAccount);
        edtEmail = findViewById(R.id.edtEmailAccount);
        edtPhone = findViewById(R.id.edtPhoneAccount);
        rotateLoading = findViewById(R.id.rotateLoading);
        layout_container=findViewById(R.id.layout_container);
        layout_container.setVisibility(View.GONE);
        layout_account_user=findViewById(R.id.layout_account_user);
    }

    private void updatePassword(String id, final String pass_new) {
        progress_change_pass.setVisibility(View.VISIBLE);
        txt_error_change_pass.setVisibility(View.GONE);

        DataService dataService = APIService.getService();
        Call<String> callback = dataService.UpdatePassword(id, pass_new);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String message = response.body();
                if (message.equals("success")) {
                    dialog_change_pass.dismiss();
                    user.setPassword(pass_new);
                    Ultil.setUserPreference(AccountUserActivity.this);
                    Toast.makeText(AccountUserActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_LONG).show();
                } else {
                    progress_check_pass.setVisibility(View.GONE);
                    txt_error_check_pass.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mListenerLoading.stop_loading(false);
                dialog_change_pass.dismiss();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void CheckPass(String id_user, String pass, String status){
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.CheckPass(id_user, pass);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String message=response.body();
                Log.d(TAG, "message: "+ message);
                if(message.equals("success")){
                    mListenerPass.pass_exist(status);
                }else{
                    mListenerPass.pass_no_exist(status);
                    mListenerLoading.stop_loading(true);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                mListenerLoading.stop_loading(false);
                dialog_check_pass.dismiss();
            }
        });
    }

    private void updateUser(String id, final String name, final String address, final String email, final String phone, final String avatar) {
        mListenerLoading.start_loading();
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.UpdateUser(id, name, email, address, phone, avatar);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mListenerLoading.stop_loading(true);
                String message = response.body();
                Log.d(TAG, "message: " + message);
                if (message.equals("success")) {
                    if (resultUri != null) {
                        uploadImage();
                    } else {
                        Toast.makeText(AccountUserActivity.this, "Cập nhật tài khoản thành công", Toast.LENGTH_SHORT).show();
                        user.setName(name);
                        user.setAddress(address);
                        user.setEmail(email);
                        user.setPhone(phone);
                        Ultil.setUserPreference(AccountUserActivity.this);
                    }
                } else if (message.equals("failed")) {
                    Ultil.showDialog(AccountUserActivity.this, null, "Sự cố cập nhật tài khoản thất bại, chúng tôi sẽ khắc phục ngay.", null, null);
                } else {
                    if (message.equals("email"))
                        message = "Email";
                    else
                        message = "Số điện thoại";
                    Ultil.showDialog(AccountUserActivity.this, null, message + " này đã tồn tại. Vui lòng nhập lại!", null, null);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mListenerLoading.stop_loading(false);
            }
        });
    }

    private void uploadImage() {
        mListenerLoading.start_loading();
        File file = new File(resultUri.getPath());

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), user.getAvatar());
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload_file", user.getAvatar(), requestBody);

        DataService service = APIService.getService();
        Call<String> callback = service.UploadImage(body);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mListenerLoading.stop_loading(true);
                String message = response.body();
                if (message.equals(user.getAvatar())) {
                    Toast.makeText(AccountUserActivity.this, "Cập nhật tài khoản thành công", Toast.LENGTH_SHORT).show();
                    user.setName(name);
                    user.setAddress(address);
                    user.setEmail(email);
                    user.setPhone(phone);
                    Ultil.setUserPreference(AccountUserActivity.this);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "uploadImage" + t.getMessage());
                mListenerLoading.stop_loading(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save_change_user:
                name = edtName.getText().toString().trim();
                address = edtAddress.getText().toString().trim();
                email = edtEmail.getText().toString().trim();
                phone = edtPhone.getText().toString().trim();

                if (validateForm(name, email, phone)) {
                    create_dialog_check_pass("update_user");
                }
                break;
            case R.id.imgCameraAccount:
                BringImagePicker();
                break;
        }
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
                imgAvatar.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, error.getMessage());
            }
        }
    }

    private boolean validateForm(String name, String email, String phone) {
        boolean valid = true;

        // kiểm tra email
        if (!email.isEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Định dạng email không hợp lệ");
                valid = false;
            }
        }

        // kiểm tra phone
        if (!phone.isEmpty()) {
            if (!(phone.length() == 10 && Ultil.check_phone_valid(phone, this))) {
                edtPhone.setError("Số điện thoại không hợp lệ");
                valid = false;
            }
        }

        // kiểm tra name
        if (name.isEmpty()) {
            edtName.setError("Không được bỏ trống");
            valid = false;
        }
        return valid;
    }

    @Override
    public void start_loading() {
        rotateLoading.start();
        layout_container.setVisibility(View.VISIBLE);
    }

    @Override
    public void stop_loading(boolean isConnect) {
        rotateLoading.stop();
        layout_container.setVisibility(View.GONE);
        if(!isConnect){
            Ultil.show_snackbar(layout_account_user, null);
        }
    }

    @Override
    public void pass_exist(String status) {
        dialog_check_pass.dismiss();

        if(status.equals("update_user")){
            updateUser(user.getId(), name, address, email, phone, user.getAvatar());
        }else {
            create_dialog_change_pass();
        }
    }

    @Override
    public void pass_no_exist(String status) {
        progress_check_pass.setVisibility(View.GONE);
        txt_error_check_pass.setVisibility(View.VISIBLE);
//        if(status.equals("update_user")){
//
//        }
    }
}
