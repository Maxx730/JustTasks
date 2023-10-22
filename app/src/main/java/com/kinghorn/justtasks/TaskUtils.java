package com.kinghorn.justtasks;

import static androidx.core.util.Preconditions.checkArgument;

public class TaskUtils {
    public static String GetDateSuffix( int day) {
        switch (day) {
            case 1: case 21: case 31:
                return ("st");

            case 2: case 22:
                return ("nd");

            case 3: case 23:
                return ("rd");

            default:
                return ("th");
        }
    }
}
