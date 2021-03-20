package com.lusle.android.soon.Adapter.Contract;

import com.lusle.android.soon.Model.Schema.Company;
import com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter.CompanyAlarmSettingPresenter;

import java.util.ArrayList;

public interface CompanyAlarmSettingAdapterContract {
    interface View extends BaseAdapterContract.View {

    }

    interface Model extends BaseAdapterContract.Model {
        void setList(ArrayList<Company> list);
        void setPresenter(CompanyAlarmSettingPresenter presenter);
    }
}
