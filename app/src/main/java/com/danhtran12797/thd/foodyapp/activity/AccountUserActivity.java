package com.danhtran12797.thd.foodyapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.model.User;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

public class AccountUserActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AccountUserActivity";
    private String pass_confirm = "";

    private Toolbar toolbar;

    private CircleImageView imgAvatar;
    private CircleImageView imgCamera;
    private Button btnSave;

    private EditText edtName;
    private EditText edtUsername;
    private EditText edtEmail;
    private EditText edtAddress;
    private EditText edtPhone;

    private Uri resultUri = null;

    String name_image = "";
    String name = "";
    String username = "";
    String address = "";
    String email = "";
    String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_user);

        initActionBar();
        initView();
        setDataView();
        evenView();
    }

    private void logout_account() {
        Toast.makeText(this, "Đã đăng xuất '" + user.getEmail() + "' thành công", Toast.LENGTH_SHORT).show();
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
                dialog_update_password();
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
    }

    private void updatePassword(String id, final String pass_new) {
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.UpdatePassword(id, pass_new);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String message = response.body();
                if (message.equals("success")) {
                    user.setPassword(pass_new);
                    Ultil.setUserPreference(AccountUserActivity.this);
                    Toast.makeText(AccountUserActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AccountUserActivity.this, "Đổi mật khẩu thất bại", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void dialog_update_password() {
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 0, 20, 0);

        final EditText edtCurrentPass = new EditText(this);
        edtCurrentPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtCurrentPass.setHint("Mật khẩu hiện tại");

        final EditText edtNewPass = new EditText(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 12, 0, 0);
        edtNewPass.setLayoutParams(layoutParams);
        edtNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtNewPass.setHint("Mật khẩu hiện mới");

        linearLayout.addView(edtCurrentPass);
        linearLayout.addView(edtNewPass);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon_app_design)
                .setTitle("Đổi mật khẩu")
                .setView(linearLayout)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancle", null)
                .show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPass = edtCurrentPass.getText().toString();
                String newPass = edtNewPass.getText().toString();

                if (currentPass.isEmpty() || newPass.isEmpty()) {
                    Toast.makeText(AccountUserActivity.this, "Không được để trống!", Toast.LENGTH_LONG).show();
                } else {
                    if (newPass.length() < 6) {
                        Toast.makeText(AccountUserActivity.this, "Độ dài mật khẩu mới phải ít nhất 6 ký tự!", Toast.LENGTH_LONG).show();
                    } else {
                        if (!currentPass.equals(user.getPassword())) {
                            Log.d(TAG, "currentPass: " + currentPass);
                            Log.d(TAG, "Pass: " + user.getPassword());
                            Toast.makeText(AccountUserActivity.this, "Bạn nhập mật khẩu hiện tại không đúng!", Toast.LENGTH_SHORT).show();
                        } else {
                            updatePassword(user.getId(), newPass);
                            dialog.dismiss();
                        }
                    }
                }
            }
        });
    }

    private void dialog_confirm_password() {
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 0, 20, 0);

        final EditText edtConfirm = new EditText(this);
        //edtConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
        edtConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtConfirm.setHint("Mật khẩu hiện tại");

        linearLayout.addView(edtConfirm);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon_app_design)
                .setTitle("Yêu cầu xác nhận lại mật khẩu")
                .setCancelable(false)
                .setView(linearLayout)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancle", null).show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass_confirm = edtConfirm.getText().toString();
                if (pass_confirm.equals(user.getPassword())) {
                    dialog.dismiss();
                    if (resultUri != null) {
                        uploadImage();
                    } else {
                        updateUser(user.getId(), name, username, address, email, phone, name_image);
                    }
                } else {
                    Toast.makeText(AccountUserActivity.this, "Bạn xác nhận sai mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUser(String id, final String name, final String username, final String address, final String email, final String phone, final String avatar) {
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.UpdateUser(id, name, username, email, address, phone, avatar);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String message = response.body();
                Log.d(TAG, "message: " + message);
                if (message.equals("success")) {
                    Toast.makeText(AccountUserActivity.this, "Cập nhật tài khoản thành công", Toast.LENGTH_SHORT).show();
                    user = new User(name, username, address, phone, avatar, email);
                    Ultil.setUserPreference(AccountUserActivity.this);
                } else {

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    private void uploadImage() {
        File file = new File(resultUri.getPath());

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), user.getAvatar());
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload_file", file.getName(), requestBody);

        DataService service = APIService.getService();
        Call<String> callback = service.UpdateImage(body, requestBody1);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                name_image = response.body();
                Log.d(TAG, name_image);
                updateUser(user.getId(), name, username, address, email, phone, name_image);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "uploadImage" + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save_change_user:
                name = edtName.getText().toString().trim();
                username = edtUsername.getText().toString().trim();
                address = edtAddress.getText().toString().trim();
                email = edtEmail.getText().toString().trim();
                phone = edtPhone.getText().toString().trim();

                if (validateForm(name, username, email, phone, address)) {
                    dialog_confirm_password();
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

    private boolean validateForm(String name, String username, String email, String phone, String address) {
        boolean valid = true;

        // kiểm tra email
        if (email.isEmpty()) {
            //Field can't be empty
            edtEmail.setError("Không được bỏ trống");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //Please enter a valid email address
            edtEmail.setError("Định dạng mail k hợp lệ");
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        // kiểm tra username
        if (username.isEmpty()) {
            edtUsername.setError("Không được bỏ trống");
            valid = false;
        } else if (username.length() < 6) {
            edtUsername.setError("Tên đăng nhập ít nhất phải 6 ký tự");
            valid = false;
        } else {
            edtUsername.setError(null);
        }

        // kiểm tra phone
        if (phone.isEmpty()) {
            edtPhone.setError("Không được bỏ trống");
            valid = false;
        } else if (phone.length() < 9) {
            edtPhone.setError("Vui lòng nhập đầy đủ số điện thoại");
            valid = false;
        } else {
            edtPhone.setError(null);
        }

        // kiểm tra phone
        if (address.isEmpty()) {
            edtAddress.setError("Không được bỏ trống");
            valid = false;
        } else {
            edtAddress.setError(null);
        }

        // kiểm tra name
        if (name.isEmpty()) {
            edtName.setError("Không được bỏ trống");
            valid = false;
        }

        return valid;
    }
}
