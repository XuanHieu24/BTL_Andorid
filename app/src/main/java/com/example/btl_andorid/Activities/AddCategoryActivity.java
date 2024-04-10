package com.example.btl_andorid.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.sinhvien.orderdrinkapp.DAO.LoaiMonDAO;
import com.sinhvien.orderdrinkapp.DTO.LoaiMonDTO;
import com.sinhvien.orderdrinkapp.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    Button BTN_addcategory_TaoLoai;
    ImageView IMG_addcategory_back, IMG_addcategory_ThemHinh;
    TextView TXT_addcategory_title;
    TextInputLayout TXTL_addcategory_TenLoai;
    LoaiMonDAO loaiMonDAO;
    int maloai = 0;
    Bitmap bitmapold;   //lưu trữ hình ảnh của loại sản phẩm

    //dùng result launcher do activityforresult ko dùng đc nữa

    ActivityResultLauncher<Intent> resultLauncherOpenIMG = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){ // nếu dữ kết quả trả về thành công và có dlieu
                        Uri uri = result.getData().getData();
                        // uri sẽ nhận dữ liệu trả về ( hình ảnh đã chọn trong thiết bị)
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(uri); // tạo luồng đầu vào tu URI sử dụng trình phân giải ContentResoler
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream); //giải mã luồng và chuyển đổi thành 1 đối tượng bitmap
                            IMG_addcategory_ThemHinh.setImageBitmap(bitmap); // hình ảnh đã chọn se được đặt lên ImageView có tên là IMG_addcategory_ThemHinh.setImageBitmap(bitmap)
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcategory_layout);

        loaiMonDAO = new LoaiMonDAO(this);  //khởi tạo đối tượng dao kết nối csdl


        BTN_addcategory_TaoLoai = (Button)findViewById(R.id.btn_addcategory_TaoLoai);
        TXTL_addcategory_TenLoai = (TextInputLayout)findViewById(R.id.txtl_addcategory_TenLoai);
        IMG_addcategory_back = (ImageView)findViewById(R.id.img_addcategory_back);
        IMG_addcategory_ThemHinh = (ImageView)findViewById(R.id.img_addcategory_ThemHinh);
        TXT_addcategory_title = (TextView)findViewById(R.id.txt_addcategory_title);


        BitmapDrawable olddrawable = (BitmapDrawable)IMG_addcategory_ThemHinh.getDrawable(); // trả về 1 đối tượng drawable mà imgview IMG_addcategory_ThemHinh đang hiển thị
        bitmapold = olddrawable.getBitmap();

// Chỉnh sửa loại món
        maloai = getIntent().getIntExtra("maloai",0); // lấy mã loại món ăn đa khởi tạo
        if(maloai != 0){
            TXT_addcategory_title.setText(getResources().getString(R.string.editcategory));
            // nếu mã loại khác 0 đặt tieue đề là sửa danh mục
            LoaiMonDTO loaiMonDTO = loaiMonDAO.LayLoaiMonTheoMa(maloai);
            // lấy thông tin của loại món ăn theo maloai trong csdl

            //Hiển thị lại thông tin từ csdl
            TXTL_addcategory_TenLoai.getEditText().setText(loaiMonDTO.getTenLoai());

            byte[] categoryimage = loaiMonDTO.getHinhAnh(); // lấy hình ảnh từ LoaiMonDTO
            Bitmap bitmap = BitmapFactory.decodeByteArray(categoryimage,0,categoryimage.length); // giải mã hình ảnh từ byte thành bitmap
            IMG_addcategory_ThemHinh.setImageBitmap(bitmap); // hiển thị hình ảnh sau khi giải mã

            BTN_addcategory_TaoLoai.setText("Sửa loại"); // nút bấm sẽ được sửa tiêu đề thành sửa loại
        }
        //endregion
// tạo sự kiện click khi được nhấn
        IMG_addcategory_back.setOnClickListener(this);
        IMG_addcategory_ThemHinh.setOnClickListener(this);
        BTN_addcategory_TaoLoai.setOnClickListener(this);
        }

    @Override
    public void onClick(View v) {
        int id = v.getId(); // sử lý đối tượng được click thông qua ID
        boolean ktra;
        String chucnang;
        switch (id){
            case R.id.img_addcategory_back:
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right); //animation
                break;

            case R.id.img_addcategory_ThemHinh: //Mở một Intent để người dùng chọn hình ảnh từ thiết bị và hiển thị hình ảnh đó trong ứng dụng
                Intent iGetIMG = new Intent();
                iGetIMG.setType("image/*"); //lấy những mục chứa hình ảnh
                iGetIMG.setAction(Intent.ACTION_GET_CONTENT);   //lấy mục hiện tại đang chứa hình
                resultLauncherOpenIMG.launch(Intent.createChooser(iGetIMG,getResources().getString(R.string.choseimg)));    //mở intent chọn hình ảnh
                break;

            case R.id.btn_addcategory_TaoLoai:
                if(!validateImage() | !validateName()){
                    //Nếu hình ảnh hoặc tên loại chưa hợp lệ, dừng thực hiện và thoát khỏi phương thức.
                    return;
                }

                String sTenLoai = TXTL_addcategory_TenLoai.getEditText().getText().toString(); // lấy tên loại từ TXTL_addcategory_TenLoai
                LoaiMonDTO loaiMonDTO = new LoaiMonDTO();
                loaiMonDTO.setTenLoai(sTenLoai);
                loaiMonDTO.setHinhAnh(imageViewtoByte(IMG_addcategory_ThemHinh));
                if(maloai != 0){
                    ktra = loaiMonDAO.SuaLoaiMon(loaiMonDTO,maloai);
                    chucnang = "sualoai";
                }else {
                    ktra = loaiMonDAO.ThemLoaiMon(loaiMonDTO);
                    chucnang = "themloai";
                }

                //Thêm, sửa loại dựa theo obj loaimonDTO
                Intent intent = new Intent();
                intent.putExtra("ktra",ktra); //ktra là một giá trị boolean cho biết việc thêm hoặc sửa danh mục có thành công hay không (true) hay không (false).
                intent.putExtra("chucnang",chucnang); // chucnang là một giá trị String cho biết hoạt động được thực hiện. Nó có thể là "themloai" (thêm danh mục) hoặc "sualoai" (sửa danh mục).
                setResult(RESULT_OK,intent);
                finish();
                break;

        }
    }

    //Chuyển ảnh bitmap về mảng byte lưu vào csdl
    private byte[] imageViewtoByte(ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    //region validate fields
    private boolean validateImage(){
        BitmapDrawable drawable = (BitmapDrawable)IMG_addcategory_ThemHinh.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        if(bitmap == bitmapold){
            Toast.makeText(getApplicationContext(),"Xin chọn hình ảnh",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    private boolean validateName(){
        String val = TXTL_addcategory_TenLoai.getEditText().getText().toString().trim();
        if(val.isEmpty()){
            TXTL_addcategory_TenLoai.setError(getResources().getString(R.string.not_empty));
            return false;
        }else {
            TXTL_addcategory_TenLoai.setError(null);
            TXTL_addcategory_TenLoai.setErrorEnabled(false);
            return true;
        }
    }
    //endregion

}