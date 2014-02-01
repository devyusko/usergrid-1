package org.usergrid.management;

import java.util.Map;

/**
 * Created by ApigeeCorporation on 1/31/14.
 */
public class ExportInfo {

    private String path;
    private Map<String, Object> properties;
    private String storage_provider;
    private Map<String, Object> storage_info;
    private String s3_token;
    private String s3_key;
    private String bucket_location;

    public ExportInfo ( Map<String, Object> exportData) {
        path = (String) exportData.get("path");
        properties = (Map) exportData.get("properties");
        storage_provider = (String) properties.get ("storage_provider");
        storage_info = (Map) properties.get("storage_info");
        s3_token = (String) storage_info.get("s3_token");
        s3_key = (String) storage_info.get("s3_key");
        bucket_location = (String) storage_info.get("bucket_location");
    }

    public String getPath () {
        return path;
    };

    //Wouldn't get exposed.
    public Map<String, Object> getProperties() {
        return properties;
    }

    public String getStorage_provider () {
        return storage_provider;
    }
    //TODO: write getter and setter methods


}
