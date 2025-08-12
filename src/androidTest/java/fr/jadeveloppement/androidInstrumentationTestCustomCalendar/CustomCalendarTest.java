package fr.jadeveloppement.androidInstrumentationTestCustomCalendar;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import fr.jadeveloppement.jadcustomcalendar.CustomCalendar;

/**
 * Android Unit Test to test the public methods of CustomCalendar class
 */
@RunWith(AndroidJUnit4.class)
public class CustomCalendarTest {

    private CustomCalendar customCalendar;

    @Before
    public void setup(){
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        customCalendar = new CustomCalendar(context);
    }

    @Test
    public void testInitialDayIsToday(){
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertEquals("Initial selected day should be today", today, customCalendar.getDaySelected());
    }

    @Test
    public void setDaySelected(){
        customCalendar.setDaySelected("2025-08-25");
        assertEquals("Date should should be 2025-08-25", "2025-08-25", customCalendar.getDaySelected());
    }

    @Test
    public void getDayToday(){
        customCalendar.setDaySelected("2025-08-25");
        assertEquals("Day should be 25", "25", customCalendar.getDay());
    }

    @Test
    public void getMonthToday(){
        customCalendar.setDaySelected("2025-08-25");
        assertEquals("Month should be 08", "08", customCalendar.getMonth());
    }

    @Test
    public void getYearToday(){
        customCalendar.setDaySelected("2025-08-25");
        assertEquals("Year should be 2025", "2025", customCalendar.getYear());
    }

    @Test
    public void getDayOfWeekIndex(){
        customCalendar.setDaySelected("2025-08-25");
        assertEquals("Day of week index should be 0", 0, customCalendar.getDayOfWeekIndex());
    }

    @Test
    public void getWeekNumber(){
        customCalendar.setDaySelected("2025-08-25");
        assertEquals("Week should be 35", 35, customCalendar.getWeekNumber());
    }

    @Test
    public void getWeekRange(){
        customCalendar.setDaySelected("2025-08-25");

        String[] weekRange = customCalendar.getWeekRange();
        assertNotNull("Weekrange should not be null", weekRange);
        assertEquals("Length should be 2", 2, weekRange.length);
        assertEquals("First element should be 2025-08-25", "2025-08-25", weekRange[0]);
        assertEquals("Second element should be 2025-08-31", "2025-08-31", weekRange[1]);
    }

    @Test
    public void getListOfDaysOfWeekFromSelectedDay(){
        customCalendar.setDaySelected("2025-08-25");

        List<String> listOfDays = customCalendar.getListOfDatesOfWeek();
        assertNotNull("listOfDays should not be null", listOfDays);
        assertEquals("Length should be 7", 7, listOfDays.size());
        assertEquals("First element should be 2025-08-25", "2025-08-25", listOfDays.get(0));
        assertEquals("Second element should be 2025-08-26", "2025-08-26", listOfDays.get(1));
        assertEquals("Fourth element should be 2025-08-28", "2025-08-28", listOfDays.get(3));
        assertEquals("Fifth element should be 2025-08-29", "2025-08-29", listOfDays.get(4));
        assertEquals("Last element should be 2025-08-31", "2025-08-31", listOfDays.get(6));
    }

    @Test
    public void addIntervalMonthPositive() {
        customCalendar.setDaySelected("2025-02-01");
        customCalendar.addInterval(1, "month");
        assertEquals("Should advance date to 2025-03-01", "2025-03-01", customCalendar.getDaySelected());
    }

    @Test
    public void addIntervalMonthNegative() {
        customCalendar.setDaySelected("2025-02-01");
        customCalendar.addInterval(-2, "month");
        assertEquals("Should advance date to 2024-12-01", "2024-12-01", customCalendar.getDaySelected());
    }

    @Test
    public void addIntervalWeek() {
        customCalendar.setDaySelected("2025-02-01");
        customCalendar.addInterval(1, "week");
        assertEquals("Should advance date to 2025-02-08", "2025-02-08", customCalendar.getDaySelected());
    }

    @Test
    public void addIntervalDays() {
        customCalendar.setDaySelected("2025-02-01");
        customCalendar.addInterval(12, "days");
        assertEquals("Should advance date to 2025-02-13", "2025-02-13", customCalendar.getDaySelected());
    }

    @Test
    public void nextMonthClicked(){
        customCalendar.setDaySelected("2025-02-04");
        customCalendar.nextMonthClicked();
        assertEquals("Should be 2025-03-04", "2025-03-04", customCalendar.getDaySelected());
    }

    @Test
    public void prevMonthClicked(){
        customCalendar.setDaySelected("2025-02-25");
        customCalendar.prevMonthClicked();
        assertEquals("Should be 2025-01-25", "2025-01-25", customCalendar.getDaySelected());
    }
}