package cn.microanswer.phonemp3.ui;

import java.util.List;

import cn.microanswer.phonemp3.entity.Ablum;
import cn.microanswer.phonemp3.logic.Main_AllMusic_A_Logic;

public interface Main_AllMucis_A_Page extends Page<Main_AllMusic_A_Logic> {

    /**
     *
     * 显示所有专辑。
     * @param ablums 专辑数据。
     */
    void displayAblums(List<Ablum> ablums);
}
