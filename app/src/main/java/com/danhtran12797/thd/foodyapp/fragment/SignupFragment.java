package com.danhtran12797.thd.foodyapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
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

import static android.app.Activity.RESULT_OK;

public class SignupFragment extends Fragment {

    private static final String TAG = "SignupFragment";

    View view;
    CircleImageView imgDefaultAvatar;
    CircleImageView imgCamera;
    EditText edtUsername;
    EditText edtEmail;
    EditText edtPass;
    EditText edtConfirmPass;
    EditText edtPhone;
    EditText edtName;
    EditText edtAddress;
    Button btnRegister;

    RotateLoading rotateloading;

    Uri resultUri = null;
    String name_image = "user_default1.png";

    String address = "";
    String name = "";
    String username = "";
    String email = "";
    String pass = "";
    String confirm = "";
    String phone = "";

    private SignupFragmentListener listener;

    public interface SignupFragmentListener {
        void onInputRegister(String username);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_signup, container, false);

        initView();
        evenView();

        return view;
    }

    private void evenView() {
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BringImagePicker();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address = edtAddress.getText().toString().trim();
                name = edtName.getText().toString().trim();
                username = edtUsername.getText().toString().trim();
                email = edtEmail.getText().toString().trim();
                pass = edtPass.getText().toString().trim();
                confirm = edtConfirmPass.getText().toString().trim();
                phone = edtPhone.getText().toString().trim();

                if (validateForm(name, username, email, phone, pass)) {
                    if (validateForm1(pass, confirm)) {
                        if (resultUri != null) {
                            uploadImage();
                        } else {
                            uploadDataUser(name, username, email, phone, pass, name_image, address);
                        }
                    }
                }
            }
        });
    }

    private void uploadDataUser(String name, final String username, String email, String phone, String pass, String name_image, String address) {
        rotateloading.start();

        DataService dataService = APIService.getService();
        Call<String> callback = dataService.Register(name, username, pass, email, address, phone, name_image);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                rotateloading.stop();

                String message = response.body();
                if (message.equals("success")) {
                    Toast.makeText(getContext(), "Đăng ký thành công", Toast.LENGTH_LONG).show();
                    listener.onInputRegister(username);
                    Log.d(TAG, "dang ky thanh cong");
                } else if (message.equals("exists")) {
                    Ultil.showDialog(getContext(), null, "Tài khoản này đã tồn tại", null, null);
                } else {
                    Log.d(TAG, "dang ky that bai");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                rotateloading.stop();
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void uploadImage() {
        File file = new File(resultUri.getPath());

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload_file", file.getName(), requestBody);

        DataService service = APIService.getService();
        Call<String> callback = service.UploadImage(body);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                name_image = response.body();
                uploadDataUser(name, username, email, phone, pass, name_image, address);
                Log.d(TAG, name_image);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private boolean validateForm(String name, String username, String email, String phone, String pass) {
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

        //kiểm tra pass
        if (pass.isEmpty()) {
            edtPass.setError("Không được bỏ trống");
            valid = false;
        } else if (pass.length() < 6) {
            edtPass.setError("Mật khẩu ít nhất phải 6 ký tự");
            valid = false;
        } else {
            edtPass.setError(null);
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
        } else if (phone.length() < 10) {
            edtPhone.setError("Vui lòng nhập đầy đủ số điện thoại");
            valid = false;
        } else {
            edtPhone.setError(null);
        }

        // kiểm tra name
        if (name.isEmpty()) {
            edtName.setError("Không được bỏ trống");
            valid = false;
        }

        return valid;
    }

    private boolean validateForm1(String pass, String confirm) {
        boolean valid = true;

        if (!pass.equals(confirm)) {
            edtConfirmPass.setError("Xác nhận mật khẩu không đúng!");
            valid = false;
        } else {
            edtConfirmPass.setError(null);
        }

        return valid;
    }

    private void initView() {
        rotateloading = view.findViewById(R.id.rotateloading);

        imgDefaultAvatar = view.findViewById(R.id.imgDefaultAvatar);
        imgCamera = view.findViewById(R.id.imgCamera);
        edtEmail = view.findViewById(R.id.edtEmailRegister);
        edtAddress = view.findViewById(R.id.edtAddress);
        edtUsername = view.findViewById(R.id.edtUserName);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtPass = view.findViewById(R.id.edtPassSignup);
        edtConfirmPass = view.findViewById(R.id.edtConfirmPassSignup);
        edtName = view.findViewById(R.id.edtName);
        btnRegister = view.findViewById(R.id.btnRegister);
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                imgDefaultAvatar.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, error.getMessage());
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignupFragmentListener) {
            listener = (SignupFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentAListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
