package com.ywl5320.pickaddress;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ywl5320.pickaddress.BaseDialog;
import com.ywl5320.pickaddress.R;
import com.ywl5320.pickaddress.wheel.widget.adapters.AbstractWheelTextAdapter;
import com.ywl5320.pickaddress.wheel.widget.views.OnWheelChangedListener;
import com.ywl5320.pickaddress.wheel.widget.views.OnWheelScrollListener;
import com.ywl5320.pickaddress.wheel.widget.views.WheelView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间选择对话框
 * 
 * @author pengjian
 *
 */
public class TimeAMPMPickerDialog extends BaseDialog implements
		View.OnClickListener {

	private int MIN_YEAR = 2000;
	private int MAX_YEAR = 2020;

	public static final int DIALOG_MODE_CENTER = 0;
	public static final int DIALOG_MODE_BOTTOM = 1;

	private Context context;
	private WheelView wvDate;
	private WheelView wvAmPm;

	private View vDialog;
	private View vDialogChild;
	private ViewGroup VDialogPicker;
	private TextView tvTitle;
	private TextView btnSure;
	private TextView btnCancel;

	private ArrayList<String> arry_dates = new ArrayList<String>();
	private ArrayList<String> arry_ampm = new ArrayList<String>();
	private CalendarTextAdapter mDateAdapter;
	private CalendarTextAdapter mAmPmAdapter;

	private int currentDate = 0;
	private int currentAmPm = 0;

	private int maxTextSize = 24;
	private int minTextSize = 14;

	private boolean issetdata = false;

	private int selectDate;
	private String selectAmPm;

	private String strTitle = "取消";

	private OnTimePickListener onTimePickListener;

	public TimeAMPMPickerDialog(Context context) {
		super(context, R.layout.dialog_picker_center);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		VDialogPicker = (ViewGroup) findViewById(R.id.ly_dialog_picker);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1.0f);
		// 此处相当于布局文件中的Android:layout_gravity属性
		lp.gravity = Gravity.CENTER_VERTICAL;

		wvDate = new WheelView(context);
		wvDate.setLayoutParams(lp);
		VDialogPicker.addView(wvDate);

		wvAmPm = new WheelView(context);
		wvAmPm.setLayoutParams(lp);
		wvAmPm.setCyclic(true);
		VDialogPicker.addView(wvAmPm);

		vDialog = findViewById(R.id.ly_dialog);
		vDialogChild = findViewById(R.id.ly_dialog_child);
		tvTitle = (TextView) findViewById(R.id.tv_dialog_title);
		btnSure = (TextView) findViewById(R.id.btn_dialog_sure);
		btnCancel = (TextView) findViewById(R.id.btn_dialog_cancel);

		tvTitle.setText(strTitle);
		tvTitle.setOnClickListener(this);
		vDialog.setOnClickListener(this);
		vDialogChild.setOnClickListener(this);
		btnSure.setOnClickListener(this);
		if (null != btnCancel) {
			btnCancel.setOnClickListener(this);
		}

		if (!issetdata) {
			initTime();
		}
		initDates();
		mDateAdapter = new CalendarTextAdapter(context, arry_dates,
				currentDate, maxTextSize, minTextSize);
		wvDate.setVisibleItems(5);
		wvDate.setViewAdapter(mDateAdapter);
		wvDate.setCurrentItem(currentDate);

		arry_ampm.add("上午");
		arry_ampm.add("下午");
		mAmPmAdapter = new CalendarTextAdapter(context, arry_ampm,
				currentAmPm, maxTextSize, minTextSize);
		wvAmPm.setVisibleItems(2);
		wvAmPm.setViewAdapter(mAmPmAdapter);
		wvAmPm.setCurrentItem(currentAmPm);

		wvDate.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String currentText = (String) mDateAdapter.getItemText(wheel
						.getCurrentItem());
				selectDate = wheel.getCurrentItem();
				setTextviewSize(currentText, mDateAdapter);
				//动态添加
				if(oldValue>newValue){
					if(newValue<100){
						//需要添加前面一年的数据
						MIN_YEAR -= 1;
						arry_dates.addAll(0, getYaerDate(MIN_YEAR));
						selectDate += calDaysOfYear(MIN_YEAR);
						mDateAdapter = new CalendarTextAdapter(context, arry_dates, selectDate,
								maxTextSize, minTextSize);
						wvDate.setVisibleItems(5);
						wvDate.setViewAdapter(mDateAdapter);
						wvDate.setCurrentItem(selectDate);
					}
				}else{
					if(mDateAdapter.getItemsCount()-newValue<100){
						//需要添加后面一年的数据
						MAX_YEAR += 1;
						arry_dates.addAll(mDateAdapter.getItemsCount(), getYaerDate(MAX_YEAR));
						mDateAdapter = new CalendarTextAdapter(context, arry_dates, selectDate,
								maxTextSize, minTextSize);
						wvDate.setVisibleItems(5);
						wvDate.setViewAdapter(mDateAdapter);
						wvDate.setCurrentItem(selectDate);
					}
				}
			}
		});

		wvDate.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) mDateAdapter.getItemText(wheel
						.getCurrentItem());
				setTextviewSize(currentText, mDateAdapter);
			}
		});

		wvAmPm.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String currentText = (String) mAmPmAdapter.getItemText(wheel
						.getCurrentItem());
				setTextviewSize(currentText, mAmPmAdapter);
				selectAmPm = currentText;
			}
		});
		wvAmPm.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) mAmPmAdapter.getItemText(wheel
						.getCurrentItem());
				setTextviewSize(currentText, mAmPmAdapter);
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	private String getTimeFormat(String format, Date date) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(date);
	}

	@SuppressLint("SimpleDateFormat")
	private Date getDateFromString(String timeStr, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			return dateFormat.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取某一年的String数据
	 * @param year
	 * @return
	 */
	private ArrayList<String> getYaerDate(int year) {
		ArrayList<String> list = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, 0, 1);
		while (calendar.get(Calendar.YEAR) <= year) {
			final Date date = calendar.getTime();
			list.add(getTimeFormat("MM月dd日 EEE", date));
			calendar.add(Calendar.DATE, 1);
		}
		return list;
	}

	//设置为当前时间
	public void initTime() {
		Calendar c = Calendar.getInstance();
		setTime(c.get(Calendar.YEAR),
				c.get(Calendar.MONTH) + 1,
				c.get(Calendar.DAY_OF_MONTH),
				"上午");
	}

	public void initDates() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(MIN_YEAR, 0, 1);
		while (calendar.get(Calendar.YEAR) <= MAX_YEAR) {
			final Date date = calendar.getTime();
			arry_dates.add(getTimeFormat("MM月dd日 EEE", date));
			calendar.add(Calendar.DATE, 1);
		}
	}

	@Override
	public void onClick(View v) {

		if (v == btnSure) {
			if (onTimePickListener != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(MIN_YEAR, 0, 1);
				calendar.add(Calendar.DATE, selectDate);
				onTimePickListener.onClick(
						calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH) + 1,
						calendar.get(Calendar.DAY_OF_MONTH)
						,selectAmPm);
			}
		} else if (v == btnCancel) {

		} else if (v == vDialogChild) {
			return;
		} else {
			dismiss();
		}
		dismiss();

	}

	public interface OnTimePickListener {
		public void onClick(int year, int month, int day ,String ampm);
	}

	public void setTimePickListener(OnTimePickListener onTimePickListener) {
		this.onTimePickListener = onTimePickListener;
	}

	/**
	 * 设置dialog弹出框模式
	 * 
	 * @param dialogMode
	 * @param DIALOG_MODE_CENTER
	 *            从屏幕中间弹出
	 * @param DIALOG_MODE_BOTTOM
	 *            从屏幕底部弹出
	 */
	public void setDialogMode(int dialogMode) {
		if (dialogMode == DIALOG_MODE_BOTTOM) {
			resetContent(R.layout.dialog_picker_bottom);
			setAnimation(R.style.AnimationBottomDialog);
			setGravity(Gravity.BOTTOM);
		}
	}

	public void setTitle(String title) {
		this.strTitle = title;
	}

	@Override
	protected int dialogWidth() {
		DisplayMetrics metric = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
		return metric.widthPixels;
	}

	/**
	 * 设置字体大小
	 * 
	 * @param curriteItemText
	 * @param adapter
	 */
	public void setTextviewSize(String curriteItemText,
			CalendarTextAdapter adapter) {
		ArrayList<View> arrayList = adapter.getTestViews();
		int size = arrayList.size();
		String currentText;
		for (int i = 0; i < size; i++) {
			TextView textvew = (TextView) arrayList.get(i);
			currentText = textvew.getText().toString();
			if (curriteItemText.equals(currentText)) {
				textvew.setTextSize(maxTextSize);
			} else {
				textvew.setTextSize(minTextSize);
			}
		}
	}

	/**
	 * 设置日期-时间
	 * 
	 * @param year
	 * @param month 1-12
	 * @param day 1-31
	 */
	public void setTime(int year, int month, int day ,String ampm) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month-1, day);
		this.currentDate = calendar.get(Calendar.DAY_OF_YEAR) - 1;
		if(month<6){
			MIN_YEAR = year -1;
			MAX_YEAR = year;
			this.currentDate += calDaysOfYear(MIN_YEAR);
		}else{
			MIN_YEAR = year;
			MAX_YEAR = year + 1;
		}
		selectDate = currentDate;
		selectAmPm = ampm;
		issetdata = true;
		this.currentAmPm = 0;
	}

	/**
	 * 设置日期
	 * 
	 * @param date
	 */
	public int setDate(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.set(MIN_YEAR, 1, 1);
		long time1 = cal.getTimeInMillis();
		cal.setTime(date);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days)) + 1;

	}
	
	/**
	 * 计算每年多少天
	 * @return
	 */
	public int calDaysOfYear(int year){
		if (year % 4 == 0 && year % 100 != 0) {
			return 366;
		} else {
			return 365;
		}
	}

	/**
	 * 计算每月多少天
	 * 
	 * @param month
	 * @param leayyear
	 */
	public int calDaysOfMonth(int year, int month) {
		int day = 0;
		boolean leayyear = false;
		if (year % 4 == 0 && year % 100 != 0) {
			leayyear = true;
		} else {
			leayyear = false;
		}
		for (int i = 1; i <= 12; i++) {
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				day = 31;
				break;
			case 2:
				if (leayyear) {
					day = 29;
				} else {
					day = 28;
				}
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				day = 30;
				break;
			}
		}
		return day;
	}

	private class CalendarTextAdapter extends AbstractWheelTextAdapter {
		ArrayList<String> list;

		protected CalendarTextAdapter(Context context, ArrayList<String> list,
				int currentItem, int maxsize, int minsize) {
			super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem,
					maxsize, minsize);
			this.list = list;
			setItemTextResource(R.id.tempValue);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return list.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return list.get(index) + "";
		}

		@Override
		protected void notifyDataChangedEvent() {
			// TODO Auto-generated method stub
			super.notifyDataChangedEvent();
		}
		
		
	}
}