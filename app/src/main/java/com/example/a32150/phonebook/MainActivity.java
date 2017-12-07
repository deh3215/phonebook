package com.example.a32150.phonebook;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView lv;
    private DB mDbHelper;
    EditText name, phone, email;
    Button btnEdit, btnDelete;
    private EditText editText1;
    private long rowId;
    private String editString1;
    private int mNoteNumber = 1;
    protected static final int MENU_INSERT = Menu.FIRST;
    protected static final int MENU_DELETE = Menu.FIRST + 1;
    protected static final int MENU_UPDATE = Menu.FIRST + 2;
    private static final int REQUEST_SMS = 1;
    //================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=(ListView)findViewById(R.id.listView);
        name = (EditText)findViewById(R.id.name);
        phone = (EditText)findViewById(R.id.phone);
        email = (EditText)findViewById(R.id.email);
        btnEdit = (Button)findViewById(R.id.btnEdit);
        btnDelete = (Button)findViewById(R.id.btnDelete);

        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        lv.setOnItemClickListener(this);
        setAdapter();


        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnEdit.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        });
    }

    private void setAdapter() {
        mDbHelper = new DB(this).open();
        fillData();
    }

    void fillData() {
        Cursor c = mDbHelper.getAll();
        startManagingCursor(c);
        SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(
                this,
                R.layout.simple_list_item_1,
                c,
                new String[]{"name", "phone", "email"},
                new int[]{R.id.text1, R.id.text2, R.id.text3},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lv.setAdapter(scAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("ID", ""+id);
        rowId = id;
        Cursor c = mDbHelper.get(rowId);
        name.setText(c.getString(c.getColumnIndex("name")));
        phone.setText(c.getString(c.getColumnIndex("phone")));
        email.setText(c.getString(c.getColumnIndex("email")));

        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    public void Click(View v)   {
        switch(v.getId())   {
            case R.id.btnAdd:
                mNoteNumber = lv.getCount() + 1;
                String _name=name.getText().toString();
                String _phone=phone.getText().toString();
                String _email=email.getText().toString();
                mDbHelper.create(_name, _phone, _email);
                fillData();
                break;
            case R.id.btnEdit:
                mDbHelper.update(rowId, name.getText().toString(), phone.getText().toString(), email.getText().toString());
                fillData();
                break;
            case R.id.btnDelete:
                mDbHelper.delete(rowId);
                fillData();
                break;
            case R.id.btnPhone:
                Intent intentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone.getText().toString()));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intentDial);
                break;
            case R.id.btnMsg:
                String number = phone.getText().toString();
                String title = getResources().getString(R.string.msg_title);
                if(!number.equals("")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
                }   else    {
                    Toast.makeText(MainActivity.this, title, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case MENU_INSERT:
                break;
            case MENU_DELETE:
                mDbHelper.delete(rowId);
                fillData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
