package ge.edu.freeuni.asignment3.ui.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import ge.edu.freeuni.asignment3.model.FileInfo;
import ge.edu.freeuni.asignment3.R;
import ge.edu.freeuni.asignment3.ui.PdfReaderActivity;
import ge.edu.freeuni.asignment3.ui.TextEditorActivity;
import ge.edu.freeuni.asignment3.ui.navigation.recycler.ExplorerRecyclerAdapter;

public class ExplorerActivity extends AppCompatActivity implements ExplorerContract.ExplorerView {
    private static final int REQUEST_EXTERNAL_STORAGE = 123456;

    private ExplorerContract.ExplorerPresenter presenter;
    private RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    ExplorerRecyclerAdapter adapter;
    private OnItemClickListenerImpl onItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        onItemClickListener = new OnItemClickListenerImpl();

        recyclerView = findViewById(R.id.rv_explorer);
        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 4);

        presenter = new ExplorerPresenterImpl(this, new ExplorerInteractorImpl());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int selfPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (selfPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            } else {
                presenter.handleFileClick("");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.handleFileClick("");
            }
        }
    }


    @Override
    public void onDataLoaded(List<FileInfo> directoryContent) {
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ExplorerRecyclerAdapter(getApplicationContext(), onItemClickListener);
        adapter.setData(directoryContent);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void loadPdf(String path) {
        TextEditorActivity.start(path, ExplorerActivity.this);
    }


    class OnItemClickListenerImpl implements ExplorerRecyclerAdapter.OnItemClickListener {

        @Override
        public void onItemClick(FileInfo item) {
            presenter.handleFileClick(item.getFileName());
        }
    }

    @Override
    public void onBackPressed() {
        presenter.handleFileClick(null);
    }


}
