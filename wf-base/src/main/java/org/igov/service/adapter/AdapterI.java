package org.igov.service.adapter;

/**
 * Created by diver on 4/8/15.
 */
public interface AdapterI<Input, Output> {

    Output apply(Input input);
}
