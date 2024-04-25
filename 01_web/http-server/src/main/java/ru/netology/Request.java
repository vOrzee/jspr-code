package ru.netology;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private final String method;

    private String path;

    private List<NameValuePair> queryParams;

    public Request(String method, String path) {
        this.method = method;
        parseFullPath(path);
    }

    private void parseFullPath(String fullPath) {
        try {
            URI uri = new URI(fullPath);
            URIBuilder uriBuilder = new URIBuilder(uri);
            this.path = uriBuilder.getPath();
            this.queryParams = uriBuilder.getQueryParams();
        } catch (Exception e) {
            e.printStackTrace();
            this.path = fullPath;
            this.queryParams = List.of();
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public List<String> getQueryParams(String name) {
        return queryParams.stream().filter(pair -> pair.getName().equals(name)).map(NameValuePair::getValue).collect(Collectors.toList());
    }

}
