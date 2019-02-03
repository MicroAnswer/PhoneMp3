package cn.microanswer.phonemp3.ui;

import cn.microanswer.phonemp3.logic.IndexLogic;

/**
 * logo/封面界面
 * <p>
 * 当没有封面或则封面不可用的时候只显示logo<br/>
 * 有封面则显示封面，如果封面内容是广告，点击封面跳转到广告链接
 * </p>
 */
public interface IndexPage extends Page<IndexLogic> {

    /**
     * 实现该方法，将封面显示到界面
     *
     * @param url 可用于显示的图片路径
     */
    void displayCover(String url);

    /**
     * 实现该方法，将版本信息显示到界面
     *
     * @param versionInfo
     */
    void displVersionInfo(String versionInfo);
}
