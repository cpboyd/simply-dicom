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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;

import us.cboyd.android.shared.ExternalIO;
import us.cboyd.android.shared.StorageUtils;
import us.cboyd.android.shared.adapters.StorageArrayAdapter;

/**
 * DICOM Browser
 * 
 * @author Christopher Boyd
 * @version 0.6
 *
 */
public class DcmBrowser extends Activity implements DcmListFragment.OnFileSelectedListener {
	
	private DcmListFragment mListFragment;
	private DcmInfoFragment mInfoFragment;
    private String          mAppName;
	
	// Drawer stuff
	private boolean 		mFragmented = false;
    private DrawerLayout 	mDrawerLayout;
    private ListView 		mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle = null;
    private Toolbar         mToolbar = null;

    private CharSequence 	mDrawerTitle, mDrawerSubtitle, mTitle, mSubtitle;

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
        
        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {
        	Log.i("cpb", "mListFrag: One-pane");
        	mFragmented = true;

            // Add the fragment to the 'fragment_container' FrameLayout
        	fragManager.beginTransaction().add(R.id.fragment_container, mListFragment).commit();
            
            generateDrawer(mToolbar);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
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
			mListFragment.setDir();
		}

		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);

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
        // If directory is null, don't save state.
        if (mListFragment.getDir() == null) {
            outState = null;
            return;
        }

		FragmentManager fragManager = getFragmentManager();
    	// If the fragment hasn't already been added to the FragmentManager, add it.
		// Otherwise, it can't be put in the Bundle.
    	if (!mInfoFragment.isAdded()) {
    		fragManager.beginTransaction().add(mInfoFragment, null).commit();
    	}

        // Otherwise, save the current directory.
		outState.putString(DcmVar.CURRDIR, mListFragment.getDir().getAbsolutePath());
    	fragManager.putFragment(outState, DcmVar.FRAGLIST, mListFragment);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
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
            if (mToolbar != null) {
                // Assume we're jumping back to the ListFragment
                if (mListFragment.isStorage())
                    mDrawerToggle.setDrawerIndicatorEnabled(false);

                // Reset the Toolbar's title
                mToolbar.setTitle(mDrawerTitle);
                mToolbar.setSubtitle(mDrawerSubtitle);
            }
			FragmentManager fm = getFragmentManager();
			if (fm.getBackStackEntryCount() > 0) {
			    fm.popBackStack();
                // If storage list, display the app name in title bar.
                if (mListFragment.isStorage())
                    onRootSelected(null);
                else
                    onDirectorySelected(temp);
            // If there's no back stack, call super.onBackPressed().
			} else {
				super.onBackPressed();
			}
			
		// If the device is on the storage list, call super.onBackPressed().
		} else if (mListFragment.isStorage()) {
			super.onBackPressed();
        // If the device is on the root directory, display the storage list.
        } else if (mListFragment.isRoot()) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mListFragment.setDir(null);
            onRootSelected(null);
        // Otherwise, go to parent directory.
		} else {
			temp = temp.getParentFile();
			mListFragment.setDir(temp);
            onDirectorySelected(temp);
		}
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
            // Handle menu items here:
            case R.id.app_about:
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_about);
                dialog.setTitle(mAppName);
                dialog.show();
                return true;

            case R.id.list_ffirst:
                item.setChecked(!item.isChecked());
                mListFragment.listFilesFirst(item.isChecked());
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
                return super.onOptionsItemSelected(item);
	    }
	}
	
	/** navigateUp replicates "Back" functionality for the Home/Up key. */
	public boolean navigateUp() {
		if (mListFragment.isVisible()) {
			mListFragment.setSelection(0);
		}
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
	
	public String getFolderTitle(File currDir) {
		if (currDir == null)
			return "";
		else {
            String currDirPath = currDir.getAbsolutePath();
            currDirPath = currDirPath.replaceFirst(mListFragment.getRoot().getAbsolutePath(), "");
			return currDirPath;
		}
	}

    // Call onRootSelected to display the app name as the title.
    public void onRootSelected(File currDir) {
        onRootSelected(currDir, mAppName);
    }

    public void onRootSelected(File currDir, String displayName) {
        mDrawerTitle = displayName;
        mToolbar.setTitle(mDrawerTitle);
        // If this is the storage list, disable the drawer indicator
        mDrawerToggle.setDrawerIndicatorEnabled(currDir != null);

        onDirectorySelected(currDir);
    }
	
	public void onDirectorySelected(File currDir) {
        mDrawerSubtitle = getFolderTitle(currDir);
		mToolbar.setSubtitle(mDrawerSubtitle);
		
		if (!mListFragment.isVisible()) {
			// set up the drawer's list view with items and click listener
			mDrawerList.setAdapter(mListFragment.getListAdapter());
		} else {
            mDrawerList.setAdapter(new StorageArrayAdapter(this, R.layout.item_file));
        }
    }
	
    public void onFileSelected(ArrayList<String> fileList, File currDir, File currFile) {
        String dirPath  = currDir.getPath();
        String fileName = currFile.getName();
        if (mFragmented && mListFragment.isVisible()) {
            // If we're in the one-pane layout and need to swap fragments
        	
        	// Enable the Home/Up button to allow the user to go back to
//            if (mToolbar != null)
//                actionBar.setDisplayHomeAsUpEnabled(true);

            // Create fragment and give it an argument for the selected article
            Bundle args = new Bundle();
            args.putStringArrayList(DcmVar.FILELIST, fileList);
            args.putString(DcmVar.CURRDIR, dirPath);
            args.putString(DcmVar.CURRFILE, fileName);

            mInfoFragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, mInfoFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null).commit();
            
            // set up the drawer's list view with items and click listener
            mDrawerList.setAdapter(mListFragment.getListAdapter());
        } else {
            // If we're in the two-pane layout or already displaying the DcmInfoFragment

            // Call a method in the DcmInfoFragment to update its content
    		mInfoFragment.updateDicomInfo(fileList, dirPath, fileName);
        }
        setTitle(fileName);
    }

    /** Load the current DICOM series */
	public void load(View view) {
		// Open the DICOM Viewer
		Intent intent = new Intent(this, DcmViewer.class);
		intent.putExtra(DcmVar.DCMFILE, mInfoFragment.getDicomFile());
		intent.putExtra(DcmVar.FILELIST, (ArrayList<String>) mInfoFragment.getFileList());
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
                toolbar,
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                mToolbar.setTitle(mTitle);
                mToolbar.setSubtitle(mSubtitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                mTitle = mToolbar.getTitle();
                mSubtitle = mToolbar.getSubtitle();
                if (mListFragment.isVisible()) {
                    mToolbar.setTitle(mAppName);
                    mToolbar.setSubtitle("");
                } else {
                    mToolbar.setTitle(mDrawerTitle);
                    mToolbar.setSubtitle(mDrawerSubtitle);
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    
	/* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mListFragment.isVisible()) {
                StorageUtils.StorageInfo temp = (StorageUtils.StorageInfo)parent.getItemAtPosition(position);
                mListFragment.setDir(temp.getFile());
                mDrawerTitle = mTitle = temp.getDisplayName();
                mDrawerSubtitle = mSubtitle = "";
            } else
                mListFragment.setSelection(position);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        // If the drawer isn't open, set the title.
        if (!mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mToolbar.setTitle(mTitle);
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
        		generateDrawer(mToolbar);
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