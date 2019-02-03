package cn.microanswer.phonemp3.entity.service.impl;

import java.util.List;

import androidx.annotation.NonNull;
import cn.microanswer.phonemp3.entity.Music;
import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.entity.service.MusicService;

public class MusicServiceImpl implements MusicService {
    @Override
    public boolean addMusic(@NonNull Music music, @NonNull String playListId) throws Exception {
        music.setListId(playListId);
        return music.save();
    }

    @Override
    public boolean addMusic(@NonNull Music music, @NonNull PlayList playList) throws Exception {
        return addMusic(music, playList.getId());
    }

    @Override
    public boolean exist(@NonNull Music music, @NonNull String playListId) throws Exception {
        music.setListId(playListId);
        return music.exists();
    }

    @Override
    public boolean updateMusic(@NonNull Music music) throws Exception {
        return false;
    }

    @Override
    public boolean updateMusicTime(String musicId) throws Exception {
        return false;
    }

    @Override
    public boolean updateMusicTime(@NonNull Music music) throws Exception {
        return false;
    }

    @Override
    public List<Music> getMusics(@NonNull PlayList playList) throws Exception {
        return null;
    }

    @Override
    public List<Music> getMusics(@NonNull String playListId) throws Exception {
        return null;
    }

    @Override
    public boolean deleteMusic(@NonNull String musicId) throws Exception {
        return false;
    }

    @Override
    public boolean deleteMusic(@NonNull Music music) throws Exception {
        return false;
    }
}
