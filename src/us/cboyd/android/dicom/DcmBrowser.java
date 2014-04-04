/*
 * Copyright (C) 2013-2014 Christopher Boyd
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
 * 
 */

package us.cboyd.android.dicom;

import java.io.File;
import java.util.ArrayList;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import us.cboyd.android.shared.ExternalIO;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * DICOM Browser
 * 
 * @author Christopher Boyd
 * @version 0.3
 *
 */
public class DcmBrowser extends FragmentActivity 
        implements DcmListFragment.OnFileSelectedListener {
	
	private DcmListFragment mListFragment;
	private DcmInfoFragment mInfoFragment;
	
	// Drawer stuff
	private boolean 		mDrawerOpen = false;
	private boolean 		mFragmented = false;
    private DrawerLayout 	mDrawerLayout;
    private ListView 		mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle = null;

    private CharSequence 	mDrawerTitle;
    private CharSequence 	mTitle;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dcm_browser);
        
        // Specify that the Home/Up button should not be enabled,
        // since there is no hierarchical parent yet.
        ActionBar actionBar = getActionBar();
        // enable ActionBar app icon to behave as action to toggle nav drawer
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {
        	mFragmented = true;
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of DcmListFragment & DcmInfoFragment
            mListFragment = new DcmListFragment();
            mInfoFragment = new DcmInfoFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            mListFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
            		.add(R.id.fragment_container, mListFragment).commit();
            
            generateDrawer();
        } else {
        	FragmentManager fragManager = getFragmentManager();
        	mListFragment = (DcmListFragment) fragManager.findFragmentById(R.id.dcmlist_fragment);
        	mInfoFragment = (DcmInfoFragment) fragManager.findFragmentById(R.id.dcminfo_fragment);
        }
    }
    

    /** Called just before activity runs (after onStart). */
	@Override
	protected void onResume() {
		// If there isn't any external storage, quit the application.
		if (!ExternalIO.checkStorage()) {
            Resources res = getResources();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(res.getString(R.string.err_mesg_disk))
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
			mListFragment.setDir();
		}

		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mLoaderCallback);
		super.onResume();
	}
	
	////openCV
   private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
       @Override
       public void onManagerConnected(int status) {
           switch (status) {
               case LoaderCallbackInterface.SUCCESS:
               {
	               Resources res = getResources();
	               Log.i(res.getString(R.string.tag_ocv), res.getString(R.string.ocv_load));
               } break;
               default:
               {
                   super.onManagerConnected(status);
               } break;
           }
       }
   };
   
	@Override
	public void onBackPressed() {
		File temp = mListFragment.getDir();
		if (!mListFragment.isVisible()) {
			// Assume we're jumping back to the ListFragment
			if (ExternalIO.isRoot(temp)) {
				ActionBar actionBar = getActionBar();
		        actionBar.setHomeButtonEnabled(false);
		        actionBar.setDisplayHomeAsUpEnabled(false);
			}
			FragmentManager fm = getFragmentManager();
			if (fm.getBackStackEntryCount() > 0) {
			    fm.popBackStack();
			} else {
				super.onBackPressed();
			}
			
		// If the directory is the external storage directory or there is no parent,
		// super.onBackPressed(). Else go to parent directory.
		} else if (ExternalIO.isRoot(temp)) {
			super.onBackPressed();
		} else {
			temp = temp.getParentFile();
			mListFragment.setDir(temp);
		}
		onDirectorySelected(temp);
	}
	
	/** onOptionsItemSelected responds to action bar item */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (!mListFragment.isVisible() && mDrawerToggle.onOptionsItemSelected(item)) {
	       return true;
		}
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	    	return navigateUp();
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	/** navigateUp replicates "Back" functionality for the Home/Up key. */
	public boolean navigateUp() {
		File temp = mListFragment.getDir();
		if (mListFragment.isVisible()) {
			if (ExternalIO.isRoot(temp)) {
				ActionBar actionBar = getActionBar();
		        actionBar.setHomeButtonEnabled(false);
		        actionBar.setDisplayHomeAsUpEnabled(false);
		        return false;
			} else {
				temp = temp.getParentFile();
				mListFragment.setDir(temp);
			}
		}
		onDirectorySelected(temp);
		return true;
	}

	/** onCreateOptionsMenu generates an options menu on the action bar */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.dcm_main, menu);
	    // Need to manually add icons to the Options menu above API v11
	    //menu.getItem(R.id.about).setIcon(R.drawable.ic_action_about);
	    return true;
	}

	/** onMenuItemSelected handles if something from the options menu is selected */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch (item.getItemId()) {
			
		case R.id.app_about:
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog_about);
       		dialog.setTitle(getResources().getString(R.string.app_name));
       		dialog.show();
			return true;
			
		case R.id.show_hidden:
			item.setChecked(!item.isChecked());
			mListFragment.setHidden(item.isChecked());
			return true;
			
		case R.id.show_info:
			item.setChecked(!item.isChecked());
			mInfoFragment.refreshTagList(item.isChecked());
			return true;
			
		case R.id.debug_mode:
			item.setChecked(!item.isChecked());
			mInfoFragment.changeMode(item.isChecked());
			return true;
			
		default:
			return super.onMenuItemSelected(featureId, item);
			
		}
		
	}
	
	public String getFolderTitle(File currDir) {
		if (currDir.equals(Environment.getExternalStorageDirectory())) {
			return getResources().getString(R.string.app_name);
		} else {
			return currDir.getName();
		}
	}
	
	public void onDirectorySelected(File currDir) {
		mDrawerTitle = getFolderTitle(currDir);
		getActionBar().setTitle(mDrawerTitle);
		
		if (!mListFragment.isVisible()) {
			// set up the drawer's list view with items and click listener
			mDrawerList.setAdapter(mListFragment.getListAdapter());
		}
    }
	
    public void onFileSelected(int position, ArrayList<String> fileList, File currDir) {
        // The user selected a DICOM file from the DcmListFragment
    	position -= 1;
    	if ((position < 0) || (position > fileList.size())) {
    		// TODO: Error
    		return;
    	}

        if (mFragmented && mListFragment.isVisible()) {
            // If we're in the one-pane layout and need to swap fragments
        	
        	// Enable the Home/Up button to allow the user to go back to 
        	ActionBar actionBar = getActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);

            // Create fragment and give it an argument for the selected article
            Bundle args = new Bundle();
            args.putInt(DcmVar.POSITION, position);
            args.putStringArrayList(DcmVar.FILELIST, fileList);
            args.putString(DcmVar.CURRDIR, currDir.getPath());
            mInfoFragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, mInfoFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
            
            // set up the drawer's list view with items and click listener
            mDrawerList.setAdapter(mListFragment.getListAdapter());
        } else {
            // If we're in the two-pane layout or already displaying the DcmInfoFragment

            // Call a method in the DcmInfoFragment to update its content
    		mInfoFragment.updateDicomInfo(position, fileList, currDir.getPath());
        }
        setTitle(fileList.get(position));
    }

    /** Load the current DICOM series */
	public void load(View view) {
		// Open the DICOM Viewer
		Intent intent = new Intent(this, DcmViewer.class);
		intent.putExtra(DcmVar.DCMFILE, mInfoFragment.getDicomFile());
		intent.putExtra(DcmVar.FILELIST, (ArrayList<String>) mInfoFragment.getFileList());
		startActivity(intent);
	}
	
	// Drawer stuff
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        if (mFragmented)
        	mDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    
    private void generateDrawer() {
        // Drawer stuff
    	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        /*mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));*/
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    
	/* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mListFragment.setSelection(position);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
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
        	if (mDrawerToggle == null) {
        		generateDrawer();
        	}
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