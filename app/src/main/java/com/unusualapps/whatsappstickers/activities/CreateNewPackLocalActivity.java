package com.unusualapps.whatsappstickers.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.unusualapps.whatsappstickers.R;
import com.unusualapps.whatsappstickers.db.DatabaseModule;
import com.unusualapps.whatsappstickers.model.Pack;
import com.unusualapps.whatsappstickers.model.db_local.PackLocal;
import com.unusualapps.whatsappstickers.utils.Common;

public class CreateNewPackLocalActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton btnClose;
    private TextView btnCreate;
    private EditText edtName;
    private EditText edtAuthor;
    private String txtError;
    private TextView tvTitle;


    private boolean isEdit;

    private PackLocal packLocal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_pack_local);


        btnClose = findViewById(R.id.btnClose);
        btnCreate = findViewById(R.id.btnCreate);
        edtName = findViewById(R.id.edtName);
        edtAuthor = findViewById(R.id.edtAuthor);
        tvTitle = findViewById(R.id.tvTitle);


        btnClose.setOnClickListener(this);
        btnCreate.setOnClickListener(this);

        packLocal = (PackLocal) getIntent().getSerializableExtra(Common.CODE_PUT_PACK);

        isEdit = packLocal != null;

        if (isEdit) {
            tvTitle.setText("Edit Pack");
            btnCreate.setText("Save");

            edtAuthor.setText(packLocal.getAuthor());
            edtName.setText(packLocal.getName());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                onBackPressed();
                break;
            case R.id.btnCreate:
                createPack();
                break;
        }
    }

    private void createPack() {
        if (!checkValidate()) {
            Toast.makeText(this, txtError, Toast.LENGTH_SHORT).show();
            return;
        }
        String name = edtName.getText().toString();
        String author = edtAuthor.getText().toString();


        if (isEdit) {
            this.packLocal.setName(name);
            this.packLocal.setAuthor(author);
            DatabaseModule.getInstance(getApplication()).packDao().update(this.packLocal);
            onBackPressed();
            finish();
            return;
        }

        PackLocal packLocal = new PackLocal(0, name, author);
        long idPack = DatabaseModule.getInstance(getApplication()).packDao().insert(packLocal);


        packLocal.setId((int) idPack);

        startActivity(new Intent(this, PackLocalDetailActivity.class).putExtra(Common.CODE_PUT_PACK, packLocal));

        this.finish();

    }

    private boolean checkValidate() {
        String name = edtName.getText().toString();
        String author = edtAuthor.getText().toString();
        if (name.trim().length() == 0) {
            txtError = "Enter name pack";
            return false;
        }
        if (author.trim().length() == 0) {
            txtError = "Enter author name";
            return false;
        }

        return true;
    }
}