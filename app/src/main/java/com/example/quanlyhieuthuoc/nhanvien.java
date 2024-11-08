package com.example.quanlyhieuthuoc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class nhanvien extends AppCompatActivity {

    EditText edt_ten, edt_diachi, edt_sdt, edt_email, edt_ngaysinh, edt_tknv, edt_ma_nv;
    Button btn_themnv, btn_sua, btn_xoa, btn_timkiem;
    ListView lv_nv;
    ArrayList<String> myList;
    ArrayList<Integer> idList;
    ArrayAdapter<String> myAdapter;
    SQLiteDatabase mydatabase;
    String DB_PATH_SUFFIX = "/databases/";
    String DATABASE_NAME = "hieuthuoc.db";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nhanvien);

        edt_ten = findViewById(R.id.edt_ten);
        edt_diachi = findViewById(R.id.edt_diachi);
        edt_sdt = findViewById(R.id.edt_sdt);
        edt_email = findViewById(R.id.edt_email);
        edt_ngaysinh = findViewById(R.id.edt_ngaysinh);
        edt_ma_nv = findViewById(R.id.edt_ma_nv);
        edt_tknv = findViewById(R.id.edt_tknv);

        btn_timkiem = findViewById(R.id.btn_timkiem);
        btn_xoa = findViewById(R.id.btn_xoa);
        lv_nv = findViewById(R.id.lv_nv);
        btn_sua = findViewById(R.id.btn_sua);
        btn_themnv = findViewById(R.id.btn_themnv);

        myList = new ArrayList<>();
        idList = new ArrayList<>();
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);
        lv_nv.setAdapter(myAdapter);

        processCopy();
        loadDataFromDatabase();

        btn_themnv.setOnClickListener(view -> themnv());
        btn_sua.setOnClickListener(view -> sua_nv());
        btn_xoa.setOnClickListener(view -> xoanv());
        btn_timkiem.setOnClickListener(view -> timkiemnv(edt_tknv.getText().toString()));

        lv_nv.setOnItemClickListener((parent, view, position, id) -> {
            int selectedId = idList.get(position);
            edt_ma_nv.setText(String.valueOf(selectedId));
            btn_xoa.setTag(selectedId);

            String selectedData = myList.get(position);
            String[] dataParts = selectedData.split(" - ");
            if (dataParts.length >= 6) {
                edt_ten.setText(dataParts[1].split(":")[1].trim());
                edt_diachi.setText(dataParts[2].split(":")[1].trim());
                edt_sdt.setText(dataParts[3].split(":")[1].trim());
                edt_email.setText(dataParts[4].split(":")[1].trim());
                edt_ngaysinh.setText(dataParts[5].split(":")[1].trim());
            }
            Toast.makeText(nhanvien.this, "Nhan vien được chọn: " + selectedId, Toast.LENGTH_SHORT).show();
        });
    }
   ////   HỆ THỐNG SQL
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

    private String getDatabaseFilePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }


    public void CopyDataBaseFromAsset() {
        try {
            InputStream myInput = getAssets().open(DATABASE_NAME);
            String outFileName = getDatabaseFilePath();
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

    private void loadDataFromDatabase() {
        myList.clear();
        idList.clear();
        Cursor c = mydatabase.query("nhanvien", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                String data = "MA_NV: " + c.getInt(0) +
                        " - Tên nhân viên: " + c.getString(1) +
                        " - Địa chỉ: " + c.getString(2) +
                        " - Số ĐT: " + c.getString(3) +
                        " - Email: " + c.getString(4) +
                        " - Ngày sinh: " + c.getString(5);
                myList.add(data);
                idList.add(c.getInt(0));
            } while (c.moveToNext());
        }

        myAdapter.notifyDataSetChanged();
    }



  ////////////CHỨC NĂNG CAC NUTS///
    private void themnv() {
        String tennv = edt_ten.getText().toString();
        String diachi = edt_diachi.getText().toString();
        String sdt = edt_sdt.getText().toString();
        String email = edt_email.getText().toString();
        String ngaysinh = edt_ngaysinh.getText().toString();

        if (tennv.isEmpty() || diachi.isEmpty() || sdt.isEmpty() || email.isEmpty() || ngaysinh.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin nhân viên", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String sql = "INSERT INTO nhanvien (ten_nv, dia_chi, sdt, email, ngay_sinh) VALUES (?, ?, ?, ?, ?)";
            mydatabase.execSQL(sql, new Object[]{tennv, diachi, sdt, email, ngaysinh});
            Toast.makeText(this, "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
            loadDataFromDatabase();
            xoadulieu();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi thêm dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sua_nv() {
        int selectedId;
        try {
            selectedId = Integer.parseInt(edt_ma_nv.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng chọn nhân viên hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        String tennv = edt_ten.getText().toString();
        String diachi = edt_diachi.getText().toString();
        String sdt = edt_sdt.getText().toString();
        String email = edt_email.getText().toString();
        String ngaysinh = edt_ngaysinh.getText().toString();

        if (tennv.isEmpty() || diachi.isEmpty() || sdt.isEmpty() || email.isEmpty() || ngaysinh.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin nhân viên", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String sql = "UPDATE nhanvien SET ten_nv = ?, dia_chi = ?, sdt = ?, email = ?, ngay_sinh = ? WHERE ma_nv = ?";
            mydatabase.execSQL(sql, new Object[]{tennv, diachi, sdt, email, ngaysinh, selectedId});
            Toast.makeText(this, "Cập nhật thông tin nhân viên thành công", Toast.LENGTH_SHORT).show();
            loadDataFromDatabase();
            xoadulieu();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi cập nhật dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void xoanv() {
        if (btn_xoa.getTag() == null) {
            Toast.makeText(this, "Vui lòng chọn một nhân viên để xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = (Integer) btn_xoa.getTag();
        int deletedRows = mydatabase.delete("nhanvien", "ma_nv = ?", new String[]{String.valueOf(selectedId)});

        if (deletedRows > 0) {
            Toast.makeText(this, "Xóa nhân viên thành công", Toast.LENGTH_SHORT).show();
            loadDataFromDatabase();
            xoadulieu();
        } else {
            Toast.makeText(this, "Không tìm thấy nhân viên cần xóa", Toast.LENGTH_SHORT).show();
        }
    }
    private void timkiemnv(String keyword) {
        myList.clear();
        idList.clear();
        Cursor c = mydatabase.rawQuery("SELECT * FROM nhanvien WHERE ten_nv LIKE ?", new String[]{"%" + keyword + "%"});
        if (c.moveToFirst()) {
            do {
                String data = "MA_NV: " + c.getInt(0) +
                        " - Tên nhân viên: " + c.getString(1) +
                        " - Địa chỉ: " + c.getString(2) +
                        " - Số ĐT: " + c.getString(3) +
                        " - Email: " + c.getString(4) +
                        " - Ngày sinh: " + c.getString(5);
                myList.add(data);
                idList.add(c.getInt(0));
            } while (c.moveToNext());
        }
        c.close();
        myAdapter.notifyDataSetChanged();
    }

    private void xoadulieu() {
        edt_ten.setText("");
        edt_diachi.setText("");
        edt_sdt.setText("");
        edt_email.setText("");
        edt_ngaysinh.setText("");
        edt_ma_nv.setText("");
    }
}
