package fr.jadeveloppement.jadcustomcalendar.components;

import static java.util.Objects.isNull;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.jadeveloppement.jadcustomcalendar.Variables;
import fr.jadeveloppement.jadcustomcalendar.R;

public class DayComponent {

    private DayClicked listener;
    private View dayLayout;

    private final Context context;
    private String dayDate = "";
    private TextView dayTv;
    private LinearLayout dayLayoutContainer;

    public interface DayClicked {
        void dayClicked(String newDate, DayComponent component);
    }

    public DayComponent(@NonNull Context c){
        this.context = c;
    }

    public DayComponent(@NonNull Context c, @NonNull ViewGroup parent, DayClicked l){
        this.context = c;
        this.dayLayout = LayoutInflater.from(context).inflate(R.layout.layout_calendar_day, parent, false);
        this.listener = l;

        initDay();
    }

    private void initDay() {
        LinearLayout.LayoutParams dayLayoutParams = new LinearLayout.LayoutParams(
                0,
                80,
                1
        );
        dayLayout.setLayoutParams(dayLayoutParams);
        dayTv = dayLayout.findViewById(R.id.layoutCustomCalendarDayTv);
        dayLayoutContainer = dayLayout.findViewById(R.id.layoutCustomCalendarDayLayout);

        dayLayout.setOnClickListener(v -> {
            if (!isNull(listener) && !this.dayDate.isBlank()) listener.dayClicked(this.dayDate, this);
        });
    }

    public void setActive(boolean isActive) {
        if (!isNull(dayLayout)) dayLayout.setBackgroundColor(isActive ? Variables.getColor(Variables.CalendarColor.ORANGE) : Variables.getColor(Variables.CalendarColor.TRANSPARENT));
        if (!isNull(dayTv)){
            dayTv.setTypeface(isActive ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            dayTv.setTextColor(isActive ? Variables.getColor(Variables.CalendarColor.WHITE) : Variables.getColor(Variables.CalendarColor.BLACK));
        }
    }

    private void updateDayTv(){
        if (!isNull(dayTv))
            if (dayDate.length() == 10 && dayDate.contains("-")){
                String dayOfMonth = this.dayDate.split("-")[2].charAt(0) == '0' ?
                        String.valueOf(this.dayDate.split("-")[2].charAt(1)) :
                        this.dayDate.split("-")[2];
                dayTv.setText(dayOfMonth);
            } else dayTv.setText("");
    }

    public LinearLayout getLayout(){
        return (LinearLayout) dayLayout;
    }

    public LinearLayout getDayLayoutContainer(){
        return dayLayoutContainer;
    }

    public TextView getDayTv(){
        return dayTv;
    }

    public String getDayDate(){
        return this.dayDate;
    }

    public void setDayDate(@NonNull String date){
        this.dayDate = date;
        updateDayTv();
    }
}
