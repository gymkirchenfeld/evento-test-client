/*
 * Copyright (C) 2021 by Stefan Rothe, Sebastian Forster, Tom Jampen
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
package ch.kinet.evento.client;

import ch.kinet.evento.EventoApi;
import ch.kinet.evento.Filter;
import ch.kinet.evento.ViewDefinition;

final class EventoFetchParent {

    private static final String VIEW_NAME = "KINET-Parents";
    private static final String FIELD_PERSON_ID = "Id1";
    private static final String FIELD_GESCHLECHT = "Geschlecht";
    private static final String FIELD_TITEL = "Titel";
    private static final String FIELD_VORNAME = "Vorname";
    private static final String FIELD_NACHNAME = "Nachname";
    private static final String FIELD_ADRESSZEILE1 = "Adresszeile1";
    private static final String FIELD_ADRESSZEILE2 = "Adresszeile2";
    private static final String FIELD_PLZ = "Plz";
    private static final String FIELD_ORT = "Ort";
    private static final String FIELD_E_MAIL = "EMail";
    private static final String FIELD_E_MAIL2 = "EMail2";
    private static final String FIELD_TEL_G = "TelG";
    private static final String FIELD_TEL_P = "TelP";
    private static final String FIELD_MOBILE = "Mobile";
    private static final String FIELD_BERUF = "Zusatzfeld03";
    private static final String FIELD_IST_GV = "PersonGruppierung_Gesetzliche Vertretung_IstChef";
    private static final String FIELD_IST_MITARBEITER_AKTIV = "IstMitarbeiterAktiv";
    private static final String FIELDS[] = {
        FIELD_PERSON_ID, FIELD_GESCHLECHT, FIELD_TITEL, FIELD_VORNAME, FIELD_NACHNAME, FIELD_ADRESSZEILE1,
        FIELD_ADRESSZEILE2, FIELD_PLZ, FIELD_ORT, FIELD_TEL_P, FIELD_E_MAIL, FIELD_E_MAIL2, FIELD_MOBILE, FIELD_TEL_G,
        FIELD_BERUF
    };

    public static ViewDefinition createKinetParentsViewDef() {
        ViewDefinition result = ViewDefinition.create(EventoApi.PERSON_CONTEXT, VIEW_NAME);
        for (String field : FIELDS) {
            result.addField(field);
        }

        result.setFilter(FIELD_IST_GV, Filter.equal("true"));
        result.setFilter(FIELD_IST_MITARBEITER_AKTIV, Filter.equal("false"));
        return result;
    }
}
