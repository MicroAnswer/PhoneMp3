package answer.android.phonemp3.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import androidx.appcompat.app.AppCompatDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import answer.android.phonemp3.R;
import answer.android.phonemp3.view.BaseDialogRootView;

/**
 * /弹出框父类
 * [实现了从底部弹出,手指可以向下滑动关闭的功能]
 * Created by Micro on 2017/6/24.
 */

public abstract class BaseDailog extends BottomSheetDialog {

  BaseDailog(Context context) {
    super(context);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setCanceledOnTouchOutside(true);
  }

  // protected int getHeight() {
  //   return Math.round(getContext().getResources().getDisplayMetrics().heightPixels * 0.7f);
  // }


  @Override
  protected void onStart() {
    super.onStart();
    Window window = getWindow();
    WindowManager.LayoutParams attributes = window.getAttributes();
    attributes.width = getContext().getResources().getDisplayMetrics().widthPixels;
    attributes.gravity = Gravity.LEFT | Gravity.BOTTOM;
    attributes.y = 0;
    attributes.x = 0;
    // attributes.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DIM_BEHIND;
    //  attributes.format = PixelFormat.RGBA_8888;
    // attributes.dimAmount = 0.4f;
    // attributes.height = getHeight();
    attributes.windowAnimations = R.style.basedialog_anim;
    window.setAttributes(attributes);
  }
}
