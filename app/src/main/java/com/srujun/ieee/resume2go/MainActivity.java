package com.srujun.ieee.resume2go;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.srujun.ieee.resume2go.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 2);
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        if(resultcode == RESULT_OK) {
            if(requestcode == 2) {
                Uri selectedImageUri = data.getData();
                Cursor fileCursor = getContentResolver().query(selectedImageUri, null, null, null, null);

                TextView fileNameLabel = (TextView) findViewById(R.id.file_name_textview);
                int nameIndex = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                fileCursor.moveToFirst();
                fileNameLabel.setText(fileCursor.getString(nameIndex));
                fileCursor.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
