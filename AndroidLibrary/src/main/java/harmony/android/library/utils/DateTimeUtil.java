package harmony.android.library.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class is not refactored yet.
 * 
 * @author zelic
 * 
 */

public class DateTimeUtil {
	public static String getHourString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("hh:mm", Locale.getDefault());
		return format.format(date);
	}

	public static String getAMPMString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("aa", Locale.getDefault());
		return format.format(date);
	}

	public static String getDateString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		return format.format(date);
	}

	public static String getDisplayDateString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
		return format.format(date);
	}

	public static String getDisplayByFormat(Date date, String formatType) {
		SimpleDateFormat format = new SimpleDateFormat(formatType, Locale.getDefault());
		return format.format(date);
	}

	public static String getDisplayDayMonthYearString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
		return format.format(date);
	}

	public static double dateDiff(Date from, Date to) {
		long diff = convertToUnixTime(to) - convertToUnixTime(from);
		return (double) diff / 86400000;
	}

	public static Date convertFromUnixTime(long dateLong) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateLong * 1000);
		return cal.getTime();
	}

	public static Date convertFromMiliUnixTime(long dateLong) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateLong);
		return cal.getTime();
	}

	public static long convertToUnixTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getTimeInMillis() / 1000;
	}

	public static Date beginOfDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	public static String getFullDisplayDateString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM, yyyy", Locale.getDefault());
		return format.format(date);
	}

	public static String getDisplayDateTimeString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM, yyyy 'at' hh:mm aa", Locale.getDefault());
		return format.format(date);
	}

	public static String getShortDisplayDateString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MMM dd", Locale.getDefault());
		return format.format(date);
	}

	public static String getDayDateString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("EEEE dd/MM/yyyy", Locale.getDefault());
		return format.format(date);
	}

	public static String getStoreDateString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		return format.format(date);
	}

	public static Date getDateFromString(String dateString) {

		String[] parts = dateString.split("[-]");

		if (parts != null && parts.length == 3) {
			Calendar cal = Calendar.getInstance(Locale.getDefault());
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));
			cal.set(Calendar.MONTH, Integer.parseInt(parts[1]) - 1);
			cal.set(Calendar.YEAR, Integer.parseInt(parts[0]));
			return cal.getTime();
		}
		return null;
	}

	public static Date getDateFromComponent(int year, int month, int day) {

		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}

	public static Date setDateWithTime(Date date, int hour, int minute) {

		Calendar cal = Calendar.getInstance(Locale.getDefault());
		if (date != null) {
			cal.setTime(date);
		}
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		return cal.getTime();
	}

	public static int getCurrentDay() {
		Calendar cal = Calendar.getInstance();

		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public static int getCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH) + 1;
	}

	public static int getCurrentYear() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR);
	}

	public static int getDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public static int getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}

	public static String getMonthString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MMM");
		return format.format(date);
	}

	public static String getDayOfWeek(Date date, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (day > 0) {
			cal.set(Calendar.DAY_OF_MONTH, day);
		}
		SimpleDateFormat format = new SimpleDateFormat("EEE");
		return format.format(cal.getTime()).toUpperCase();
	}

	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	public static int getMonthFromDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}

	public static int getYearFromDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	public static List<Date> convertFromListString(List<String> dateStrings) {
		List<Date> ret = new ArrayList<Date>();
		if (dateStrings != null && dateStrings.size() > 0) {
			for (String dateString : dateStrings) {
				ret.add(getDateFromString(dateString));
			}
		}
		return ret;
	}

	public static boolean isSunday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	public static boolean isSaturday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
	}

	public static String getGreetingBasedOnTime(long dateTime) {
		Date time = convertFromMiliUnixTime(dateTime);
		if (time.getHours() < 12) {
			return "Good Morning, %s!";
		} else if (time.getHours() < 17) {
			return "Good Afternoon, %s!";
		} else {
			return "Good Evening, %s!";
		}
	}

	public static String getYearMonthString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy MMM", Locale.getDefault());
		return format.format(date).toUpperCase();
	}

	public static Date increaseMonth(Date date, Date maxDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 1);
		if (maxDate != null) {
			Calendar minCal = Calendar.getInstance();
			minCal.setTime(maxDate);
			if (minCal.before(cal)) {
				return date;
			}
		}
		return cal.getTime();
	}

	public static Date decreaseMonth(Date date, Date minDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);
		if (minDate != null) {
			Calendar minCal = Calendar.getInstance();
			minCal.setTime(minDate);
			if (minCal.before(cal)) {
				return date;
			}
		}
		return cal.getTime();
	}

	public static Date increaseDay(Date date, Date maxDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		if (maxDate != null) {
			Calendar minCal = Calendar.getInstance();
			minCal.setTime(maxDate);
			if (minCal.before(cal)) {
				return date;
			}
		}
		return cal.getTime();
	}

	public static Date decreaseDay(Date date, Date minDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		if (minDate != null) {
			Calendar minCal = Calendar.getInstance();
			minCal.setTime(minDate);
			if (minCal.before(cal)) {
				return date;
			}
		}
		return cal.getTime();
	}

	/**
	 * For DISCO use only
	 * 
	 * @param date
	 * @return
	 */
	public static int getNumberOfDaysInCurrentMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (cal.get(Calendar.MONTH) + 1 != getCurrentMonth()) {
			return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public static int getMaxDaysNumberInMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static int getNumberOfDaysLeftInCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		return cal.getActualMaximum(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH);
	}

	public static String getCurrentDateWithFormat(String formatDate) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df1 = new SimpleDateFormat(formatDate);
		String date = df1.format(c.getTime());
		return date;
	}
}
