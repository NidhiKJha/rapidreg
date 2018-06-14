package org.unicef.rapidreg.service.impl;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.repository.remote.FormRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.FormRemoteService;

import java.util.concurrent.TimeUnit;


import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class FormRemoteServiceImpl extends BaseRetrofitService<FormRepository> implements FormRemoteService {
    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }

    public Observable<CaseTemplateForm> getCaseForm(String cookie, String locale,
                                                    Boolean isMobile, String parentForm, String moduleId) {
        return getRepository(FormRepository.class).getCaseForm(cookie, locale, isMobile, parentForm,
                moduleId)
                .flatMap(new Function<CaseTemplateForm, Observable<CaseTemplateForm>>() {
                    @Override
                    public Observable<CaseTemplateForm> apply(CaseTemplateForm caseForm) {
                        if (caseForm == null) {
                            return Observable.error(new Exception());
                        }
                        return Observable.just(caseForm);
                    }
                })
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<TracingTemplateForm> getTracingForm(String cookie, String locale,
                                                          Boolean isMobile, String parentForm, String moduleId) {
        return getRepository(FormRepository.class).getTracingForm(cookie, locale, isMobile,
                parentForm, moduleId)
                .flatMap(new Function<TracingTemplateForm, Observable<TracingTemplateForm>>() {
                    @Override
                    public Observable<TracingTemplateForm> apply(TracingTemplateForm
                                                                        tracingTemplateForm) {
                        if (tracingTemplateForm == null) {
                            return Observable.error(new Exception());
                        }
                        return Observable.just(tracingTemplateForm);
                    }
                })
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<IncidentTemplateForm> getIncidentForm(String cookie, String locale,
                                                            Boolean isMobile, String parentForm, String moduleId) {
        return getRepository(FormRepository.class).getIncidentForm(cookie, locale, isMobile,
                parentForm, moduleId)
                .flatMap(new Function<IncidentTemplateForm, Observable<IncidentTemplateForm>>() {
                    @Override
                    public Observable<IncidentTemplateForm> apply(IncidentTemplateForm
                                                                         incidentForm) {
                        if (incidentForm == null) {
                            return Observable.error(new Exception());
                        }
                        return Observable.just(incidentForm);
                    }
                })
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}


