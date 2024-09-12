package mir.jan.chatme;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class ProgressDialog {
    static AlertDialog getAlertDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View customLayout =
                LayoutInflater.from(activity).inflate(R.layout.custom_progress_dialog_layout, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
