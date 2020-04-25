package com.example.meetchrysallis.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetchrysallis.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;


public class PdfViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        File file = (File) getIntent().getSerializableExtra("file");
        String extension = getIntent().getExtras().getString("ext");
        Toast.makeText(this, extension + "\n\n" +  file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        PDFView pdfView     = findViewById(R.id.pdfViewer_pdfView);
        ImageView imageView = findViewById(R.id.pdfViewer_imageView);

        if (extension.equals("pdf")){
            imageView.setVisibility(View.GONE);
            pdfView.fromFile(file)
                .load();
        } else if (extension.equals("png")){
            pdfView.setVisibility(View.GONE);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
    }
}
