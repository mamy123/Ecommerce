package com.example.gmithighracks.ecommerce.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.example.gmithighracks.ecommerce.EmployeeHomeActivity;
import com.example.gmithighracks.ecommerce.R;

import java.util.ArrayList;

public class DropDownListAdapter extends BaseAdapter {

    private ArrayList<Ability> mListItems;
    private LayoutInflater mInflater;
    private TextView mSelectedItems;
    private static int selectedCount = 0;
    private static String firstSelected = "";
    private ViewHolder holder;
    private static String selected = "";	//shortened selected values representation

    public static String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        DropDownListAdapter.selected = selected;
    }

    public DropDownListAdapter(Context context, ArrayList<Ability> items,
                               TextView tv) {
        mListItems = new ArrayList<Ability>();
        mListItems.addAll(items);
        mInflater = LayoutInflater.from(context);
        mSelectedItems = tv;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mListItems.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drop_down_list_row, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.DropDownListSelectOption);
            holder.chkbox = (CheckBox) convertView.findViewById(R.id.DropDownListCheckbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(mListItems.get(position).getName());

        final int position1 = position;

        //whenever the checkbox is clicked the selected values textview is updated with new selected values
        holder.chkbox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               setText(position1);
            }
        });
        Log.d("WAHT", "checksele:" + position);
        if(EmployeeHomeActivity.checkSelected[position])
            holder.chkbox.setChecked(true);
        else
            holder.chkbox.setChecked(false);
        return convertView;
    }


    /*
     * Function which updates the selected values display and information(checkSelected[])
     * */
    private void setText(int position1){
        if (!EmployeeHomeActivity.checkSelected[position1]) {
            EmployeeHomeActivity.checkSelected[position1] = true;
            selectedCount++;
        } else {
            EmployeeHomeActivity.checkSelected[position1] = false;
            selectedCount--;
        }

        if (selectedCount == 0) {
          //  mSelectedItems.setText(R.string.select_string);
        } else if (selectedCount == 1) {
            for (int i = 0; i < EmployeeHomeActivity.checkSelected.length; i++) {
                if (EmployeeHomeActivity.checkSelected[i] == true) {
                    firstSelected = mListItems.get(i).getName();
                    break;
                }
            }
          //  mSelectedItems.setText(firstSelected);
            setSelected(firstSelected);
        } else if (selectedCount > 1) {
            for (int i = 0; i < EmployeeHomeActivity.checkSelected.length; i++) {
                if (EmployeeHomeActivity.checkSelected[i] == true) {
                    firstSelected = mListItems.get(i).getName();
                    break;
                }
            }
            mSelectedItems.setText(firstSelected + " & "+ (selectedCount - 1) + " more");
            setSelected(firstSelected + " & "+ (selectedCount - 1) + " more");
        }
    }

    private class ViewHolder {
        TextView tv;
        CheckBox chkbox;
    }
}
