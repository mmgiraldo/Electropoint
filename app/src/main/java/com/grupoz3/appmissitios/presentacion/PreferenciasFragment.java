package com.grupoz3.appmissitios.presentacion;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.grupoz3.appmissitios.R;

public class PreferenciasFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
