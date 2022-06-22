package ru.jamsys;

import java.lang.reflect.Array;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Util {

    public static <T>T[] splice(final T[] array, int start) {
        if (start < 0)
            start += array.length;

        return splice(array, start, array.length - start);
    }

    @SuppressWarnings("unchecked")
    public static <T>T[] splice(final T[] array, int start, final int deleteCount) {
        if (start < 0)
            start += array.length;

        final T[] spliced = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length - deleteCount);
        if (start != 0)
            System.arraycopy(array, 0, spliced, 0, start);

        if (start + deleteCount != array.length)
            System.arraycopy(array, start + deleteCount, spliced, start, array.length - start - deleteCount);

        return spliced;
    }

    @SuppressWarnings("unchecked")
    public static <T>T[] splice(final T[] array, int start, final int deleteCount, final T ... items) {
        if (start < 0)
            start += array.length;

        final T[] spliced = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - deleteCount + items.length);
        if (start != 0)
            System.arraycopy(array, 0, spliced, 0, start);

        if (items.length > 0)
            System.arraycopy(items, 0, spliced, start, items.length);

        if (start + deleteCount != array.length)
            System.arraycopy(array, start + deleteCount, spliced, start + items.length, array.length - start - deleteCount);

        return spliced;
    }

    public static String join(final String[] elements, String delimiter){
        return String.join(delimiter, elements);
    }

    public static boolean check(String value, String pattern){
        try {
            return Pattern.matches(pattern, value);
        } catch(Exception e) {
            return false;
        }
    }
}
