/*
 * Copyright (C) 2013 - 2015. Christopher Boyd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package us.cboyd.android.dicom;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;

import org.opencv.android.OpenCVLoader;

import java.io.File;

import us.cboyd.android.shared.ExternalIO;
import us.cboyd.android.shared.files.FileAdapterOptions;

/**
 * DICOM Browser
 * 
 * @author Christopher Boyd
 * @version 0.7
 *
 */
public class DcmBrowser extends Activity implements DcmFilesFragment.OnFileSelectedListener,
        Toolbar.OnMenuItemClickListener, CompoundButton.OnCheckedChangeListener, ListView.OnItemClickListener {
    private static final String DEBUG_MODE_SETTING  = "DebugMode";
    public static final String SORT_SETTINGS  = "DcmFileSort";
    private static final String LAST_ROOT_DIRECTORY = "RootDir";
    private static final String LAST_OPEN_DIRECTORY = "OpenDir";
	private SharedPreferences mPreferences;
	private DcmFilesFragment mListFragment;
	private DcmInfoFragment mInfoFragment;
    private String          mAppName;
    private View            mSortView;
    private int             mSortSettings;
	
	// Drawer stuff
	private boolean 		mFragmented = false;
    private Toolbar         mListToolbar;
    private CharSequence    mTitle, mSubtitle;

    // Static initialization of OpenCV
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            Log.d("cpb", "No openCV");
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dcm_newfrag);

        FragmentManager fragManager = getFragmentManager();
        if (savedInstanceState != null) {
	        mListFragment = (DcmFilesFragment) fragManager.getFragment(savedInstanceState, DcmVar.FRAGLIST);
	        mInfoFragment = (DcmInfoFragment) fragManager.getFragment(savedInstanceState, DcmVar.FRAGINFO);

			// Remove existing fragments from associated views.
	    	fragManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	    	fragManager.beginTransaction().remove(mListFragment).commit();
	    	fragManager.beginTransaction().remove(mInfoFragment).commit();
	    	fragManager.executePendingTransactions();
        }
        
        // Restore the retained fragments, if this is a configuration change.
        if (mListFragment == null) {
        	mListFragment = new DcmFilesFragment();
        }
        
        if (mInfoFragment == null) {
            mInfoFragment = new DcmInfoFragment();
        }
        
        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {
        	Log.i("cpb", "mListFrag: One-pane");
        	mFragmented = true;

            // Add the fragment to the 'fragment_container' FrameLayout
        	fragManager.beginTransaction().add(R.id.fragment_container, mListFragment).commit();
        } else {
        	Log.i("cpb", "mListFrag: Two-pane");
        	mFragmented = false;
        	
            // Add the fragments to the respective FrameLayouts
        	fragManager.beginTransaction().add(R.id.fragment_left, 	mListFragment).commit();
        	fragManager.beginTransaction().add(R.id.fragment_right, mInfoFragment).commit();
        }

//        mListToolbar = mListFragment.getToolbar();
//        mListToolbar.setTitle(mAppName);
//
//        mListToolbar.setOnMenuItemClickListener(this);
        // Load the last used root directory
        mPreferences = getPreferences(MODE_PRIVATE);
        mSortSettings = mPreferences.getInt(SORT_SETTINGS, 0);
        // Ensure the list fragment has the stored settings
        mListFragment.setSortOptions(mSortSettings);
        String rootDir = mPreferences.getString(LAST_ROOT_DIRECTORY, null);
        if (rootDir != null) {
            if(mListFragment.setRoot(new File(rootDir))) {
                String openDir = mPreferences.getString(LAST_OPEN_DIRECTORY, null);
                if (openDir != null) {
                    mListFragment.setDir(new File(openDir));
                }
            }
        }

        // If debug mode is disabled, make the icon semi-transparent
        Boolean checked = mPreferences.getBoolean(DEBUG_MODE_SETTING, false);
        mInfoFragment.setMode(checked);
    }
    

    /** Called just before activity runs (after onStart). */
	@Override
	protected void onResume() {
		// If there isn't any external storage, quit the application.
		if (!ExternalIO.checkStorage()) {
            Resources res = getResources();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(res.getString(R.string.err_mesg_disk)
                    + "\n\nState: " + Environment.getExternalStorageState())
				   .setTitle(res.getString(R.string.err_title_disk))
			       .setCancelable(false)
			       .setPositiveButton(res.getString(R.string.err_close),
			    	new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                DcmBrowser.this.finish();
			           }
			       	});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
		// Else display data
		} else {
			mListFragment.onRefresh();
		}

        // If the info fragment isn't visible, remove it from the fragment manager.
        // Required because we add it in onSaveInstanceState()
        if (mFragmented && !mInfoFragment.isVisible() && mInfoFragment.isAdded()) {
            FragmentManager fragManager = getFragmentManager();
            // Remove existing fragments from associated views.
            fragManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragManager.beginTransaction().remove(mInfoFragment).commit();
            fragManager.executePendingTransactions();
        }
		super.onResume();
	}
	
	/** Called when orientation changes. */
	@Override
    public void onSaveInstanceState(Bundle outState) {
        File currDir = mListFragment.getDir();
        // If directory is null, don't save state.
        if (currDir == null)
            return;

		FragmentManager fragManager = getFragmentManager();
    	// If the fragment hasn't already been added to the FragmentManager, add it.
		// Otherwise, it can't be put in the Bundle.
    	if (!mInfoFragment.isAdded()) {
    		fragManager.beginTransaction().add(mInfoFragment, null).commit();
    	}

        // Otherwise, save the current directory.
		outState.putString(DcmVar.CURRDIR, currDir.getAbsolutePath());
    	fragManager.putFragment(outState, DcmVar.FRAGLIST, mListFragment);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
	}
   
	@Override
	public void onBackPressed() {
		File temp = mListFragment.getDir();
		if (!mListFragment.isVisible()) {
            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
			    fm.popBackStack();
                // If storage list, display the app name in title bar.
                if (mListFragment.isStorage())
                    setRoot(null);
                else
                    onFileSelected(temp);
            // If there's no back stack, call super.onBackPressed().
			} else {
				super.onBackPressed();
			}
		// If the device is on the storage list, call super.onBackPressed().
		} else if (mListFragment.isStorage()) {
			super.onBackPressed();
        // If the device is on the root directory, display the storage list.
        } else if (mListFragment.isRoot()) {
            setRoot(null);
        // Otherwise, go to parent directory.
		} else {
            onFileSelected(temp.getParentFile());
		}
	}
	
	/** onOptionsItemSelected responds to action bar item */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
	    switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                return mListFragment.navigateUp();
//            case R.id.menu_refresh:
//                // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
//                if (!mListFragment.isRefreshing()) {
//                    mListFragment.setRefreshing(true);
//                }
//
//                // Start our refresh background task
//                mListFragment.onRefresh();
//                return true;
            // Handle menu items here:
            case R.id.app_about:
                AppCompatDialog dialog = new AppCompatDialog(this);
                dialog.setContentView(R.layout.dialog_about);
                dialog.setTitle(mAppName);
                dialog.show();
                return true;

            case R.id.file_sort:
                AlertDialog sortDialog = generateSortDialog(mSortSettings);
                sortDialog.show();
                // Change the Reset button after calling show()
                sortDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // User reset the settings
                        setSortSettings(mSortView, 0);
                    }
                });
                return true;

            case R.id.debug_mode:
                boolean checked = !mInfoFragment.getMode();
                // Store the user's choice.
                mPreferences.edit().putBoolean(DEBUG_MODE_SETTING, checked).apply();
                // Change the state.
                mInfoFragment.setMode(checked);
                return true;

            default:
                return super.onOptionsItemSelected(item);
	    }
	}

    public AlertDialog generateSortDialog(int initialValues) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sort);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mSortView = getLayoutInflater().inflate(R.layout.dialog_sort, null);
        builder.setView(mSortView);

        // Add the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                // Store the user's choice.
                mSortSettings = getSortSettings(mSortView);
                mPreferences.edit().putInt(SORT_SETTINGS, mSortSettings).apply();
                mListFragment.setSortOptions(mSortSettings);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setNeutralButton("Reset", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing here because we override this button later to change the close behaviour.
                // However, we still need this because on older versions of Android unless we
                // pass a handler the button doesn't get instantiated
            }
        });

        // Set other dialog properties
        setSortSettings(mSortView, initialValues);

        // Create the AlertDialog
        return builder.create();
    }

    public int getSortSettings(View view) {
        int values = 0;
        if (isChecked(view, R.id.btn_file_first))
            values |= FileAdapterOptions.LIST_FILES_FIRST;

        if (isChecked(view, R.id.btn_file_filter))
            values |= FileAdapterOptions.NO_FILE_EXT_FILTER;
        if (isChecked(view, R.id.btn_file_visible))
            values |= FileAdapterOptions.SHOW_HIDDEN_FILES;
        if (isChecked(view, R.id.btn_folder_visible))
            values |= FileAdapterOptions.SHOW_HIDDEN_FOLDERS;

        if (isChecked(view, R.id.btn_sort_descend))
            values |= FileAdapterOptions.SORT_DESCENDING;
        values |= ((Spinner) view.findViewById(R.id.spinner_sort_method)).getSelectedItemPosition() << FileAdapterOptions.OFFSET_SORT_METHOD;
        return values;
    }

    public void setSortSettings(View view, int values) {
        ((Spinner) view.findViewById(R.id.spinner_file_first)).setOnItemSelectedListener(new SpinnerItemSelectedListener());
        CheckBox box = (CheckBox) view.findViewById(R.id.btn_file_first);
        // The OnCheckedChangeListener should set the spinner.
        box.setOnCheckedChangeListener(this);
        box.setChecked(getOption(values, FileAdapterOptions.LIST_FILES_FIRST));

        ((Spinner) view.findViewById(R.id.spinner_sort_method)).setSelection(
                values >> FileAdapterOptions.OFFSET_SORT_METHOD, false);
        ((Checkable) view.findViewById(R.id.btn_sort_descend)).setChecked(getOption(values, FileAdapterOptions.SORT_DESCENDING));
        setCheckedAlpha(view, values, FileAdapterOptions.NO_FILE_EXT_FILTER, R.id.btn_file_filter);
        setCheckedAlpha(view, values, FileAdapterOptions.SHOW_HIDDEN_FILES, R.id.btn_file_visible);
        setCheckedAlpha(view, values, FileAdapterOptions.SHOW_HIDDEN_FOLDERS, R.id.btn_folder_visible);
    }

    private class SpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(position)
            switch(parent.getId()) {
                case R.id.spinner_file_first:
                    if (mSortView != null)
                        ((Checkable) mSortView.findViewById(R.id.btn_file_first)).setChecked(position > 0);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public void setCheckedAlpha(View parent, int values, int option, int buttonId) {
        setCheckedAlpha(parent, getOption(values, option), buttonId);
    }

    public void setCheckedAlpha(View parent, boolean value, int buttonId) {
        CheckBox box = (CheckBox) parent.findViewById(buttonId);
        box.setOnCheckedChangeListener(this);
        box.setChecked(value);
        if (!value)
            box.setAlpha(0.5f);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()) {
            // If File/Folder button, switch spinner too.
            case R.id.btn_file_first:
                if (mSortView != null)
                    ((Spinner) mSortView.findViewById(R.id.spinner_file_first)).setSelection(isChecked ? 1 : 0, false);
                break;
            default:
                buttonView.setAlpha(isChecked ? 1.0f : 0.5f);
                break;
        }
    }

    public boolean isChecked(View parent, int buttonId) {
        return ((Checkable) parent.findViewById(buttonId)).isChecked();
    }

    public static boolean getOption(int values, int option) {
        return (values & option) == option;
    }

    public void setRoot(File currDir) {
        if (currDir == null) {
            mPreferences.edit().putString(LAST_ROOT_DIRECTORY, null).apply();
            onFileSelected(null);
            return;
        }
        // Check that the root directory exists and can be read.
        if (!mListFragment.setRoot(currDir))
            return;
        // Store the new root directory
        mPreferences.edit().putString(LAST_ROOT_DIRECTORY, currDir.getAbsolutePath()).apply();
        onFileSelected(currDir);
    }

    public void onFileSelected(File currFile) {
        // If this is a directory
        if (currFile == null || currFile.isDirectory()) {
            // Store the current directory
            mPreferences.edit().putString(LAST_OPEN_DIRECTORY,
                    (currFile == null) ? null : currFile.getAbsolutePath()).apply();
            mListFragment.setDir(currFile);
            mListFragment.onRefresh();

            // Setup the drawer's list view with items and click listener
//            if (mFragmented && !mListFragment.isVisible()) {
//                mInfoFragment.setDrawerList(mListFragment.getListAdapter());
//            }
            return;
        }
        // Otherwise
        String filePath = currFile.getPath();
        if (mFragmented && mListFragment.isVisible()) {
            // If we're in the one-pane layout and need to swap fragments
        	
        	// Enable the Home/Up button to allow the user to go back to
//            if (mToolbar != null)
//                actionBar.setDisplayHomeAsUpEnabled(true);

            // Create fragment and give it an argument for the selected article
            Bundle args = new Bundle();
            args.putString(DcmVar.DCMFILE, filePath);

            mInfoFragment.setArguments(args);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, mInfoFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null).commit();
            
            // Setup the drawer's ListView with items and click listener
//            mInfoFragment.setDrawerList(mListFragment.getListAdapter());
        } else {
            // If we're in the two-pane layout or already displaying the DcmInfoFragment
            // Call a method in the DcmInfoFragment to update its content
    		mInfoFragment.updateDicomInfo(filePath);
        }
    }

    /** Load the current DICOM series */
	public void load(View view) {
		// Open the DICOM Viewer
		Intent intent = new Intent(this, DcmViewer.class);
		intent.putExtra(DcmVar.DCMFILE, mInfoFragment.getDicomFile());
		startActivity(intent);
	}
	
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //TODO: If the nav drawer is open, hide action items related to the content view
//        if (mFragmented)
//        	mDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /* The click listener for ListView in the navigation drawer */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        mListFragment.onListItemClick((ListView) parent, view, position, id);
    }
}