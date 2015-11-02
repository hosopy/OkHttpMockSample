package com.hosopy.okhttpmocksample.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hosopy.okhttpmocksample.R;
import com.hosopy.okhttpmocksample.api.entity.Repository;

import java.util.List;

public class RepositoryListActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_list);

        mListView = (ListView) findViewById(R.id.list_view);
    }

    protected void showRepositories(List<Repository> repositories) {
        mListView.setAdapter(new RepositoryAdapter(getApplicationContext(), repositories));
    }

    protected void showError(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private static class ViewHolder {
        TextView tvName;
        TextView tvHtmlUrl;
    }

    private static class RepositoryAdapter extends ArrayAdapter<Repository> {

        public RepositoryAdapter(Context context, List<Repository> repositories) {
            super(context, R.layout.repository_item, repositories);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.repository_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tvHtmlUrl = (TextView) convertView.findViewById(R.id.tv_html_url);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();

            }

            final Repository repository = getItem(position);

            viewHolder.tvName.setText(repository.getName());
            viewHolder.tvHtmlUrl.setText(repository.getHtmlUrl());

            return convertView;
        }
    }
}
