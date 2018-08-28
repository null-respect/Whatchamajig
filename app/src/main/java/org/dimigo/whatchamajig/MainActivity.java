package org.dimigo.whatchamajig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.PaintDrawable;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Animation fab_open, fab_close, fab_rotate1, fab_rotate2, fab_rotate_close , left_trans2, right_trans2,  open, close, left_trans_fast;
    private Boolean isFabOpen = false;
    private Boolean isDelOpen = false;
    private FloatingActionButton fab, fab_add, fab_del, fab_delete, fab_cancel;
    public final static int Overlay_REQUEST_CODE = 251;
    private Activity mActivity;
    private final static int MAX_LENGTH = 14;
    private Boolean recentView = false;


    ListView listView;
    List list = new ArrayList<>();
    DBManager manager;

    CustomListViewAdapter customListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_rotate1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate1);
        fab_rotate2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate2);
        fab_rotate_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_close);
        left_trans2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_trans2);
        right_trans2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_trans2);
        open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.open);
        close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close);
        left_trans_fast = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_trans_fast);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_del = (FloatingActionButton) findViewById(R.id.fab_del);
        fab_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fab_cancel = (FloatingActionButton) findViewById(R.id.fab_cancel);

        fab.setOnClickListener(this);
        fab_add.setOnClickListener(this);
        fab_del.setOnClickListener(this);
        fab_delete.setOnClickListener(this);
        fab_cancel.setOnClickListener(this);

        listView = findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //데이터 상세조회 액티비티 띄워주기
                if(!isDelOpen) {

                    ListRowModel i = (ListRowModel) list.get(position);

                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);

                    intent.putExtra("id", i.getId());
//                    intent.putExtra("title", i.getTitle());
//                    intent.putExtra("content", i.getContent());

                    startActivity(intent);
                }
                else{
                    listView.setItemChecked(position, true);
                }
            }
        });

        checkDrawOverlayPermission();

        manager = DBManager.getInstance(this);
        manager.open();

        startService(new Intent(getApplicationContext(), lockService.class));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                break;
            case R.id.fab_add:
                anim();
                startActivity(new Intent(getApplicationContext(), insertActivity.class));
                break;
            case R.id.fab_del:
                anim();
                anim2();
                break;
            case R.id.fab_delete:
                delete();
                break;
            case R.id.fab_cancel:
                list.clear();
                selectAll();
                anim2();
                break;
        }
    }

    private void delete() {
        String sql = "DELETE FROM " + manager.TABLE_NAME
                + " WHERE ";
        int cnt = 0;
        SparseBooleanArray selected = customListViewAdapter.getSelectedIds();
        // Captures all selected ids with a loop
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {

                ListRowModel selectedListItem = (ListRowModel) customListViewAdapter.getItem(selected.keyAt(i));

                if(cnt == 0) {
                    cnt ++;
                    sql = sql + "id = " + selectedListItem.getId() + " ";
                }
                else sql = sql +  " OR id = " + selectedListItem.getId();
            }
        }

        boolean result = manager.execSql(sql);

        if(result) {
            list.clear();
            selectAll();
            anim2();
        }else{
            if(cnt!=0) {
                Toast.makeText(this, "삭제할 항목을 선택하세요.", Toast.LENGTH_LONG);
            }
            else {
                Toast.makeText(this, "데이터 삭제 실패", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void anim() {
        if (isFabOpen) {
            fab_add.startAnimation(fab_close);
            fab_del.startAnimation(fab_close);
            fab.startAnimation(fab_rotate2);

            fab_add.setClickable(false);
            fab_del.setClickable(false);
            isFabOpen = false;
        } else {
            fab_add.startAnimation(fab_open);
            fab_del.startAnimation(fab_open);
            fab.startAnimation(fab_rotate1);

            fab_add.setClickable(true);
            fab_del.setClickable(true);
            isFabOpen = true;
        }
    }

    public void anim2(){
        if(isDelOpen){
            fab_delete.startAnimation(close);
            fab_cancel.startAnimation(close);
            fab.startAnimation(open);

            fab_delete.setClickable(false);
            fab_cancel.setClickable(false);
            fab.setClickable(true);
            isDelOpen = false;
        }
        else{ // 나타남

            fab_delete.startAnimation(open);
            fab_cancel.startAnimation(open);
            fab.startAnimation(fab_rotate_close);

            fab_delete.setClickable(true);
            fab_cancel.setClickable(true);
            fab.setClickable(false);
            isDelOpen = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();

        if(!recentView) {
            recentView = true;
            last();
        }
        selectAll();
    }

    private void last() {
        String sql = "select id"
                + " from " + DBManager.TABLE_NAME
                + " WHERE recent = 1 ";

        Cursor cursor = manager.rawQuery(sql);

        if(cursor.getCount()>0) {
            Log.d("TAG", "액티비티");
            startActivity(new Intent(this, LastActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        manager.close();
        recentView = false;
        super.onDestroy();

    }
    private void selectAll() {
        // 데이터 목록 조회
        String sql = "select id, title, content, date"
                + " from " + DBManager.TABLE_NAME
                + " order by id desc";

        Cursor cursor = manager.rawQuery(sql);

        for(int i = 0; i<cursor.getCount(); i++){
            cursor.moveToNext();

            long now = System.currentTimeMillis();
            Date dateNow = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String getTime = sdf.format(dateNow);
            String date = cursor.getString(3);

            String dateStr;
            if(date.substring(0,10).equals(getTime)){
                dateStr = date.substring(11,16);
            }
            else{
                dateStr = date.substring(0,10);
            }

            String titleStr = cursor.getString(1);
            String contentStr = cursor.getString(2);

            if(titleStr!=null) {
                if (titleStr.length() > MAX_LENGTH) {
                    titleStr = titleStr.substring(0, MAX_LENGTH) + "...";
                }
            }
            if(contentStr!=null) {
                if (contentStr.length() > (MAX_LENGTH + 5)) {
                    contentStr = contentStr.substring(0, MAX_LENGTH + 5) + "...";
                }
            }

            ListRowModel item =  new ListRowModel(cursor.getInt(0) +""
                    , titleStr, contentStr, dateStr);

            list.add(item);

        }


        cursor.close();

        customListViewAdapter = new CustomListViewAdapter(this, R.layout.list_item, list);
        listView.setAdapter(customListViewAdapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if(isDelOpen) {
                    final int checkedCount = listView.getCheckedItemCount();
                    // Set the CAB title according to total checked items
                    mode.setTitle(checkedCount + "개 선택됨");
                    // Calls toggleSelection method from ListViewAdapter Class
                    customListViewAdapter.toggleSelection(position);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.delete_menu_option, menu);
                    if(isDelOpen){
                        return true;
                    }
                    else{
                        return false;
                    }
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
               /* switch (item.getItemId()) {
                    case R.id.delete:
                        delete();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }*/
               return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                customListViewAdapter.removeSelection();
            }
        });
    }



    public void checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(mActivity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
//                startActivity(intent);
                startActivityForResult(intent, Overlay_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Overlay_REQUEST_CODE: {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(mActivity)) {
                        startService(new Intent(getApplicationContext(), BootService.class));
                        Log.d("TAG", "11111");

                    }else{
                        Toast.makeText(getApplicationContext()
                                , "권한을 설정해야 팝업 메모 기능을 사용할 수 있습니다."
                                , Toast.LENGTH_LONG).show();
                        Log.d("TAG", "2222");
                    }
                }
                break;
            }
        }
    }

}
