package com.car.andrproxy.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.car.Globals;
import com.car.JNIHelper;
import com.car.LogHelper;
import com.car.NaiveConfig;
import com.car.NaiveHelper;
import com.car.ProxyHelper;
import com.car.andrproxy.Constants;
import com.car.andrproxy.AppService;
import com.car.andrproxy.R;
import com.car.andrproxy.adapter.FileListAdapter;
import com.car.naive.connection.TrojanConnection;
import com.car.naive.proxy.aidl.ITrojanService;
import com.car.service.ProxyService;
import com.github.clans.fab.FloatingActionButton;
import com.kangc.ServiceTool;
import com.kangc.fileread;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.ACTIVITY_SERVICE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.refreshView)
    SwipeRefreshLayout refreshView;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_logcat)
    TextView tvLogcat;
    @BindView(R.id.sv_logcat)
    NestedScrollView svLogcat;

    private Unbinder bind;
    private FileListAdapter listAdapter;

    private static final String TAG = "Home";
    private static final int READ_WRITE_EXT_STORAGE_PERMISSION_REQUEST = 514;
    private static final int VPN_REQUEST_CODE = 233;
    private static final int SERVER_LIST_CHOOSE_REQUEST_CODE = 1024;
    private static final int EXEMPT_APP_CONFIGURE_REQUEST_CODE = 2077;
    private static final String CONNECTION_TEST_URL = "https://www.google.com";

    private @ProxyService.ProxyState
    int proxyState = ProxyService.STATE_NONE;
    private final TrojanConnection connection = new TrojanConnection(false);
    public ITrojanService trojanService;

    private String ConfigJson;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        bind = ButterKnife.bind(this, root);
        init();

        return root;
    }

    private void init() {
        listAdapter = new FileListAdapter();
        listAdapter.addChildClickViewIds(R.id.iv_delete, R.id.iv_edit, R.id.info_container);

        listAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.iv_edit) {
                editIni(position);
            } else if (view.getId() == R.id.iv_delete) {
                deleteFile(position);
            } else if (view.getId() == R.id.info_container) {
                if (isRunService(getContext())) {
                    Toast.makeText(getContext(), R.string.needStopService, Toast.LENGTH_SHORT).show();
                    return;
                }
                listAdapter.setSelectItem(listAdapter.getItem(position));
            }
        });
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        refreshView.setOnRefreshListener(this::setData);

        syncServiceState();
    }

    private void syncServiceState() {
        if (!isRunService(getContext())) {
            setServiceState(R.color.colorPlay, R.drawable.ic_play_white, R.string.notOpened);
        } else {
            setServiceState(R.color.colorStop, R.drawable.ic_stop_white, R.string.hasOpened);
        }
    }

    private void setServiceState(int color, int res, int text) {
        fab.setColorNormal(getResources().getColor(color));
        fab.setImageResource(res);
        tvState.setText(text);
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    private void editIni(int position) {
        File item = listAdapter.getItem(position);
        checkPermissions(aBoolean -> {
            if (!aBoolean) {
                Constants.tendToSettings(getContext());
                return;
            }
            Intent intent = new Intent(getContext(), IniEditActivity.class);
            intent.putExtra(getString(R.string.intent_key_file), item.getPath());
            startActivity(intent);
        });

    }

    private void deleteFile(int position) {
        File item = listAdapter.getItem(position);
        Observable.just(item)
                .map(file -> item.delete())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            listAdapter.removeAt(position);
                        } else {
                            Toast.makeText(getContext(), item.getName() + getString(R.string.actionDeleteFailed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setData() {
        getFiles().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<File>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        refreshView.setRefreshing(true);

                    }

                    @Override
                    public void onNext(List<File> files) {
                        refreshView.setRefreshing(false);
                        listAdapter.setList(files);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                    }

                    @Override
                    public void onComplete() {
                        refreshView.setRefreshing(false);

                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }

    public Observable<List<File>> getFiles() {
        return Observable.create((ObservableOnSubscribe<List<File>>) emitter -> {
            File path = Constants.getIniFileParent(getContext());
            File[] files = path.listFiles();
            emitter.onNext(files != null ? Arrays.asList(files) : new ArrayList<>());
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    private void checkPermissions(Consumer<Boolean> consumer) {
        Disposable subscribe = new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(consumer);


    }

    @OnClick(R.id.fab)
    public void onViewClicked() {





        if (isRunService(getContext())) {
            getContext().stopService(new Intent(getContext(), AppService.class));
            setServiceState(R.color.colorPlay, R.drawable.ic_play_white, R.string.notOpened);
            // stop ProxyService
            ProxyHelper.stopProxyService(getContext());
        } else {

            if (listAdapter.getSelectItem() == null) {
                Toast.makeText(getContext(), R.string.notSelectIni, Toast.LENGTH_SHORT).show();
                return;
            }
            ConfigJson = listAdapter.getItem(listAdapter.getItemPosition(listAdapter.getSelectItem())).toString();
            //Toast.makeText(getContext(), ConfigJson, Toast.LENGTH_SHORT).show();
            NaiveConfig ins = Globals.getNaiveConfigInstance();

            ins.setConfig(fileread.readTxt(ConfigJson));
            Globals.setNaiveConfigInstance(ins);

            //Toast.makeText(MainActivity.this,Globals.getTrojanConfigInstance().getConfig(),Toast.LENGTH_LONG).show();
            NaiveHelper.WriteNaiveConfig(Globals.getNaiveConfigInstance(), Globals.getNaiveConfigPath());

            NaiveHelper.ShowConfig(Globals.getNaiveConfigPath());
            // start ProxyService
            Intent i = VpnService.prepare(getContext());
            if (i != null) {
                startActivityForResult(i, VPN_REQUEST_CODE);
            } else {
                ProxyHelper.startProxyService(getContext());
            }

            readLog();

            Intent service = new Intent(getContext(), AppService.class);
            service.putExtra(getResources().getString(R.string.intent_key_file), listAdapter.getSelectItem().getPath());

            getContext().startService(service);
            setServiceState(R.color.colorStop, R.drawable.ic_stop_white, R.string.hasOpened);
        }



    }

    public boolean isRunService(Context context) {
        return ServiceTool.isServiceRunning(context,"com.car.service.ProxyService");
    }

    private Disposable readingLog = null;

    private void readLog() {
        tvLogcat.setText("");
        if (readingLog != null) return;
        HashSet<String> lst = new LinkedHashSet<String>();
        lst.add("logcat");
        lst.add("-T");
        lst.add("0");
        lst.add("-v");
        lst.add("time");
        lst.add("-s");
        lst.add("GoLog,com.car.andrproxy.AppService");
        readingLog = Observable.create((ObservableOnSubscribe<String>) emitter -> {

            Process process = Runtime.getRuntime().exec(lst.toArray(new String[0]));

            InputStreamReader in = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                emitter.onNext(line);
            }
            in.close();
            bufferedReader.close();
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        tvLogcat.append(s);
                        tvLogcat.append("\r\n");
                        svLogcat.fullScroll(View.FOCUS_DOWN);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        tvLogcat.append(throwable.toString());
                        tvLogcat.append("\r\n");
                    }
                });


    }




}
