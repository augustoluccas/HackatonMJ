package br.org.projeto.vigilante.util;

import android.content.Context;
import android.location.Address;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class AppUtil {

    public static void closeKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getAddressFormatted(Address address) {
        if (address != null) {

            // Street, number, neighborhood, city - state
            StringBuilder builder = new StringBuilder();
            builder.append(TextUtils.isEmpty(address.getThoroughfare()) ? "" : address.getThoroughfare() + ", ");
            builder.append(TextUtils.isEmpty(address.getSubThoroughfare()) ? "" : address.getSubThoroughfare() + ", ");
            builder.append(TextUtils.isEmpty(address.getSubLocality()) ? "" : address.getSubLocality() + ", ");
            builder.append(TextUtils.isEmpty(address.getLocality()) ? "" : address.getLocality() + " - ");
            builder.append(TextUtils.isEmpty(address.getAdminArea()) ? "" : address.getAdminArea());
            return builder.toString();
        }
        return "";
    }

    public static void showToast(final Context context, @StringRes final int message) {
        if (message > 0) {
            Toast.makeText(context.getApplicationContext(), context.getString(message), Toast.LENGTH_SHORT).show();
        }
    }


    public static String getStateCityNull(String local) {
        return TextUtils.isEmpty(local) ? "Não disponível" : local;
    }

}
