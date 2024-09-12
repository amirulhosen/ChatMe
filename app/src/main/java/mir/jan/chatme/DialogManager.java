package mir.jan.chatme;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class DialogManager {
    public static EventListener listener;

    public static AlertDialog buildDialog(Activity activity, String header, String message, String positiveText, @Nullable String nagativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Set the message show for the Alert time
        builder.setMessage(message);

        // Set Alert Title
        builder.setTitle(header);

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            listener.onPositiveClick();
        });

        builder.setNegativeButton(nagativeText, (dialog, which) -> {
            listener.onNegativeClick();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        return alertDialog;
    }
}
