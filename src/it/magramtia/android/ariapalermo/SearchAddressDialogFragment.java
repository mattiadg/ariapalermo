/**
 * 
 */
package it.magramtia.android.ariapalermo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

/**
 * @author mattiadigan
 *
 */
public class SearchAddressDialogFragment extends DialogFragment {

	/** 
	 * L'activity che crea un'istanza di questo Dialog deve implementare la seguente interfaccia 
	 */
	public interface SearchAddressDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);
		public void onDialogNegativeClick(DialogFragment dialog);
	}

	//Usa quest'istanza dell'interfaccia per consegnare le azioni del listener
	SearchAddressDialogListener mListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		mListener = (SearchAddressDialogListener) getActivity();
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(inflater.inflate(R.layout.address_dialog, null));

		// Add action buttons
		builder.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// Send the positive button event back to the host activity
					mListener.onDialogPositiveClick(SearchAddressDialogFragment.this);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mListener.onDialogNegativeClick(SearchAddressDialogFragment.this);
			}
		});      
		return builder.create();
	}

	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (SearchAddressDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
	}

}
