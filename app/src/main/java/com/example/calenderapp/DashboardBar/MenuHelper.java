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
    public static boolean handleMenuItem(MenuItem item, FirebaseUser user, Context context) {
        if (item.getItemId() == R.id.profileMenu) {
            MenuItem usernameItem = item.getSubMenu().findItem(R.id.username);
            if (usernameItem != null) {
                String tmp = user.getDisplayName() + " (" + user.getEmail() + ")";
                usernameItem.setTitle(tmp);
            }
            return true;
        } else if (item.getItemId() == R.id.dashboard) {
            Intent intent = new Intent(context, Dashboard.class);
            context.startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.editProfile) {
            Intent intent = new Intent(context, EditProfile.class);
            context.startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.logout) {
            //Sign-Out befehl der User
            FirebaseAuth.getInstance().signOut();
            //Nun wieder Login offnen
            Intent intent = new Intent(context, Login.class);
            context.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }


}
