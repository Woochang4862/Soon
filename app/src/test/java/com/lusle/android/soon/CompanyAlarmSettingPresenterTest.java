package com.lusle.android.soon;

import android.content.Context;

import com.lusle.android.soon.Adapter.Contract.CompanyAlarmSettingAdapterContract;
import com.lusle.android.soon.Model.Source.CompanyAlarmManagerRemoteSource;
import com.lusle.android.soon.Model.Source.FavoriteCompanyDataLocalSource;
import com.lusle.android.soon.Model.Source.SubscribeCheckDataRemoteSource;
import com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter.CompanyAlarmSettingContractor;
import com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter.CompanyAlarmSettingPresenter;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompanyAlarmSettingPresenterTest {

    SubscribeCheckDataRemoteSource mockSubscribeCheckModel;
    CompanyAlarmManagerRemoteSource mockCompanyAlarmModel;
    FavoriteCompanyDataLocalSource mockFavoriteCompanyModel;
    CompanyAlarmSettingAdapterContract.Model mockCompanyAlarmAdapterModel;
    CompanyAlarmSettingAdapterContract.View mockCompanyAlarmAdapterView;
    CompanyAlarmSettingContractor.View mockView;
    CompanyAlarmSettingPresenter presenter;
    Context mockContext;

    @Before
    public void setup() {
        //Model
        mockSubscribeCheckModel = mock(SubscribeCheckDataRemoteSource.class);
        mockCompanyAlarmModel = mock(CompanyAlarmManagerRemoteSource.class);
        mockFavoriteCompanyModel = mock(FavoriteCompanyDataLocalSource.class);

        //View
        mockView = mock(CompanyAlarmSettingContractor.View.class);
        mockContext = mock(Context.class);

        //Adapter Model
        mockCompanyAlarmAdapterModel = mock(CompanyAlarmSettingAdapterContract.Model.class);
        mockCompanyAlarmAdapterView = mock(CompanyAlarmSettingAdapterContract.View.class);

        //Presenter
        presenter = new CompanyAlarmSettingPresenter();
        presenter.attachView(mockView);
        presenter.setModel(mockSubscribeCheckModel);
        presenter.setModel(mockCompanyAlarmModel);
        presenter.setModel(mockFavoriteCompanyModel);
        presenter.setAdapterModel(mockCompanyAlarmAdapterModel);
        presenter.setAdapterView(mockCompanyAlarmAdapterView);
    }

    //Initiating items
    //[START]
    @Test
    public void loadItems_whenViewCreated() {
        presenter.loadItems();

        verify(mockCompanyAlarmAdapterModel).setList(presenter.getAlarms());
    }

    @Test
    public void getAlarms_whenLoadItemsCalled() {
        when(mockView.getContext()).thenReturn(mockContext);

        presenter.getAlarms();

        verify(mockFavoriteCompanyModel).getFavoriteCompany(mockContext);
        verify(mockView).getContext();
    }
    //[END]

    //Checking subscribe to topic
    //[START]
    @Test
    public void checkSubscribe() {
        presenter.checkSubscribe("token", "topic");

        verify(mockSubscribeCheckModel).checkSubscribe("token", "topic");
    }
    //[END]

    //Adding or Removing company alarm
    //[START]
    @Test
    public void addCompanyAlarm() {
        presenter.addCompanyAlarm("token", "company_id");

        verify(mockCompanyAlarmModel).addCompanyAlarm("token", "company_id");
    }

    @Test
    public void removeCompanyAlarm() {
        presenter.removeCompanyAlarm("token", "company_id");
        verify(mockCompanyAlarmModel).removeCompanyAlarm("token", "company_id");
    }
    //[END]
}
