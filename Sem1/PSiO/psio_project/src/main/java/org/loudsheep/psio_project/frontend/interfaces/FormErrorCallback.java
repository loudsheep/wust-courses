package org.loudsheep.psio_project.frontend.interfaces;


@FunctionalInterface
public interface FormErrorCallback {
    void setError(String text);
}