package it.smartcommunitylabdhub.commons;

public class Keys {

    public static final String SLUG_PATTERN = "^[a-zA-Z0-9._+-]+$";
    public static final String PATH_PATTERN = "([^:/]+)://([^/]+)/([^:]+):(.+)";
    public static final String STORE_PREFIX = "store://";
    public static final String PATH_DIVIDER = "/";
    public static final String ID_DIVIDER = ":";
    public static final String FILE_PATTERN = "(([^/]+)/)*([^/]+)";
    public static final String FOLDER_PATTERN = ".*\\/$";
    public static final String ZIP_PATTERN = ".*\\.zip$";
    public static final String CRONTAB_PATTERN =
        "(@(annually|yearly|monthly|weekly|daily|hourly|reboot))|(@every (\\d+(ns|us|µs|ms|s|m|h))+)|((((\\d+,)+\\d+|(\\d+(\\/|-)\\d+)|\\d+|\\*) ?){5,7})";

    private Keys() {}
}
