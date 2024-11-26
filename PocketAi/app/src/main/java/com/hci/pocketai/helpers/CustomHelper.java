package com.hci.pocketai.helpers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class CustomHelper {

    // Static method to show an alert dialog
    public static void showAlert(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Copy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Copy the message to the clipboard
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Copied Text", message);
                        clipboard.setPrimaryClip(clip);

                        // Show a Toast message
                        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
