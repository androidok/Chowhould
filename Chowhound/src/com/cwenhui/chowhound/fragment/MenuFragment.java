package com.cwenhui.chowhound.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwenhui.chowhound.adapter.MenuAdapter;
import com.cwenhui.chowhound.adapter.PinnedHeaderListViewAdapter;
import com.cwenhui.chowhound.bean.GoodsBean;
import com.cwenhui.chowhound.config.Configs;
import com.cwenhui.chowhound.ui.OrderConfirmActivity;
import com.cwenhui.chowhound.utils.HttpUtil;
import com.cwenhui.chowhound.utils.ImageFirstDisplayListener;
//import com.cwenhui.chowhound.widget.ShopCartQuickAction.onClickShopCartListener;
import com.cwenhui.chowhound.widget.ShopCartQuickAction2;
import com.cwenhui.chowhound.widget.ShopCartQuickAction2.onClickShopCartListener;
import com.example.chowhound.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MenuFragment extends Fragment implements OnItemClickListener, OnScrollListener, OnClickListener, onClickShopCartListener{
	private final String TAG = "MenuFragment";
	private View view;
	public static int mPosition; 					  // 用于记录左侧分类列表被点击的item的位置，在ShopAdapter中的convert方法(相当于getView方法)中用来判断是否被点击的item
	private List<String> classifyCatalog;
	private Map<String, List<GoodsBean>> rightGoods;
	private ListView classify;
	private PinnedHeaderListView headerListView;
	private MenuAdapter menuAdapter;
	private PinnedHeaderListViewAdapter headerListViewAdapter;
	private boolean isScroll = true;
	private int shopId;
	private Button submit;
	private ShopCartQuickAction2 quickAction2;
	private ImageView shopCart;
	private TextView sum;
	private TextView buyNum;
	private LinearLayout bottomBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_menu, container, false);
		initData();
		initView();
		return view;
	}

	private void initData() { 
		shopId = getArguments().getInt("shopId");
		classifyCatalog = new ArrayList<String>();
		rightGoods = new HashMap<String, List<GoodsBean>>();
		menuAdapter = new MenuAdapter(getActivity(), classifyCatalog, R.layout.item_fragment_menu_classify_name);
		headerListViewAdapter = new PinnedHeaderListViewAdapter(getActivity(), classifyCatalog, rightGoods, 
				(TextView)view.findViewById(R.id.tv_fragment_menu_sum), (TextView)view.findViewById(R.id.tv_fragment_menu_buy_num));

		getGoods();				//从服务器获取商品数据
	}

	private void initView() {
		submit = (Button) view.findViewById(R.id.btn_fragment_menu_submit);
		classify = (ListView) view.findViewById(R.id.fragment_menu_classify);
		headerListView = (PinnedHeaderListView) view.findViewById(R.id.fragment_menu_goods);
		shopCart = (ImageView) view.findViewById(R.id.iv_fragment_menu_shop_car);
		sum = (TextView) view.findViewById(R.id.tv_fragment_menu_sum);
		buyNum = (TextView) view.findViewById(R.id.tv_fragment_menu_buy_num);
		bottomBar = (LinearLayout) view.findViewById(R.id.ll_fragment_menu_bottom_bar);
		quickAction2 = new ShopCartQuickAction2(getActivity());
		classify.setAdapter(menuAdapter);
		headerListView.setAdapter(headerListViewAdapter);
		
		submit.setOnClickListener(this);
		shopCart.setOnClickListener(this);
		classify.setOnItemClickListener(this);
		headerListView.setOnScrollListener(this);
		quickAction2.setOnClickShopCartListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		isScroll = false;
		mPosition = position;														// 拿到当前位置
		menuAdapter.notifyDataSetChanged();											// 即时刷新adapter
		int rightSection = 0;
		for(int i=0;i<position;i++){
			rightSection += headerListViewAdapter.getCountForSection(i)+1;
		}
		headerListView.setSelection(rightSection); 
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(isScroll){
			for (int i = 0; i < classify.getChildCount(); i++)
			{
				//根据firstVisibleItem(当前第一个可见item的position)得到当前的Section
				if (i == headerListViewAdapter.getSectionForPosition(firstVisibleItem))	
				{
					mPosition = i;							// 拿到当前位置
					menuAdapter.notifyDataSetChanged();     // 即使刷新adapter
				}
			}
		}else{
			isScroll = true;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {}
	
	/**
	 * 从服务器获取商品数据
	 */
	private void getGoods() {
		HttpUtil.get(Configs.APIGoodsTypeByShopId+shopId, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				GoodsBean bean = null;
				try {
					JSONArray array = new JSONArray(new String(data));
					for (int i = 0; i < array.length(); i++) { 											// 加载数据
						String classify = array.getJSONObject(i).getString("goodsTypeName");
						classifyCatalog.add(classify);													//将分类名加入分类名列表中
						List<GoodsBean> goods = new ArrayList<GoodsBean>();
						JSONArray goodses = new JSONArray(array.getJSONObject(i).getString("goods"));	//将该分类中的商品json转化为数组
						for (int j = 0; j < goodses.length(); j++) {
							bean = new GoodsBean();
							bean.setGoodsId(goodses.getJSONObject(j).getInt("goodsId"));
							bean.setGoodsName(goodses.getJSONObject(j).getString("goodsName"));
							bean.setPrice(goodses.getJSONObject(j).getInt("goodsPrice"));
							bean.setSalesVolume(goodses.getJSONObject(j).getInt("goodsSales"));
							bean.setGoodsImg(goodses.getJSONObject(j).getString("goodsImgPath"));
							goods.add(bean);															//将商品加入商品列表
						}
						rightGoods.put(classify, goods);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				menuAdapter.notifyDataSetChanged();
				headerListViewAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable throwable) {
				Log.v(TAG, "throwable:  " + throwable.toString());
			}
		});
	}

	@Override
	public void onDestroy() {
		ImageFirstDisplayListener.displayedImages.clear();				
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_fragment_menu_shop_car:
			List<GoodsBean> selectedGoods = headerListViewAdapter.getSelectedItem();
			if(selectedGoods.size()>0){
				quickAction2.addQuickActionItems(selectedGoods);
				quickAction2.show(bottomBar);
			}else{
				Toast.makeText(getActivity(), "未选择商品", 0).show();
			}
			break;
		case R.id.btn_fragment_menu_submit:
			if(headerListViewAdapter.getSelectedItem().size() > 0){
				Intent intent = new Intent(getActivity(), OrderConfirmActivity.class);
				Bundle bundle = new Bundle();
				//获得选中的item并传给OrderConfirmActivity
				bundle.putParcelableArrayList("selectedGoods", (ArrayList<? extends Parcelable>) headerListViewAdapter.getSelectedItem());
				bundle.putString("shopName", getArguments().getString("shopName"));
				intent.putExtras(bundle);
//				startActivity(intent);
				startActivityForResult(intent, getActivity().RESULT_FIRST_USER);
			}else{
				Toast.makeText(getActivity(), "未选择商品", 0).show();
			}
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==getActivity().RESULT_FIRST_USER && resultCode==getActivity().RESULT_OK){
			clearShopCart();
		}
	}

	private void setSumTextView(int totalMoney) {
		headerListViewAdapter.setTotalMoney(totalMoney);
		sum.setText(totalMoney+"元");
	}
	
	private void setBuyNumTextView(int buyNum){
		headerListViewAdapter.setiBuyNum(buyNum);
		this.buyNum.setText(buyNum+"");
	}

	@Override
	public void onClickDel(View view, TextView num) {
		GoodsBean bean = (GoodsBean)view.getTag();
		List<GoodsBean> selected = headerListViewAdapter.getSelectedItem();
		int selectedNum = bean.getSelectedNum();
		bean.setSelectedNum(--selectedNum); 
		setSumTextView(headerListViewAdapter.getTotalMoney()-bean.getPrice());
		setBuyNumTextView(headerListViewAdapter.getiBuyNum()-1);
		if(selectedNum == 0){
			quickAction2.removeItem(selected.indexOf(bean), bottomBar);
			selected.remove(selected.indexOf(bean));
		}else{
			num.setText(selectedNum+"");
			selected.set(selected.indexOf(bean), bean);
		}
		updateView(bean);
	}
	
	@Override
	public void onClickAdd(View view, TextView num) {
		GoodsBean bean = (GoodsBean)view.getTag();
		List<GoodsBean> selected = headerListViewAdapter.getSelectedItem();
		int selectedNum = bean.getSelectedNum();
		bean.setSelectedNum(++selectedNum);
		setSumTextView(headerListViewAdapter.getTotalMoney()+bean.getPrice());
		setBuyNumTextView(headerListViewAdapter.getiBuyNum()+1);
		num.setText(selectedNum+"");
		selected.set(selected.indexOf(bean), bean);
		updateView(bean);
	}
	

	@Override
	public void onClickClear(View view) {
		clearShopCart();
	}

	/**
	 * 清空购物车
	 */
	private void clearShopCart() {
		quickAction2.removeAll(bottomBar);
		List<GoodsBean> selected = headerListViewAdapter.getSelectedItem();
		for (GoodsBean goodsBean : selected) {
			goodsBean.setSelectedNum(0); 
			updateView(goodsBean);
		}
		selected.clear();
		setSumTextView(0);
		setBuyNumTextView(0);
	}
	/**
	 * 获取被改变的数据进行局部更新
	 * @param bean
	 */
	public void updateView(GoodsBean bean){
		String key = null;
		int section = 0, itemPosition = 0, viewPosition = 0;
		List<GoodsBean> value;
		
		//获取被修改的item的位置(包括数据位置和视图位置)
		for (Entry<String, List<GoodsBean>> entry : rightGoods.entrySet()) {
			key = entry.getKey();						//取map的key
			value = entry.getValue();					//取map的value
			if(value.contains(bean)){ 					//如果value中包含需要更新的数据,则获取待更新数据在value(List)中的position
				itemPosition = value.indexOf(bean);
				viewPosition = itemPosition;
				break;
			}
		}
		section = classifyCatalog.indexOf(key);			//根据key得到section
		viewPosition++;									//因为用的是PinnedHeaderListView,header也要占有一个position,所以View的position等于数据的position+1
		for(int i=0; i<section; i++){					//view的正确position=当前section的position+1(因为header占一个position)再加上之前的所有section的所有item数
			viewPosition += headerListViewAdapter.getCountForSection(i)+1;		
		}
		
		//得到第一个可显示控件的位置，
		int firstVisiblePosition = headerListView.getFirstVisiblePosition();
		int lastVisiblePosition = headerListView.getLastVisiblePosition();
		//只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
		if (viewPosition - firstVisiblePosition >= 0 && viewPosition-lastVisiblePosition <= 0) {
			//得到要更新的item的view
			View view = headerListView.getChildAt(viewPosition - firstVisiblePosition);
			//调用adapter更新界面
			headerListViewAdapter.updateView(view, section, itemPosition);		//view的position-1对应相应数据的position
		}
	}
	
}
