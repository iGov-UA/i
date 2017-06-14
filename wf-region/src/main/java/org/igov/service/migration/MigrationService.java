package org.igov.service.migration;

import org.springframework.stereotype.Service;

/**
 * Created by dpekach on 01.05.17.
 */
public interface MigrationService {
    void migrateOldRecords(String processId);
}
