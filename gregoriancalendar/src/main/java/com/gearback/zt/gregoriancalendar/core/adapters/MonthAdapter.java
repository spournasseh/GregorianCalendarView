package com.gearback.zt.gregoriancalendar.core.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.gearback.zt.calendarcore.core.Constants;
import com.gearback.zt.calendarcore.core.models.Day;
import com.gearback.zt.gregoriancalendar.R;
import com.gearback.zt.gregoriancalendar.core.GregorianCalendarHandler;
import com.gearback.zt.gregoriancalendar.core.fragments.MonthFragment;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {
    private Context mContext;
    private MonthFragment mMonthFragment;
    private final int TYPE_HEADER = 0;
    private final int TYPE_DAY = 1;
    private List<Day> mDays;
    private int mSelectedDay = -1;
    private GregorianCalendarHandler mCalendarHandler;
    private final int mFirstDayOfWeek;
    private final int mTotalDays;

    public MonthAdapter(Context context, MonthFragment monthFragment, List<Day> days) {
        mFirstDayOfWeek = days.get(0).getDayOfWeek();
        mTotalDays = days.size();
        this.mMonthFragment = monthFragment;
        this.mContext = context;
        this.mDays = days;
        mCalendarHandler = GregorianCalendarHandler.getInstance(context);
    }

    public void clearSelectedDay() {
        mSelectedDay = -1;
        notifyDataSetChanged();
    }

    public void selectDay(int dayOfMonth) {
        mSelectedDay = dayOfMonth + 6 + mFirstDayOfWeek;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView mNum;
        View mToday;
        View mSelectDay;
        View mEvent;
        TextView mDot;

        public ViewHolder(View itemView) {
            super(itemView);

            mNum = itemView.findViewById(R.id.num);
            mToday = itemView.findViewById(R.id.today);
            mSelectDay = itemView.findViewById(R.id.select_day);
            mEvent = itemView.findViewById(R.id.event);
            mDot = itemView.findViewById(R.id.dotEvent);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            position += 6 - (position % 7) * 2;
            if (mTotalDays < position - 6 - mFirstDayOfWeek) {
                return;
            }

            if (position - 7 - mFirstDayOfWeek >= 0) {
                mMonthFragment.onClickItem(mDays.get(position - 7 - mFirstDayOfWeek).getCivilDate());

                mSelectedDay = position;
                notifyDataSetChanged();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            position += 6 - (position % 7) * 2;
            if (mTotalDays < position - 6 - mFirstDayOfWeek) {
                return false;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                try {
                    mMonthFragment.onLongClickItem(mDays.get(position - 7 - mFirstDayOfWeek).getCivilDate());
                } catch (Exception e) {
                    // Ignore it for now
                    // I guess it will occur on CyanogenMod phones
                    // where Google extra things is not installed
                }
            }
            return false;
        }
    }

    @Override
    public MonthAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_day, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MonthAdapter.ViewHolder holder, int position) {
        holder.mToday.setBackgroundResource(mCalendarHandler.getTodayBackground());
        holder.mSelectDay.setBackgroundResource(mCalendarHandler.getSelectedDayBackground());
        holder.mEvent.setBackgroundColor(mCalendarHandler.getColorEventUnderline());
        holder.mEvent.setVisibility(View.GONE);
        holder.mDot.setVisibility(View.GONE);
        position += 6 - (position % 7) * 2;
        if (mTotalDays < position - 6 - mFirstDayOfWeek) {
            return;
        }
        if (!isPositionHeader(position)) {
            if (position - 7 - mFirstDayOfWeek >= 0) {
                holder.mNum.setText(mDays.get(position - 7 - mDays.get(0).getDayOfWeek()).getNum());
                holder.mNum.setVisibility(View.VISIBLE);

                holder.mNum.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCalendarHandler.getDaysFontSize());

                if (mDays.get(position - 7 - mFirstDayOfWeek).isHoliday()) {
                    holder.mNum.setTextColor(mCalendarHandler.getColorHoliday());
                    holder.mEvent.setBackgroundColor(mCalendarHandler.getColorHoliday());
                    holder.mDot.setTextColor(mCalendarHandler.getColorHoliday());
                }
                else {
                    holder.mNum.setTextColor(mCalendarHandler.getColorNormalDay());
                    holder.mEvent.setBackgroundColor(mCalendarHandler.getColorNormalDay());
                    holder.mDot.setTextColor(mCalendarHandler.getColorNormalDay());
                }

                Day day = mDays.get(position - 7 - mFirstDayOfWeek);
                if (day.isEvent() && mCalendarHandler.isHighlightingOfficialEvents()) {
                    holder.mDot.setVisibility(View.VISIBLE);
                }
                else {
                    holder.mDot.setVisibility(View.GONE);
                }

                if(day.isLocalEvent() && mCalendarHandler.isHighlightingLocalEvents()) {
                    holder.mEvent.setVisibility(View.VISIBLE);
                }
                else {
                    holder.mEvent.setVisibility(View.GONE);
                }

                if (mDays.get(position - 7 - mFirstDayOfWeek).isToday()) {
                    holder.mToday.setVisibility(View.VISIBLE);
                }
                else {
                    holder.mToday.setVisibility(View.GONE);
                }

                if (position == mSelectedDay) {
                    holder.mSelectDay.setVisibility(View.VISIBLE);
                    if (mDays.get(position - 7 - mFirstDayOfWeek).isHoliday()) {
                        holder.mNum.setTextColor(mCalendarHandler.getColorHoliday());
                        holder.mEvent.setBackgroundColor(mCalendarHandler.getColorHoliday());
                        holder.mDot.setTextColor(mCalendarHandler.getColorHoliday());
                    }
                    else {
                        holder.mNum.setTextColor(mCalendarHandler.getColorBackground());
                        holder.mEvent.setBackgroundColor(mCalendarHandler.getColorBackground());
                        holder.mDot.setTextColor(mCalendarHandler.getColorBackground());
                    }
                }
                else {
                    holder.mSelectDay.setVisibility(View.GONE);
                }

            }
            else {
                holder.mToday.setVisibility(View.GONE);
                holder.mSelectDay.setVisibility(View.GONE);
                holder.mNum.setVisibility(View.GONE);
                holder.mEvent.setVisibility(View.GONE);
                holder.mDot.setVisibility(View.GONE);
            }
            mCalendarHandler.setFontAndShape(holder.mNum);
        }
        else {
            holder.mNum.setText(Constants.FIRST_CHAR_OF_DAYS_OF_WEEK_NAME[position]);
            holder.mNum.setTextColor(mCalendarHandler.getColorDayName());
            holder.mEvent.setBackgroundColor(mCalendarHandler.getColorDayName());
            holder.mDot.setTextColor(mCalendarHandler.getColorDayName());
            holder.mNum.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCalendarHandler.getHeadersFontSize());
            holder.mNum.setTypeface(mCalendarHandler.getHeadersTypeface());
            holder.mToday.setVisibility(View.GONE);
            holder.mSelectDay.setVisibility(View.GONE);
            holder.mEvent.setVisibility(View.GONE);
            holder.mDot.setVisibility(View.GONE);
            holder.mNum.setVisibility(View.VISIBLE);
            mCalendarHandler.setFont(holder.mNum);
        }
    }

    @Override
    public int getItemCount() {
        return 7 * 7;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_DAY;
        }
    }

    private boolean isPositionHeader(int position) {
        return position < 7;
    }
}