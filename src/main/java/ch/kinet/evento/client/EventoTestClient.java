package ch.kinet.evento.client;

import ch.kinet.JsonArray;
import ch.kinet.evento.EventoApi;
import ch.kinet.evento.EventoConfig;
import ch.kinet.evento.ViewDefinition;

public class EventoTestClient {

    public static void main(String[] args) {
        EventoConfig eventoConfig = EventoConfig.create();
        // urls
        eventoConfig.setServer("evento-test.erz.be.ch");
        eventoConfig.setApiBase("/restApi");
        eventoConfig.setOauthBase("/OAuth");

        // instance
        eventoConfig.setInstance("TODO");
        eventoConfig.setClientId("TODO");
        eventoConfig.setSecret("TODO");

        // client certificate
        eventoConfig.setKeyStoreJKS(false);
        eventoConfig.setKeyStorePath("evento-oauth-protected.erz.be.ch_20230530.pfx");
        eventoConfig.setKeyStorePassword("TODO");
        eventoConfig.setKeyPassword("TODO");

        // user
        eventoConfig.setUserName("TODO");
        eventoConfig.setUserPassword("TODO");

        EventoApi api = EventoApi.create(eventoConfig);
        api.connectClient();

        // create view definition
        ViewDefinition viewDef = EventoFetchParent.createKinetParentsViewDef();

        // fetch data
        JsonArray result = api.loadView(viewDef);
        if (api.hasError()) {
            System.err.println(api.getErrorMessage());
        }
        else {
            System.out.println(result.toString());
        }
    }
}
