/**
 * Filename:  DialogListener.java
 * Author:  Team SpellTest
 * Date:  05 April 2019
 *
 * Purpose:  This listener interface provides a means by which the various application dialog box
 * classes can trigger actions on the activities from which the dialog boxes were created.  For
 * example, methods in this class can notify calling activities that the underlying data set has
 * been changed, which is then used by the calling activity to trigger a refresh of the data
 * displayed by the activity.
 */

package com.example.spelltest;

public interface DialogListener {

    /**
     * Public method that is called when the positive or "OK" button in a dialog box is clicked.
     * @param id  the id of the object that was created, or null if the id field is
     *            not used.  (In this app, we use this method to create objects)
     */
    public void onDialogPositiveClick(long id);

    /**
     * Public method that is called when the negative or "cancel" button in a dialog box is clicked.
     */
    public void onDialogNegativeClick();
}
