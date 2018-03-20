package org.unicef.rapidreg.tracing.tracinglist;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.tracing.TracingFeature;
import org.unicef.rapidreg.utils.JsonUtils;
import org.unicef.rapidreg.utils.StreamUtil;
import org.unicef.rapidreg.utils.Utils;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;
import static org.unicef.rapidreg.service.TracingService.TRACING_PRIMARY_ID;

public class TracingListAdapter extends RecordListAdapter {

    @Inject
    TracingService tracingService;

    @Inject
    public TracingListAdapter(@ActivityContext Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(final RecordListViewHolder holder, int position) {
        final long recordId = recordList.get(position);
        final RecordModel record = tracingService.getById(recordId);
        final String recordJson = new String(record.getContent().getBlob());
        final ItemValuesMap itemValues = new ItemValuesMap(JsonUtils.toMap(new Gson().fromJson
                (recordJson, JsonObject.class)));

        final String shortUUID = tracingService.getShortUUID(record.getUniqueId());
        String age = itemValues.getAsString(RecordService.RELATION_AGE);
        holder.setValues(itemValues.getAsString(RecordService.SEX), shortUUID, age, record);
        holder.setViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putLong(TRACING_PRIMARY_ID, record.getId());
                ((RecordActivity) context).turnToFeature(TracingFeature.DETAILS_MINI, args, null);
                try {
                    Utils.clearAudioFile(AUDIO_FILE_PATH);
                    if (record.getAudio() != null) {
                        StreamUtil.writeFile(record.getAudio().getBlob(), RecordService
                                .AUDIO_FILE_PATH);
                    }
                } catch (IOException e) {
                }
            }
        });
        toggleTextArea(holder);
        toggleDeleteArea(holder, record.isSynced());
        toggleDeleteCheckBox(holder);
    }

    @Override
    public void removeRecords() {
        List<Long> recordIds = getRecordWillBeDeletedList();
        for (Long recordId : recordIds) {
            tracingService.deleteByRecordId(recordId);
        }
        super.removeRecords();
        tracingService.execSQL(RecordService.SQL_VACUUM);
    }
}
