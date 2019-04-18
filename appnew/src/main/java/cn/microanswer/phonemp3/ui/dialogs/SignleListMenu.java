package cn.microanswer.phonemp3.ui.dialogs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.android.material.checkbox.MaterialCheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import answer.android.phonemp3.R;

public class SignleListMenu extends BaseDialog implements AdapterView.OnItemClickListener {

    // 数组资源id
    private int stringArrayResId;
    private ItemClickListener itemClickListener;
    private Integer checkedPosition = -1;

    public SignleListMenu(@NonNull Context context, int stringArrayResId) {
        super(context);
        this.stringArrayResId = stringArrayResId;
    }

    public SignleListMenu(@NonNull Context context, int stringArrayResId, Integer checkedPosition) {
        super(context);
        this.stringArrayResId = stringArrayResId;
        this.checkedPosition = checkedPosition;
    }

    @Override
    protected View getContentView(FrameLayout parent) {
        ListView listView = new ListView(getContext());
        listView.setOnItemClickListener(this);
        String[] entries = getContext().getResources().getStringArray(stringArrayResId);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.dialog_signle_list_menu,
                android.R.id.text1,
                entries
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                MaterialCheckBox checkBox = v.findViewById(R.id.checkbox);
                if (checkedPosition == position) {
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setChecked(true);
                }else {
                    checkBox.setVisibility(View.GONE);
                    checkBox.setChecked(false);
                }
                return v;
            }
        };
        listView.setAdapter(stringArrayAdapter);
        return listView;
    }

    @Override
    protected boolean hasCancelBtn() {
        return false;
    }

    @Override
    protected boolean hasSureBtn() {
        return false;
    }

    @Override
    protected boolean hasTitle() {
        return false;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        if (itemClickListener!=null) itemClickListener.onItemClick(position);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public static interface ItemClickListener {
        void onItemClick(int position);
    }
}
