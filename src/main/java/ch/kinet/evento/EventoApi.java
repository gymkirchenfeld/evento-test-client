/*
 * Copyright (C) 2017 - 2023 by Stefan Rothe, Sebastian Forster, Tom Jampen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY); without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.kinet.evento;

import ch.kinet.HttpClient;
import ch.kinet.HttpConnection;
import ch.kinet.JsonArray;
import ch.kinet.JsonObject;
import ch.kinet.Util;
import org.json.JSONException;

/**
 * Manages user authentication with an OAuth server.
 */
public final class EventoApi {

    public static final String ASSIGNMENT_CONTEXT = "PersonenAnmeldung";
    public static final String COURSE_CONTEXT = "Anlass";
    public static final String EMPLOYEES_CONTEXT = "Anstellung";
    public static final String PERSON_CONTEXT = "Person";
    public static final String TEACHER_ASSIGNMENT_CONTEXT = "AnlassLeitung";
    private static final String CULTURE_INFO = "de-CH";
    private static final String PROTOCOL_HTTPS = "https://";
    private final EventoConfig config;
    private final HttpClient httpClient;
    private String accessToken;
    private String clientToken;
    private String errorMessage;
    private String tokenType;

    public static EventoApi create(EventoConfig config) {
        return new EventoApi(config);

    }

    private EventoApi(EventoConfig config) {
        this.config = config;
        if (config.isKeyStoreJKS()) {
            httpClient = HttpClient.createJKS(config.getKeyStorePath(), config.getKeyStorePassword(), config.getKeyPassword(),
                                              config.getCaCertStorePath(), config.getCaCertStorePassword());
        }
        else {
            httpClient = HttpClient.createPKIX(config.getKeyStorePath(), config.getKeyStorePassword(), config.getKeyPassword(),
                                               config.getCaCertStorePath(), config.getCaCertStorePassword());
        }
    }

    public void connectClient() {
        HttpConnection http;
        http = httpClient.post(
            PROTOCOL_HTTPS + config.getServer() + config.getOauthBase() + "/Authorization/" + config.getInstance() + "/Connect/" +
            config.getClientId());
        http.writeBody(config.getSecret());

        String response = http.readResponse();
        if (http.getResponseCode() >= 400) {
            error("Der Evento-Server antwortet mit Fehler: " + http.getResponseCode() + ": " + http.getResponseMessage());
            return;
        }

        try {
            JsonObject result = JsonObject.create(response);
            clientToken = result.getString("access_token");
        }
        catch (JSONException ex) {
            error("Der Evento-Server sendet ungültiges JSON: '" + response + "'.");
        }
    }

    public EventoSearchDefinition loadSearchDefinition(String context, String name) {
        HttpConnection http = httpClient.get(
            PROTOCOL_HTTPS + config.getServer() + config.getApiBase() + "/search/definitions/" + context + "/" + name);
        http.setHeader("CLX-Authorization", authorization());
        return new EventoSearchDefinition(JsonObject.create(http.readResponse()));
    }

    public JsonArray loadView(ViewDefinition viewDef) {
        EventoSearchDefinition searchDefinition = loadSearchDefinition(viewDef.getContext(), viewDef.getName());
        for (String fieldName : viewDef.getFieldNames()) {
            Filter filter = viewDef.getFilter(fieldName);
            if (filter != null) {
                searchDefinition.applyFilter(fieldName, filter);
            }
        }

        return loadView(searchDefinition);
    }

    public JsonArray loadView(EventoSearchDefinition searchDefinition) {
        return search(searchDefinition.toJsonTerse().toString());
    }

    public void loginUser() {
        HttpConnection http = httpClient.post(
            PROTOCOL_HTTPS + config.getServer() + config.getOauthBase() + "/Authorization/LoginClient?client_token=" +
            clientToken + "&username=" + config.getUserName() + "&culture_info=" + CULTURE_INFO +
            "&application_scope=");

        http.writeBody(config.getUserPassword());
        String response = http.readResponse();

        if (http.getResponseCode() >= 400) {
            error("Der Evento-Server antwortet mit Fehler: " + http.getResponseCode() + ": " + http.getResponseMessage());
            return;
        }

        try {
            JsonObject result = JsonObject.create(response);
            accessToken = result.getString("access_token");
            tokenType = result.getString("token_type");
        }
        catch (JSONException ex) {
            error("Der Evento-Server sendet ungültiges JSON: '" + response + "'.");
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getPerson(int id) {
        HttpConnection http = httpClient.get(
            PROTOCOL_HTTPS + config.getServer() + config.getApiBase() + "/persons/" + id);
        http.setHeader("CLX-Authorization", authorization());
        return http.readResponse();
    }

    public boolean hasError() {
        return !Util.isEmpty(errorMessage);
    }

    private JsonArray search(String searchDefinition) {
        HttpConnection http = httpClient.post(
            PROTOCOL_HTTPS + config.getServer() + config.getApiBase() + "/Search/");
        http.setHeader("CLX-Authorization", authorization());
        http.setHeader("Content-Type", "application/json");
        http.writeBody(searchDefinition);
        return JsonObject.create(http.readResponse()).getArray("Result");
    }

    private String authorization() {
        return "token_type=" + tokenType + ", access_token=" + accessToken;
    }

    private void error(String message) {
        errorMessage = message;
    }
}
