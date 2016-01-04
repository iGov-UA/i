package org.igov.activiti.common;

/**
 * Интерфейс для моделей с ключем
 * (для хранения в списках в execution бизнес процесса)
 *
 * @author kpi
 */
public interface ListKeyable {

    public String getKey();
}