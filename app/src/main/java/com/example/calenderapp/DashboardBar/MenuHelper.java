/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        None
 *   Beschreibung :     Hier wird den Schaltflächen der Aktionsleiste
 *                      ein Callback zugewiesen. Einmal zu Profil bearbeiten
 *                      oder auch einmal ausloggen und zurück zum Dashboard.
 *                      Letzte Änderung :  13/02/2024
 * *************************************************
 */
package com.example.calenderapp.DashboardBar;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.example.calenderapp.Login.EditProfile;
import com.example.calenderapp.Login.Login;
import com.example.calenderapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuHelper {

    // Methode zur Behandlung von Menüelementen
    public static boolean handleMenuItem(MenuItem item, FirebaseUser user, Context context) {
        // Profilmenü behandeln
        if (item.getItemId() == R.id.profileMenu) {
            MenuItem usernameItem = item.getSubMenu().findItem(R.id.username);
            // Benutzername und E-Mail im Profilmenü anzeigen
            if (usernameItem != null) {
                usernameItem.setTitle(user.getDisplayName() + " (" + user.getEmail() + ")");
            }
            return true;
        } else if (item.getItemId() == R.id.dashboard) {
            // Zum Dashboard navigieren
            Intent intent = new Intent(context, Dashboard.class);
            context.startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.editProfile) {
            // Zum Profil bearbeiten navigieren
            Intent intent = new Intent(context, EditProfile.class);
            context.startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.logout) {
            // Abmelden und zum Anmeldebildschirm navigieren
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(context, Login.class);
            context.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
}

