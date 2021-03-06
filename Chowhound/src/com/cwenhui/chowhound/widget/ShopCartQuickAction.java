package com.cwenhui.chowhound.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cwenhui.chowhound.bean.ShopCartActionItem;
import com.example.chowhound.R;

public class ShopCartQuickAction extends CustomQuickAction<ShopCartActionItem> {
	private static final String TAG = "ShopCartQuickAction";
	private onClickShopCartListener shopCartListener;

	public ShopCartQuickAction(Context context) {
		super(context);
	}

	public void setOnClickShopCartListener(onClickShopCartListener shopCartListener) {
		this.shopCartListener = shopCartListener;
	}

	@Override
	public void addQuickActionItem(ShopCartActionItem item) {
		mItems.add(item);

		View container = mInflater.inflate(R.layout.item_shop_cart_quick_action, null);

		TextView goodsName = (TextView) container.findViewById(R.id.tv_item_shop_cart_quickaction_name);
		TextView price = (TextView) container.findViewById(R.id.tv_item_shop_cart_quickaction_price);
		TextView selectedNum = (TextView) container.findViewById(R.id.tv_item_shop_cart_quickaction_num);
		Button add = (Button) container.findViewById(R.id.btn_item_shop_cart_quickaction_add);
		Button del = (Button) container.findViewById(R.id.btn_item_shop_cart_quickaction_del);

		if(item != null){
			goodsName.setText(item.getGoodsName());
			price.setText(item.getPrice());
			selectedNum.setText(item.getSelectedNum());
			del.setTag(item);
		}else{
			container.setVisibility(View.GONE);
		} 

		final int mCurrentActionId = item.getActionId();
		setOnShopCartClickListener(add, mCurrentActionId, selectedNum);
		setOnShopCartClickListener(del, mCurrentActionId, selectedNum);

		mQuickActionLayout.addView(container);
	}

	private void setOnShopCartClickListener(Button add, final int mCurrentActionId, final TextView selectedNum) {
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(shopCartListener != null){
					if(v.getId() == R.id.btn_item_shop_cart_quickaction_add){
						shopCartListener.onClickAdd(mCurrentActionId, selectedNum);
					}else{
						shopCartListener.onClickDel(v, /*mCurrentActionId,*/ selectedNum);
					}
				}
			}
		});
	}

	@Override
	public void initQuickAction(Context context) {
		mRootView = (ViewGroup) mInflater.inflate(R.layout.layout_shop_cart_quickaction, null);
		mQuickActionLayout = (LinearLayout) mRootView.findViewById(R.id.shop_cart_layout_quickaction);
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setContentView(mRootView);
	}
	
	public void removeActionItem(View view, View anchor){ 
		mQuickActionLayout.removeView(view);
		mItems.remove((ShopCartActionItem)view.getTag());
		dismiss();															//先让quickaction消失,再重新显示,否则被移出处会有一块空白
		Direction showDirection = computeDisplayPosition(anchor);			// 位置
		int[] locations = preShow(anchor, showDirection);					// 根据位置，显示箭头
		if (locations != null) {											// 显示PopupWindow
			showAtLocation(anchor, Gravity.NO_GRAVITY, locations[0], locations[1]);
		}

	}

	public void removeAllActionItem(){
		mQuickActionLayout.removeAllViews();								//除了将数据清除之外还要将mQuickActionLayout里面的子视图清除
		mItems.clear();
	}
	
	public interface onClickShopCartListener{
		public void onClickDel(View view, /*int mCurrentActionId,*/ TextView num);
		public void onClickAdd(int mCurrentActionId, TextView num);
	}
}
