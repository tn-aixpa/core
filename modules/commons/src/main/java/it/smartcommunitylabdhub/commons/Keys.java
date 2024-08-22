package it.smartcommunitylabdhub.commons;

public class Keys {

    public static final String SLUG_PATTERN = "^[a-zA-Z0-9._+-]+$";
    public static final String PATH_PATTERN = "([^:/]+)://([^/]+)/([^:]+):(.+)";
    public static final String STORE_PREFIX = "store://";
    public static final String PATH_DIVIDER = "/";
    public static final String ID_DIVIDER = ":";
    public static final String FILE_PATTERN = "(([^/]+)/)*([^/]+)";
    
    private Keys() {}
}
