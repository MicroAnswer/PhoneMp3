package cn.microanswer.phonemp3.ui;

import android.support.v4.media.session.PlaybackStateCompat;

import androidx.fragment.app.Fragment;
import cn.microanswer.phonemp3.logic.MainLogic;

public interface MainPage extends Page<MainLogic> {
    void show(Class<? extends Fragment> fragmentClass);
    Fragment getFragment(String name);
    void setDayModeMenu(boolean isDayMode);
    void updateControllerInfo(String coverPath, String title, String desc, boolean love);

    void applyPlayStateChange(PlaybackStateCompat state);
}
