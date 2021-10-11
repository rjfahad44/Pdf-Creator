package com.example.pdf_creator;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText name, email, number;
    Button saveButton;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        name = findViewById(R.id.NameET);
        email = findViewById(R.id.EmailET);
        number = findViewById(R.id.NumberET);

        saveButton = findViewById(R.id.SaveET);

//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        }

        if (!isCheckPermission()){
            takePermission();
        }

        saveButton.setOnClickListener(v -> {
            String Name = name.getText().toString();
            String Email = email.getText().toString();
            String Number = number.getText().toString();
            createPDF(Name, Email, Number);

        });
    }

    private boolean isCheckPermission(){
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
            //For Android 11//
            return Environment.isExternalStorageManager();
        }else {
            //For Android 10 or below//
            int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return readExternalStoragePermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void takePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 100);
            }catch (Exception e){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 100);
            }
        }else {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 100){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    if (Environment.isExternalStorageManager()){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }else {
                        takePermission();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            if (requestCode == 101){
                boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//            boolean writePer = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if (readExternalStorage){
                    Toast.makeText(this, "Permission Granted for Android 10 or below", Toast.LENGTH_SHORT).show();
                }else {
                    takePermission();
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createPDF(String Name, String Email, String Number) {

        try {
            String folderPath = Environment.getExternalStorageDirectory().toString();
            File newFolder = new File(folderPath, "/MyPDF");
            if (!newFolder.mkdirs()){
                newFolder.mkdirs();
            }

            String year = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
            String pdfName = "myPdf "+year+".pdf";
            File file = new File(newFolder, pdfName);


            PdfWriter pdfWriter = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

//            pdfDocument.setDefaultPageSize(PageSize.A4);
//            document.setMargins(0,0,0,0);
//
//            Drawable drawable = getDrawable(R.drawable.headerimg);
//            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayOutputStream);
//            byte [] bitmapData = byteArrayOutputStream.toByteArray();
//
//            ImageData imageData = ImageDataFactory.create(bitmapData);
//            Image image = new Image(imageData);

            Paragraph paragraph = new Paragraph("Test PDF").setBold().setFontSize(24).setTextAlignment(TextAlignment.CENTER);
//
            Paragraph userDataParagraph = new Paragraph("User info").setFontSize(12).setTextAlignment(TextAlignment.CENTER);

            //Current Date And Time Function//
            String currentDate = new SimpleDateFormat("EEE, d MMM, yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(new Date());

            Paragraph DateTime = new Paragraph("Date : "+ currentDate + "   " + "Time : " + currentTime).setFontSize(12).setTextAlignment(TextAlignment.CENTER);

            //Table Column No.//
            float [] width = {50f,200f, 150f, 200f};

            //Table Create//
            Table table = new Table(width);

            //Table Position Center of the page//
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);


            //Add Table Head//
            table.addCell(new Cell().add(new Paragraph("No."))).setBold().setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Name"))).setBold();
            table.addCell(new Cell().add(new Paragraph("Number"))).setBold();
            table.addCell(new Cell().add(new Paragraph("Email"))).setBold();


            //Add Table Data//
            for (int i = 1; i<=50; i++){
                table.addCell(new Cell().add(new Paragraph(i+".")));
                table.addCell(new Cell().add(new Paragraph(Name)));
                table.addCell(new Cell().add(new Paragraph(Number)));
                table.addCell(new Cell().add(new Paragraph(Email)));
            }


//            document.add(image);
            document.add(paragraph);
            document.add(DateTime);
            document.add(userDataParagraph);
            document.add(table);

            document.close();

            Toast.makeText(this, "PDF Created : " + file, Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}