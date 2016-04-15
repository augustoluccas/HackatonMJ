package br.org.projeto.vigilante.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.org.projeto.vigilante.R;

public class FormatUtil {

    public static String formatDate(Date date) {
        return new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(date);
    }

    public static String formatTime(Date date) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }

    public static String formatDateTime(Date date) {
        return new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(date);
    }

    public static SpannableStringBuilder setBold(Context context, String text) {

        //noinspection deprecation
        final int color = context.getResources().getColor(R.color.colorAccent);
        final int endIndex = text.indexOf("\n");

        SpannableStringBuilder appName = new SpannableStringBuilder(text);
        appName.setSpan(new ForegroundColorSpan(color), 0, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        appName.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return appName;
    }

}
