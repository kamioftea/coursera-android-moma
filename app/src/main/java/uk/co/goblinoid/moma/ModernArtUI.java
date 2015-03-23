package uk.co.goblinoid.moma;

import android.animation.ArgbEvaluator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;


public class ModernArtUI extends ActionBarActivity {

    private static String TAG = "modern_art_ui";

    public static final String DIALOG_TAG = "more_info_dialog";

    private static int COLOUR_1_START = 0xffff5959;
    private static int COLOUR_1_END = 0xff73aff0;

    private static int COLOUR_2_START = 0xff73aff0;
    private static Pair<Integer, Integer> COLOUR_2_BREAKPOINT = new Pair<>(33, 0xff92c663);
    private static int COLOUR_2_END = 0xffff5959;

    private static int COLOUR_3_START = 0xfffad725;
    private static int COLOUR_3_END = 0xff92c663;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the view
        setContentView(R.layout.activity_modern_art_ui);

        // Grab references to specific elements
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);

        final TextView colourBlock1 = (TextView) findViewById(R.id.colourBlock1);
        final TextView colourBlock2 = (TextView) findViewById(R.id.colourBlock2);
        final TextView colourBlock3 = (TextView) findViewById(R.id.colourBlock3);

        // And setup a couple of things needed for the colour shifting
        final ArgbEvaluator evaluator = new ArgbEvaluator();
        final int seekMax = seekBar.getMax();

        // Add a listener to change colours when the seek bar is used
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Block one and three are pretty simple - just use the ArgbEvaluator to get the current colour
                colourBlock1.setBackgroundColor((Integer) evaluator.evaluate((float)progress/seekMax, COLOUR_1_START, COLOUR_1_END));

                // For Block 2 we need to split the processing into sections: before and after the breakpoint
                if(progress <= COLOUR_2_BREAKPOINT.first)
                    colourBlock2.setBackgroundColor((Integer) evaluator.evaluate(
                            (float)progress/COLOUR_2_BREAKPOINT.first,
                            COLOUR_2_START,
                            COLOUR_2_BREAKPOINT.second));
                else
                    colourBlock2.setBackgroundColor((Integer) evaluator.evaluate(
                            ((float)progress - COLOUR_2_BREAKPOINT.first)/(seekMax - COLOUR_2_BREAKPOINT.first),
                            COLOUR_2_BREAKPOINT.second,
                            COLOUR_2_END));

                colourBlock3.setBackgroundColor((Integer) evaluator.evaluate((float)progress/seekMax, COLOUR_3_START, COLOUR_3_END));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "onStartTrackingTouch()");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "onStopTrackingTouch()");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu from xml
        getMenuInflater().inflate(R.menu.menu_modern_art_ui, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // We only have one item, which should show the more information dialogue
        if (id == R.id.action_more_info) {
            showMoreInfoDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    void showMoreInfoDialog() {
        DialogFragment newFragment = MoreInfoDialogFragment.newInstance(
                R.string.alert_dialog_two_buttons_title,
                R.string.alert_dialog_two_buttons_message);

        newFragment.show(getFragmentManager(), DIALOG_TAG);
    }

    void doNegativeClick()
    {
        // Do nothing
    }

    void doPositiveClick()
    {
        String URL = "http://www.moma.org/collection/artist.php?artist_id=4057";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(intent);
    }


    // Internal Fragment, used to show a More Information dialog when the menu item is clicked
    public static class MoreInfoDialogFragment extends DialogFragment {

        public static final String TITLE_KEY = "title";
        public static final String MESSAGE_KEY = "message";

        public static MoreInfoDialogFragment newInstance(int title, int message) {
            MoreInfoDialogFragment dialog_fragment = new MoreInfoDialogFragment();
            Bundle args = new Bundle();
            args.putInt(TITLE_KEY, title);
            args.putInt(MESSAGE_KEY, message);
            dialog_fragment.setArguments(args);
            return dialog_fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt(TITLE_KEY);
            int message = getArguments().getInt(MESSAGE_KEY);

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((ModernArtUI) getActivity()).doPositiveClick();
                                }
                            }
                    )
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((ModernArtUI)getActivity()).doNegativeClick();
                                }
                            }
                    )
                    .create();
        }
    }
}
