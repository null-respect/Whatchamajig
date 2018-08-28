package org.dimigo.whatchamajig;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    EditText title, content;
    DBManager manager;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setTitle("메모 수정");
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF339999));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        title = findViewById(R.id.editText);
        content = findViewById(R.id.editText2);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        manager = DBManager.getInstance(this);

        String sql = "select title, content"
                + " from " + DBManager.TABLE_NAME
                + " WHERE id = " + id ;
        Cursor cursor = manager.rawQuery(sql);
        cursor.moveToNext();
        title.setText(cursor.getString(0));
        content.setText(cursor.getString(1));

//        title.setText(intent.getStringExtra("title"));
//        content.setText(intent.getStringExtra("content"));

    }

//    public void onDeleteClicked(View view) {
//        String sql = "delete from " + manager.TABLE_NAME +
//                " WHERE id=" + id;
//
//        boolean result = manager.execSql(sql);
//        if(result) {
//            finish();
//        }else{
//            Toast.makeText(this,
//                    "데이터 삭제 실패",
//                    Toast.LENGTH_LONG).show();
//        }
//    }

    public void update() {
        String sql = "update " + manager.TABLE_NAME
                +" set title = '" +  title.getText().toString() + "'"
                +" , content = '" + content.getText().toString() + "'"
                +" WHERE id=" + id;

        boolean result = manager.execSql(sql);

        if(result) {
            Log.d("TAG", "피니쉬 전");
            finish();
            Log.d("TAG", "피니쉬 후");
        }else{
            Toast.makeText(this,
                    "데이터 수정 실패",
                    Toast.LENGTH_LONG).show();
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
            update();
        }
        return false;
    }
}