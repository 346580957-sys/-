package cn.huamantang.health;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

final class SystemBars {
    private SystemBars() {}

    static void apply(Activity activity, View root, View bottomView) {
        if (Build.VERSION.SDK_INT < 35) return;
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(activity.getWindow(), root);
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(0, bars.top, 0, bottomView == null ? bars.bottom : 0);
            if (bottomView != null) bottomView.setPadding(0, 0, 0, bars.bottom);
            return windowInsets;
        });
    }
}
