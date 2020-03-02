package com.danhtran12797.thd.foodyapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.DialogUserLoveAdapter;
import com.danhtran12797.thd.foodyapp.model.Category;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.model.ShopingCart;
import com.danhtran12797.thd.foodyapp.model.User;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProductActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DetailProductActivity";
    Toolbar toolbar;
    LikeButton btnLove;
    ImageView imageView;
    Button btnAddToCard;
    TextView txtCountLove;
    TextView txtSale1;
    TextView txtSale2;
    TextView txtPrice;
    TextView txtCost;
    TextView txtDesc;
    TextView txtCompo;
    TextView txtCategoryProduct;

    RotateLoading rotateloading;

    Product product;
    ArrayList<User> arrCountLove = null;
    DecimalFormat decimalFormat;

    BottomSheetDialog bottomSheetDialog;
    View view;

    // bottom sheet
    Button btn_seen_shoping;
    ImageView img_botoom_sheet;
    ImageView img_close_botoom_sheet;
    TextView txt_name_bottom_sheet;
    TextView txt_price_bottom_sheet;
    TextView txt_category_bottom_sheet;

    int position = -1;
    int gia;
    int sale1;
    String category;

//    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        Log.d(TAG, "onCreate");
//        rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        Intent intent = getIntent();

        product = (Product) intent.getSerializableExtra("detail_product");
        decimalFormat = new DecimalFormat("###,###,###");

        initView();
        initActionBar();
        getDataCountLove();
        eventAddToCard();
        eventLove();
        //get name and set UI category product
        GetCategoryProduct(product.getId());
    }

    private void GetCategoryProduct(String idsp) {
        rotateloading.start();

        DataService dataService = APIService.getService();
        Call<List<Category>> callback = dataService.GetCategoryProduct(idsp);
        callback.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                ArrayList<Category> arrayList = (ArrayList<Category>) response.body();
                Log.d(TAG, "name category: " + arrayList.get(0).getName());
                category = arrayList.get(0).getName();
                txtCategoryProduct.setText(arrayList.get(0).getName());
                txt_category_bottom_sheet.setText(arrayList.get(0).getName());

                rotateloading.stop();
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                rotateloading.stop();
            }
        });
    }

    private void dialogQuestionLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon_app_design);
        builder.setTitle("Bạn có muốn đăng nhập");
        builder.setMessage("Bạn cần đăng nhập để thích sản phẩm này");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(DetailProductActivity.this, LoginActivity.class);
                intent.putExtra("user_love_product", product);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    private void eventLove() {
        btnLove.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (Ultil.user != null) {
                    insertUserLoveProduct();
                } else {
                    btnLove.setLiked(false);
                    dialogQuestionLogin();
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                deleteUserLoveProduct();
            }
        });
    }

    private void deleteUserLoveProduct() {
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.DeleteUserLoveProduct(product.getId(), Ultil.user.getId());
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String message = response.body();
                Log.d(TAG, message);
                if (message.equals("success")) {
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void insertUserLoveProduct() {
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.InsertUserLoveProduct(product.getId(), Ultil.user.getId());
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String message = response.body();
                Log.d(TAG, message);
                if (message.equals("success")) {
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void eventAddToCard() {
        btnAddToCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopingCart shopingCart = new ShopingCart(product.getId(), product.getName(), product.getImage(), gia, 1);
                shopingCart.setCategoty(category);
                Ultil.add_product_shoping_cart(shopingCart);
                Ultil.setShopingCart(DetailProductActivity.this);
                bottomSheetDialog.show();
            }
        });
    }

    private void getDataCountLove() {
        setValueUI();
        DataService dataService = APIService.getService();
        Call<List<User>> callback = dataService.CountLoveProduct(product.getId());
        callback.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                arrCountLove = (ArrayList<User>) response.body();
                setValueUI();
                setButtonLove();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void setValueUI() {
        Picasso.get().load(Ultil.url_image_product + product.getImage())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(imageView);
        gia = Integer.parseInt(product.getPrice());
        sale1 = Integer.parseInt(product.getSale1());

        if (sale1 != 0) {
            txtSale1.setVisibility(View.VISIBLE);
            txtCost.setVisibility(View.VISIBLE);
            txtSale1.setText(sale1 + "% OFF");
            txtCost.setPaintFlags(txtCost.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtCost.setText(decimalFormat.format(gia) + " Đ/Phần");
            gia = gia * sale1 / 100;
        } else {
            txtSale1.setVisibility(View.GONE);
            txtCost.setVisibility(View.GONE);
        }

        if (!product.getSale2().equals("")) {
            txtSale2.setVisibility(View.VISIBLE);
            txtSale2.setText(product.getSale2());
        } else {
            txtSale2.setVisibility(View.GONE);
        }

        if (arrCountLove != null) {
            txtCountLove.setText(arrCountLove.size() + " lượt yêu thích");
            txtCountLove.setPaintFlags(txtCountLove.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            txtCountLove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (arrCountLove != null) {
                        show_dialog_user_love();
                    }
                }
            });
        } else {
            txtCountLove.setText("Hãy là người đầu tiên thích thực đơn này");
        }
        txtPrice.setText(decimalFormat.format(gia) + " Đ/Phần");
        txtDesc.setText(product.getDesc());
        txtCompo.setText(product.getCompo());

        //bottom sheet
        txt_name_bottom_sheet.setText(product.getName());
        txt_price_bottom_sheet.setText(decimalFormat.format(gia) + " VNĐ");

        Picasso.get().load(Ultil.url_image_product + product.getImage())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.error)
                .into(img_botoom_sheet);
    }

    private void show_dialog_user_love() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_list_user_love);

        TextView txtCountUser = dialog.findViewById(R.id.txt_dialog_count_love);
        ImageView imgClose = dialog.findViewById(R.id.img_exit_dialog);
        ListView listView = dialog.findViewById(R.id.lv_dialog_user_love);

        DialogUserLoveAdapter adapter = new DialogUserLoveAdapter(dialog.getContext(), arrCountLove);
        listView.setAdapter(adapter);
        txtCountUser.setText(arrCountLove.size() + " lượt yêu thích");

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void initView() {
        rotateloading = findViewById(R.id.rotateloading);

        btnLove = findViewById(R.id.star_button);
        imageView = findViewById(R.id.imgProductDetail);
        txtCost = findViewById(R.id.txtCostDetail);
        txtPrice = findViewById(R.id.txtPriceDetail);
        txtDesc = findViewById(R.id.txtDescDetail);
        txtSale1 = findViewById(R.id.txtSale1Detail);
        txtSale2 = findViewById(R.id.txtSale2Detail);
        txtCountLove = findViewById(R.id.txtCountLoveDetail);
        txtCompo = findViewById(R.id.txtCompo);
        txtCategoryProduct = findViewById(R.id.txtCategoryProduct);

        btnAddToCard = findViewById(R.id.btnAddToCard);

        // bottom sheet
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);

        view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_shoping_cart, null);
        txt_price_bottom_sheet = view.findViewById(R.id.txt_price_bottom_sheet);
        txt_name_bottom_sheet = view.findViewById(R.id.txt_name_bottom_sheet);
        img_botoom_sheet = view.findViewById(R.id.img_bottom_sheet);
        img_close_botoom_sheet = view.findViewById(R.id.img_close_bottom_sheet);
        btn_seen_shoping = view.findViewById(R.id.btn_seen_shoping);
        txt_category_bottom_sheet = view.findViewById(R.id.txt_category_bottom_sheet);
        btn_seen_shoping.setOnClickListener(this);
        img_close_botoom_sheet.setOnClickListener(this);

        bottomSheetDialog.setContentView(view);
    }

    private void setButtonLove() {
        if (Ultil.user != null) {
            if (arrCountLove != null) {
                for (int i = 0; i < arrCountLove.size(); i++) {
                    if (Ultil.user.getId().equals(arrCountLove.get(i).getId())) {
                        btnLove.setLiked(true);
                        position = i;
                    }
                }
            }
        }
    }

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbar_detail_product);
        toolbar.setTitle(product.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_product, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_detail_product_card:
                startActivity(new Intent(this, ShopingCartActivity.class));
                break;
            case R.id.menu_detail_product_share:
                getLocationPermission();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void ShareImage() {
        Uri uri;
        try {
            File file = saveBitmap(takeScreenshot());
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.putExtra("android.intent.extra.SUBJECT", "Hỏi bạn bè!");
            intent.putExtra("android.intent.extra.TITLE", "Chơi Game Hỏi Ngu Siêu Hại Não với mình nào!");
            intent.putExtra("android.intent.extra.STREAM", uri);
            intent.setType("image/*");
            startActivity(intent);
        } catch (Exception unused) {
            Toast.makeText(this, "Error2: "+unused.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("UUU", "ShareImage: "+unused.getMessage());
        }
    }

    public File saveBitmap(Bitmap bitmap) {
        File imagepath=null;
        if (Environment.getExternalStorageState().equals("mounted")) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            StringBuilder sb = new StringBuilder();
            sb.append(externalStorageDirectory.getAbsolutePath());
            sb.append("/HoiNguSieuHaiNao");
            File file = new File(sb.toString());
            if (!file.exists()) {
                file.mkdirs();
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(file.getPath());
            sb2.append("/screenhoingu.jpg");
            imagepath = new File(sb2.toString());
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(imagepath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                Log.d(TAG, "saveBitmap: GOOD");
            } catch (Exception e) {
                Toast.makeText(this, "Error1: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("UUU", "saveBitmap: "+e.getMessage());
            }
        }
        return imagepath;
    }

    public Bitmap takeScreenshot() {
        View rootView = getWindow().getDecorView().getRootView().findViewById(android.R.id.content);;
        rootView.setDrawingCacheEnabled(false);
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            ShareImage();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==101){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                ShareImage();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_seen_shoping:
                startActivity(new Intent(this, ShopingCartActivity.class));
                bottomSheetDialog.dismiss();
                break;
            case R.id.img_close_bottom_sheet:
                bottomSheetDialog.dismiss();
                break;
        }
    }
}
