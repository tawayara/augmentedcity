package edu.dhbw.andar;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This class creates the base behavior for a Fragment that uses the augmented reality functionality
 * from AndAR.
 */
public class AndARFragment extends Fragment {

	// Stores the AndARView instance that is being used by the Fragment
	private AndARView andarView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Create the view to be presented by the Fragment
		this.andarView = new AndARView();
		return this.andarView.createView(this.getActivity());
	}

	@Override
	public void onPause() {
		this.andarView.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		this.andarView.resume();
		super.onResume();
	}

	/**
	 * Retrieve the AndARView instance that is being used by the Fragment.
	 * 
	 * @return The AndarView instance.
	 */
	public AndARView getAndARView() {
		return this.andarView;
	}

}
