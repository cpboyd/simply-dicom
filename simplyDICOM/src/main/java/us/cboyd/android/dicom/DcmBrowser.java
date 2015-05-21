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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import us.cboyd.android.shared.list.RefreshArrayAdapter;

/**
 * DICOM Browser
 * 
 * @author Christopher Boyd
 * @version 0.7
 *
 */
public class DcmBrowser extends Activity implements DcmListFragment.OnFileSelectedListener,
        Toolbar.OnMenuItemClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String DEBUG_MODE_SETTING  = "DebugMode";
    public static final String SORT_SETTINGS  = "DcmFileSort";
    private static final String LAST_ROOT_DIRECTORY = "RootDir";
    private static final String LAST_OPEN_DIRECTORY = "OpenDir";
	private SharedPreferences mPreferences;
	private DcmListFragment mListFragment;
	private DcmInfoFragment mInfoFragment;
    private String          mAppName;
    private View            mSortView;
    private int             mSortSettings;
	
	// Drawer stuff
	private boolean 		mFragmented = false;
    private DrawerLayout 	mDrawerLayout;
    private ListView 		mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle = null;
    private Toolbar         mToolbar;
    private CharSequence    mTitle, mSubtitle, mDrawerSubtitle;

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
        setContentView(R.layout.dcm_browser);
        mAppName = getResources().getString(R.string.app_name);

        FragmentManager fragManager = getFragmentManager();
        if (savedInstanceState != null) {
	        mListFragment = (DcmListFragment) fragManager.getFragment(savedInstanceState, DcmVar.FRAGLIST);
	        mInfoFragment = (DcmInfoFragment) fragManager.getFragment(savedInstanceState, DcmVar.FRAGINFO);

			// Remove existing fragments from associated views.
	    	fragManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	    	fragManager.beginTransaction().remove(mListFragment).commit();
	    	fragManager.beginTransaction().remove(mInfoFragment).commit();
	    	fragManager.executePendingTransactions();
        }
        
        // Restore the retained fragments, if this is a configuration change.
        if (mListFragment == null) {
        	mListFragment = new DcmListFragment();
        }
        
        if (mInfoFragment == null) {
            mInfoFragment = new DcmInfoFragment();
        }
        
        // Specify that the Home/Up button should not be enabled,
        // since there is no hierarchical parent yet.
        mToolbar = (Toolbar) findViewById(R.id.dcmBrowser_toolbar);
        mToolbar.setTitle(mAppName);

        mToolbar.setOnMenuItemClickListener(this);
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
                    mToolbar.setSubtitle(getFolderTitle(rootDir, openDir));
                }
            }
        }
        
        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {
        	Log.i("cpb", "mListFrag: One-pane");
        	mFragmented = true;

            // Add the fragment to the 'fragment_container' FrameLayout
        	fragManager.beginTransaction().add(R.id.fragment_container, mListFragment).commit();
            
            generateDrawer(mToolbar);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mToolbar.inflateMenu(R.menu.file_list);
        } else {
        	Log.i("cpb", "mListFrag: Two-pane");
        	mFragmented = false;
        	
            // Add the fragments to the respective FrameLayouts
        	fragManager.beginTransaction().add(R.id.fragment_left, 	mListFragment).commit();
        	fragManager.beginTransaction().add(R.id.fragment_right, mInfoFragment).commit();
        }
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
			mListFragment.refresh();
		}

        // If the info fragment isn't visible, remove it from the fragment manager.
        // Required because we add it in onSaveInstanceState()
        if (!mInfoFragment.isVisible() && mInfoFragment.isAdded()) {
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
            if (mToolbar != null) {
                // Assume we're jumping back to the ListFragment
                displayFileListMenu();
                mDrawerToggle.setDrawerIndicatorEnabled(false);

                // Reset the Toolbar's title
                mToolbar.setTitle(mAppName);
            }
            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
			    fm.popBackStack();
                // If storage list, display the app name in title bar.
                if (mListFragment.isStorage())
                    resetTitleAndSubtitle();
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
            resetTitleAndSubtitle();
        // Otherwise, go to parent directory.
		} else {
            onFileSelected(temp.getParentFile());
		}
	}
	
	/** onOptionsItemSelected responds to action bar item */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (!mListFragment.isVisible() && mDrawerToggle.onOptionsItemSelected(item)) {
	       return true;
		}
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
                Dialog dialog = new Dialog(this);
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
                boolean checked = !item.isChecked();
                // Store the user's choice.
                mPreferences.edit().putBoolean(DEBUG_MODE_SETTING, checked).apply();
                // Change the state.
                setDebugState(item, checked);
                return true;

            default:
                return super.onOptionsItemSelected(item);
	    }
	}

    public AlertDialog generateSortDialog(int initialValues) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sort_extension);

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

    public String getFolderTitle(String rootPath, String currDirPath) {
        return (currDirPath == null) ? "" : currDirPath.replaceFirst(rootPath, "");
    }
	
	public String getFolderTitle(File currDir) {
        return (currDir == null) ? ""
                : currDir.getAbsolutePath().replaceFirst(mListFragment.getRoot().getAbsolutePath(), "");
	}

    // Display the app name as the title.
    public void resetTitleAndSubtitle() {
        setTitleAndSubtitle(null, null);
    }

    public void setTitleAndSubtitle(String displayName, File currDir) {
        // If displayName is null, display the app's name.
        if (displayName == null)
            displayName = mAppName;
        // Store the new root directory
        mPreferences.edit().putString(LAST_ROOT_DIRECTORY,
                (currDir == null) ? null : currDir.getAbsolutePath()).apply();
        mToolbar.setTitle(displayName);
        onFileSelected(currDir);
    }

    public void onFileSelected(File currFile) {
        // If this is a directory
        if (currFile == null || currFile.isDirectory()) {
            // Store the current directory
            mPreferences.edit().putString(LAST_OPEN_DIRECTORY,
                    (currFile == null) ? null : currFile.getAbsolutePath()).apply();
            mListFragment.setDir(currFile);
            mListFragment.refresh();
            mToolbar.setSubtitle(getFolderTitle(currFile));

            // Setup the drawer's list view with items and click listener
            if (!mListFragment.isVisible()) {
                mDrawerList.setAdapter(mListFragment.getListAdapter());
                mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
            }
            return;
        }
        // Otherwise
        String filePath = currFile.getPath();
        if (mFragmented && mListFragment.isVisible()) {
            // If we're in the one-pane layout and need to swap fragments
            displayDcmInfoMenu();
        	
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
            mDrawerList.setAdapter(mListFragment.getListAdapter());
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        } else {
            // If we're in the two-pane layout or already displaying the DcmInfoFragment
            // Call a method in the DcmInfoFragment to update its content
    		mInfoFragment.updateDicomInfo(filePath);
        }
        mTitle = currFile.getName();
        mSubtitle = getFolderTitle(currFile.getParentFile());
        mToolbar.setTitle(mTitle);
        mToolbar.setSubtitle(mSubtitle);
    }

    // Clear the Toolbar's existing menu and inflate the file list menu
    private void displayFileListMenu() {
        mToolbar.getMenu().clear();
        mToolbar.inflateMenu(R.menu.file_list);
    }

    // Clear the Toolbar's existing menu and inflate the new one
    private void displayDcmInfoMenu() {
        mToolbar.getMenu().clear();
        mToolbar.inflateMenu(R.menu.dcm_preview);
        // If debug mode is disabled, make the icon semi-transparent
        MenuItem item = mToolbar.getMenu().findItem(R.id.debug_mode);
        Boolean checked = mPreferences.getBoolean(DEBUG_MODE_SETTING, false);
        setDebugState(item, checked);
    }

    // Have to manually handle MenuItem icon state changes.
    private void setDebugState(MenuItem item, boolean checked) {
        item.setChecked(checked);
        if (checked) {
            item.setIcon(R.drawable.ic_visibility_white_24dp);
        } else {
            item.setIcon(R.drawable.ic_visibility_off_white_24dp);
            item.getIcon().setAlpha(128);
        }
        mInfoFragment.changeMode(checked);
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
    
    private void generateDrawer(Toolbar toolbar) {
        // If the toolbar is null, try to find it.
        if (toolbar == null)
            toolbar = (Toolbar) findViewById(R.id.dcmBrowser_toolbar);

        // Drawer stuff
    	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // If API < 21, set a custom shadow that overlays the main content when the drawer opens
        if (Build.VERSION.SDK_INT < 21)
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                mDrawerSubtitle = mToolbar.getSubtitle();
                mToolbar.setTitle(mTitle);
                mToolbar.setSubtitle(mSubtitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                mToolbar.setTitle(mAppName);
                if (mDrawerSubtitle != null)
                    mToolbar.setSubtitle(mDrawerSubtitle);
                // Refresh the drawer list.
                ((RefreshArrayAdapter<File>) mDrawerList.getAdapter()).onRefresh();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mListFragment.onListItemClick((ListView) parent, view, position, id);
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mFragmented) {
        	if (mDrawerToggle == null)
        		generateDrawer(mToolbar);
        	mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        if (mFragmented)
        	mDrawerToggle.onConfigurationChanged(newConfig);
    }
}