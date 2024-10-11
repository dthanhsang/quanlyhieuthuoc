package com.example.quanlyhieuthuoc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    // Tên và phiên bản cơ sở dữ liệu
    private static final String DATABASE_NAME = "PharmacyDB";
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Phương thức này được gọi khi cơ sở dữ liệu được tạo lần đầu
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng Thuoc, KhachHang, DonHang từ file SQL
        String createTableThuoc = "CREATE TABLE IF NOT EXISTS Thuoc (" +
                "maThuoc INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenThuoc TEXT NOT NULL, " +
                "loaiThuoc TEXT NOT NULL, " +
                "donViTinh TEXT NOT NULL, " +
                "giaBan REAL NOT NULL, " +
                "soLuong INTEGER NOT NULL);";
        db.execSQL(createTableThuoc);

        String createTableKhachHang = "CREATE TABLE IF NOT EXISTS KhachHang (" +
                "maKhachHang INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenKhachHang TEXT NOT NULL, " +
                "diaChi TEXT NOT NULL, " +
                "soDienThoai TEXT NOT NULL);";
        db.execSQL(createTableKhachHang);

        String createTableDonHang = "CREATE TABLE IF NOT EXISTS DonHang (" +
                "maDonHang INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "maKhachHang INTEGER, " +
                "ngayDatHang TEXT NOT NULL, " +
                "tongTien REAL NOT NULL, " +
                "FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang));";
        db.execSQL(createTableDonHang);

        // Thêm dữ liệu mẫu
        String insertThuoc = "INSERT INTO Thuoc (tenThuoc, loaiThuoc, donViTinh, giaBan, soLuong) " +
                "VALUES ('Paracetamol', 'Giảm đau', 'Viên', 5000, 200), " +
                "('Amoxicillin', 'Kháng sinh', 'Viên', 10000, 150), " +
                "('Vitamin C', 'Vitamin', 'Viên', 3000, 500);";
        db.execSQL(insertThuoc);
    }

    // Phương thức này được gọi khi cần nâng cấp cơ sở dữ liệu (thay đổi phiên bản)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Thuoc");
        db.execSQL("DROP TABLE IF EXISTS KhachHang");
        db.execSQL("DROP TABLE IF EXISTS DonHang");
        onCreate(db);
    }
}
