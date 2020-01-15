package com.lch.we.alchemy.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.lch.we.alchemy.R;


/**
 * Created by cyb on 2018/2/1.
 */

public class SettingFragment extends PreferenceFragment {

    private MediaPlayer mediaPlayer;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setPrefListeners();
    }


    private void setPrefListeners() {

        //提示音
        Preference pref = findPreference("pref_audio_alarm");
        String tipp = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("pref_audio_alarm", "");
        pref.setSummary(getVideoListSummary(Integer.parseInt(tipp)));
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ListPreference lp = (ListPreference) preference;
                String tip = newValue.toString();
                lp.setValue(tip);
                preference.setSummary(lp.getEntry());
                if(mediaPlayer != null) mediaPlayer.stop();
                if(!tip.equals("0")) {
                    if (tip.equals("1")) mediaPlayer = MediaPlayer.create(getContext(), R.raw.hongbao);
                    if (tip.equals("2")) mediaPlayer = MediaPlayer.create(getContext(), R.raw.jingbao);
                    if (tip.equals("3")) mediaPlayer = MediaPlayer.create(getContext(), R.raw.dingding);
                    mediaPlayer.start();
                }else{
                    mediaPlayer = null;
                }
                return true;
            }
        });

        // Check for updates
        Preference updatePref = findPreference("pref_app_setting");
        updatePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                startActivity( new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) mediaPlayer.stop();
        mediaPlayer = null;
    }

    private String getVideoListSummary(int index){
        Resources res = getResources();
        String[] test = res.getStringArray(R.array.audioChange);
        return test[index];
    }


}
