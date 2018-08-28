package org.dimigo.whatchamajig;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class insertActivity extends AppCompatActivity {

    EditText title, content;
    DBManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        getSupportActionBar().setTitle("새 메모");
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF339999));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        title = findViewById(R.id.editText);
        content = findViewById(R.id.editText2);
//        Button button = findViewById(R.id.btn_save);

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 데이터 저장
//                insert();
//            }
//        });

        manager = DBManager.getInstance(this);
    }

    private void insert() {
        // sql
        String s_title = title.getText().toString();
        String s_content = content.getText().toString();

        String sql = "insert into " + manager.TABLE_NAME + "(title, content, recent) values (" + "'" + s_title + "', " + "'" + s_content + "'" + " , 0)";

        boolean result = manager.execSql(sql);

        if (result) {
            Intent intent = new Intent(this, BootService.class);
            intent.putExtra("titleText", s_title);
            startService(intent);
            finish();
        } else {
            Toast.makeText(this, "데이터 저장 실패", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.insert_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if(id==R.id.action_button){
            insert();
        }
        return false;
    }
}