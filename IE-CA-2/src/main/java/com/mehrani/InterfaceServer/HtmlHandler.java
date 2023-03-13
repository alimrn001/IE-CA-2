package com.mehrani.InterfaceServer;

import java.util.HashMap;
import java.util.Map;

public class HtmlHandler {
    private String dataDelimiter = "%";

    public String fillTemplatePage(String htmlFileContentsStr, HashMap<String, String> context) throws Exception {
        for(Map.Entry<String, String> entry : context.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            htmlFileContentsStr = htmlFileContentsStr.replaceAll(this.dataDelimiter + key + this.dataDelimiter, value);
        }
        return htmlFileContentsStr;
    }
    public void setDelimiter(String delimiter) {
        this.dataDelimiter = delimiter;
    }
}
