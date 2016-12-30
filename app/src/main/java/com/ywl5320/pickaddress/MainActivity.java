package com.ywl5320.pickaddress;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ywl5320.pickaddress.AddressPickerDialog.OnAddressCListener;
import com.ywl5320.pickaddress.DatePickerDialog.OnDatePickListener;
import com.ywl5320.pickaddress.TimePickerDialog.OnTimePickListener;

public class MainActivity extends Activity {

	private TextView mDatePick, mTime, mAddress;
	private RadioButton rbnDialogMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDatePick = (TextView) findViewById(R.id.tv_datePicker);
		mTime = (TextView) findViewById(R.id.tv_birth);
		mAddress = (TextView) findViewById(R.id.tv_address);

		rbnDialogMode = (RadioButton) findViewById(R.id.rbnDialogMode);
		rbnDialogMode.setChecked(true);

		mDatePick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerDialog mChangeBirthDialog = new DatePickerDialog(
						MainActivity.this);
				// mChangeBirthDialog.setDate(2015, 03, 29);
				if (rbnDialogMode.isChecked()) {
					mChangeBirthDialog
							.setDialogMode(DatePickerDialog.DIALOG_MODE_BOTTOM);
				}
				mChangeBirthDialog.show();
				mChangeBirthDialog
						.setDatePickListener(new OnDatePickListener() {

							@Override
							public void onClick(String year, String month,
									String day) {
								// TODO Auto-generated method stub
								Toast.makeText(MainActivity.this,
										year + "-" + month + "-" + day,
										Toast.LENGTH_LONG).show();
							}
						});
			}
		});

		mTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TimeAMPMPickerDialog mTimePickerDialog = new TimeAMPMPickerDialog(
						MainActivity.this);
				// mTimePickerDialog.setDate(2015, 03, 29);
				if (rbnDialogMode.isChecked()) {
					mTimePickerDialog
							.setDialogMode(TimeAMPMPickerDialog.DIALOG_MODE_BOTTOM);
				}
				mTimePickerDialog.show();
				mTimePickerDialog.setTimePickListener(new TimeAMPMPickerDialog.OnTimePickListener() {
					@Override
					public void onClick(int year, int month, int day, String ampm) {
						Toast.makeText(
								MainActivity.this,
								year + "-" + month + "-" + day + " " + ampm, Toast.LENGTH_LONG)
								.show();
					}
				});
			}
		});

		mAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AddressPickerDialog mChangeAddressDialog = new AddressPickerDialog(
						MainActivity.this);
				mChangeAddressDialog.setAddress("四川", "自贡");
				if (rbnDialogMode.isChecked()) {
					mChangeAddressDialog
							.setDialogMode(DatePickerDialog.DIALOG_MODE_BOTTOM);
				}
				mChangeAddressDialog.show();
				mChangeAddressDialog
						.setAddresskListener(new OnAddressCListener() {

							@Override
							public void onClick(String province, String city) {
								// TODO Auto-generated method stub
								Toast.makeText(MainActivity.this,
										province + "-" + city,
										Toast.LENGTH_LONG).show();
							}
						});
			}
		});
	}
}
