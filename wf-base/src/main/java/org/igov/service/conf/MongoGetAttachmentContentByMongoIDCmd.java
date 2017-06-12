package org.igov.service.conf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.activiti.engine.impl.cmd.GetAttachmentContentCmd;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.AttachmentEntity;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntity;
import org.apache.commons.lang3.StringUtils;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoGetAttachmentContentByMongoIDCmd extends GetAttachmentContentCmd {

    private static final long serialVersionUID = 1L;
    private final transient Logger LOG = LoggerFactory.getLogger(MongoGetAttachmentContentByMongoIDCmd.class);
    protected transient IBytesDataStorage durableBytesDataStorage;

    public MongoGetAttachmentContentByMongoIDCmd(String contentId, IBytesDataStorage durableBytesDataStorage) {
        super(contentId);
        this.durableBytesDataStorage = durableBytesDataStorage;
    }

    @Override
    public InputStream execute(CommandContext commandContext) {
        String realKey = StringUtils.substringAfter(attachmentId, MongoCreateAttachmentCmd.MONGO_KEY_PREFIX);
        byte[] content = durableBytesDataStorage.getData(realKey);

        LOG.info("Retrieved content ID from MOngo DB by key:{}", realKey);
        return new ByteArrayInputStream(content);
    }

}
