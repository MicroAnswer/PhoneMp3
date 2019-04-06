package cn.microanswer.phonemp3.ui.fragments.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import answer.android.phonemp3.R;

public class LogListRecyclerViewAdapter extends RecyclerView.Adapter<LogListRecyclerViewAdapter.LogListItem> {

    private List<File> fileList;
    private LayoutInflater layoutInflater;
    private onMyLogListItemClick onMyLogListItemClick;

    public void setFileList(List<File> fileList) {
        if (this.fileList == null) {
            this.fileList = new ArrayList<>();
        }
        this.fileList.clear();

        this.fileList.addAll(fileList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogListItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        return new LogListItem(layoutInflater.inflate(R.layout.view_loglist_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LogListItem holder, int position) {
        holder.bind(position, fileList.get(position));
    }

    @Override
    public int getItemCount() {
        return fileList == null ? 0 : fileList.size();
    }

    public void add(File f) {
        if (this.fileList == null) {
            this.fileList = new ArrayList<>();
        }
        int size = fileList.size();
        fileList.add(f);
        notifyItemInserted(size);
    }

    public void setOnMyLogListItemClick(onMyLogListItemClick onMyLogListItemClick) {
        this.onMyLogListItemClick = onMyLogListItemClick;
    }

    public void remove(int position) {
        if (fileList != null && fileList.size() > position) {
            fileList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public class LogListItem extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView name;

        private int position;
        private File file;

        public LogListItem(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
        }

        private void bind(int position, File file) {
            this.file = file;
            this.position = position;
            name.setText(file.getName());
        }

        @Override
        public void onClick(View v) {
            if (onMyLogListItemClick != null) {
                onMyLogListItemClick.onClickLogListItem(position, file, v);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onMyLogListItemClick != null) {
                return onMyLogListItemClick.onLongClickLogListItem(position, file, v);
            }
            return true;
        }
    }

    public interface onMyLogListItemClick {
        void onClickLogListItem(int position, File f, View view);

        boolean onLongClickLogListItem(int position, File f, View view);
    }
}
