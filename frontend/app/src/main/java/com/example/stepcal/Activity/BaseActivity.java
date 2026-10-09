package com.example.stepcal.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.stepcal.R;

public class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected FrameLayout screenContainer;
    protected LinearLayout drawerMenu;

    private final int PURPLE = Color.rgb(111, 53, 168);
    private final int DARK_PURPLE = Color.rgb(74, 23, 76);
    private final int ORANGE = Color.rgb(244, 123, 32);

    @Override
    public void setContentView(int layoutResID) {

        DrawerLayout root =
                new DrawerLayout(this);

        root.setLayoutParams(
                new DrawerLayout.LayoutParams(
                        DrawerLayout.LayoutParams.MATCH_PARENT,
                        DrawerLayout.LayoutParams.MATCH_PARENT
                )
        );

        screenContainer =
                new FrameLayout(this);

        View screen =
                getLayoutInflater().inflate(
                        layoutResID,
                        screenContainer,
                        false
                );

        screenContainer.addView(
                screen,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        root.addView(
                screenContainer,
                new DrawerLayout.LayoutParams(
                        DrawerLayout.LayoutParams.MATCH_PARENT,
                        DrawerLayout.LayoutParams.MATCH_PARENT
                )
        );

        drawerMenu =
                createDrawerMenu();

        DrawerLayout.LayoutParams drawerParams =
                new DrawerLayout.LayoutParams(
                        dpToPx(315),
                        DrawerLayout.LayoutParams.MATCH_PARENT
                );

        drawerParams.gravity =
                Gravity.START;

        root.addView(
                drawerMenu,
                drawerParams
        );

        drawerLayout =
                root;

        super.setContentView(
                root
        );

        /*
         * Every logged-in page can have a menu button with this id.
         * BaseActivity automatically connects it to the same drawer.
         */

        View menuButton =
                screen.findViewById(
                        R.id.menu_button
                );

        if (menuButton != null) {

            menuButton.setOnClickListener(
                    v -> openNavigation()
            );
        }
    }

    private LinearLayout createDrawerMenu() {

        boolean darkMode =
                isDarkMode();

        /*
         * ============================================================
         * DRAWER COLORS
         * ============================================================
         */

        int drawerBackground;
        int primaryText;
        int secondaryText;
        int dividerColor;

        if (darkMode) {

            drawerBackground =
                    Color.rgb(25, 20, 29);

            primaryText =
                    Color.rgb(245, 239, 248);

            secondaryText =
                    Color.rgb(190, 178, 196);

            dividerColor =
                    Color.rgb(55, 46, 61);

        } else {

            /*
             * Soft warm background instead of plain white.
             */
            drawerBackground =
                    Color.rgb(255, 249, 245);

            primaryText =
                    Color.rgb(52, 39, 55);

            secondaryText =
                    Color.rgb(112, 98, 116);

            dividerColor =
                    Color.rgb(232, 222, 232);
        }

        LinearLayout menu =
                new LinearLayout(this);

        menu.setOrientation(
                LinearLayout.VERTICAL
        );

        menu.setBackgroundColor(
                drawerBackground
        );

        /*
         * ============================================================
         * STEPCAL COVER
         * ============================================================
         */

        LinearLayout cover =
                new LinearLayout(this);

        cover.setOrientation(
                LinearLayout.VERTICAL
        );

        cover.setGravity(
                Gravity.CENTER_VERTICAL
        );

        cover.setPadding(
                dpToPx(24),
                dpToPx(28),
                dpToPx(24),
                dpToPx(24)
        );

        int coverStart;
        int coverMiddle;
        int coverEnd;

        if (darkMode) {

            coverStart =
                    Color.rgb(52, 24, 67);

            coverMiddle =
                    Color.rgb(79, 32, 91);

            coverEnd =
                    Color.rgb(155, 73, 36);

        } else {

            coverStart =
                    Color.rgb(74, 23, 76);

            coverMiddle =
                    Color.rgb(104, 42, 112);

            coverEnd =
                    Color.rgb(244, 123, 32);
        }

        GradientDrawable coverBackground =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                coverStart,
                                coverMiddle,
                                coverEnd
                        }
                );

        cover.setBackground(
                coverBackground
        );

        TextView coverSmall =
                new TextView(this);

        coverSmall.setText(
                "STEPCAL"
        );

        coverSmall.setTextSize(
                13
        );

        coverSmall.setTextColor(
                Color.rgb(255, 224, 190)
        );

        coverSmall.setTypeface(
                null,
                Typeface.BOLD
        );

        coverSmall.setLetterSpacing(
                0.15f
        );

        cover.addView(
                coverSmall,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        TextView coverTitle =
                new TextView(this);

        coverTitle.setText(
                "Your fitness journey"
        );

        coverTitle.setTextSize(
                27
        );

        coverTitle.setTextColor(
                Color.WHITE
        );

        coverTitle.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.setMargins(
                0,
                dpToPx(7),
                0,
                0
        );

        cover.addView(
                coverTitle,
                titleParams
        );

        TextView coverSubtitle =
                new TextView(this);

        coverSubtitle.setText(
                "Move better. Live better."
        );

        coverSubtitle.setTextSize(
                14
        );

        coverSubtitle.setTextColor(
                Color.rgb(250, 235, 250)
        );

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        subtitleParams.setMargins(
                0,
                dpToPx(5),
                0,
                0
        );

        cover.addView(
                coverSubtitle,
                subtitleParams
        );

        menu.addView(
                cover,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(175)
                )
        );

        /*
         * ============================================================
         * ACCOUNT SECTION
         * ============================================================
         */

        LinearLayout profileSection =
                new LinearLayout(this);

        profileSection.setOrientation(
                LinearLayout.VERTICAL
        );

        profileSection.setPadding(
                dpToPx(18),
                dpToPx(16),
                dpToPx(18),
                dpToPx(10)
        );

        TextView profileLabel =
                new TextView(this);

        profileLabel.setText(
                "MY ACCOUNT"
        );

        profileLabel.setTextSize(
                11
        );

        profileLabel.setTextColor(
                ORANGE
        );

        profileLabel.setTypeface(
                null,
                Typeface.BOLD
        );

        profileLabel.setLetterSpacing(
                0.12f
        );

        profileSection.addView(
                profileLabel
        );

        /*
         * ============================================================
         * PROFILE CARD
         * ============================================================
         *
         * Profile is the ONLY navigation item with an emoji.
         */

        TextView profileButton =
                new TextView(this);

        profileButton.setText(
                "👤   My Profile"
        );

        profileButton.setTextSize(
                17
        );

        profileButton.setTextColor(
                darkMode
                        ? Color.rgb(238, 216, 245)
                        : Color.rgb(74, 23, 76)
        );

        profileButton.setTypeface(
                null,
                Typeface.BOLD
        );

        profileButton.setGravity(
                Gravity.CENTER_VERTICAL
        );

        profileButton.setPadding(
                dpToPx(18),
                0,
                dpToPx(18),
                0
        );

        profileButton.setBackground(
                createCardBackground(
                        darkMode
                                ? Color.rgb(48, 35, 54)
                                : Color.rgb(239, 230, 249),
                        darkMode
                                ? Color.rgb(76, 57, 84)
                                : Color.rgb(215, 195, 231)
                )
        );

        profileButton.setOnClickListener(
                v -> {

                    closeDrawer();

                    if (!(this instanceof Profile)) {

                        Intent intent =
                                new Intent(
                                        BaseActivity.this,
                                        Profile.class
                                );

                        startActivity(
                                intent
                        );
                    }
                }
        );

        LinearLayout.LayoutParams profileParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(58)
                );

        profileParams.setMargins(
                0,
                dpToPx(10),
                0,
                dpToPx(4)
        );

        profileSection.addView(
                profileButton,
                profileParams
        );

        menu.addView(
                profileSection
        );

        /*
         * ============================================================
         * DIVIDER
         * ============================================================
         */

        View divider =
                new View(this);

        divider.setBackgroundColor(
                dividerColor
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(1)
                );

        dividerParams.setMargins(
                dpToPx(18),
                dpToPx(5),
                dpToPx(18),
                dpToPx(8)
        );

        menu.addView(
                divider,
                dividerParams
        );

        /*
         * ============================================================
         * MAIN NAVIGATION
         * ============================================================
         *
         * Four cards in a clean 2 x 2 glass grid.
         *
         * Row 1:
         *   View My Tasks | Today's Progress
         *
         * Row 2:
         *   Step Count   | StepCal Coach
         */

        LinearLayout navigation =
                new LinearLayout(this);

        navigation.setOrientation(
                LinearLayout.VERTICAL
        );

        navigation.setPadding(
                dpToPx(18),
                dpToPx(2),
                dpToPx(18),
                dpToPx(8)
        );

        /*
         * ============================================================
         * ROW 1
         * ============================================================
         */

        LinearLayout rowOne =
                createNavigationRow();

        /*
         * VIEW MY TASKS
         */

        addGridMenuButton(
                rowOne,
                "View My Tasks",
                darkMode
                        ? Color.rgb(40, 47, 57)
                        : Color.rgb(238, 246, 253),
                darkMode
                        ? Color.rgb(70, 80, 94)
                        : Color.rgb(211, 226, 239),
                primaryText,
                v -> {

                    closeDrawer();

                    if (!(this instanceof Tasks)) {

                        Intent intent =
                                new Intent(
                                        BaseActivity.this,
                                        Tasks.class
                                );

                        startActivity(
                                intent
                        );
                    }
                }
        );

        /*
         * TODAY'S PROGRESS
         */

        addGridMenuButton(
                rowOne,
                "Today's Progress",
                darkMode
                        ? Color.rgb(43, 39, 55)
                        : Color.rgb(246, 240, 252),
                darkMode
                        ? Color.rgb(70, 62, 88)
                        : Color.rgb(224, 209, 237),
                primaryText,
                v -> {

                    closeDrawer();

                    if (!(this instanceof Progress)) {

                        Intent intent =
                                new Intent(
                                        BaseActivity.this,
                                        Progress.class
                                );

                        startActivity(
                                intent
                        );
                    }
                }
        );

        navigation.addView(
                rowOne
        );

        /*
         * ============================================================
         * ROW 2
         * ============================================================
         */

        LinearLayout rowTwo =
                createNavigationRow();

        /*
         * STEP COUNT
         */

        addGridMenuButton(
                rowTwo,
                "Step Count",
                darkMode
                        ? Color.rgb(39, 49, 45)
                        : Color.rgb(237, 248, 241),
                darkMode
                        ? Color.rgb(65, 82, 73)
                        : Color.rgb(209, 231, 217),
                primaryText,
                v -> {

                    closeDrawer();

                    if (!(this instanceof Capture)) {

                        Intent intent =
                                new Intent(
                                        BaseActivity.this,
                                        Capture.class
                                );

                        startActivity(
                                intent
                        );
                    }
                }
        );

        /*
         * STEPCAL COACH
         */

        addGridMenuButton(
                rowTwo,
                "StepCal Coach",
                darkMode
                        ? Color.rgb(45, 38, 53)
                        : Color.rgb(255, 242, 233),
                darkMode
                        ? Color.rgb(78, 61, 89)
                        : Color.rgb(239, 215, 201),
                primaryText,
                v -> {

                    closeDrawer();

                    if (!(this instanceof Coach)) {

                        Intent intent =
                                new Intent(
                                        BaseActivity.this,
                                        Coach.class
                                );

                        startActivity(
                                intent
                        );
                    }
                }
        );

        navigation.addView(
                rowTwo
        );

        /*
         * ============================================================
         * SCROLLABLE NAVIGATION
         * ============================================================
         *
         * The four navigation cards can scroll if needed.
         * Log Out remains fixed at the bottom.
         */

        ScrollView navigationScroll =
                new ScrollView(this);

        navigationScroll.setFillViewport(
                true
        );

        navigationScroll.setVerticalScrollBarEnabled(
                false
        );

        navigationScroll.addView(
                navigation,
                new ScrollView.LayoutParams(
                        ScrollView.LayoutParams.MATCH_PARENT,
                        ScrollView.LayoutParams.WRAP_CONTENT
                )
        );

        LinearLayout.LayoutParams navigationScrollParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                );

        menu.addView(
                navigationScroll,
                navigationScrollParams
        );

        /*
         * ============================================================
         * BOTTOM DIVIDER
         * ============================================================
         */

        View bottomDivider =
                new View(this);

        bottomDivider.setBackgroundColor(
                dividerColor
        );

        LinearLayout.LayoutParams bottomDividerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(1)
                );

        bottomDividerParams.setMargins(
                dpToPx(18),
                0,
                dpToPx(18),
                0
        );

        menu.addView(
                bottomDivider,
                bottomDividerParams
        );

        /*
         * ============================================================
         * LOG OUT
         * ============================================================
         */

        TextView logout =
                new TextView(this);

        logout.setText(
                "Log Out"
        );

        logout.setTextSize(
                16
        );

        logout.setTextColor(
                darkMode
                        ? Color.rgb(255, 150, 150)
                        : Color.rgb(177, 55, 55)
        );

        logout.setTypeface(
                null,
                Typeface.BOLD
        );

        logout.setGravity(
                Gravity.CENTER
        );

        logout.setBackground(
                createCardBackground(
                        darkMode
                                ? Color.rgb(52, 29, 34)
                                : Color.rgb(255, 241, 241),
                        darkMode
                                ? Color.rgb(89, 48, 55)
                                : Color.rgb(241, 211, 211)
                )
        );

        logout.setOnClickListener(
                v -> logout()
        );

        LinearLayout.LayoutParams logoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(54)
                );

        logoutParams.setMargins(
                dpToPx(18),
                dpToPx(12),
                dpToPx(18),
                dpToPx(12)
        );

        menu.addView(
                logout,
                logoutParams
        );

        return menu;
    }

    /*
     * ================================================================
     * CREATE 2-COLUMN NAVIGATION ROW
     * ================================================================
     */

    private LinearLayout createNavigationRow() {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(70)
                );

        rowParams.setMargins(
                0,
                dpToPx(5),
                0,
                dpToPx(5)
        );

        row.setLayoutParams(
                rowParams
        );

        return row;
    }

    /*
     * ================================================================
     * CREATE ONE GRID CARD
     * ================================================================
     */

    private void addGridMenuButton(
            LinearLayout row,
            String text,
            int backgroundColor,
            int borderColor,
            int textColor,
            View.OnClickListener listener
    ) {

        TextView button =
                new TextView(this);

        button.setText(
                text
        );

        button.setTextSize(
                15
        );

        button.setTextColor(
                textColor
        );

        button.setTypeface(
                null,
                Typeface.BOLD
        );

        button.setGravity(
                Gravity.CENTER
        );

        button.setPadding(
                dpToPx(8),
                0,
                dpToPx(8),
                0
        );

        button.setMaxLines(
                2
        );

        button.setBackground(
                createCardBackground(
                        backgroundColor,
                        borderColor
                )
        );

        button.setOnClickListener(
                listener
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        dpToPx(62),
                        1
                );

        params.setMargins(
                dpToPx(4),
                0,
                dpToPx(4),
                0
        );

        row.addView(
                button,
                params
        );
    }

    /*
     * ================================================================
     * CARD BACKGROUND
     * ================================================================
     */

    private GradientDrawable createCardBackground(
            int backgroundColor,
            int borderColor
    ) {

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                backgroundColor
        );

        background.setCornerRadius(
                dpToPx(17)
        );

        background.setStroke(
                dpToPx(1),
                borderColor
        );

        return background;
    }

    private boolean isDarkMode() {

        int mode =
                getResources()
                        .getConfiguration()
                        .uiMode
                        & Configuration.UI_MODE_NIGHT_MASK;

        return mode ==
                Configuration.UI_MODE_NIGHT_YES;
    }

    protected void openNavigation() {

        if (drawerLayout != null) {

            drawerLayout.openDrawer(
                    Gravity.START
            );
        }
    }

    protected void closeDrawer() {

        if (drawerLayout != null) {

            drawerLayout.closeDrawer(
                    Gravity.START
            );
        }
    }

    private void logout() {

        getSharedPreferences(
                "MyPrefs",
                MODE_PRIVATE
        )
                .edit()
                .clear()
                .apply();

        Intent intent =
                new Intent(
                        this,
                        Login.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(
                intent
        );

        finish();
    }

    private int dpToPx(
            int dp
    ) {

        return (int) (
                dp *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}