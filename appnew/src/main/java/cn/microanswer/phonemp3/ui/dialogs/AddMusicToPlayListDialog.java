package cn.microanswer.phonemp3.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.entity.Music;

// TODO 完成添加歌曲到歌单的功能。 此弹出框只有在有多个歌单的时候弹出，没有歌单的情况。只有一个歌单默认自动添加到默认歌单。
public class AddMusicToPlayListDialog extends BaseDialog {

    private Music mMusic;

    public AddMusicToPlayListDialog(@NonNull Context context, Music music) {
        super(context);
        mMusic = music;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_addmusic_to_playlist);
    }

    @Override
    protected View getContentView(FrameLayout parent) {
        return null;
    }
}
