package org.igov.service.business.document.access.handler;

import org.igov.service.exception.HandlerNotFoundException;
import org.igov.service.business.document.access.handler.DocumentAccessHandler;
import org.igov.model.document.DocumentOperator_SubjectOrgan;
import org.igov.model.document.DocumentOperator_SubjectOrgan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author dgroup
 * @since 28.06.15
 */
@Component
public class HandlerFactory {

    @Autowired
    private ApplicationContext context;

    public DocumentAccessHandler buildHandlerFor(DocumentOperator_SubjectOrgan operator) {
        try {
            return (DocumentAccessHandler) context
                    .getAutowireCapableBeanFactory()
                    .createBean(Class.forName(operator.getsHandlerClass()));

        } catch (ClassNotFoundException e) {
            throw new HandlerNotFoundException(
                    "Handler for operator:" + operator.getnID_SubjectOrgan() + " not found.", e);
        }
    }
}
