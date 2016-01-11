package org.igov.model.action.task.core.entity;

/**
 * Created by diver on 4/8/15.
 */
public interface CoverInterface<Input, Output> {

    Output apply(Input input);
}
