package org.loudsheep.psio_project.frontend.controllers;

import org.loudsheep.psio_project.frontend.interfaces.FormSubmitCallback;

import java.util.Map;

public interface FormControllerInterface {
    void setParams(Map<String, Object> params);

    void setSubmitCallback(FormSubmitCallback callback);
}