package harmony.android.library.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class StandardAlertDialog {

	private static final String	TAG	= "StandardAlertDialog";

	public static void showWithDismissButton(Context c, String message) {
		new AlertDialog.Builder(c).setMessage(message).setPositiveButton("Dismiss", null).show();
	}

	public static void showWithOKButton(Context c, String message, OnClickListener onClickListener) {
		new AlertDialog.Builder(c).setMessage(message).setPositiveButton("OK", onClickListener).show();
	}
	
	public static void showWithOKButton(Context c, String message) {
		new AlertDialog.Builder(c).setMessage(message).setPositiveButton("OK", null).show();
	}

	public static void showWith2Button(Context c, String message, String messageLeft, String messageRight, OnClickListener leftBtnListener, OnClickListener rightBtnListener) {
		new AlertDialog.Builder(c).setMessage(message).setPositiveButton(messageLeft, leftBtnListener).setNegativeButton(messageRight, rightBtnListener).show();
	}

	// maintaining
	public static void showFunctionInProgressWithDismissButton(Context c) {
		new AlertDialog.Builder(c).setMessage("This function is in maintaining...").setPositiveButton("Dismiss", null).show();
	}

	public static void showFunctionIsNotImplemented(Context c) {
		new AlertDialog.Builder(c).setMessage("This function is not implemented...").setPositiveButton("Dismiss", null).show();
	}
}
