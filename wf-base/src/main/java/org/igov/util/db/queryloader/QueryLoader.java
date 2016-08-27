package org.igov.util.db.queryloader;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import ru.qatools.properties.Property;
import ru.qatools.properties.PropertyLoader;
import ru.qatools.properties.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static org.springframework.util.Assert.notNull;

/**
 * This class allow to store sql/hql queries in external files
 *
 * @author dgroup
 * @since 02.08.15
 */
@Component
@Resource.Classpath("DB.SQL.properties")
public class QueryLoader {

    @Property("queryloader.db.profile")
    private String dbProfile;

    @Property("queryloader.root.folder")
    private String rootFolder;

    private String homeDirectory;

    private Map<String, String> removeOldProcessQueries = new HashMap<>();

    public QueryLoader() {
        PropertyLoader.newInstance().populate(this);
        homeDirectory = rootFolder + TypeDB.define(dbProfile).getPath();
        setRemoveOldProcessQueries();
    }

    public QueryLoader(String directory) {
        this.homeDirectory = directory;
    }

    /**
     * @param fileWithQuery file's name which contains sql/hql query.
     * @return sql/hql query from external file.
     * @throws QueryLoadingException when unable to load the file.
     * @throws MissingResourceException when unable to find the file in
     * classpath.
     */
    public String get(String fileWithQuery) {
        notNull(fileWithQuery, "Key can't be a null");
        try (InputStream in = assertLoading(homeDirectory + fileWithQuery)) {
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new QueryLoadingException(fileWithQuery, e);
        }
    }

    /**
     * @param fileWithQuery file's name which contains sql/hql queries.
     * @return sql/hql query from external file.
     * @throws QueryLoadingException when unable to load the file.
     * @throws MissingResourceException when unable to find the file in
     * classpath.
     */
    private Map<String, String> setRemoveOldProcessQueries() {
        String sFileName = "removeOldProcess.sql";
        try (InputStream in = QueryLoader.class.getResourceAsStream(homeDirectory + sFileName)) {
            for (String text : (List<String>) IOUtils.readLines(in)) {
                StringTokenizer tokenizer = new StringTokenizer(text, ";");
                if (tokenizer.hasMoreTokens()) {
                    removeOldProcessQueries.put(tokenizer.nextToken(), tokenizer.nextToken());
                }
            }
            return removeOldProcessQueries;
        } catch (IOException e) {
            throw new QueryLoadingException(sFileName, e);
        }
    }

    private InputStream assertLoading(String file) {
        InputStream in = QueryLoader.class.getResourceAsStream(file);
        if (in == null) {
            throw new MissingResourceException("File not found " + file);
        }
        return in;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    private enum TypeDB {
        Postgres("PostgreSQL"),
        H2("H2");

        private String profile;

        private TypeDB(String profile) {
            this.profile = profile;
        }

        public static final TypeDB define(String profile) {
            notNull(profile, "Profile can't be empty");
            for (TypeDB type : values()) {
                if (type.getPath().startsWith(profile)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Database type " + profile + " not found");
        }

        public String getPath() {
            return profile + '/';
        }
    }

    public Map<String, String> getRemoveOldProcessQueries() {
        return removeOldProcessQueries;
    }
}
