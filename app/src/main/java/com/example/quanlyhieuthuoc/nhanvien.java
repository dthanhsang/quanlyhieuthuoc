package com.example.quanlyhieuthuoc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class nhanvien extends AppCompatActivity {

    // Khai báo các biến cho các thành phần UI

    EditText edt_ten, edt_diachi,edt_sua,edt_sdt,edt_email, edt_ngaysinh,edt_tknv;
    Button btn_themnv,btn_sua,btn_xoa,btn_timkiem;
    ListView lv_nv;
    ArrayList<String> myList;
    ArrayList<Integer> idList; // Danh sách chứa các ID
    ArrayAdapter<String> myAdapter;
    SQLiteDatabase mydatabase;
    String DB_PATH_SUFFIX = "/databases/";
    String DATABASE_NAME = "hieuthuoc.db";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Thiết lập layout cho Activity
        setContentView(R.layout.nhanvien);

        edt_ten= findViewById(R.id.edt_ten);

        edt_diachi= findViewById(R.id.edt_diachi);

        // Khởi tạo các thành phần UI
        edt_sdt= findViewById(R.id.edt_sdt);
        edt_email= findViewById(R.id.edt_email);

        edt_ngaysinh= findViewById(R.id.edt_ngaysinh);

        edt_tknv= findViewById(R.id.edt_tknv);

        btn_timkiem = findViewById(R.id.btn_timkiem);
        btn_xoa = findViewById(R.id.btn_xoa);
        lv_nv = findViewById(R.id.lv_nv);
        btn_sua=findViewById(R.id.btn_sua);
        btn_themnv=findViewById(R.id.btn_themnv);

        // Khởi tạo các danh sách và adapter
        myList = new ArrayList<>();
        idList = new ArrayList<>();
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);
        lv_nv.setAdapter(myAdapter);

        // Gọi phương thức để sao chép cơ sở dữ liệu và tải dữ liệu
        processCopy();

        // Lấy dữ liệu từ cơ sở dữ liệu vào ListView
        loadDataFromDatabase();

        // Lắng nghe sự kiện cho nút thêm nhân viên




        // Lắng nghe sự kiện cho nút tìm kiếm
        btn_timkiem.setOnClickListener(view -> {
            String keyword = edt_tknv.getText().toString();
            searchEmployee(keyword);
        });

        // Lắng nghe sự kiện cho nút xóa
        btn_xoa.setOnClickListener(view -> deleteDataFromDatabase());
    }

    // Hàm sao chép cơ sở dữ liệu từ thư mục assets
    private void processCopy() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
                Toast.makeText(this, "Sao chép thành công từ thư mục Assets", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        mydatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
    }

    // Lấy đường dẫn tới database trong thư mục cài đặt ứng dụng
    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }

    // Hàm sao chép database từ thư mục assets
    public void CopyDataBaseFromAsset() {
        try {
            InputStream myInput = getAssets().open(DATABASE_NAME);
            String outFileName = getDatabasePath();
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists()) {
                f.mkdir();
            }
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm tải dữ liệu từ cơ sở dữ liệu vào ListView
    private void loadDataFromDatabase() {
        myList.clear(); // Xóa danh sách hiện tại
        idList.clear(); // Xóa danh sách ID hiện tại
        Cursor c = mydatabase.query("nhanvien", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                // Hiển thị tất cả thông tin trong ListView
                String data = "MA_NV: " + c.getInt(0) +
                        " - Tên nhân vien: " + c.getString(1) +
                        " - Đia Chỉ: " + c.getString(2) +
                        " - So DT: " + c.getString(3) +
                        " - Email: " + c.getString(4) +
                        " - Ngay sinh: " + c.getInt(5);
                myList.add(data);
                idList.add(c.getInt(0)); // Lưu ID vào danh sách
            } while (c.moveToNext());
        }
        c.close();
        myAdapter.notifyDataSetChanged(); // Cập nhật ListView
    }

    // Hàm tìm kiếm nhân viên theo tên
    private void searchEmployee(String keyword) {
        myList.clear(); // Xóa danh sách hiện tại
        Cursor c = mydatabase.rawQuery("SELECT * FROM nhanvien WHERE ten_nv LIKE ?", new String[]{"%" + keyword + "%"});

        c.close();
        myAdapter.notifyDataSetChanged(); // Cập nhật ListView
    }

    // Hàm xóa dữ liệu từ cơ sở dữ liệu
    private void deleteDataFromDatabase() {
        if (btn_xoa.getTag() == null) {
            Toast.makeText(this, "Vui lòng chọn một bản ghi để xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = (Integer) btn_xoa.getTag(); // Lấy ID từ tag của nút xóa

        // Thực hiện xóa từ cơ sở dữ liệu
        int deletedRows = mydatabase.delete("nhanvien", "ma_nv = ?", new String[]{String.valueOf(selectedId)});

        // Thông báo và cập nhật lại ListView
        if (deletedRows > 0) {
            Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
            loadDataFromDatabase(); // Tải lại dữ liệu sau khi xóa
        } else {
            Toast.makeText(this, "Không tìm thấy bản ghi để xóa", Toast.LENGTH_SHORT).show();
        }
    }
}
