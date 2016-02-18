package com.xiaobukuaipao.vzhi.util;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.xiaobukuaipao.vzhi.R;

public class ChoiceItemUtil {
	
	
	public static ListView getMutipleChoiceListview(Context context, final boolean[] checks , final SparseArray<String> items ,final OnItemClickListener itemClickListener){
		ListView listView = new ListView(context);
		listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				checks[position] = !checks[position];
				if(view instanceof CheckedTextView){
					CheckedTextView checkedTextView = (CheckedTextView) view;
					checkedTextView.setChecked(checks[position]);
				}
				
				if(itemClickListener != null){
					itemClickListener.onItemClick(parent, view, position, id);
				}
			}
		});
		
		listView.setAdapter(new ArrayAdapter<String>(context, R.layout.item_suggest_selection){
			
			@Override
			public int getCount() {
				return items.size();
			}
			
			
			@Override
			public String getItem(int position) {
				return items.valueAt(position);
			}

			@Override
			public int getPosition(String item) {
				return items.indexOfValue(item);
			}


			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				if(view instanceof CheckedTextView){
					CheckedTextView checkedTextView = (CheckedTextView) view;
					checkedTextView.setText(getItem(position));
					checkedTextView.setChecked(checks[position]);
				}
				return view;
			}
		});
		return listView;
	}
	
	
	public static ListView getSingleChoiceListview(Context context, final SparseArray<String> items ,final OnItemClickListener itemClickListener){
		ListView listView = new ListView(context);
		listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(view instanceof CheckedTextView){
					CheckedTextView checkedTextView = (CheckedTextView) view;
					checkedTextView.setChecked(checkedTextView.isChecked());
				}
				
				if(itemClickListener != null){
					itemClickListener.onItemClick(parent, view, position, id);
				}
			}
		});
		
		listView.setAdapter(new ArrayAdapter<String>(context, R.layout.item_suggest_selection){
			
			@Override
			public int getCount() {
				return items.size();
			}
			
			
			@Override
			public String getItem(int position) {
				return items.valueAt(position);
			}

			@Override
			public int getPosition(String item) {
				return items.indexOfValue(item);
			}


			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				if(view instanceof CheckedTextView){
					CheckedTextView checkedTextView = (CheckedTextView) view;
					checkedTextView.setText(getItem(position));
				}
				return view;
			}
		});
		return listView;
	}
}
