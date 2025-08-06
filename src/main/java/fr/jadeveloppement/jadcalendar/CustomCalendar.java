package fr.jadeveloppement.jadcalendar;

import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.jadeveloppement.jadcalendar.components.DayComponent;
import fr.jadeveloppement.jadcalendar.components.DaysShortNamesComponents;
import fr.jadeveloppement.jadcalendar.components.MonthComponent;
import fr.jadeveloppement.jadcalendar.components.WeekComponent;
import fr.jadeveloppement.jadcustomcalendar.R;

public class CustomCalendar extends LinearLayout implements MonthComponent.MonthLayoutClicked,
DayComponent.DayClicked {

    private DateChanged listener;

    @Override
    public void dayClicked() {
        Log.d(TAG, "dayClicked: ");
    }

    public interface DateChanged {
        void selectedDayChanged();
    }

    private int DAYTV_TXT_ALIGNEMENT = TextView.TEXT_ALIGNMENT_CENTER;
    private final int[] allowedAlignement = {
            TextView.TEXT_ALIGNMENT_INHERIT,
            TextView.TEXT_ALIGNMENT_GRAVITY,
            TextView.TEXT_ALIGNMENT_CENTER,
            TextView.TEXT_ALIGNMENT_VIEW_START,
            TextView.TEXT_ALIGNMENT_VIEW_END,
            TextView.TEXT_ALIGNMENT_TEXT_START,
            TextView.TEXT_ALIGNMENT_TEXT_END
    };

    /**
     * Set text alignment of textview inside the days layout. Allowed alignment :
     * TextView.TEXT_ALIGNMENT_INHERIT,
     * TextView.TEXT_ALIGNMENT_GRAVITY,
     * TextView.TEXT_ALIGNMENT_CENTER,
     * TextView.TEXT_ALIGNMENT_VIEW_START,
     * TextView.TEXT_ALIGNMENT_VIEW_END,
     * TextView.TEXT_ALIGNMENT_TEXT_START,
     * TextView.TEXT_ALIGNMENT_TEXT_END
     * @param alignment : alignment to set
     */
    public void setDayTvTextAlignment(int alignment) throws Exception{
        if (Arrays.stream(allowedAlignement).noneMatch(a -> a == alignment)) throw new Exception("CustomCalendar > setDayTvTextAlignement : text alignement not allowed. Please check documentation.");
        this.DAYTV_TXT_ALIGNEMENT = alignment;
    }
    private int CALENDAR_ACTIVE_COLOR = Color.parseColor("#FFA500");
    private Integer CALENDAR_DAYLAYOUT_BACKGROUND = null;

    public void setCalendarDayLayoutBackground(Integer resource){
        this.CALENDAR_DAYLAYOUT_BACKGROUND = resource;
    }

    private Typeface DAYTV_TYPEFACE_ACTIVE = Typeface.DEFAULT_BOLD;
    private Typeface DAYTV_TYPEFACE_INACTIVE = Typeface.DEFAULT;

    /**
     * Set typeface of day textview
     * @param active : typeface of textview if selected
     * @param inactive : typeface of textview if not selected
     */
    public void setDayTvTypefaceStates(Typeface active, Typeface inactive){
        this.DAYTV_TYPEFACE_ACTIVE = active;
        this.DAYTV_TYPEFACE_INACTIVE = inactive;
    }

    private int DAYTV_COLOR_ACTIVE = android.R.color.white;
    private int DAYTV_COLOR_INACTIVE = android.R.color.black;

    /**
     * Set the day textview color text
     * @param active : color of text of day if selected
     * @param inactive : color of text of day if not selected
     */
    public void setDayTvColorStates(int active, int inactive){
        this.DAYTV_COLOR_ACTIVE = active;
        this.DAYTV_COLOR_INACTIVE = inactive;
    }

    private int CALENDAR_DAYLAYOUT_HEIGHT = 150 ;

    /**
     * Set the height of the layout containing the days of the calendar
     * Logs warning if height < 50.
     * @param height : height of the layout for the days
     * @throws Exception if 0 or negative number is given.
     */
    public void setCalendarDayLayoutHeight(int height) throws Exception {
        if (height <= 0) throw new Exception("CustomCalendar > setCalendarDayLayoutHeight : \nNegative or null number given.");
        else if (height <= 80) Log.w(TAG, "CustomCalendar > setCalendarDayLayoutHeight : height given ( "+height+" ) may be too small.");
        this.CALENDAR_DAYLAYOUT_HEIGHT = height;
    }

    /**
     * Set background color of selectedDay
     * @param color : color to use
     */
    public void setSelectionDayLayoutColor(int color){
        this.CALENDAR_ACTIVE_COLOR = color;
    }

    private int CALENDAR_DAYTV_GRAVITY = Gravity.CENTER_HORIZONTAL;

    /**
     * sets gravity of textview inside the layout of the day textview.
     * @param gravity : Gravity to apply
     * @throws Exception if value used is not a valid Gravity
     */
    public void setDayLayoutGravity(int gravity) throws Exception{
        if (gravity != Gravity.NO_GRAVITY &&
                (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == 0 &&
                (gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0)
            throw new Exception("CustomCalendar > setDayLayoutGravity : \nInvalid gravity value. Please use a valid Gravity constant or a combination of valid constants (e.g., Gravity.CENTER_HORIZONTAL | Gravity.TOP).");

        this.CALENDAR_DAYTV_GRAVITY = gravity;
    }

    private final String TAG = "Agenda";

    private String[] monthesLong = { "", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Décembre"};

    /**
     * Set new monthes name
     * @param monthes : arrays of 13 string values (first one won't be used)
     * @throws Exception : if less or more than an array of 13 values is used
     */
    public void setMonthesName(String[] monthes) throws Exception{
        if (monthes.length != 13) throw new Exception("CustomCalendar > setMonthesName : \nNot enough month in array (expected 13 got " + monthes.length + " ) ");

        this.monthesLong = monthes;
    }

    private String[] daysLong = { "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche" };

    /**
     * Set new day names
     * @param days : arrays of 7 string values (first one must be Monday, last Sunday)
     * @throws Exception : if less or more than an array of 7 values is used
     */
    public void setDaysName(String[] days) throws Exception{
        if (days.length != 7) throw new Exception("CustomCalendar > setDaysName :\nLength of array incorrected (expected 7 got " + days.length + " ) ");

        this.daysLong = days;
    }

    private int DAYTV_PADDING_LEFT, DAYTV_PADDING_TOP, DAYTV_PADDING_RIGHT, DAYTV_PADDING_BOTTOM;

    /**
     * set padding of the textview of the day inside calendar view
     * @param left : left padding
     * @param top : top padding
     * @param right : right padding
     * @param bottom : bottom padding
     * @throws Exception : if a negative number is given
     */
    public void setDayTvPadding(int left, int top, int right, int bottom) throws Exception {
        if (left < 0 || top < 0 || right < 0 || bottom < 0) throw new Exception("CanCustomCalendar > setDayTvPadding : \nYou cannot use a negative padding.");
        DAYTV_PADDING_LEFT = left;
        DAYTV_PADDING_RIGHT = right;
        DAYTV_PADDING_TOP = top;
        DAYTV_PADDING_BOTTOM = bottom;
    }

    public String[] daysShort = { "L", "M", "M", "J", "V", "S", "D" };
    /**
     * Set new short day names
     * Logs warning if name's not adapted
     * @param days : arrays of 7 string values (first one must be Monday, last Sunday)
     * @throws Exception : if less or more than an array of 7 values is used
     */
    public void setDaysShortName(String[] days) throws Exception{
        if (days.length != 7) throw new Exception("Length of array incorrected (expected 7 got " + days.length + " ) ");
        boolean dayTooLongWarning = false;
        for (int index = 0; index < days.length && !dayTooLongWarning ; index++){
            if (days[index].length() > 2 ) dayTooLongWarning = true;
        }
        if (dayTooLongWarning)
            Log.w(TAG, "CustomCalendar > setDaysShortName: one or more short day name may be too long and may render bad.");

        this.daysShort = days;
    }

    /**
     * Get the week's number of the selected date
     * @return : week number of the selected date
     */
    public int getWeekNumber(){
        return calendarWeekNumber;
    }

    /**
     * get the index of the day selected in the week
     * @return : index of the day selected in the week
     */
    public int getDayOfWeekIndex(){
        return getDayOfWeekIndex(selectedDay);
    }

    /**
     * get selected day of the calendar
     * @return : selected day (first day = 1 )
     */
    public String getDay(){
        return calendarDay;
    }

    /**
     * get selected month of the calendar
     * @return : selected month (first month = 1 )
     */
    public String getMonth(){
        return calendarMonth;
    }

    /**
     * get selected year of the calendar
     * @return : selected year
     */
    public String getYear(){
        return calendarYear;
    }

    private final Context context;
    private TextView btnSetToday;

    private String selectedDay;
    private String calendarMonth, calendarDay, calendarYear;
    private int calendarWeekNumber;

    private Runnable dayTvClicked;

    /**
     * Initializes calendar : no action available with this public constructor
     * @param c : context of the application
     */
    public CustomCalendar(Context c){
        super(c);
        this.context = c.getApplicationContext();

        initVariables();
    }

    private View customCalendarLayout, calendarMonthLayout, calendarDaysShortLayout, calendarDaysLayout_v2;
    private LinearLayout calendarMonthLayoutContainer, layoutCustomCalendarShortDaysContainer, layoutCustomCalendarDaysContainer;

    /**
     * Initializes calendar with a callback function when you click on a day
     * @param c : context of the application
     * @param r : callback to run when day is clicked
     */
    public CustomCalendar(Context c, Runnable r){
        super(c);
        this.context = c;
        this.customCalendarLayout = LayoutInflater.from(context).inflate(R.layout.layout_custom_calendar, this, false);

        this.calendarMonthLayoutContainer = customCalendarLayout.findViewById(R.id.layoutCustomCalendarMonthContainer);
        this.layoutCustomCalendarShortDaysContainer = customCalendarLayout.findViewById(R.id.layoutCustomCalendarShortDaysContainer);
        this.layoutCustomCalendarDaysContainer = customCalendarLayout.findViewById(R.id.layoutCustomCalendarDaysContainer);
        this.calendarMonthLayout = LayoutInflater.from(context).inflate(R.layout.layout_month, (ViewGroup) customCalendarLayout, false);
        this.calendarDaysShortLayout = LayoutInflater.from(context).inflate(R.layout.layout_days_short, (ViewGroup) customCalendarLayout, false);
        this.calendarDaysLayout_v2 = LayoutInflater.from(context).inflate(R.layout.layout_calendar_day, (ViewGroup) customCalendarLayout, false);
        this.dayTvClicked = r;

        initVariables();
        initCalendar();
    }

    public CustomCalendar(Context c, DateChanged l){
        super(c);
        this.context = c;
        this.customCalendarLayout = LayoutInflater.from(context).inflate(R.layout.layout_custom_calendar, this, false);

        this.calendarMonthLayoutContainer = customCalendarLayout.findViewById(R.id.layoutCustomCalendarMonthContainer);
        this.layoutCustomCalendarShortDaysContainer = customCalendarLayout.findViewById(R.id.layoutCustomCalendarShortDaysContainer);
        this.layoutCustomCalendarDaysContainer = customCalendarLayout.findViewById(R.id.layoutCustomCalendarDaysContainer);
        this.listener = l;

        initVariables();
        initCalendar();
    }

    private MonthComponent monthComponent;
    private DaysShortNamesComponents daysShortNamesComponents;

    private void initCalendar() {
        monthComponent = new MonthComponent(context, selectedDay.substring(0, 7), calendarMonthLayoutContainer, this);
        calendarMonthLayoutContainer.addView(monthComponent.getLayout());

        daysShortNamesComponents = new DaysShortNamesComponents(context, (ViewGroup) calendarDaysShortLayout);
        layoutCustomCalendarShortDaysContainer.addView(daysShortNamesComponents.getLayout());
    }

    public View getCustomCalendarLayout(){
        setDaysLayout();
        return customCalendarLayout;
    }

    private void initVariables(){
        this.selectedDay = getTodayDate();
        this.calendarDay = selectedDay.split("-")[2];
        this.calendarMonth = selectedDay.split("-")[1];
        this.calendarYear = selectedDay.split("-")[0];
        this.calendarWeekNumber = getWeekNumberFromDate(selectedDay);
    }

    private void setDaysLayout(){
        layoutCustomCalendarDaysContainer.removeAllViews();
        int daysOfMonth = getDaysInMonth(calendarYear + "-" + calendarMonth + "-01");
        int firstDayOfWeek = getDayOfWeekIndex(calendarYear + "-" + calendarMonth + "-01");

        int dayOfMonth = 1;

        for (int week = 0; week < (int) Math.ceil((firstDayOfWeek + daysOfMonth) / 7.0); week++) {
            List<DayComponent> daysComponentList = new ArrayList<>();
            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                DayComponent dayComponent = new DayComponent(context, String.valueOf(dayOfMonth), layoutCustomCalendarDaysContainer, this);
                if ((week == 0 && dayOfWeek >= firstDayOfWeek) || (week > 0 && dayOfMonth <= daysOfMonth)){
                    String dayOfMonthStr = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                    dayComponent.getDayTv().setText(dayOfMonthStr);
                    dayOfMonth++;
                }
                daysComponentList.add(dayComponent);
            }
            WeekComponent weekComponent = new WeekComponent(context, daysComponentList, layoutCustomCalendarDaysContainer);
            layoutCustomCalendarDaysContainer.addView(weekComponent.getLayout());
        }
    }

    private void makeDayLayoutActive(@NonNull LinearLayout dayOfWeekLayout, @NonNull TextView dayOfWeekTv, boolean active) {
        if (!isNull(CALENDAR_DAYLAYOUT_BACKGROUND)) dayOfWeekLayout.setBackground(active ? ResourcesCompat.getDrawable(context.getResources(), CALENDAR_DAYLAYOUT_BACKGROUND, context.getTheme()) : null);
        else dayOfWeekLayout.setBackgroundColor(active ? CALENDAR_ACTIVE_COLOR : context.getColor(android.R.color.transparent));

        dayOfWeekTv.setTypeface(active ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        dayOfWeekTv.setTextColor(active ? context.getColor(android.R.color.white) : context.getColor(android.R.color.black));
    }

    private int getDpInPx(Context c, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    /**
     * Return a clickable textview to select the today date
     * @return : clickable textview
     */
    public TextView getButtonTodayLayout(){
        return btnSetToday;
    }

    public void setButtonTodayLayout(){
        btnSetToday = new TextView(context);
        btnSetToday.setPadding(
                getDpInPx(context, 8),
                getDpInPx(context, 4),
                getDpInPx(context, 8),
                getDpInPx(context, 4)
        );
        LinearLayout.LayoutParams btnSetTodayParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        btnSetToday.setLayoutParams(btnSetTodayParams);
        btnSetToday.setBackgroundColor(CALENDAR_ACTIVE_COLOR);
        btnSetToday.setTextColor(context.getColor(android.R.color.white));
        btnSetToday.setTypeface(Typeface.DEFAULT_BOLD);
        btnSetTodayParams.setMargins(
                getDpInPx(context, 4),
                getDpInPx(context, 8),
                getDpInPx(context, 4),
                getDpInPx(context, 8)
        );
        btnSetToday.setText("Aujourd'hui");
        btnSetToday.setOnClickListener(v -> {
            setDaySelected(getTodayDate());
            if (!isNull(dayTvClicked)) dayTvClicked.run();
        });
    }

    // Basic date manipulation methods

    /**
     * @return today's date in a YYYY-MM-DD format
     */
    private String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int monthOfYear = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);

        String day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
        String month = monthOfYear < 10 ? "0" + monthOfYear : String.valueOf(monthOfYear);

        return year + "-" + month + "-" + day;
    }

    /**
     * Get the extremes of the week selected
     * @return : array with date of first day and last day of the selected week
     */
    public String[] getWeekRange(){
        WeekFields weekFields = WeekFields.of(Locale.FRANCE);
        LocalDate startDate = LocalDate.of(parseInt(calendarYear), 1, 1)
                .with(weekFields.weekOfYear(), calendarWeekNumber)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate endDate = startDate.plusDays(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return new String[]{startDate.format(formatter), endDate.format(formatter)};
    }

    /**
     * Get list of String of YYYY-MM-DD of the week selected
     *
     * @return : list of String
     *
     */
    public List<String> getListOfDatesOfWeek() {
        List<String> weekDates = new ArrayList<>();
        WeekFields weekFields = WeekFields.of(Locale.FRANCE);

        LocalDate startDate = LocalDate.of(parseInt(calendarYear), 1, 1)
                .with(weekFields.weekOfYear(), calendarWeekNumber)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); // Or SUNDAY for US

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < 7; i++) {
            weekDates.add(startDate.plusDays(i).format(formatter));
        }

        return weekDates;
    }

    /**
     * Get the weeknumber of a day
     *
     * @param dateString : date to get the weeknumber from
     * @return : the weeknumber of the date
     *
     */
    private int getWeekNumberFromDate(String dateString) {
        try {
            Locale locale = Locale.FRANCE;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateString, formatter);

            WeekFields weekFields = WeekFields.of(locale);
            return date.get(weekFields.weekOfYear());

        } catch (Exception e) {
            // Handle parsing errors or other exceptions
            return -1; // Or throw an exception
        }
    }

    /**
     * Function to get the number of day a month has
     * @param dateString : date to get the number of days from the month of this date should be DD/MM/YYYY format
     * @return : the number of days the month has
     */
    private int getDaysInMonth(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate locale = LocalDate.parse(dateString, formatter);

        return locale.lengthOfMonth();
    }

    /**
     * Get the day's number (between 0 and 6) of a date string in the week.
     * E.G : Monday > 0, Tuesday > 1, ...
     *
     * @param dateString : date (DD/MM/YYYY) to get the index of the day inside the week
     * @return : the index of the day
     *
     */
    private int getDayOfWeekIndex(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getValue() - 1;
    }

    /**
     * Get the day of month number of a date clicked
     * @param dayClicked : the month's day clicked
     */
//    private void dayClicked(int dayClicked){
//        String day = dayClicked < 10 ? "0" + dayClicked : String.valueOf(dayClicked);
//        setDaySelected(calendarYear + "-" + calendarMonth + "-" + day);
//    }

    /**
     * Set the day selected in the daysLayout
     * @param dayS : date to select (YYYY-MM-DD)
     */
    public void setDaySelected(String dayS){
        selectedDay = dayS;
        calendarDay = dayS.split("-")[2];
        calendarMonth = dayS.split("-")[1];
        calendarYear = dayS.split("-")[0];
        calendarWeekNumber = getWeekNumberFromDate(selectedDay);

        if (!isNull(monthComponent))
            monthComponent.updateMonthName(selectedDay.substring(0, 7));

        setDaysLayout();
    }

    /**
     * get the day selected inside the calendar
     * @return : string YYYY-MM-DD of the date selected inside the calendar
     */
    public String getDaySelected(){
        return selectedDay;
    }

    /**
     * Add X day
     * @param x : number of day to add
     */
    private void addXDay(int x){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(selectedDay, formatter);
        LocalDate newDate = date.plusDays(x);
        setDaySelected(newDate.format(formatter));
    }

    /**
     * Add X week
     * @param x : number of week to add
     */
    private void addXWeek(int x){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(selectedDay, formatter);
        LocalDate newDate = date.plusDays((long) 7*x);
        setDaySelected(newDate.format(formatter));
    }

    /**
     * Add X month
     * @param x : number of month to add
     */
    private void addXMonth(int x){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(selectedDay, formatter);
        LocalDate newDate = date.plusMonths(x);

        setDaySelected(newDate.format(formatter));
    }

    /**
     * Add X year
     * @param x : number of year to add
     */
    private void addXYear(int x){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(selectedDay, formatter);
        LocalDate newDate = date.plusYears(x);
        setDaySelected(newDate.format(formatter));
    }

    /**
     * Add an interval to a date string
     * @param incr : number to add
     * @param i : interval to add (day, week, month, year)
     */
    public void addInterval(int incr, String i){
        String interval = i.toLowerCase();
        switch(interval){
            case "day":
            case "days":
                addXDay(incr);
                break;
            case "week":
            case "weeks":
                addXWeek(incr);
                break;
            case "month":
            case "monthes":
                addXMonth(incr);
                break;
            case "year":
            case "years":
                addXYear(incr);
                break;
            default:
                // empty default
                break;
        }
    }

    @Override
    public void prevMonthClicked() {
        addInterval(-1, "month");
    }

    @Override
    public void nextMonthClicked() {
        addInterval(1, "month");
    }
}
