package com.suda.yzune.youngcommemoration.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.suda.yzune.youngcommemoration.R;
import com.suda.yzune.youngcommemoration.utils.SharedPreferencesUtils;
import com.suda.yzune.youngcommemoration.utils.ViewUtil;

public class SettingsActivity extends AppCompatActivity {
    Switch s_update, s_anim, s_swipe, s_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
        initEvent();
    }

    public void initView(){
        s_update = (Switch) findViewById(R.id.s_update);
        s_anim = (Switch) findViewById(R.id.s_anim);
        s_swipe = (Switch) findViewById(R.id.s_swipe);
        s_back = (Switch) findViewById(R.id.s_back);

        s_update.setChecked(SharedPreferencesUtils.getBooleanFromSP(SettingsActivity.this, "s_update", true));
        s_anim.setChecked(SharedPreferencesUtils.getBooleanFromSP(SettingsActivity.this, "s_anim", true));
        s_swipe.setChecked(SharedPreferencesUtils.getBooleanFromSP(SettingsActivity.this, "s_swipe", false));
        s_back.setChecked(SharedPreferencesUtils.getBooleanFromSP(SettingsActivity.this, "s_back", true));
    }

    public void initEvent(){
        s_update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBooleanToSP(SettingsActivity.this, "s_update", isChecked);
            }
        });
        s_anim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBooleanToSP(SettingsActivity.this, "s_anim", isChecked);
            }
        });
        s_back.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBooleanToSP(SettingsActivity.this, "s_back", isChecked);
            }
        });
    }
}
