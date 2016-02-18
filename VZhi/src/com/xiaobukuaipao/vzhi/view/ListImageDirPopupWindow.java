package com.xiaobukuaipao.vzhi.view;

import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.domain.ImageFolder;

public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFolder> {
	private ListView mListDir;

	public ListImageDirPopupWindow(int width, int height,
			List<ImageFolder> datas, View convertView)
	{
		super(convertView, width, height, true, datas);
	}

	@Override
	public void initViews()
	{
		mListDir = (ListView) findViewById(R.id.id_list_dir);
		mListDir.setAdapter(new CommonAdapter<ImageFolder>(context, mDatas, R.layout.list_dir_item) {

			@Override
			public void convert(ViewHolder viewHolder, ImageFolder item,
					int position) {
				viewHolder.setText(R.id.id_dir_item_name, item.getName());
				viewHolder.setImageByUrl(R.id.id_dir_item_image,
						item.getFirstImagePath());
				viewHolder.setText(R.id.id_dir_item_count, item.getCount() + "张");
			}
		});
	}

	public interface OnImageDirSelected
	{
		void selected(ImageFolder floder);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected)
	{
		this.mImageDirSelected = mImageDirSelected;
	}

	@Override
	public void initEvents()
	{
		mListDir.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{

				if (mImageDirSelected != null)
				{
					mImageDirSelected.selected(mDatas.get(position));
				}
			}
		});
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params)
	{
		// TODO Auto-generated method stub
	}

}
