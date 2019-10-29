package com.greenaddress.abcore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LogLinesAdapter extends RecyclerView.Adapter<LogLinesAdapter.LogViewHolder> {

    private String[] mLogLines = new String[0];

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LogViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        holder.bind(mLogLines[position]);
    }

    @Override
    public int getItemCount() {
        return mLogLines.length;
    }

    void updateLogs(String[] logLines) {
        if (logLines != null) {
            mLogLines = logLines;
            notifyDataSetChanged();
        }
    }

    final static class LogViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        LogViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.logLineText);
        }

        void bind(String logLine) {
            mTextView.setText(logLine);
        }
    }

}
