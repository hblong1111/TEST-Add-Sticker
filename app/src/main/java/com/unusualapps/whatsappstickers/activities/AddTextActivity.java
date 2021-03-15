package com.unusualapps.whatsappstickers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.utils.Common;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class AddTextActivity extends AppCompatActivity {
    private PhotoEditorView mPhotoEditorView ;

    String string;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        img  = findViewById(R.id.img);

        string = getIntent().getStringExtra(Common.CODE_PUT_PACK);

        img.setImageURI(Uri.parse(string));

    }
}