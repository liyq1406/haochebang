
package com.haoche51.checker.activity.recommend;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.haoche51.checker.Checker;
import com.haoche51.checker.GlobalData;
import com.haoche51.checker.R;
import com.haoche51.checker.activity.widget.CommonStateActivity;
import com.haoche51.checker.entity.CityInfoEntity;
import com.haoche51.checker.net.HCHttpRequestParam;
import com.haoche51.checker.net.HCHttpResponse;
import com.haoche51.checker.net.HttpConstants;
import com.haoche51.checker.net.OKHttpManager;
import com.haoche51.checker.util.JsonParseUtil;
import com.haoche51.checker.util.ProgressDialogUtil;
import com.haoche51.checker.util.ToastUtil;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class BuyerAddCluesActivity extends CommonStateActivity {

	@Required(order = 1, message = "买家号码不能为空")
	@TextRule(order = 2, minLength = 11, message = "买家号码必须为11位")
	@ViewInject(R.id.et_addbuyer_lead_buyerphone)
	private EditText et_buyerphone;

	@Required(order = 3, message = "买家信息不能为空")
	@ViewInject(R.id.et_addbuyer_lead_buyercomment)
	private EditText edt_buyerpinfo;

	@ViewInject(R.id.spn_city)
	private Spinner spn_city;

	@ViewInject(R.id.btn_positive)
	private Button btn_positive;

	private String buyerPhone, buyerInfo;

	private List<CityInfoEntity> cityInfoList = new ArrayList<>();

	@Override
	protected int getContentView() {
		return R.layout.activity_add_buyer_clue;
	}

	@Override
	protected void initView() {
		super.initView();
		registerTitleBack();
		setScreenTitle(R.string.hc_addbuyer_lead_activity_title);
		btn_positive.setText(getString(R.string.button_ok));
	}

	@Override
	protected void initData() {
		OKHttpManager.getInstance().post(HCHttpRequestParam.getOnlineCity(), this, 0);
	}

	@Event(R.id.btn_positive)
	private void onClick(View v) {
		validator.validate();
	}

	/**
	 * 访问网络添加买家线索
	 */
	private void addBuyerClues() {
		buyerPhone = et_buyerphone.getText().toString();
		buyerInfo = edt_buyerpinfo.getText().toString();

		OKHttpManager.getInstance().post(HCHttpRequestParam.addBuyerClues(buyerPhone, buyerInfo,
				cityInfoList.isEmpty() ? 0 : ((CityInfoEntity) spn_city.getSelectedItem()).getId()), this, 0);
	}

	@Override
	public void onValidationSucceeded() {
		ProgressDialogUtil.showProgressDialog(this, null);
		addBuyerClues();
		super.onValidationSucceeded();
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (!isFinishing())
			ProgressDialogUtil.closeProgressDialog();

		if (action.equals(HttpConstants.ACTION_ADD_BUYERLEAD)) {
			responseAddLead(response);
		} else if (action.equals(HttpConstants.ACTION_GET_ONLINE_CITY)) {
			responseGetOnLineCity(response);
		}
	}


	/**
	 * 返回提交线索
	 */
	private void responseAddLead(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				ToastUtil.showInfo("添加线索成功！");
				setResult(RESULT_OK);
				finish();
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}

	/**
	 * 返回获得在线城市
	 */
	private void responseGetOnLineCity(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
//				cityInfoList = new HCJsonParse().parseOnLineCityInfos(response.getData());
				cityInfoList = JsonParseUtil.fromJsonArray(response.getData(), CityInfoEntity.class);
				if (cityInfoList != null) {
					Checker user = GlobalData.userDataHelper.getChecker();
					spn_city.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityInfoList));
					int size = cityInfoList.size();
					for (int i = 0; i < size; i++) {
						if (user.getCity() == cityInfoList.get(i).getId()) {
							spn_city.setSelection(i);
							break;
						}
					}
				}
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}
}