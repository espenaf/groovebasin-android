package com.melissanoelle.groovebasin;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Something about sockets.
        TextView title = (TextView) findViewById(R.id.now_playing_song_title);
        GroovebasinTask gbTask = new GroovebasinTask(title);
        gbTask.execute();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.controller_fragment, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    public class GroovebasinTask extends AsyncTask<Void, Integer, Void> {
        public final String GROOVEBASIN_URL = "demo.groovebasin.com";
        public final Integer GROOVEBASIN_PORT = 80;
        public final String PROTOCOL_UPGRADE = "OHqIjsp9g9dpGRZ3p8LcxVey9tpMh_bT";

        private WeakReference<TextView> mUpdateView;

        private boolean mRun = true;
        private PrintWriter out;
        private BufferedReader in;
        private String incomingMessage;

        public GroovebasinTask(TextView view) {
            this.mUpdateView = new WeakReference<TextView>(view);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Socket socket = new Socket(GROOVEBASIN_URL, GROOVEBASIN_PORT);
                try {
                    Log.d(LOG_TAG, "trying!");

                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.println("protocolupgrade " + PROTOCOL_UPGRADE);
                    out.flush();

                    //                this.sendMessage(command);
                    while (mRun) {
                        incomingMessage = in.readLine();

                        if (incomingMessage != null) {
                            Log.d(LOG_TAG, "got message:");
                            Log.d(LOG_TAG, incomingMessage);
                        }
                    }

                } catch (Exception e) {
                    Log.d(LOG_TAG, "odin seems bummed");
                    e.printStackTrace();
                } finally {
                    out.flush();
                    out.close();
                    in.close();
                    socket.close();
                    Log.d(LOG_TAG, "closed up shop.");
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, "odin is definitely bummed");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(LOG_TAG, "oh shit");
            Log.d(LOG_TAG, values.toString());
            if (mUpdateView.get() != null && values.length > 0) {
                mUpdateView.get().setText(values[0].toString());
            }
        }
    }
}
