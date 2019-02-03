package cn.microanswer.phonemp3.logic.answer;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.List;

import cn.microanswer.phonemp3.logic.Logic;
import cn.microanswer.phonemp3.services.MyMediaController;
import cn.microanswer.phonemp3.ui.Page;
import cn.microanswer.phonemp3.ui.activitys.PhoneMp3Activity;

public abstract class BaseAnswer<P extends Page> implements Logic<P>,MyMediaController {
    private P page;

    public BaseAnswer(P page) {
        this.page = page;
    }

    public P getPage() {
        return page;
    }

    PhoneMp3Activity getPhoneMp3Activity() {
        return page.getPhoneMp3Activity();
    }

    public void onResume() {}

    @Override
    public void onBrowserConnected() {

    }

    @Override
    public void onSessionReady() {

    }

    @Override
    public void onSessionDestroyed() {

    }

    @Override
    public void onSessionEvent(String event, Bundle extras) {

    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {

    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat metadata) {

    }

    @Override
    public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {

    }

    @Override
    public void onQueueTitleChanged(CharSequence title) {

    }

    @Override
    public void onExtrasChanged(Bundle extras) {

    }

    @Override
    public void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info) {

    }

    @Override
    public void onCaptioningEnabledChanged(boolean enabled) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeChanged(int shuffleMode) {

    }
}
