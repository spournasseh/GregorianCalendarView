package com.gearback.zt.gregoriancalendar.core.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import com.gearback.zt.calendarcore.core.Constants;
import com.gearback.zt.calendarcore.core.interfaces.OnEventUpdateListener;
import com.gearback.zt.calendarcore.core.models.CivilDate;
import com.gearback.zt.calendarcore.core.models.IslamicDate;
import com.gearback.zt.calendarcore.helpers.DateConverter;
import com.gearback.zt.gregoriancalendar.R;
import com.gearback.zt.gregoriancalendar.core.GregorianCalendarHandler;
import com.gearback.zt.gregoriancalendar.core.adapters.CalendarAdapter;

public class CalendarFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private ViewPager mMonthViewPager;
    private GregorianCalendarHandler mGregorianCalendarHandler;
    private int mViewPagerPosition;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        mGregorianCalendarHandler = GregorianCalendarHandler.getInstance(getContext());
        mViewPagerPosition = 0;
        mMonthViewPager = view.findViewById(R.id.calendar_pager);
        mGregorianCalendarHandler.setOnEventUpdateListener(new OnEventUpdateListener() {
            @Override
            public void update() {
                createViewPagers();
            }
        });

        createViewPagers();
        return view;
    }

    private void createViewPagers() {
        mMonthViewPager.setAdapter(new CalendarAdapter(getChildFragmentManager()));
        mMonthViewPager.setCurrentItem(Constants.MONTHS_LIMIT / 2);

        mMonthViewPager.addOnPageChangeListener(this);
    }

    public void changeMonth(int position) {
        mMonthViewPager.setCurrentItem(mMonthViewPager.getCurrentItem() + position, true);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void addEventOnCalendar(CivilDate civilDate) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);

        intent.putExtra(CalendarContract.Events.DESCRIPTION,
                mGregorianCalendarHandler.dayTitleSummary(civilDate));

        Calendar time = Calendar.getInstance();
        time.set(civilDate.getYear(), civilDate.getMonth() - 1, civilDate.getDayOfMonth());

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                time.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                time.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        startActivity(intent);
    }

    private void bringTodayYearMonth() {
        Intent intent = new Intent(Constants.BROADCAST_INTENT_TO_MONTH_FRAGMENT);
        intent.putExtra(Constants.BROADCAST_FIELD_TO_MONTH_FRAGMENT,
                Constants.BROADCAST_TO_MONTH_FRAGMENT_RESET_DAY);
        intent.putExtra(Constants.BROADCAST_FIELD_SELECT_DAY, -1);

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

        if (mMonthViewPager.getCurrentItem() != Constants.MONTHS_LIMIT / 2) {
            mMonthViewPager.setCurrentItem(Constants.MONTHS_LIMIT / 2, false);
        }
    }

    public void bringDate(CivilDate date) {
        CivilDate today = mGregorianCalendarHandler.getToday();
        mViewPagerPosition = (today.getYear() - date.getYear()) * 12 + today.getMonth() - date.getMonth();

        mMonthViewPager.setCurrentItem(mViewPagerPosition + Constants.MONTHS_LIMIT / 2, false);

        Intent intent = new Intent(Constants.BROADCAST_INTENT_TO_MONTH_FRAGMENT);
        intent.putExtra(Constants.BROADCAST_FIELD_TO_MONTH_FRAGMENT, mViewPagerPosition);
        intent.putExtra(Constants.BROADCAST_FIELD_SELECT_DAY, date.getDayOfMonth());

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mViewPagerPosition = position - Constants.MONTHS_LIMIT / 2;

        Intent intent = new Intent(Constants.BROADCAST_INTENT_TO_MONTH_FRAGMENT);
        intent.putExtra(Constants.BROADCAST_FIELD_TO_MONTH_FRAGMENT, mViewPagerPosition);
        intent.putExtra(Constants.BROADCAST_FIELD_SELECT_DAY, -1);

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
    public int getViewPagerPosition() {
        return mViewPagerPosition;
    }
}