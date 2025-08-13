package fr.jadeveloppement.jadcustomcalendar;

import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.jadeveloppement.jadcustomcalendar.components.DayComponent;
import fr.jadeveloppement.jadcustomcalendar.components.DaysShortNamesComponents;
import fr.jadeveloppement.jadcustomcalendar.components.MonthComponent;
import fr.jadeveloppement.jadcustomcalendar.components.WeekComponent;

public class CustomCalendar extends LinearLayout implements
        MonthComponent.MonthLayoutClicked, DayComponent.DayClicked {

    private final String TAG = "JADCustomCalendar";
    private DateChanged listener;
    private DayComponent selectedDayComponent = null;
    private Context context;
    private String selectedDay;
    private LinearLayout layoutCustomCalendarDaysContainer;
    private MonthComponent monthComponent;
    private List<DayComponent> daysComponentList, daysComponentActuallyDisplayed;
    private List<String> daysActuallyDisplayed;
    private AttributeSet attributeSet = null;
    private Integer defStyleAttribute = null;

    public interface DateChanged {
        void selectedDayChanged();
    }

    /**
     * Initializes calendar : no action available with this public constructor
     * @param c : context of the application
     */
    public CustomCalendar(@NonNull Context c){
        super(c);

        init(c, null, 0, null);
    }

    public CustomCalendar(@NonNull Context c, DateChanged l){
        super(c);

        init(c, null, 0, l);
    }

    public CustomCalendar(Context c, AttributeSet attrs) {
        super(c, attrs);

        init(c, attrs, 0, null);
    }

    public CustomCalendar(Context c, AttributeSet attrs, int defStyleAttr) {
        super(c, attrs, defStyleAttr);

        init(c, attrs, defStyleAttr, null);
    }

    private void initVariables(){
        this.selectedDay = getTodayDate();
    }

    private void init(Context c, AttributeSet attrS, int defStyleAttr, DateChanged l) {
        this.listener = l;
        this.context = c;
        this.attributeSet = attrS;
        this.defStyleAttribute = defStyleAttr;

        initVariables();
        initCalendar();
    }

    private void initCalendar() {
        LayoutInflater.from(context).inflate(R.layout.layout_custom_calendar, this, true);

        LinearLayout calendarMonthLayoutContainer = findViewById(R.id.layoutCustomCalendarMonthContainer);
        LinearLayout layoutCustomCalendarShortDaysContainer = findViewById(R.id.layoutCustomCalendarShortDaysContainer);
        this.layoutCustomCalendarDaysContainer = findViewById(R.id.layoutCustomCalendarDaysContainer);

        monthComponent = new MonthComponent(context, selectedDay.substring(0, 7), calendarMonthLayoutContainer, this);
        calendarMonthLayoutContainer.addView(monthComponent.getLayout());

        DaysShortNamesComponents daysShortNamesComponents = new DaysShortNamesComponents(context, layoutCustomCalendarShortDaysContainer);
        layoutCustomCalendarShortDaysContainer.addView(daysShortNamesComponents.getLayout());

        setDaysLayout();
    }

    private void setDaysLayout(){
        layoutCustomCalendarDaysContainer.removeAllViews();
        daysActuallyDisplayed = new ArrayList<>();
        daysComponentActuallyDisplayed = new ArrayList<>();
        int daysOfMonth = getDaysInMonth(getYear() + "-" + getMonth() + "-01");
        int firstDayOfWeek = getDayOfWeekIndex(getYear() + "-" + getMonth() + "-01");

        int dayOfMonth = 1;

        for (int week = 0; week < (int) Math.ceil((firstDayOfWeek + daysOfMonth) / 7.0); week++) {
            daysComponentList = new ArrayList<>();
            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                DayComponent dayComponent = new DayComponent(context, layoutCustomCalendarDaysContainer, this, this.attributeSet, this.defStyleAttribute);
                if ((week == 0 && dayOfWeek >= firstDayOfWeek) || (week > 0 && dayOfMonth <= daysOfMonth)){
                    String dayOfMonthStr = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                    String dateOfComponent = getYear() + "-" + getMonth() + "-" + dayOfMonthStr;

                    dayComponent.setDayDate(dateOfComponent);
                    daysActuallyDisplayed.add(dateOfComponent);
                    daysComponentActuallyDisplayed.add(dayComponent);

                    if (dateOfComponent.equalsIgnoreCase(selectedDay)){
                        dayComponent.setActive(true);
                        selectedDayComponent = dayComponent;
                    }

                    dayOfMonth++;
                }
                daysComponentList.add(dayComponent);
            }
            WeekComponent weekComponent = new WeekComponent(context, daysComponentList, layoutCustomCalendarDaysContainer);
            layoutCustomCalendarDaysContainer.addView(weekComponent.getLayout());
        }
    }

    public void setListener(DateChanged l){
        this.listener = l;
    }

    public void putBadgeToDates(String[] dates){
        for(String date : dates){
            int indexOfDay = daysActuallyDisplayed.indexOf(date);

        }
    }

    // Basic date manipulation methods

    /**
     * get selected day of the calendar
     * @return : selected day (first day = 1 )
     */
    public String getDay(){
        return selectedDay.split("-")[2];
    }

    /**
     * get selected month of the calendar
     * @return : selected month (first month = 1 )
     */
    public String getMonth(){
        return selectedDay.split("-")[1];
    }

    /**
     * get selected year of the calendar
     * @return : selected year
     */
    public String getYear(){
        return selectedDay.split("-")[0];
    }

    /**
     * Get the week's number of the selected date
     * @return : week number of the selected date
     */
    public int getWeekNumber(){
        return getWeekNumberFromDate(selectedDay);
    }

    /**
     * get the index of the day selected in the week
     * @return : index of the day selected in the week
     */
    public int getDayOfWeekIndex(){
        return getDayOfWeekIndex(selectedDay);
    }

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
        LocalDate startDate = LocalDate.of(parseInt(getYear()), parseInt(getMonth()), parseInt(getDay()))
                .with(weekFields.weekOfYear(), getWeekNumberFromDate(selectedDay))
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

        LocalDate startDate = LocalDate.of(parseInt(getYear()), 1, 1)
                .with(weekFields.weekOfYear(), getWeekNumberFromDate(selectedDay))
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
     * Set the day selected in the daysLayout
     * @param dayS : date to select (YYYY-MM-DD)
     */
    public void setDaySelected(String dayS){
        String newYear = dayS.split("-")[0];
        String newMonth = dayS.split("-")[1];

        String oldYear = selectedDay.split("-")[0];
        String oldMonth = selectedDay.split("-")[1];

        selectedDay = dayS;

        if (!oldMonth.equalsIgnoreCase(newMonth) || !oldYear.equalsIgnoreCase(newYear)) {
            updateMonthUI();
        } else {
            updateDaysUI();
        }

        if (!isNull(listener)) listener.selectedDayChanged();
    }

    /**
     * Update the selected day component
     */
    private void updateDaysUI() {
        selectedDayComponent.setActive(false);
        int indexOfDayToSelect = daysActuallyDisplayed.indexOf(selectedDay);
        DayComponent dayComponentSelected = daysComponentActuallyDisplayed.get(indexOfDayToSelect == -1 ? 0 : indexOfDayToSelect);
        dayComponentSelected.setActive(true);
        selectedDayComponent = dayComponentSelected;
    }

    /**
     * Update month component and days component if month changed
     */
    private void updateMonthUI() {
        if (!isNull(monthComponent)) monthComponent.updateMonthName(selectedDay.substring(0, 7));
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
        LocalDate newDate = date.plusWeeks(x);
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
     * Add an interval to the selected date from the calendar
     * @param incr : number to add
     * @param i : interval to add (day, week, month, year)
     * @return : new selectedDate
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

    @Override
    public void dayClicked(String newDay, DayComponent day) {
        day.setActive(true);
        if (!isNull(selectedDayComponent) && !newDay.equalsIgnoreCase(selectedDay)){
            selectedDayComponent.setActive(false);
            selectedDayComponent = day;
            selectedDay = selectedDayComponent.getDayDate();
        }

        if (!isNull(listener)) listener.selectedDayChanged();
    }
}
