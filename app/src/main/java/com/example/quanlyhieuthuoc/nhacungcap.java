package com.example.quanlyhieuthuoc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class nhacungcap extends AppCompatActivity {
    EditText edtid, edttenncc, edtdiachi, edthopdong, edtsdt, edtemail;
    Button btnthem, btnsua, btnxoa, btntim;

    ListView lv;
    ArrayList<String> myList;
    ArrayList<Integer> idList; // Danh sách chứa các ID
    ArrayAdapter<String> myAdapter;

    SQLiteDatabase mydatabase;
    String DB_PATH_SUFFIX = "/databases/";
    String DATABASE_NAME = "qlht.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nhacungcap);

        edtid = findViewById(R.id.edtid);
        edttenncc = findViewById(R.id.edttenncc);
        edtdiachi = findViewById(R.id.edtdiachi);
        edthopdong = findViewById(R.id.edthopdong);
        edtsdt = findViewById(R.id.edtsdt);
        edtemail = findViewById(R.id.edtemail);
        btnthem = findViewById(R.id.btnthem);
        btnsua = findViewById(R.id.btnsua);
        btnxoa = findViewById(R.id.btnxoa);
        btntim = findViewById(R.id.btntim);


        lv = findViewById(R.id.lv);
        myList = new ArrayList<>();
        idList = new ArrayList<>();
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);
        lv.setAdapter(myAdapter);

        processCopy(); // Sao chép cơ sở dữ liệu từ thư mục assets
        loadDataFromDatabase(); // Tải dữ liệu vào ListView

        // Thiết lập sự kiện cho nút thêm
        btnthem.setOnClickListener(view -> addDataToDatabase());
        btnsua.setOnClickListener(view -> sua());
        btnxoa.setOnClickListener(view -> deleteDataFromDatabase());

        // Event listener for item selection in ListView
        lv.setOnItemClickListener((parent, view, position, id) -> {
            // Get selected ID and display it in edt_ma_nv (non-editable)
            int selectedId = idList.get(position);
            edtid.setText(String.valueOf(selectedId));
            btnxoa.setTag(selectedId);
            // Display all other information in respective EditText fields
            String selectedData = myList.get(position);
            String[] dataParts = selectedData.split(" - ");
            if (dataParts.length >= 6) {
                edttenncc.setText(dataParts[1].split(":")[1].trim());
                edtdiachi.setText(dataParts[2].split(":")[1].trim());
                edthopdong.setText(dataParts[3].split(":")[1].trim());
                edtsdt.setText(dataParts[4].split(":")[1].trim());
                edtemail.setText(dataParts[5].split(":")[1].trim());
            }

            // Show a toast with the selected ID
            Toast.makeText(nhacungcap.this, "ID được chọn: " + selectedId, Toast.LENGTH_SHORT).show();
        });



        // Tìm kiếm nhà cung cấp (nếu có)
        btntim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thêm logic tìm kiếm
            }
        });
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
        Cursor c = mydatabase.query("nhacungcap", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                // Hiển thị tất cả thông tin trong ListView
                String data = "id: " + c.getInt(0) +
                        " - Tên nha cung cap: " + c.getString(1) +
                        " - Đia Chỉ: " + c.getString(2) +
                        " - Hop dong: " + c.getString(3) +
                        " - Sdt: " + c.getString(4) +
                        " - Email: " + c.getInt(5);
                myList.add(data);
                idList.add(c.getInt(0)); // Lưu ID vào danh sách
            } while (c.moveToNext());
        }
        c.close();
        myAdapter.notifyDataSetChanged(); // Cập nhật ListView
    }

    //btnthem
    private void addDataToDatabase() {
        String tenncc = edttenncc.getText().toString();
        String diachi = edtdiachi.getText().toString();
        String hopdong = edthopdong.getText().toString();
        String sdt = edtsdt.getText().toString();
        String email = edtemail.getText().toString();

        if (tenncc.isEmpty() || diachi.isEmpty() || hopdong.isEmpty() || sdt.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin nhân viên", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String sql = "INSERT INTO nhacungcap (ten_ncc, dia_chi, hopdong, sdt, email) VALUES (?, ?, ?, ?, ?)";
            mydatabase.execSQL(sql, new Object[]{tenncc, diachi, hopdong, sdt, email});
            Toast.makeText(this, "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
            loadDataFromDatabase();
            xoadulieu();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi thêm dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //btnsua
    private void sua() {
        int selectedId;
        try {
            selectedId = Integer.parseInt(edtid.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng chọn nhân viên hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        String tenncc = edttenncc.getText().toString();
        String diachi = edtdiachi.getText().toString();
        String sdt = edthopdong.getText().toString();
        String email = edtsdt.getText().toString();
        String ngaysinh = edtemail.getText().toString();

        if (tenncc.isEmpty() || diachi.isEmpty() || sdt.isEmpty() || email.isEmpty() || ngaysinh.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin nhân viên", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String sql = "UPDATE nhacungcap SET ten_ncc = ?, dia_chi = ?, hopdong = ?, sdt = ?, email = ? WHERE id = ?";
            mydatabase.execSQL(sql, new Object[]{tenncc, diachi, sdt, email, ngaysinh, selectedId});
            Toast.makeText(this, "Cập nhật thông tin nhân viên thành công", Toast.LENGTH_SHORT).show();
            loadDataFromDatabase();
            xoadulieu();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi cập nhật dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Hàm xóa dữ liệu từ cơ sở dữ liệu
    private void deleteDataFromDatabase() {
        // Lấy ID đã lưu trong nút xóa (lấy từ Tag của nút)
        Integer selectedId = (Integer) btnxoa.getTag();

        if (selectedId == null || selectedId == 0) {
            Toast.makeText(this, "Vui lòng chọn một bản ghi để xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thực hiện xóa từ cơ sở dữ liệu
        int deletedRows = mydatabase.delete("nhacungcap", "id = ?", new String[]{String.valueOf(selectedId)});

        // Thông báo và cập nhật lại ListView
        if (deletedRows > 0) {
            Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
            loadDataFromDatabase(); // Tải lại dữ liệu sau khi xóa
            // Xóa các trường sau khi xóa thành công
            btnxoa.setTag(0); // Đặt lại ID trong nút xóa
            xoadulieu();

        } else {
            Toast.makeText(this, "Không tìm thấy bản ghi để xóa", Toast.LENGTH_SHORT).show();
        }
    }



     private void xoadulieu(){
        edttenncc.setText("");
        edtdiachi.setText("");
        edthopdong.setText("");
        edtsdt.setText("");
        edtemail.setText("");
        edtid.setText("");
    }
}