package com.example.calenderapp.events;

import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;

/** Longclick:
 * public abstract boolean onLongClick (View v)
 * Dateien aus Database löschen:
 * DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
 * Query applesQuery = ref.child("firebase-test").orderByChild("title").equalTo("Apple");
 *
 * applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
 *     @Override
 *     public void onDataChange(DataSnapshot dataSnapshot) {
 *         for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
 *             appleSnapshot.getRef().removeValue();
 *         }
 *     }
 *
 *     @Override
 *     public void onCancelled(DatabaseError databaseError) {
 *         Log.e(TAG, "onCancelled", databaseError.toException());
 *     }
 * });
 *
 *
 * A simple double-click listener
 * Usage:
 * 			// Scenario 1: Setting double click listener for myView
 * 			myView.setOnClickListener(new DoubleClickListener() {
 *
 *				@Override
 *				public void onDoubleClick() {
 *					// double-click code that is executed if the user double-taps
 *					// within a span of 200ms (default).
 *				}
 *			});
 *
 * 			// Scenario 2: Setting double click listener for myView, specifying a custom double-click span time
 * 			myView.setOnClickListener(new DoubleClickListener(500) {
 *
 *				@Override
 *				public void onDoubleClick() {
 *					// double-click code that is executed if the user double-taps
 *					// within a span of 500ms (default).
 *				}
 *			});
 *
 * @author	Srikanth Venkatesh
 * @version	1.0
 * @since	2014-09-15
 */
public abstract class DoubleClickListener implements OnClickListener {

    // The time in which the second tap should be done in order to qualify as
    // a double click
    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;

    public DoubleClickListener() {
        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
    }

    public DoubleClickListener(long doubleClickQualificationSpanInMillis) {
        this.doubleClickQualificationSpanInMillis = doubleClickQualificationSpanInMillis;
        timestampLastClick = 0;
    }

    @Override
    public void onClick(View v) {
        if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
            onDoubleClick();
        }
        timestampLastClick = SystemClock.elapsedRealtime();
    }

    public abstract void onDoubleClick();

}
