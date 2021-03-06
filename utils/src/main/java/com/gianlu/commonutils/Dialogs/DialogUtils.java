package com.gianlu.commonutils.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.Toaster;


public class DialogUtils {
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void showDialog(@Nullable Activity activity, @NonNull AlertDialog.Builder builder) {
        if (activity == null) return;
        activityWithDialog(activity).showDialog(builder);
    }

    public static void showDialog(@Nullable Activity activity, @NonNull Dialog dialog) {
        if (activity == null) return;
        activityWithDialog(activity).showDialog(dialog);
    }

    public static void showDialog(@Nullable FragmentActivity activity, @NonNull DialogFragment dialog) {
        if (activity == null) return;

        FragmentManager manager = activity.getSupportFragmentManager();
        try {
            dialog.show(manager, null);
        } catch (IllegalStateException ex) {
            Logging.log(ex); // We can't do nothing
        }
    }

    @NonNull
    private static ActivityWithDialog activityWithDialog(@NonNull Activity activity) {
        if (activity instanceof ActivityWithDialog)
            return (ActivityWithDialog) activity;
        else
            throw new IllegalArgumentException("Activity is not a subclass of ActivityWithDialog: " + activity);
    }

    public static void dismissDialog(@Nullable Activity activity) {
        if (activity == null) return;
        activityWithDialog(activity).dismissDialog();
    }

    public static boolean isContextValid(Context context) {
        return context != null && context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed();
    }

    public static void showDialogValid(@NonNull final Context context, @NonNull final Dialog dialog) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!isContextValid(context)) return;
                dialog.show();
            }
        });
    }

    public static void showDialogValid(@NonNull final Context context, @NonNull final AlertDialog.Builder builder, final OnDialogCreatedListener listener) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!isContextValid(context)) return;
                Dialog dialog = builder.create();
                if (listener != null) listener.created(dialog);
                dialog.show();
            }
        });
    }

    public static void showToast(Context context, @NonNull Toaster toaster) {
        if (context == null) return;
        toaster.show(context);
    }

    @NonNull
    private static ProgressDialog progressDialog(@NonNull Context context, @NonNull String message) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage(message);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        return pd;
    }

    @NonNull
    public static ProgressDialog progressDialog(@NonNull Context context, @StringRes int message) {
        return progressDialog(context, context.getString(message));
    }

    public static boolean hasVisibleDialog(@Nullable Activity activity) {
        return activity != null && activityWithDialog(activity).hasVisibleDialog();
    }

    public interface OnDialogCreatedListener {
        @UiThread
        void created(@NonNull Dialog dialog);
    }
}
