package ve.com.abicelis.pingwidget.app.preference;

/**
 * Created by abice on 13/2/2017.
 */

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ve.com.abicelis.pingwidget.R;
import ve.com.abicelis.pingwidget.enums.WidgetTheme;


public class ThemeView extends FrameLayout {

    public ThemeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThemeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(context).inflate(R.layout.preference_theme_view_item, this, true);
        ImageView imageViewBackground = (ImageView) view.findViewById(R.id.preference_theme_view_item_background);
        ImageView imageViewChart = (ImageView) view.findViewById(R.id.preference_theme_view_item_chart);

        WidgetTheme theme = WidgetTheme.getByThemeViewId(view.getId());

        imageViewBackground.setColorFilter(ContextCompat.getColor(context, theme.getColorBackgroundContainerTop()));
        imageViewChart.setColorFilter(ContextCompat.getColor(context, theme.getChartColor()));

    }

}