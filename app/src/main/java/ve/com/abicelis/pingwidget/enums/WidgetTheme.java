package ve.com.abicelis.pingwidget.enums;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import ve.com.abicelis.pingwidget.R;

/**
 * Created by abice on 13/2/2017.
 */

public enum WidgetTheme {
    BLUE_YELLOW(R.id.theme_blue_yellow, R.string.widget_theme_summary_blue_yellow, R.drawable.widget_background_top_blue, R.color.widget_theme_blue_yellow_background_top, R.color.widget_theme_blue_yellow_chart),
    GREEN_GREEN(R.id.theme_green_green, R.string.widget_theme_summary_green_green, R.drawable.widget_background_top_green, R.color.widget_theme_green_background_top, R.color.widget_theme_green_chart),
    ORANGE_AMBER(R.id.theme_orange_amber, R.string.widget_theme_summary_orange_amber, R.drawable.widget_background_top_orange, R.color.widget_theme_orange_amber_background_top, R.color.widget_theme_orange_amber_chart)
    ;

    private @IdRes int mThemeViewId;
    private @StringRes int mSummary;
    private @DrawableRes int mDrawableBackgroundContainerTop;
    private @ColorRes int mColorBackgroundContainerTop;
    private @ColorRes int mColorChart;

    WidgetTheme(@IdRes int themeViewId, @StringRes int summary, @DrawableRes int drawableBackgroundContainerTop, @ColorRes int colorBackgroundContainerTop, @ColorRes int colorChart) {
        mThemeViewId = themeViewId;
        mSummary = summary;
        mDrawableBackgroundContainerTop = drawableBackgroundContainerTop;
        mColorBackgroundContainerTop = colorBackgroundContainerTop;
        mColorChart = colorChart;
    }

    public int getSummaryRes() {
        return mSummary;
    }

    public int getThemeViewId() {
        return mThemeViewId;
    }

    public int getDrawableBackgroundContainerTop() {
        return mDrawableBackgroundContainerTop;
    }

    public int getColorBackgroundContainerTop() {
        return mColorBackgroundContainerTop;
    }

    public int getColorChart() {
        return mColorChart;
    }

    public static WidgetTheme getByThemeWiewId(@IdRes int themeViewId) {
        switch(themeViewId) {
            case R.id.theme_blue_yellow: return WidgetTheme.BLUE_YELLOW;
            case R.id.theme_green_green: return WidgetTheme.GREEN_GREEN;
            case R.id.theme_orange_amber: return WidgetTheme.ORANGE_AMBER;
            default: throw new IllegalArgumentException(String.valueOf(themeViewId));
        }
    }
}
