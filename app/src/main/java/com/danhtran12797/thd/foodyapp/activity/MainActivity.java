package com.danhtran12797.thd.foodyapp.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.MainFragmentAdapter;
import com.danhtran12797.thd.foodyapp.fragment.ConnectionFragment;
import com.danhtran12797.thd.foodyapp.fragment.DealFragment;
import com.danhtran12797.thd.foodyapp.fragment.MostLikeFragment;
import com.danhtran12797.thd.foodyapp.fragment.NewProductFragment;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.http.HTTP;

import static com.danhtran12797.thd.foodyapp.ultil.Ultil.user;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout layout_infor_user;
    private LinearLayout layout_login_register;
    private Button btn_login;
    private Button btn_register;
    private Intent intent;
    private View header;
    private TextView txtNameUser;
    private TextView txtEmailUser;
    private CircleImageView imgUserHeader;
    private TextView txtAllProduct;
//    private NestedScrollView scrollView;

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //scrollView=findViewById(R.id.scrollView);
//        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { @Override public void onGlobalLayout() { scrollView.fullScroll(View.FOCUS_UP);  } });

        initToolbar();
        initDrawer();
        if (Ultil.isNetworkConnected(this)) {
            initTabLayout();
            initTextViewAll();
            Ultil.arrShoping = Ultil.getShopingCart(this);
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_container, new ConnectionFragment()).commit();
        }
    }

//    private final void focusOnView() {
//        scrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.scrollTo(0, txtAllProduct.getBottom());
//            }
//        });
//    }

    private void initTextViewAll() {
        txtAllProduct = findViewById(R.id.txt_all_product);
        txtAllProduct.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refesh_activity_main:
                finish();
                startActivity(getIntent());
                break;
            case R.id.menu_search_activity_main:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_shoping_activity_main:
                intent = new Intent(this, ShopingCartActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initHeaderView() {
        layout_infor_user = header.findViewById(R.id.layout_infor_user_header);
        layout_login_register = header.findViewById(R.id.layout_header_login_register);
        btn_login = header.findViewById(R.id.btn_login_header);
        btn_register = header.findViewById(R.id.btn_register_header);
        txtNameUser = header.findViewById(R.id.txt_name_header);
        txtEmailUser = header.findViewById(R.id.txt_email_header);
        imgUserHeader = header.findViewById(R.id.img_avatar_user_header);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("BBB", "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("BBB", "onStart");

        user = Ultil.getUserPreference(this);
        if (user != null) {
            Log.d("BBB", "good login");
            layout_infor_user.setVisibility(View.VISIBLE);
            layout_login_register.setVisibility(View.GONE);

            set_nav_header();
        } else {
            Log.d("BBB", "faild login");
            layout_infor_user.setVisibility(View.GONE);
            layout_login_register.setVisibility(View.VISIBLE);
        }
    }

    private void set_nav_header() {
        txtEmailUser.setText(user.getEmail());
        txtNameUser.setText(user.getName());
        Picasso.get().load(Ultil.url_image_avatar + user.getAvatar())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(imgUserHeader);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("BBB", "onResume");
    }

    private void initDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);
        initHeaderView();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initTabLayout() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        MainFragmentAdapter adapter = new MainFragmentAdapter(getSupportFragmentManager());

        adapter.addFragment(new NewProductFragment(), "Mới Nhất");
        adapter.addFragment(new DealFragment(), "Khuyến Mãi");
        adapter.addFragment(new MostLikeFragment(), "Lượt thích");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    private void dialogQuestionLogin(String message, final int code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon_app_design);
        builder.setTitle("Bạn có muốn đăng nhập");
        //builder.setMessage("Bạn cần đăng nhập để xem danh sách thực đơn bạn đã thích");
        builder.setMessage(message);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (code == 1) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("list_love_product_of_user", true);
                    startActivity(intent);
                } else if (code == 2) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("list_order_of_user", true);
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    private void dialog_infor_app() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông tin ứng dụng");
        builder.setIcon(R.drawable.icon_app_design);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_infor_app, null);
        builder.setView(dialogView);

        TextView txtDeveloper = dialogView.findViewById(R.id.txtDeveloper);
        txtDeveloper.setMovementMethod(LinkMovementMethod.getInstance());

        TextView txtPrivacy = dialogView.findViewById(R.id.txtPrivacyPolicy);
        txtPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void exit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("THD Foody");
        alertDialogBuilder
                .setMessage("Bạn có muốn thoát ứng dụng?")
                .setCancelable(false)
                .setPositiveButton("Đồng ý",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_category:
                startActivity(new Intent(this, CategoryActivity.class));
                break;
            case R.id.nav_my_fav:
                if (user != null) {
                    intent = new Intent(this, ListUserProductLoveActivity.class);
                    startActivity(intent);
                } else {
                    dialogQuestionLogin("Bạn cần đăng nhập để xem danh sách thực đơn bạn đã thích", 1);
                }
                break;
            case R.id.nav_account:
                if (user != null) {
                    startActivity(new Intent(this, AccountUserActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_list_order:
                if (user != null) {
                    startActivity(new Intent(this, OrderActivity.class));
                } else {
                    dialogQuestionLogin("Bạn cần đăng nhập để xem danh sách hóa đơn của bạn", 2);
                }
                break;
            case R.id.nav_shop:
                startActivity(new Intent(this, ShopingCartActivity.class));
                break;
            case R.id.nav_location:
//                removeUserPreference(this);
//                onStart();
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.nav_about:
                //Ultil.removeShopingCart(this);
                //Ultil.removeAddressShipping(this);
                dialog_infor_app();
                break;
            case R.id.nav_rate:
                rateMe();
                break;
            case R.id.nav_share:
                shareAppLinkViaFacebook();
                break;
            case R.id.nav_exit:
                //startActivity(new Intent(this, AddLocationOrderActivity.class));
                exit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    public void shareAppLinkViaFacebook() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", "Trải nghiệm THD Foody với mình nào bạn!");
        intent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id="+getPackageName()); // or BuildConfig.APPLICATION_ID
        startActivity(Intent.createChooser(intent, "Chia THD Foody cho bạn bè!"));
    }

    public void rateMe() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_header:
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("login_register", 0);
                startActivity(intent);
                break;
            case R.id.btn_register_header:
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("login_register", 1);
                startActivity(intent);
                break;
            case R.id.txt_all_product:
                intent = new Intent(this, AllProductActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("BBB", "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("BBB", "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("BBB", "onDestroy: ");
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Chạm lần nữa để thoát", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }
}
