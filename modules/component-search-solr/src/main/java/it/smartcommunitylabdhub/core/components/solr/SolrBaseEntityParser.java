package it.smartcommunitylabdhub.core.components.solr;

public class SolrBaseEntityParser {

    public static String buildKeyGroup(String kind, String project, String name) {
        return kind + "_" + project + "_" + name;
    }
}
