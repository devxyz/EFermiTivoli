package it.gov.fermitivoli.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by stefano on 02/07/15.
 */
public class C_MyDate implements Comparable<C_MyDate> {
    final static String[] giornoSettimana = new String[]{"??", "DOM", "LUN", "MAR", "MER", "GIO", "VEN", "SAB"};
    private final Date dateMidnight;
    private final Calendar c;
    private final String code;

    public C_MyDate(Date d) {
        c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        this.dateMidnight = c.getTime();

        code = new SimpleDateFormat("yyyy/MM/dd").format(dateMidnight);
    }

    public C_MyDate(int day, int month, int year) {
        c = Calendar.getInstance();

        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        this.dateMidnight = c.getTime();
        code = new SimpleDateFormat("yyyy/MM/dd").format(dateMidnight);
    }

    public static void main(String[] args) {
        final C_MyDate o1 = new C_MyDate(new Date());
        final C_MyDate o2 = new C_MyDate(3, 7, 2015);
        System.out.println(o1);
        System.out.println(o2);
        System.out.println(o1.equals(o2));
        System.out.println(o1.compareTo(o2));
    }

    public String giornoSettimana() {
        final int i = c.get(Calendar.DAY_OF_WEEK);
        return giornoSettimana[i];
    }

    public Date getDate() {
        return dateMidnight;
    }

    public int year() {
        return c.get(Calendar.YEAR);
    }

    public int month() {
        return c.get(Calendar.MONTH) + 1;
    }

    public int day() {
        return c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        C_MyDate myDate = (C_MyDate) o;

        return dateMidnight.equals(myDate.dateMidnight);

    }

    public String toDDMMYYYY() {
        return new SimpleDateFormat("dd/MM/yyyy").format(dateMidnight);
    }

    public String toYYYYMMDD() {
        return code;
    }

    public String toString() {
        return toDDMMYYYY();
    }

    @Override
    public int hashCode() {
        return code.hashCode();

    }

    @Override
    public int compareTo(C_MyDate another) {
        return code.compareTo(another.code);
    }
}
