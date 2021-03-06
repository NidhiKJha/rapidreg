package org.unicef.rapidreg.incident;

import android.support.v4.app.Fragment;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.exception.FragmentSwitchException;
import org.unicef.rapidreg.incident.incidentlist.IncidentListFragment;
import org.unicef.rapidreg.incident.incidentregister.IncidentMiniFormFragment;
import org.unicef.rapidreg.incident.incidentregister.IncidentRegisterWrapperFragment;
import org.unicef.rapidreg.incident.incidentsearch.IncidentSearchFragment;

public enum IncidentFeature implements Feature {
    LIST(R.string.incidents, IncidentListFragment.class),
    ADD_MINI(R.string.new_incident, IncidentMiniFormFragment.class),
    ADD_FULL(R.string.new_incident, IncidentRegisterWrapperFragment.class),
    EDIT_MINI(R.string.edit, IncidentMiniFormFragment.class),
    EDIT_FULL(R.string.edit, IncidentRegisterWrapperFragment.class),
    DETAILS_MINI(R.string.incident_details, IncidentMiniFormFragment.class),
    DETAILS_FULL(R.string.incident_details, IncidentRegisterWrapperFragment.class),
    DELETE(R.string.delete, IncidentListFragment.class),
    SEARCH(R.string.search, IncidentSearchFragment.class);

    private int titleId;
    private Class clz;

    IncidentFeature(int titleId, Class clz) {
        this.titleId = titleId;
        this.clz = clz;
    }

    public int getTitleId() {
        return titleId;
    }

    public Fragment getFragment() throws FragmentSwitchException {
        try {
            return (Fragment) clz.newInstance();
        } catch (InstantiationException e) {
            throw new FragmentSwitchException("The constructor is not accessible", e);
        } catch (IllegalAccessException e) {
            throw new FragmentSwitchException("The method or field is not accessible", e);
        }
    }

    public boolean isEditMode() {
        return this == ADD_MINI || this == ADD_FULL || this == EDIT_MINI || this == EDIT_FULL;
    }

    public boolean isListMode() {
        return this == LIST;
    }

    public boolean isDetailMode() {
        return this == DETAILS_FULL || this == DETAILS_MINI;
    }

    @Override
    public boolean isAddMode() {
        return this == ADD_MINI || this == ADD_FULL;
    }

    @Override
    public boolean isDeleteMode() {
        return this == DELETE;
    }

    public boolean isWebMode() { return false; }
}

