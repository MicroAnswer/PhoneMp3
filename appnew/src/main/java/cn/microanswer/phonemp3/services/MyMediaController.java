package cn.microanswer.phonemp3.services;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.List;

public interface MyMediaController {
    void onBrowserConnected();
    void onSessionReady();
    void onSessionDestroyed();
    void onSessionEvent(String event, Bundle extras);
    void onPlaybackStateChanged(PlaybackStateCompat state);
    void onMetadataChanged(MediaMetadataCompat metadata);
    void onQueueChanged(List<MediaSessionCompat.QueueItem> queue);
    void onQueueTitleChanged(CharSequence title);
    void onExtrasChanged(Bundle extras);
    void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info);
    void onCaptioningEnabledChanged(boolean enabled);
    void onRepeatModeChanged(int repeatMode);
    void onShuffleModeChanged(int shuffleMode);
}