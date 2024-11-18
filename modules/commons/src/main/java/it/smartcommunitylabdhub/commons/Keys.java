package it.smartcommunitylabdhub.commons;

public class Keys {

    public static final String SLUG_PATTERN = "^[a-zA-Z0-9._+-]+$";
    public static final String FUNCTION_PATTERN = "([^:/]+)://([^/]+)/([^:]+):(.+)";
    public static final String WORKFLOW_PATTERN = "([^:/]+)://([^/]+)/([^:]+):(.+)";
    public static final String TASK_PATTERN = "([^:/]+)://([^/]+)/([^:]+)";
    public static final String KEY_PATTERN = "store://([^/]+)/([^/]+)/([^/]+)/([^:]+):(.+)";
    public static final String KEY_PATTERN_NO_ID = "store://([^/]+)/([^/]+)/([^/]+)/([^:]+)";
    public static final String STORE_PREFIX = "store://";
    public static final String PATH_DIVIDER = "/";
    public static final String ID_DIVIDER = ":";
    public static final String FILE_PATTERN = "(([^/]+)/)*([^/]+)";
    public static final String FOLDER_PATTERN = ".*\\/$";
    public static final String ZIP_PATTERN = ".*\\.zip$";
    public static final String CRONTAB_PATTERN =
        "((((\\d+,)+\\d+|(\\d+(\\/|-|#)\\d+)|\\d+L?|\\*(\\/\\d+)?|L(-\\d+)?|\\?|[A-Z]{3}(-[A-Z]{3})?) ?){5,7})|(@(annually|yearly|monthly|weekly|daily|hourly))";

    private Keys() {}
}
