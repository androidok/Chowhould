package com.cwenhui.chowhound.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AddGoodsAnim {
	private ViewGroup anim_mask_layout;									// 动画层,用来放在界面上跑的小图片
	private View buyNum;
	private Context context;
	
	public AddGoodsAnim(View buyNum, Context context) {
		super();
		this.buyNum = buyNum;
		this.context = context;
	}

	/**
	 * 设置购买数量动画
	 * @param v
	 * @param start_location
	 */
	public void setScalAnim(View v){
		ScaleAnimation myAnimation_Scale = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,   
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);   
        myAnimation_Scale.setInterpolator(new AccelerateInterpolator());   
        AnimationSet set = new AnimationSet(true);   
        set.addAnimation(myAnimation_Scale);   
	    set.setDuration(200);   
	    v.startAnimation(set);   
//	    v.setText(iBuyNum+"");
	}
	
	/**
	 * 设置添加商品动画
	 * @param v
	 * @param start_location
	 */
	public void setAddAnim(final ImageView v, int[] start_location) {
		anim_mask_layout = null;
		anim_mask_layout = createAnimLayout();
		anim_mask_layout.addView(v);// 把动画小球添加到动画层
		View view = addViewToAnimLayout(anim_mask_layout, v, start_location);
		final int[] end_location = new int[2];// 这是用来存储动画结束位置的X、Y坐标
		buyNum.getLocationInWindow(end_location);// shopCart是那个购物车

		// 计算位移
		int endX = 0 - start_location[0]+40;// 动画位移的X坐标
		int endY = end_location[1] - start_location[1];// 动画位移的y坐标 
		TranslateAnimation translateAnimationX = new TranslateAnimation(0, endX, 0, 0);
		translateAnimationX.setInterpolator(new LinearInterpolator());
		translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
		translateAnimationX.setFillAfter(true);

		TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
				0, endY);
		translateAnimationY.setInterpolator(new AccelerateInterpolator());
		translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
		translateAnimationX.setFillAfter(true);

		AnimationSet set = new AnimationSet(false);
		set.setFillAfter(false);
		set.addAnimation(translateAnimationY);
		set.addAnimation(translateAnimationX);
		set.setDuration(800);// 动画的执行时间
		view.startAnimation(set);
		
		// 动画监听事件
		set.setAnimationListener(new AnimationListener() {
			// 动画的开始
			@Override
			public void onAnimationStart(Animation animation) {
				v.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			// 动画的结束
			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.GONE);
				setScalAnim(buyNum);
			}
		});
	}
	
	/**
	 * 将动画控件在动画层上和被点击的按钮重合
	 * @param anim_mask_layout2
	 * @param v
	 * @param start_location
	 * @return
	 */
	private View addViewToAnimLayout(ViewGroup anim_mask_layout2, /*Image*/View view,
			int[] start_location) {
		int x = start_location[0];
		int y = start_location[1];
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = x;
		lp.topMargin = y;
		view.setLayoutParams(lp);
		return view;
	}

	/**
	 * 创建动画层
	 * @return
	 */
	private ViewGroup createAnimLayout() {
		ViewGroup rootView = (ViewGroup) ((Activity)context).getWindow().getDecorView();
		LinearLayout animLayout = new LinearLayout(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		animLayout.setLayoutParams(lp);
		animLayout.setId(Integer.MAX_VALUE);
		animLayout.setBackgroundResource(android.R.color.transparent);
		rootView.addView(animLayout);
		return animLayout;
	}
}
