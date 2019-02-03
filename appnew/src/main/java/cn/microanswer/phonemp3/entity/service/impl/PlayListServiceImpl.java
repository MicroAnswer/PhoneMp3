package cn.microanswer.phonemp3.entity.service.impl;

import java.util.List;

import androidx.annotation.NonNull;
import cn.microanswer.phonemp3.entity.PlayList;
import cn.microanswer.phonemp3.entity.service.PlayListService;

public class PlayListServiceImpl implements PlayListService {
    @Override
    public List<PlayList> getPlayList(@NonNull String playListId) throws Exception {
        return null;
    }

    @Override
    public boolean addPlayList(@NonNull PlayList playList) throws Exception {
        return false;
    }

    @Override
    public boolean deletePlayList(@NonNull String playListId) throws Exception {
        return false;
    }

    @Override
    public boolean updatePlayList(@NonNull PlayList playList) throws Exception {
        return false;
    }
}
