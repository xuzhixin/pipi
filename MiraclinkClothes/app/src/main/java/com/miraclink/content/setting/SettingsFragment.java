package com.miraclink.content.setting;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.miraclink.R;
import com.miraclink.bluetooth.DeviceListActivity;
import com.miraclink.content.ContentActivity;
import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.database.UserDatabaseManager;
import com.miraclink.model.User;
import com.miraclink.networks.NetworkUtil;
import com.miraclink.utils.AppExecutors;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.SharePreUtils;
import com.miraclink.widget.ScrollerLayout;
import com.miraclink.widget.SettingLineLayout;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements View.OnClickListener, ScrollerLayout.OnSelectPositionClick, SettingsContract.IView, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private String[] strongs = {"0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};
    private List<String> strongsList;

    private ScrollerLayout scrollerLayout;
    private SettingsContract.Presenter presenter;
    private RadioGroup rgRate, rgCompose, rgMode;
    private RadioButton rbCompose1, rbCompose2, rbCompose3, rbCompose4, rbCompose5;
    private RadioButton rbMode1, rbMode2, rbMode3, rbMode4, rbMode5;
    private RadioButton rbRate1, rbRate2, rbRate3;

    private SettingLineLayout lineLayoutDeviceSelect;
    private SettingLineLayout lineLayoutTimeSelect;

    private Button btSelectDevice;
    private Button btSaveSettings;
    private IUserDatabaseManager iUserDatabaseManager;
    private BroadcastReceiver receiver;
    private User userSettings;

    private int time;
    private int strong;
    private int rate;
    private int compose;
    private int mode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View messageView = inflater.inflate(R.layout.fragment_user_settings, container, false);
        return messageView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initParam();
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        lineLayoutDeviceSelect.getTextInfo().setText(ContentActivity.bleAddress);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadCastAction.USER_CHANGED);
        getContext().registerReceiver(receiver, filter);
        presenter.queryUser(iUserDatabaseManager, SharePreUtils.getCurrentID(getContext()));
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        presenter.queryUser(iUserDatabaseManager, SharePreUtils.getCurrentID(getContext()));
    }

    private void initParam() {
        iUserDatabaseManager = UserDatabaseManager.getInstance(getContext(), AppExecutors.getInstance());
        presenter = new SettingsPresenter(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BroadCastAction.USER_CHANGED)) {
                    presenter.queryUser(iUserDatabaseManager, SharePreUtils.getCurrentID(getContext()));
                }
            }
        };
    }

    private void initView(View view) {
        btSaveSettings = view.findViewById(R.id.btSettingsFragmentSave);
        btSaveSettings.setOnClickListener(this);
        scrollerLayout = view.findViewById(R.id.scrollerUserSettingsFragmentStrong);
        scrollerLayout.setOnSelectPositionClick(this);
        lineLayoutDeviceSelect = view.findViewById(R.id.layoutUserSettingFragmentDeviceSelect);
        lineLayoutTimeSelect = view.findViewById(R.id.layoutUserSettingFragmentTimeSelect);
        lineLayoutDeviceSelect.setOnClickListener(deviceSelectClick);
        lineLayoutTimeSelect.setOnClickListener(timeSelectClick);

        rgRate = view.findViewById(R.id.rgUserSettingFragmentRate);
        rgRate.setOnCheckedChangeListener(this);
        rgCompose = view.findViewById(R.id.rgUserSettingFragmentCompose);
        rgCompose.setOnCheckedChangeListener(this);
        rgMode = view.findViewById(R.id.rgUserSettingFragmentMode);
        rgMode.setOnCheckedChangeListener(this);
        rbCompose1 = view.findViewById(R.id.rbUserSettingFragmentCompose1);
        rbCompose2 = view.findViewById(R.id.rbUserSettingFragmentCompose1);
        rbCompose3 = view.findViewById(R.id.rbUserSettingFragmentCompose1);
        rbCompose4 = view.findViewById(R.id.rbUserSettingFragmentCompose1);
        rbCompose5 = view.findViewById(R.id.rbUserSettingFragmentCompose1);
        rbMode1 = view.findViewById(R.id.rbUserSettingFragmentMode1);
        rbMode2 = view.findViewById(R.id.rbUserSettingFragmentMode2);
        rbMode3 = view.findViewById(R.id.rbUserSettingFragmentMode3);
        rbMode4 = view.findViewById(R.id.rbUserSettingFragmentMode4);
        rbMode5 = view.findViewById(R.id.rbUserSettingFragmentMode5);
        rbRate1 = view.findViewById(R.id.rbUserSettingFragmentRate1);
        rbRate2 = view.findViewById(R.id.rbUserSettingFragmentRate2);
        rbRate3 = view.findViewById(R.id.rbUserSettingFragmentRate3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSettingsFragmentSave:
                if (NetworkUtil.getConnectivityEnable(getContext())) {

                } else {
                    presenter.updateUserSettings(iUserDatabaseManager, time, strong, rate, compose, mode, SharePreUtils.getCurrentID(getContext()));
                }
                break;
            default:
                break;
        }
    }

    private View.OnClickListener deviceSelectClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), DeviceListActivity.class);
            getActivity().startActivityForResult(intent, ContentActivity.REQUEST_SELECT_DEVICE);
        }
    };
    private View.OnClickListener timeSelectClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public void onGetSelectPosition(int position) {
        LogUtil.i(TAG, "scoller position:" + position);
        time = position * 10;
    }

    private void setTimeInit(int i) {
        int b;
        if (i <= 0) {
            i = 10;    //default time
        }
        b = i / 10;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scrollerLayout.scrollTo(scrollerLayout.getLayoutLong() * b / 5, 0);
            }
        });
    }

    private void setRateInit(int i) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (i == 1) {
                    rate = 1;
                    rbRate1.setChecked(true);
                } else if (i == 2) {
                    rate = 2;
                    rbRate2.setChecked(true);
                } else if (i == 3) {
                    rate = 3;
                    rbRate3.setChecked(true);
                }
            }
        });
    }

    private void setComposeInit(int i) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (i == 1) {
                    compose = 1;
                    rbCompose1.setChecked(true);
                } else if (i == 2) {
                    compose = 2;
                    rbCompose2.setChecked(true);
                } else if (i == 3) {
                    compose = 3;
                    rbCompose3.setChecked(true);
                } else if (i == 4) {
                    compose = 4;
                    rbCompose4.setChecked(true);
                } else if (i == 5) {
                    compose = 5;
                    rbCompose5.setChecked(true);
                }
            }
        });
    }

    private void setModeInit(int i) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (i == 1) {
                    mode = 1;
                    rbMode1.setChecked(true);
                } else if (i == 2) {
                    mode = 2;
                    rbMode2.setChecked(true);
                } else if (i == 3) {
                    mode = 3;
                    rbMode3.setChecked(true);
                } else if (i == 4) {
                    mode = 4;
                    rbMode4.setChecked(true);
                } else if (i == 5) {
                    mode = 5;
                    rbMode5.setChecked(true);
                }
            }
        });
    }

    @Override
    public void setUserView(User user) {
        userSettings = user;
        if (userSettings == null) {
            return;
        }
        LogUtil.i(TAG, "test move:" + userSettings.getStrong());
        //slideHorizontalViewRate.setInitPos(userSettings.getRate());
        //slideHorizontalViewStrong.setInitPos(userSettings.getStrong() / 10);
        //setTimeInit(userSettings.getTime() - 1);
        //setComposeInit(userSettings.getCompose());
        //setModeInit(userSettings.getMode());
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == rgCompose) {
            switch (checkedId) {
                case R.id.rbUserSettingFragmentCompose1:
                    compose = 1;
                    break;
                case R.id.rbUserSettingFragmentCompose2:
                    compose = 2;
                    break;
                case R.id.rbUserSettingFragmentCompose3:
                    compose = 3;
                    break;
                case R.id.rbUserSettingFragmentCompose4:
                    compose = 4;
                    break;
                case R.id.rbUserSettingFragmentCompose5:
                    compose = 5;
                    break;
                default:
                    break;
            }
        } else if (group == rgMode) {
            switch (checkedId) {
                case R.id.rbUserSettingFragmentMode1:
                    mode = 1;
                    break;
                case R.id.rbUserSettingFragmentMode2:
                    mode = 2;
                    break;
                case R.id.rbUserSettingFragmentMode3:
                    mode = 3;
                    break;
                case R.id.rbUserSettingFragmentMode4:
                    mode = 4;
                    break;
                case R.id.rbUserSettingFragmentMode5:
                    mode = 5;
                    break;
                default:
                    break;
            }

        } else if (group == rgRate) {
            switch (checkedId) {
                case R.id.rbUserSettingFragmentRate1:
                    rate = 1;
                    break;
                case R.id.rbUserSettingFragmentRate2:
                    rate = 2;
                    break;
                case R.id.rbUserSettingFragmentRate3:
                    rate = 3;
                    break;
                default:
                    break;
            }
        }
    }
}
