package cn.microanswer.phonemp3.ui.fragments.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import answer.android.phonemp3.R;

public class CodeUseListRecyclerViewAdapter extends RecyclerView.Adapter<CodeUseListRecyclerViewAdapter.LogListItem> {

    private String[] names;
    private String[] descs;
    private LayoutInflater layoutInflater;
    private onCodeUseListItemClick onClickLogListItem;

    public void setData(String[] names, String[] lists) {
        this.names = names;
        this.descs = lists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogListItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        return new LogListItem(layoutInflater.inflate(R.layout.view_codeuselist_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LogListItem holder, int position) {
        holder.bind(position, names[position], descs[position]);
    }

    @Override
    public int getItemCount() {
        return names == null ? 0 : names.length;
    }

    public void setOnClickLogListItem(onCodeUseListItemClick onClickLogListItem) {
        this.onClickLogListItem = onClickLogListItem;
    }

    public class LogListItem extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView name;
        private TextView desc;

        private int position;

        public LogListItem(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
            itemView.setOnClickListener(this);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
        }

        private void bind(int position, String title, String descs) {
            this.position = position;
            name.setText(title);
            desc.setText(descs);
        }

        @Override
        public void onClick(View v) {
            if (onClickLogListItem != null) {
                onClickLogListItem.onClickCodeUseListItem(position, name.getText().toString(), desc.getText().toString(), v);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onClickLogListItem != null) {
                return onClickLogListItem.onLongClickCodeUseListItem(position, name.getText().toString(), desc.getText().toString(), v);
            }
            return true;
        }
    }

    public interface onCodeUseListItemClick {
        void onClickCodeUseListItem(int position, String name, String desc, View view);

        boolean onLongClickCodeUseListItem(int position, String name, String desc, View view);
    }
}
