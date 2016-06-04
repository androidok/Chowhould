package com.cwenhui.chowhound.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cwenhui.chowhound.common.CommonAdapter;
import com.cwenhui.chowhound.common.ViewHolder;
import com.cwenhui.chowhound.fragment.MenuFragment;
import com.example.chowhound.R;

public class MenuAdapter extends CommonAdapter<String> {
	private final String TAG = "MenuAdapter";

	public MenuAdapter(Context context, List<String> datas, int layoutId) {
		super(context, datas, layoutId);
		
	}

	@Override
	public void convert(ViewHolder holder, String t) {
		TextView tv = holder.getView(R.id.tv_item_fragment_menu_name);
		tv.setText(t);
		if(holder.getPosition() == MenuFragment.mPosition){					//如果当前加载的item是被点击的item，则设置该item的字体颜色和背景
			tv.setTextColor(mContext.getResources().getColor(R.color.app_color));
			holder.getView(R.id.iv_fragment_menu_classify_name_bg).setVisibility(View.VISIBLE);
		}
		else{
			tv.setTextColor(mContext.getResources().getColor(R.color.black));
			holder.getView(R.id.iv_fragment_menu_classify_name_bg).setVisibility(View.GONE);
		}
	}

}
