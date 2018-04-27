package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.model.IncidentForm_Table;
import org.unicef.rapidreg.repository.IncidentFormDao;

public class IncidentFormDaoImpl implements IncidentFormDao {
    @Override
    public IncidentForm getIncidentForm(String moduleId, String apiBaseUrl, String formLocale) {
        return SQLite.select().from(IncidentForm.class)
                .where(IncidentForm_Table.module_id.eq(moduleId))
                .and(IncidentForm_Table.server_url.eq(apiBaseUrl))
                .and(IncidentForm_Table.form_locale.eq(formLocale))
                .querySingle();
    }
}
