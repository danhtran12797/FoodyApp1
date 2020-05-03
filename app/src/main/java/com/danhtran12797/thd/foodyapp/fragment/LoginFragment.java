package com.danhtran12797.thd.foodyapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.AddressOrderActivity;
import com.danhtran12797.thd.foodyapp.activity.DetailProductActivity;
import com.danhtran12797.thd.foodyapp.activity.ListUserProductLoveActivity;
import com.danhtran12797.thd.foodyapp.activity.MainActivity;
import com.danhtran12797.thd.foodyapp.activity.OrderActivity;
import com.danhtran12797.thd.foodyapp.activity.SignupActivity;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.model.User;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    View view;
    EditText edtEmail;
    EditText edtPass;
    Button btnLogin;

    Button btnGoolge;
    Button btnFacebook;

    Product product = null;
    String login_to = "";

    ILoading mListener;

    private static final int RC_SIGN_IN = 9001;

    public static LoginFragment getInstance(Product product, String login_to) {
        LoginFragment fragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        bundle.putString("login_to", login_to);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            product = (Product) bundle.getSerializable("product");
            login_to = bundle.getString("login_to");
            Log.d("AAA", "LoginFragment: "+login_to);
        }

        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        initView();
        evenLogin();
        callBackFacebook();

        return view;
    }

    public void callBackFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Login SUCCESS");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.d(TAG, response.getJSONObject().toString());

                                        // Application code
                                        try {
                                            String id = object.getString("id");
                                            String name = object.getString("name");
//                                            String short_name = object.getString("short_name");

                                            String url_facebook = "https://graph.facebook.com/" + id + "/picture?type=large";

                                            checkUser(id, name, url_facebook);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "onCompleted: " + e.getMessage());
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,first_name,middle_name" +
                                ",last_name,name_format,short_name");

                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getContext(), "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void setTextUsername(String username) {
        edtEmail.setText(username);
        edtPass.setFocusable(true);
    }

    private void evenLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Ultil.isNetworkConnected(getContext())) {
                    String email = edtEmail.getText().toString().trim();
                    String password = edtPass.getText().toString().trim();

                    if (validateForm(email, password)) {
                        getData(email, password);
                    }
                } else {
                    Snackbar.make(view, "Vui lòng kiểm tra Internet", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        btnGoolge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Ultil.isNetworkConnected(getContext())) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    Ultil.show_snackbar(view, view);
                }
            }
        });
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Ultil.isNetworkConnected(getContext())) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginFragment.this, Arrays.asList("public_profile", "email"));
                } else {
                    Ultil.show_snackbar(view, view);
                }
            }
        });
    }

    private void checkUser(String id_user, String name, String url) {
        mListener.start_loading();

        DataService dataService = APIService.getService();
        Call<List<User>> callback = dataService.LoginSocial(id_user);
        callback.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                mListener.stop_loading(true);
                ArrayList<User> arrayList = (ArrayList<User>) response.body();
                Log.d(TAG, "onResponse: size - " + arrayList.size());
                if (arrayList.size() > 0) {
                    ArrayList<User> arrUser = (ArrayList<User>) response.body();
                    Ultil.user = arrUser.get(0);
                    Ultil.setUserPreference(getActivity());
                    Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    Intent intent=new Intent(getContext(), SignupActivity.class);
                    intent.putExtra("login_to", login_to);
                    intent.putExtra("product", product);
                    intent.putExtra("id_user",id_user);
                    intent.putExtra("name",name);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                mListener.stop_loading(false);
            }
        });
    }

    private void getData(String username, String password) {
        mListener.start_loading();

        DataService dataService = APIService.getService();
        Call<List<User>> callback = dataService.Login(username, password);
        callback.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                mListener.stop_loading(true);

                ArrayList<User> arrUser = (ArrayList<User>) response.body();
                Ultil.user = arrUser.get(0);
                Ultil.setUserPreference(getActivity());
                Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                Intent intent = Ultil.create_intent(login_to, getActivity(), product);
                getActivity().startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                mListener.stop_loading(true);
                Ultil.showDialog(getContext(), null, "Bạn đăng nhập sai. Vui lòng kiểm tra lại!", null, null);
            }
        });
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (email.isEmpty()) {
            //Field can't be empty
            edtEmail.setError("Không được bỏ trống");
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        if (password.isEmpty()) {
            edtPass.setError("Không được bỏ trống");
            valid = false;
        } else {
            edtPass.setError(null);
        }

        return valid;
    }

    private void initView() {
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPass = view.findViewById(R.id.edtPass);
        btnLogin = view.findViewById(R.id.btnLogin);

        btnFacebook = view.findViewById(R.id.button_facebook);
        btnGoolge = view.findViewById(R.id.button_google);
    }

    private void LoginGoogle(@Nullable GoogleSignInAccount account) {
        if (account != null) {

//            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
//            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
            Log.d(TAG, "personGivenName: " + personGivenName + " - personFamilyName: " + personFamilyName);
            if (personPhoto != null) {
                Log.d(TAG, "updateUI: NOT NULL");
                Log.d(TAG, "LoginGoogle: " + personPhoto.toString());
                checkUser(personId, personFamilyName, personPhoto.toString());
            } else {
                Log.d(TAG, "updateUI: NULL");
                String url_google_default = "https://avapp.000webhostapp.com/foody/anh/anhuser/google_default.png";
                checkUser(personId, personFamilyName, url_google_default);
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            LoginGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            LoginGoogle(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "result code google: "+ resultCode);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ILoading){
            mListener= (ILoading) context;
        }else{
            throw new RuntimeException(context.toString()
                    +" must implement ILoading");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener=null;
    }

//    private void dialog_infor_user(String name, String email) {
//
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("Tên: " + name);
//        stringBuilder.append("\nEmail: " + email);
//        stringBuilder.append("\nMật khẩu: 654321");
//        stringBuilder.append("\nBạn có thể đổi mật khẩu tại danh mục tài khoản");
//
//        final AlertDialog dialog = new AlertDialog.Builder(getContext())
//                .setIcon(R.drawable.ic_account_circle)
//                .setTitle("Thông tin tài khoản")
//                .setMessage(stringBuilder.toString())
//                .setCancelable(false)
//                .setPositiveButton("Ok", null)
//                .show();
//
//        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        positiveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                getActivity().finish();
//            }
//        });
//    }
}
