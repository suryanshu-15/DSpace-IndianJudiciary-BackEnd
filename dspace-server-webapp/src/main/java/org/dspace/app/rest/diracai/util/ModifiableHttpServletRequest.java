package org.dspace.app.rest.diracai.util;

import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

public class ModifiableHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String[]> modifiedParameters = new HashMap<>();

    public ModifiableHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public void setParameter(String name, String value) {
        modifiedParameters.put(name, new String[] { value });
    }

    @Override
    public String getParameter(String name) {
        String[] params = modifiedParameters.get(name);
        return (params != null && params.length > 0) ? params[0] : super.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        return modifiedParameters.getOrDefault(name, super.getParameterValues(name));
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> allParams = new HashMap<>(super.getParameterMap());
        allParams.putAll(modifiedParameters);
        return allParams;
    }
}
