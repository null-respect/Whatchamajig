package org.dimigo.whatchamajig;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CustomListViewAdapter2 extends ArrayAdapter {

    DBManager manager;
    Context context;
    private SparseBooleanArray selectedListItemsIds;
    List multipleSelectionList;

    public CustomListViewAdapter2(Context context, int resourceId, List items) {
        super(context, resourceId, items);
        this.context = context;
        selectedListItemsIds = new SparseBooleanArray();
        this.multipleSelectionList = items;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView title;
        TextView id;
        TextView date;
        FloatingActionButton btn_sea, btn_del;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ListRowModel2 rowItem = (ListRowModel2) getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_last, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.id = (TextView) convertView.findViewById(R.id.id);

            holder.btn_sea = (FloatingActionButton)convertView.findViewById(R.id.btn_sea);
            holder.btn_del = (FloatingActionButton)convertView.findViewById(R.id.btn_del);

            holder.btn_sea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    View oParentView = (View)v.getParent();
                    TextView text = (TextView) oParentView.findViewById(R.id.title);

                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_WEB_SEARCH);
                    i.putExtra(SearchManager.QUERY, text.getText().toString());
                    context.startActivity(i);
                }
            });

            holder.btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager = DBManager.getInstance(context);
                    manager.open();

                    View oParentView = (View)v.getParent();
                    TextView text = (TextView) oParentView.findViewById(R.id.id);

                    String sql = "DELETE FROM " + manager.TABLE_NAME
                            + " WHERE id = " + text.getText().toString();

                    boolean result = manager.execSql(sql);


                    ((LastActivity)LastActivity.CONTEXT).onStart();
//                    Intent intent = new Intent(context, LastActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

//                    context.startActivity(intent);

                    manager.close();

                }
            });

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.title.setText(rowItem.getTitle());
        holder.date.setText(rowItem.getDate());
        holder.id.setText(rowItem.getId());

        return convertView;
    }


    public void remove(ListRowModel2 object) {
        multipleSelectionList.remove(object);
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedListItemsIds.get(position));
    }

    public void removeSelection() {
        selectedListItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            selectedListItemsIds.put(position, value);
        else
            selectedListItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedListItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectedListItemsIds;
    }

}