package org.dimigo.whatchamajig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LastActivity extends AppCompatActivity {

    private Animation fab_open, fab_close, fab_rotate1, fab_rotate2, fab_rotate_close , left_trans2, right_trans2,  open, close, left_trans_fast;
    private Boolean isFabOpen = false;
    private Boolean isDelOpen = false;
    private FloatingActionButton fab, fab_add, fab_del, fab_delete, fab_cancel;
    public final static int Overlay_REQUEST_CODE = 251;
    private Activity mActivity;
    public static Context CONTEXT;

    ListView listView;
    List list = new ArrayList<>();
    DBManager manager;

    CustomListViewAdapter2 customListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);

        mActivity = this;
        CONTEXT = this;

        getSupportActionBar().setTitle("최근 메모");
        listView = findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //데이터 상세조회 액티비티 띄워주기
                if(!isDelOpen) {

                    ListRowModel2 i = (ListRowModel2) list.get(position);

                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);

                    intent.putExtra("id", i.getId());
                    intent.putExtra("title", i.getTitle());

                    startActivity(intent);
                }
                else{
                    listView.setItemChecked(position, true);
                }
            }
        });

        manager = DBManager.getInstance(this);
        manager.open();
    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        selectAll();
    }

    @Override
    protected void onDestroy() {
        String sql = "update " + manager.TABLE_NAME
                +" set recent = 0 WHERE recent = 1";

        boolean result = manager.execSql(sql);

        manager.close();
        super.onDestroy();

        Log.d("TAG", "des");
    }
    private void selectAll() {
        // 데이터 목록 조회
        String sql = "select id, title, date" + " from " + DBManager.TABLE_NAME + " WHERE recent = 1 order by id desc";

        Cursor cursor = manager.rawQuery(sql);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();

            long now = System.currentTimeMillis();
            Date dateNow = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String getTime = sdf.format(dateNow);
            String date = cursor.getString(2);

            String dateStr;
            if (date.substring(0, 10).equals(getTime)) {
                dateStr = date.substring(11, 16);
            } else {
                dateStr = date.substring(0, 10);
            }


            String titleStr =  cursor.getString(1);
            if (titleStr.length() > 15) {
                titleStr = titleStr.substring(0, 15) + "...";
            }

            ListRowModel2 item = new ListRowModel2(cursor.getInt(0) + "",titleStr, dateStr);

            list.add(item);

        }

        cursor.close();

        customListViewAdapter = new CustomListViewAdapter2(this, R.layout.list_item_last, list);
        listView.setAdapter(customListViewAdapter);
    }
}
