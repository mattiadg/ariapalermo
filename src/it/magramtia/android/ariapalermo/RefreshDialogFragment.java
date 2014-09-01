package it.magramtia.android.ariapalermo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class RefreshDialogFragment extends DialogFragment {
	
	public interface RefreshDialogFragmentListener{
		public void onPositiveClick();
	}
	
	RefreshDialogFragmentListener mListener;

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        mListener = (RefreshDialogFragmentListener) getActivity();
        builder.setMessage(R.string.error_dialog_message)
               .setPositiveButton(R.string.error_dialog_button, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       mListener.onPositiveClick();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
