package cn.microanswer.phonemp3.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import answer.android.phonemp3.R;
import cn.microanswer.phonemp3.logic.PlayLogic;
import cn.microanswer.phonemp3.logic.answer.PlayAnswer;
import cn.microanswer.phonemp3.ui.PlayPage;
import cn.microanswer.phonemp3.ui.activitys.PhoneMp3Activity;

public class PlayFragment extends BaseFragment<PlayLogic> implements PlayPage, View.OnClickListener {

    private ImageView imageView;
    private Toolbar toolbar;
    private ActionBar supportActionBar;
    private ImageView imageviewPlay$pause;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = findViewById(R.id.imageviewBg);
        imageviewPlay$pause = findViewById(R.id.imageviewPlay_pause);
        toolbar = findViewById(R.id.toolbar);
        getPhoneMp3Activity().setSupportActionBar(toolbar);
        supportActionBar = getPhoneMp3Activity().getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        imageviewPlay$pause.setOnClickListener(this);

        Glide.with(this).load(R.drawable.playbg).into(imageView);


        getLogic().onPageCreated(savedInstanceState, getArguments());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem share = menu.add(0, R.id.share, 0, R.string.share);
        share.setIcon(R.drawable.icon_share);
        share.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            getPhoneMp3Activity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {

        PhoneMp3Activity phoneMp3Activity = getPhoneMp3Activity();
        if (phoneMp3Activity != null) {
            phoneMp3Activity.setSupportActionBar(null);
        }

        super.onDestroy();
    }

    @Override
    PlayAnswer newLogic() {
        return new PlayAnswer(this);
    }

    @Override
    public void onClick(View view) {
        getLogic().onPause$Playclick();
    }
}
