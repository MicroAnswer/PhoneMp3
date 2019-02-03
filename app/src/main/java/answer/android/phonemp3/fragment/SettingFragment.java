package answer.android.phonemp3.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import answer.android.phonemp3.R;
import answer.android.phonemp3.activity.NewVersionDownloadActivity;
import answer.android.phonemp3.activity.SettingActivity;
import answer.android.phonemp3.activity.WebViewActivity;
import answer.android.phonemp3.tool.Tool;

/**
 * 设置fragment
 * Created by Microanswer on 2017/7/13.
 */

public class SettingFragment extends PreferenceFragment {
  public static final String KEY_NEWVERSIONCHECKDATE = "newversioncheckdate";
  Preference set_checkupdate;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.setting);
    set_checkupdate = findPreference("set_checkupdate");
    set_checkupdate.setSummary(Tool.getSelfVersion(getActivity()));
  }

  @Override
  public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    String key = preference.getKey();
    if ("set_about".equals(key)) {
      // 点击 关于
      Intent intent = new Intent(getActivity(), WebViewActivity.class);
      intent.putExtra("url", getString(R.string.abouturl));
      intent.putExtra("title", getString(R.string.about));
      startActivity(intent);
      return true;
    } else if ("set_checkupdate".equals(key)) {
      // 点击检查更新
      Tool.checkupdate(getActivity(), new Tool.UpdateListener() {
        @Override
        public void onNew(final String url,
                          final String name,
                          final String version,
                          final String newFunctions,
                          final String size,
                          final String updateat,
                          final String createdat,
                          final boolean mustDownload) {

          AlertDialog dialog = new AlertDialog.Builder(getActivity())
                  .setTitle(name)
                  .setMessage(newFunctions)
                  .setPositiveButton("立刻下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      Intent intent = new Intent();
                      intent.setClass(getActivity(), NewVersionDownloadActivity.class);
                      intent.putExtra("url", url);
                      intent.putExtra("name", name);
                      intent.putExtra("version", version);
                      intent.putExtra("newfunction", newFunctions);
                      intent.putExtra("size", size);
                      intent.putExtra("updateat", updateat);
                      intent.putExtra("createdat", createdat);
                      getActivity().startActivity(intent);
                    }
                  }).setCancelable(!mustDownload).create();
          if (!mustDownload) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "以后再说", (DialogInterface.OnClickListener) null);
          }

          dialog.show();
        }

        @Override
        public void onThis() {
          Activity activity = getActivity();
          ((SettingActivity) activity).tip("已经是最新版");
        }
      }, true);
      return true;
    }


    return super.onPreferenceTreeClick(preferenceScreen, preference);
  }


}
