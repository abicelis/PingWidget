package ve.com.abicelis.pingwidget.enums;

import androidx.annotation.LayoutRes;

import ve.com.abicelis.pingwidget.R;

/**
 * Created by abice on 11/5/2017.
 */

public enum WidgetLayoutType {
    SHORT(R.layout.widget_layout_short),
    TALL(R.layout.widget_layout_tall);

    private @LayoutRes int mLayout;
    private static final int WIDGET_LAYOUT_HEIGHT_THRESHOLD = 100;


    WidgetLayoutType(@LayoutRes int layout) {
        mLayout = layout;
    }

    public int getLayout() {
        return mLayout;
    }



    public static WidgetLayoutType getWidgetLayoutTypeByHeight(int minHeight) {
        if(minHeight < WIDGET_LAYOUT_HEIGHT_THRESHOLD) {
            //Use Short view
            return WidgetLayoutType.SHORT;
        } else {
            //Use Tall view
            return WidgetLayoutType.TALL;
        }
    }
}
