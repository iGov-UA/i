package org.igov.model.action.task.core.entity;

/**
 * Интерфейс для моделей с ключем
 * (для хранения в списках в execution бизнес процесса)
 *
 * @author kpi
 */
public interface ListKeyable {

    public String getKey();
}