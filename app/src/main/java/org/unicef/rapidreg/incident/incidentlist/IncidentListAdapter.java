package org.unicef.rapidreg.incident.incidentlist;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordlist.RecordListViewHolder;
import org.unicef.rapidreg.incident.IncidentFeature;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.JsonUtils;
import org.unicef.rapidreg.utils.StreamUtil;
import org.unicef.rapidreg.utils.Utils;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;

public class IncidentListAdapter extends RecordListAdapter {

    @Inject
    IncidentService incidentService;

    @Inject
    public IncidentListAdapter(@ActivityContext Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(final RecordListViewHolder holder, int
            position) {
        final long recordId = recordList.get(position);
        final RecordModel record = incidentService.getById(recordId);
        final String recordJson = new String(record.getContent().getBlob());
        final ItemValuesMap itemValues = new ItemValuesMap(JsonUtils.toMap(new Gson().fromJson
                (recordJson, JsonObject.class)));

        final String shortUUID = incidentService.getShortUUID(record.getUniqueId());
        String age = itemValues.getAsString(RecordService.AGE);
        holder.disableRecordImageView();
        holder.setValues(
                itemValues.getAsString(RecordService.SEX),
                shortUUID,
                age,
                record,
                recordList,
                recordWillBeDeletedList,
                syncedRecordsCount
        );
        holder.setViewOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong(IncidentService.INCIDENT_PRIMARY_ID, recordId);
            ((RecordActivity) context).turnToFeature(IncidentFeature.DETAILS_MINI, args, null);
            try {
                Utils.clearAudioFile(AUDIO_FILE_PATH);
                if (record.getAudio() != null) {
                    StreamUtil.writeFile(record.getAudio().getBlob(), RecordService
                            .AUDIO_FILE_PATH);
                }
            } catch (IOException e) {
            }
        });
        holder.disableRecordGenderView();
        toggleTextArea(holder);
        toggleDeleteArea(holder, record.isSynced());
        toggleDeleteCheckBox(holder);
    }

    @Override
    public void removeRecords() {
        List<Long> recordIds = getRecordWillBeDeletedList();
        for (Long recordId : recordIds) {
            incidentService.deleteByRecordId(recordId);
        }
        super.removeRecords();
        incidentService.execSQL(RecordService.SQL_VACUUM);
    }
}
