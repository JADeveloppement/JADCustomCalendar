package fr.jadeveloppement.jadcustomcalendar;

import android.graphics.Color;

public class Variables {
    private static final String TAG = "JADCustomCalendar";

    public enum CalendarColor {
        ORANGE, RED, TRANSPARENT, BLACK, WHITE, BLUE
    };

    public static int getColor(Enum color){
        if (color == CalendarColor.ORANGE)
            return Color.parseColor("#FFA500");
        else if (color == CalendarColor.RED)
            return Color.parseColor("#B22222");
        else if (color == CalendarColor.BLUE)
            return Color.parseColor("#4169E1");
        else if (color == CalendarColor.TRANSPARENT)
            return Color.parseColor("#00FFFFFF");
        else if (color == CalendarColor.WHITE)
            return Color.parseColor("#FFFFFF");
        else if (color == CalendarColor.BLACK)
            return Color.parseColor("#000000");
        else return Color.parseColor("#000000");
    }

    public static final String[] monthesNamesLong = {
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre",
            "Novembre", "Décembre"
    };

    public static final String[] daysShortNames = {
            "L", "M", "M", "J", "V", "S", "D"
    };
}
