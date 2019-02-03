package answer.android.phonemp3.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import answer.android.phonemp3.R;
import answer.android.phonemp3.api.API;
import answer.android.phonemp3.tool.Tool;

/**
 * 反馈建议Activity
 * Created by Microanswer on 2017/7/17.
 */

public class FeedBackActivity extends BaseActivity implements View.OnClickListener {

  private EditText mFeedBackInput, mContact;
  private Button mSubmit;
  private ProgressDialog mDialog;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feedback);
    Toolbar toolBar = getToolBar();
    setSupportActionBar(toolBar);
    setActionBarcontentShadow(8);
    int c = getResources().getColor(R.color.colorPrimary);
    drawNavigationBarColor(c);
    drawStateBarcolor(c);

    mFeedBackInput = (EditText) findViewById(R.id.activity_feedback_input);
    mSubmit = (Button) findViewById(R.id.activity_feedback_submit);
    mContact = (EditText) findViewById(R.id.activity_feedback_contact);

    mSubmit.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    final String txt = mFeedBackInput.getText().toString();
    if (TextUtils.isEmpty(txt)) {
      tip("写点反馈内容吧。");
      return;
    }

    final String contact = mContact.getText().toString();
    final String version = Tool.getSelfVersion(this);
    if (mDialog != null) {
      mDialog.show();
    } else {
      mDialog = ProgressDialog.show(this, null, getString(R.string.submiting));
    }
    API.useApi(this, R.string.FEEDBACK, new API.Config() {
      @Override
      public void success(JSONObject data) {
        try {
          if (data.has("code") && data.getInt("code") == 200) {
            alert("提交成功\n感谢你的建议，我会做的更好的。");
            mFeedBackInput.setText("");
          } else {
            if (data.has("msg")) {
              alert(data.getString("msg"));
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      @Override
      public void fail(Throwable e) {
        alert(e.getMessage());
      }

      @Override
      public void finish() {
        super.finish();
        if (mDialog != null) {
          mDialog.hide();
        }
      }

      @Override
      public Map<String, String> getParam() {
        TreeMap<String, String> p = new TreeMap<String, String>();
        p.put("contact", contact);
        p.put("version", version);
        p.put("content", txt);
        return p;
      }
    });

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mDialog != null) {
      mDialog.dismiss();
    }
  }
}
