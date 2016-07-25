package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordActivity;
import org.unicef.rapidreg.base.RecordRegisterAdapter;
import org.unicef.rapidreg.base.RecordRegisterFragment;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.forms.CaseFormRoot;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.JsonUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.OnClick;

public class CaseMiniFormFragment extends RecordRegisterFragment {
    public static final String TAG = CaseMiniFormFragment.class.getSimpleName();

    private long recordId;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveCase(SaveCaseEvent event) {
        if (validateRequiredField()) {
            List<String> photoPaths = photoAdapter.getAllItems();
            ItemValues itemValues = new ItemValues(new Gson().fromJson(new Gson().toJson(
                    this.itemValues.getValues()), JsonObject.class));

            if (savedSuccessfully(itemValues, photoPaths)) {
                Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_SHORT).show();
            }

            Case record = CaseService.getInstance().getByUniqueId(itemValues.getAsString(CaseService.CASE_ID));

            Bundle args = new Bundle();
            args.putLong(CaseService.CASE_ID, record.getId());
            args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) photoAdapter.getAllItems());
            ((RecordActivity) getActivity()).turnToFeature(CaseFeature.DETAILS_MINI, args);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initItemValues();

        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Field> fields = getFields();
        presenter.initContext(getActivity(), fields, true);
    }

    @Override
    protected List<Field> getFields() {
        List<Field> fields = new ArrayList<>();

        RecordForm form = CaseFormService.getInstance().getCurrentForm();
        List<Section> sections = form.getSections();

        for (Section section : sections) {
            for (Field field : section.getFields()) {
                if (field.isShowOnMiniForm()) {
                    if (field.isPhotoUploadBox()) {
                        fields.add(0, field);
                    } else {
                        fields.add(field);
                    }
                }
            }
        }
        if (!fields.isEmpty()) {
            addProfileFieldForDetailsPage(fields);
        }

        return fields;
    }

    @Override
    public void initView(RecordRegisterAdapter adapter) {
        super.initView(adapter);
        formSwitcher.setText(R.string.show_more_details);

        if (((RecordActivity) getActivity()).getCurrentFeature().isDetailMode()) {
            editButton.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.edit)
    public void onEditClicked() {
        Bundle args = new Bundle();
        args.putLong(CaseService.CASE_ID, recordId);
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) photoAdapter.getAllItems());
        ((CaseActivity) getActivity()).turnToFeature(CaseFeature.EDIT, args);
    }

    protected void initItemValues() {
        if (getArguments() != null) {
            recordId = getArguments().getLong(CaseService.CASE_ID);
            Case item = CaseService.getInstance().getById(recordId);
            String caseJson = new String(item.getContent().getBlob());
            try {
                itemValues = new ItemValuesMap(JsonUtils.toMap(ItemValues.generateItemValues(caseJson).getValues()));
                itemValues.addStringItem(CaseService.CASE_ID, item.getUniqueId());
            } catch (JSONException e) {
                Log.e(TAG, "Json conversion error");
            }

            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            String shortUUID = RecordService.getShortUUID(item.getUniqueId());
            itemValues.addStringItem(ItemValues.RecordProfile.ID_NORMAL_STATE, shortUUID);
            itemValues.addStringItem(ItemValues.RecordProfile.REGISTRATION_DATE,
                    dateFormat.format(item.getRegistrationDate()));
            itemValues.addStringItem(ItemValues.RecordProfile.GENDER_NAME, shortUUID);
            itemValues.addNumberItem(ItemValues.RecordProfile.ID, item.getId());
            photoAdapter = new CasePhotoAdapter(getContext(),
                    getArguments().getStringArrayList(RecordService.RECORD_PHOTOS));
        } else {
            itemValues = new ItemValuesMap();
            photoAdapter = new CasePhotoAdapter(getContext(), new ArrayList<String>());
        }
    }

    private boolean validateRequiredField() {
        CaseFormRoot caseForm = CaseFormService.getInstance().getCurrentForm();
        List<String> requiredFieldNames = new ArrayList<>();

        for (Section section : caseForm.getSections()) {
            Collections.addAll(requiredFieldNames, RecordService
                    .fetchRequiredFiledNames(section.getFields()).toArray(new String[0]));
        }
        for (String field : requiredFieldNames) {
            if (TextUtils.isEmpty((CharSequence) itemValues.getValues().get(field))) {
                Toast.makeText(getActivity(), R.string.required_field_is_not_filled,
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private boolean savedSuccessfully(ItemValues itemValues, List<String> photoPaths) {
        try {
            CaseService.getInstance().saveOrUpdate(itemValues, photoPaths);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}