package org.unicef.rapidreg.loadform;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.LookupService;
import org.unicef.rapidreg.service.SystemSettingsService;
import org.unicef.rapidreg.service.TracingFormService;

import edu.emory.mathcs.backport.java.util.Arrays;
import io.reactivex.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PrimeroAppConfiguration.class})
public class AppRemotePresenterTest {
    private FormRemoteService formRemoteService;
    private CaseFormService caseFormService;
    private TracingFormService tracingFormService;
    private IncidentFormService incidentFormService;
    private SystemSettingsService systemSettingsService;
    private LookupService lookupService;

    private AppRemotePresenter appRemotePresenter;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(PrimeroAppConfiguration.class);

        formRemoteService = mock(FormRemoteService.class);
        caseFormService = mock(CaseFormService.class);
        tracingFormService = mock(TracingFormService.class);
        incidentFormService = mock(IncidentFormService.class);
        systemSettingsService = mock(SystemSettingsService.class);
        lookupService = mock(LookupService.class);

        appRemotePresenter = new AppRemotePresenter(formRemoteService,
                caseFormService,
                tracingFormService,
                incidentFormService,
                lookupService,
                systemSettingsService);
    }

    @Test
    public void should_save_form_when_give_record_form_and_module_id() throws Exception {
        CaseTemplateForm caseTemplateForm = createCaseTemplateForm();
        String moduleId = "primeromodule-cp";
        appRemotePresenter.saveCaseForm(caseTemplateForm, moduleId);

        verify(caseFormService, times(1)).saveOrUpdate(any(CaseForm.class));
    }

    @Test
    public void should_load_case_form_when_give_cookie_and_module_id() throws Exception {
        CaseTemplateForm caseTemplateForm = createCaseTemplateForm();
        Observable<CaseTemplateForm> observable = Observable.just(caseTemplateForm);
        when(formRemoteService.getCaseForm("cookie", "en", true, "case", PrimeroAppConfiguration.MODULE_ID_CP))
                .thenReturn(observable);
        when(PrimeroAppConfiguration.getCookie()).thenReturn("cookie");
        when(PrimeroAppConfiguration.getDefaultLanguage()).thenReturn("en");
        AppRemoteService.LoadCallback callback = mock(AppRemoteService.LoadCallback.class);
        appRemotePresenter.loadCaseForm(PrimeroAppConfiguration.MODULE_ID_CP, callback);

        verify(caseFormService, times(1)).saveOrUpdate(any(CaseForm.class));
        verify(callback, times(1)).onSuccess();
        verify(callback, never()).onFailure();
    }

    @Test
    public void should_show_error_when_sync_case_form_fail() throws Exception {
        Observable observable = Observable.error(new Exception());
        when(formRemoteService.getCaseForm("cookie", "en", true, "case", "primeromodule-cp"))
                .thenReturn(observable);

        when(PrimeroAppConfiguration.getCookie()).thenReturn("cookie");
        when(PrimeroAppConfiguration.getDefaultLanguage()).thenReturn("en");
        AppRemoteService.LoadCallback callback = mock(AppRemoteService.LoadCallback.class);
        appRemotePresenter.loadCaseForm("primeromodule-cp", callback);

        verify(callback, times(1)).onFailure();
        verify(callback, never()).onSuccess();
    }

    @Test
    public void should_save_form_when_give_tracing_form() throws Exception {
        TracingTemplateForm tracingTemplateForm = createTracingTemplateForm();
        appRemotePresenter.saveTracingForm(tracingTemplateForm);

        verify(tracingFormService, times(1)).saveOrUpdate(any(TracingForm.class));
    }

    @Test
    public void should_load_tracing_form_when_give_cookie_and_module_id() throws Exception {
        TracingTemplateForm tracingTemplateForm = createTracingTemplateForm();
        Observable<TracingTemplateForm> observable = Observable.just(tracingTemplateForm);
        when(formRemoteService.getTracingForm("cookie", "en", true, "tracing_request", "primeromodule-cp"))
                .thenReturn(observable);
        Mockito.when(PrimeroAppConfiguration.getCookie()).thenReturn("cookie");
        Mockito.when(PrimeroAppConfiguration.getDefaultLanguage()).thenReturn("en");
        AppRemoteService.LoadCallback callback = mock(AppRemoteService.LoadCallback.class);
        appRemotePresenter.loadTracingForm(callback);

        verify(tracingFormService, times(1)).saveOrUpdate(any(TracingForm.class));
        verify(callback, times(1)).onSuccess();
        verify(callback, never()).onFailure();
    }

    @Test
    public void should_show_error_when_sync_tracing_form_fail() throws Exception {
        Observable observable = Observable.error(new Exception());
        when(formRemoteService.getTracingForm("cookie", "en", true, "tracing_request", "primeromodule-cp"))
                .thenReturn(observable);

        Mockito.when(PrimeroAppConfiguration.getCookie()).thenReturn("cookie");
        Mockito.when(PrimeroAppConfiguration.getDefaultLanguage()).thenReturn("en");
        AppRemoteService.LoadCallback callback = mock(AppRemoteService.LoadCallback.class);
        appRemotePresenter.loadTracingForm(callback);

        verify(callback, times(1)).onFailure();
        verify(callback, never()).onSuccess();
    }

    @Test
    public void should_load_incident_form_when_give_cookie_and_module_id() throws Exception {
        IncidentTemplateForm incidentTemplateForm = createIncidentTemplateForm();
        Observable<IncidentTemplateForm> observable = Observable.just(incidentTemplateForm);
        when(formRemoteService.getIncidentForm("cookie", "en", true, "incident",
                "primeromodule-gbv")).thenReturn(observable);
        Mockito.when(PrimeroAppConfiguration.getCookie()).thenReturn("cookie");
        Mockito.when(PrimeroAppConfiguration.getDefaultLanguage()).thenReturn("en");
        AppRemoteService.LoadCallback callback = mock(AppRemoteService.LoadCallback.class);
        appRemotePresenter.loadIncidentForm(callback);

        verify(incidentFormService, times(1)).saveOrUpdate(any(IncidentForm.class));
        verify(callback, times(1)).onSuccess();
        verify(callback, never()).onFailure();
    }

    @Test
    public void should_show_error_when_sync_incident_form_fail() throws Exception {
        Observable observable = Observable.error(new Exception());
        when(formRemoteService.getIncidentForm("cookie", "en", true, "incident", "primeromodule-gbv"))
                .thenReturn(observable);
        Mockito.when(PrimeroAppConfiguration.getCookie()).thenReturn("cookie");
        Mockito.when(PrimeroAppConfiguration.getDefaultLanguage()).thenReturn("en");
        AppRemoteService.LoadCallback callback = mock(AppRemoteService.LoadCallback.class);
        appRemotePresenter.loadIncidentForm(callback);

        verify(callback, times(1)).onFailure();
        verify(callback, never()).onSuccess();
    }

    @Test
    public void should_save_form_when_give_incident_form() throws Exception {
        IncidentTemplateForm incidentTemplateForm = createIncidentTemplateForm();
        appRemotePresenter.saveIncidentForm(incidentTemplateForm);
        verify(incidentFormService, times(1)).saveOrUpdate(any(IncidentForm.class));
    }

    private CaseTemplateForm createCaseTemplateForm() {
        CaseTemplateForm caseTemplateForm = new CaseTemplateForm();
        Section section = createSection();
        caseTemplateForm.setSections(Arrays.asList(new Section[]{section}));
        return caseTemplateForm;
    }

    private TracingTemplateForm createTracingTemplateForm() {
        TracingTemplateForm tracingTemplateForm = new TracingTemplateForm();
        Section section = createSection();
        tracingTemplateForm.setSections(Arrays.asList(new Section[]{section}));
        return tracingTemplateForm;
    }

    private IncidentTemplateForm createIncidentTemplateForm() {
        IncidentTemplateForm incidentTemplateForm = new IncidentTemplateForm();
        Section section = createSection();
        incidentTemplateForm.setSections(Arrays.asList(new Section[]{section}));
        return incidentTemplateForm;
    }

    private Section createSection() {
        Section section = new Section();
        section.setBaseLanguage("base_language");
        section.setOrder(12);
        return section;
    }
}