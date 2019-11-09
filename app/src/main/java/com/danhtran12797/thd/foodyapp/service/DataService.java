package com.danhtran12797.thd.foodyapp.service;

import com.danhtran12797.thd.foodyapp.model.Banner;
import com.danhtran12797.thd.foodyapp.model.Category;
import com.danhtran12797.thd.foodyapp.model.Order;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface DataService {
    @GET("banner.php")
    Call<List<Banner>> GetDatabanner();

    @GET("RecentProduct.php")
    Call<List<Product>> GetRecentProduct();

    @GET("DealProduct.php")
    Call<List<Product>> GetDealProduct();

    @GET("LoveProduct.php")
    Call<List<Product>> GetLoveProduct();

    @GET("category.php")
    Call<List<Category>> GetCategory();

    @GET("GetTotalPage.php")
    Call<String> GetTotalPage(@Query("iddmm") String iddm);

    @GET("GetDetailCategory.php")
    Call<List<Product>> GetDetailCategory(@Query("iddm") String iddm, @Query("page") int page);

    @GET("GetTotalAllPage.php")
    Call<String> GetTotalAllPage();

    @GET("GetAllProduct.php")
    Call<List<Product>> GetAllProduct(@Query("page") int page);

    @FormUrlEncoded
    @POST("CountLoveProduct.php")
    Call<List<User>> CountLoveProduct(@Field("idsp") String idsp);

    @FormUrlEncoded
    @POST("Login.php")
    Call<List<User>> Login(@Field("username") String username, @Field("password") String password);

    @Multipart
    @POST("UploadImage.php")
    Call<String> UploadImage(@Part MultipartBody.Part photo);

    @Multipart
    @POST("UpdateImage.php")
    Call<String> UpdateImage(@Part MultipartBody.Part photo, @Part("name_file") RequestBody name_file);

    @FormUrlEncoded
    @POST("Register.php")
    Call<String> Register(@Field("name") String name
            , @Field("username") String username
            , @Field("password") String password
            , @Field("email") String email
            , @Field("address") String address
            , @Field("phone") String phone
            , @Field("avatar") String avatar);

    @FormUrlEncoded
    @POST("UpdateUser.php")
    Call<String> UpdateUser(@Field("id") String id
            , @Field("name") String name
            , @Field("username") String username
            , @Field("email") String email
            , @Field("address") String address
            , @Field("phone") String phone
            , @Field("avatar") String avatar);

    @FormUrlEncoded
    @POST("UpdatePassword.php")
    Call<String> UpdatePassword(@Field("id") String id, @Field("pass_new") String pass_new);

    @FormUrlEncoded
    @POST("GetProductToBanner.php")
    Call<List<Product>> GetProductToBanner(@Field("id_product") String id_product);

    @FormUrlEncoded
    @POST("DeleteUserLoveProduct.php")
    Call<String> DeleteUserLoveProduct(@Field("idsp") String idsp, @Field("iduser") String iduser);

    @FormUrlEncoded
    @POST("DeleteAllUserLoveProduct.php")
    Call<String> DeleteAllUserLoveProduct(@Field("iduser") String iduser);

    @FormUrlEncoded
    @POST("InsertUserLoveProduct.php")
    Call<String> InsertUserLoveProduct(@Field("idsp") String idsp, @Field("iduser") String iduser);

    @FormUrlEncoded
    @POST("GetProductUserLove.php")
    Call<List<Product>> GetProductUserLove(@Field("iduser") String iduser);

    @GET("GetProductFromSearch.php")
    Call<List<Product>> GetProductFromSearch(@Query("key_name") String key_name);

    @FormUrlEncoded
    @POST("GetCategoryProduct.php")
    Call<List<Category>> GetCategoryProduct(@Field("idsp") String idsp);

    @FormUrlEncoded
    @POST("Orders.php")
    Call<String> Orders(@Field("id_user") String id_user
            , @Field("name") String name
            , @Field("phone") String phone
            , @Field("address") String address
            , @Field("request") String request
            , @Field("email") String email
            , @Field("delivery") String delivery
            , @Field("payment") String payment);

    @FormUrlEncoded
    @POST("OrdersDetail.php")
    Call<String> OrdersDetail(@Field("json_shoping_cart") String json);

    @FormUrlEncoded
    @POST("GetOrder.php")
    Call<List<Order>> GetOrder(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("Update_Order_Detail.php")
    Call<String> Update_Order_Detail(@Field("id_order") String id_order);

    @FormUrlEncoded
    @POST("GetProduct.php")
    Call<List<Product>> GetProduct(@Field("id_product") String id_product);
}
