package com.github.damiano1996.intellijplugin.incoder.initializable;

public interface Initializable {

    void subscribe(InitializableListener listener);

    void init() throws InitializableException;

    void close() throws InitializableException;
}
