package cn.microanswer.phonemp3.logic;

import cn.microanswer.phonemp3.ui.CodeUsePage;

public interface CodeUseLogic extends Logic<CodeUsePage> {
    void onClickCodeUseItem(int position, String name, String desc);
}
