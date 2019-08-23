/**********************************************

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see
<http://www.gnu.org/licenses/>.
In addition to the rights granted to the GNU General Public License,
you opt to use this program as specified in the following:

MUSITECH LINKING EXCEPTION

Linking this library statically or dynamically with other modules is making
a combined work based on this library. Thus, the terms and conditions of the
GNU General Public License cover the whole combination.

As a special exception, the copyright holders of this library give you permission
to link this library with independent modules to produce an executable, regardless
of the license terms of these independent modules, and to copy and distribute the
resulting executable under terms of your choice, provided that you also meet,
for each linked independent module, the terms and conditions of the license of
that module. An independent module is a module which is not derived from or based
on this library.

For the MUSITECH library, this exceptional permission described in the paragraph
above is subject to the following three conditions:
- If you modify this library, you must extend the GNU General Public License and
       this exception including these conditions to your version of the MUSITECH library.
- If you distribute a combined work with this library, you have to mention the
       MUSITECH project and link to its web site www.musitech.org in a location
       easily accessible to the users of the combined work (typically in the "About"
       section of the "Help" menu) and in any advertising material for the combined
       software.
- If you distribute a combined work with the MUSITECH library, you allow the MUSITECH
               project to use mention your combined work for promoting the MUSITECH project.
       For the purpose of this licence, 'distribution' includes the provision of software
       services (e.g. over the World Wide Web).

**********************************************/
/*
 * Created on 2005-1-6
 */
package de.uos.fmt.musitech.data.structure.lyrics;

import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * contains methods to support I18N = I-nternationalisatio-n
 * 
 * @author Jens Wissmann
 */
public class I18N {

    static Hashtable iso639 = new Hashtable();
    static {
        iso639.put("ab", "Abkhazian");
        iso639.put("af", "Afrikaans");
        iso639.put("am", "Amharic");
        iso639.put("ar", "Arabic");
        iso639.put("as", "Assamese");
        iso639.put("ay", "Aymara");
        iso639.put("az", "Azerbaijani");
        iso639.put("ba", "Bashkir");
        iso639.put("be", "Byelorussian");
        iso639.put("bg", "Bulgarian");
        iso639.put("bh", "Bihari");
        iso639.put("bi", "Bislama");
        iso639.put("bn", "Bengali; Bangla");
        iso639.put("bo", "Tibetan");
        iso639.put("br", "Breton");
        iso639.put("ca", "Catalan");
        iso639.put("co", "Corsican");
        iso639.put("cs", "Czech");
        iso639.put("cy", "Welsh");
        iso639.put("da", "Danish");
        iso639.put("de", "German");
        iso639.put("dz", "Bhutani");
        iso639.put("el", "Greek");
        iso639.put("en", "English");
        iso639.put("eo", "Esperanto");
        iso639.put("es", "Spanish");
        iso639.put("et", "Estonian");
        iso639.put("eu", "Basque");
        iso639.put("fa", "Persian");
        iso639.put("fi", "Finnish");
        iso639.put("fj", "Fiji");
        iso639.put("fo", "Faroese");
        iso639.put("fr", "French");
        iso639.put("fy", "Frisian");
        iso639.put("ga", "Irish");
        iso639.put("gd", "Scots Gaelic");
        iso639.put("gl", "Galician");
        iso639.put("gn", "Guarani");
        iso639.put("gu", "Gujarati");
        iso639.put("ha", "Hausa");
        iso639.put("he", "Hebrew");
        iso639.put("hi", "Hindi");
        iso639.put("hr", "Croatian");
        iso639.put("hu", "Hungarian");
        iso639.put("hy", "Armenian");
        iso639.put("ia", "Interlingua");
        iso639.put("id", "Indonesian");
        iso639.put("ie", "Interlingue");
        iso639.put("ik", "Inupiak");
        iso639.put("is", "Icelandic");
        iso639.put("it", "Italian");
        iso639.put("iu", "Inuktitut");
        iso639.put("ja", "Japanese");
        iso639.put("jw", "Javanese");
        iso639.put("ka", "Georgian");
        iso639.put("kk", "Kazakh");
        iso639.put("kl", "Greenlandic");
        iso639.put("km", "Cambodian");
        iso639.put("kn", "Kannada");
        iso639.put("ko", "Korean");
        iso639.put("ks", "Kashmiri");
        iso639.put("ku", "Kurdish");
        iso639.put("ky", "Kirghiz");
        iso639.put("la", "Latin");
        iso639.put("ln", "Lingala");
        iso639.put("lo", "Laothian");
        iso639.put("lt", "Lithuanian");
        iso639.put("lv", "Latvian, Lettish");
        iso639.put("mg", "Malagasy");
        iso639.put("mi", "Maori");
        iso639.put("mk", "Macedonian");
        iso639.put("ml", "Malayalam");
        iso639.put("mn", "Mongolian");
        iso639.put("mo", "Moldavian");
        iso639.put("mr", "Marathi");
        iso639.put("ms", "Malay");
        iso639.put("mt", "Maltese");
        iso639.put("my", "Burmese");
        iso639.put("na", "Nauru");
        iso639.put("ne", "Nepali");
        iso639.put("nl", "Dutch");
        iso639.put("no", "Norwegian");
        iso639.put("oc", "Occitan");
        iso639.put("om", "(Afan) Oromo");
        iso639.put("or", "Oriya");
        iso639.put("pa", "Punjabi");
        iso639.put("pl", "Polish");
        iso639.put("ps", "Pashto, Pushto");
        iso639.put("pt", "Portuguese");
        iso639.put("qu", "Quechua");
        iso639.put("rm", "Rhaeto-Romance");
        iso639.put("rn", "Kirundi");
        iso639.put("ro", "Romanian");
        iso639.put("ru", "Russian");
        iso639.put("rw", "Kinyarwanda");
        iso639.put("sa", "Sanskrit");
        iso639.put("sd", "Sindhi");
        iso639.put("sg", "Sangho");
        iso639.put("sh", "Serbo-Croatian");
        iso639.put("si", "Sinhalese");
        iso639.put("sk", "Slovak");
        iso639.put("sl", "Slovenian");
        iso639.put("sm", "Samoan");
        iso639.put("sn", "Shona");
        iso639.put("so", "Somali");
        iso639.put("sq", "Albanian");
        iso639.put("sr", "Serbian");
        iso639.put("ss", "Siswati");
        iso639.put("st", "Sesotho");
        iso639.put("su", "Sundanese");
        iso639.put("sv", "Swedish");
        iso639.put("sw", "Swahili");
        iso639.put("ta", "Tamil");
        iso639.put("te", "Telugu");
        iso639.put("tg", "Tajik");
        iso639.put("th", "Thai");
        iso639.put("ti", "Tigrinya");
        iso639.put("tk", "Turkmen");
        iso639.put("tl", "Tagalog");
        iso639.put("tn", "Setswana");
        iso639.put("to", "Tonga");
        iso639.put("tr", "Turkish");
        iso639.put("ts", "Tsonga");
        iso639.put("tt", "Tatar");
        iso639.put("tw", "Twi");
        iso639.put("ug", "Uighur");
        iso639.put("uk", "Ukrainian");
        iso639.put("ur", "Urdu");
        iso639.put("uz", "Uzbek");
        iso639.put("vi", "Vietnamese");
        iso639.put("vo", "Volapuk");
        iso639.put("wo", "Wolof");
        iso639.put("xh", "Xhosa");
        iso639.put("yi", "Yiddish");
        iso639.put("yo", "Yoruba");
        iso639.put("za", "Zhuang");
        iso639.put("zh", "Chinese");
        iso639.put("zu", "Zulu");
    }
    static Hashtable iso3166 = new Hashtable();
    static {
        iso3166.put("AF", "AFGHANISTAN");
        iso3166.put("AL", "ALBANIA");
        iso3166.put("DZ", "ALGERIA");
        iso3166.put("AS", "AMERICAN SAMOA");
        iso3166.put("AD", "ANDORRA");
        iso3166.put("AO", "ANGOLA");
        iso3166.put("AI", "ANGUILLA");
        iso3166.put("AQ", "ANTARCTICA");
        iso3166.put("AG", "ANTIGUA AND BARBUDA");
        iso3166.put("AR", "ARGENTINA");
        iso3166.put("AM", "ARMENIA");
        iso3166.put("AW", "ARUBA");
        iso3166.put("AU", "AUSTRALIA");
        iso3166.put("AT", "AUSTRIA");
        iso3166.put("AZ", "AZERBAIJAN");
        iso3166.put("BS", "BAHAMAS");
        iso3166.put("BH", "BAHRAIN");
        iso3166.put("BD", "BANGLADESH");
        iso3166.put("BB", "BARBADOS");
        iso3166.put("BY", "BELARUS");
        iso3166.put("BE", "BELGIUM");
        iso3166.put("BZ", "BELIZE");
        iso3166.put("BJ", "BENIN");
        iso3166.put("BM", "BERMUDA");
        iso3166.put("BT", "BHUTAN");
        iso3166.put("BO", "BOLIVIA");
        iso3166.put("BA", "BOSNIA AND HERZEGOVINA");
        iso3166.put("BW", "BOTSWANA");
        iso3166.put("BV", "BOUVET ISLAND");
        iso3166.put("BR", "BRAZIL");
        iso3166.put("IO", "BRITISH INDIAN OCEAN TERRITORY");
        iso3166.put("BN", "BRUNEI DARUSSALAM");
        iso3166.put("BG", "BULGARIA");
        iso3166.put("BF", "BURKINA FASO");
        iso3166.put("BI", "BURUNDI");
        iso3166.put("KH", "CAMBODIA");
        iso3166.put("CM", "CAMEROON");
        iso3166.put("CA", "CANADA");
        iso3166.put("CV", "CAPE VERDE");
        iso3166.put("KY", "CAYMAN ISLANDS");
        iso3166.put("CF", "CENTRAL AFRICAN REPUBLIC");
        iso3166.put("TD", "CHAD");
        iso3166.put("CL", "CHILE");
        iso3166.put("CN", "CHINA");
        iso3166.put("CX", "CHRISTMAS ISLAND");
        iso3166.put("CC", "COCOS (KEELING) ISLANDS");
        iso3166.put("CO", "COLOMBIA");
        iso3166.put("KM", "COMOROS");
        iso3166.put("CG", "CONGO");
        iso3166.put("CK", "COOK ISLANDS");
        iso3166.put("CR", "COSTA RICA");
        iso3166.put("CI", "COTE D'IVOIRE");
        iso3166.put("HR", "CROATIA (local name: Hrvatska)");
        iso3166.put("CU", "CUBA");
        iso3166.put("CY", "CYPRUS");
        iso3166.put("CZ", "CZECH REPUBLIC");
        iso3166.put("DK", "DENMARK");
        iso3166.put("DJ", "DJIBOUTI");
        iso3166.put("DM", "DOMINICA");
        iso3166.put("DO", "DOMINICAN REPUBLIC");
        iso3166.put("TL**", "EAST TIMOR");
        iso3166.put("EC", "ECUADOR");
        iso3166.put("EG", "EGYPT");
        iso3166.put("SV", "EL SALVADOR");
        iso3166.put("GQ", "EQUATORIAL GUINEA");
        iso3166.put("ER", "ERITREA");
        iso3166.put("EE", "ESTONIA");
        iso3166.put("ET", "ETHIOPIA");
        iso3166.put("FK", "FALKLAND ISLANDS (MALVINAS)");
        iso3166.put("FO", "FAROE ISLANDS");
        iso3166.put("FJ", "FIJI");
        iso3166.put("FI", "FINLAND");
        iso3166.put("FR", "FRANCE");
        iso3166.put("FX", "FRANCE, METROPOLITAN");
        iso3166.put("GF", "FRENCH GUIANA");
        iso3166.put("PF", "FRENCH POLYNESIA");
        iso3166.put("TF", "FRENCH SOUTHERN TERRITORIES");
        iso3166.put("GA", "GABON");
        iso3166.put("GM", "GAMBIA");
        iso3166.put("GE", "GEORGIA");
        iso3166.put("DE", "GERMANY");
        iso3166.put("GH", "GHANA");
        iso3166.put("GI", "GIBRALTAR");
        iso3166.put("GR", "GREECE");
        iso3166.put("GL", "GREENLAND");
        iso3166.put("GD", "GRENADA");
        iso3166.put("GP", "GUADELOUPE");
        iso3166.put("GU", "GUAM");
        iso3166.put("GT", "GUATEMALA");
        iso3166.put("GN", "GUINEA");
        iso3166.put("GW", "GUINEA-BISSAU");
        iso3166.put("GY", "GUYANA");
        iso3166.put("HT", "HAITI");
        iso3166.put("HM", "HEARD ISLAND & MCDONALD ISLANDS");
        iso3166.put("HN", "HONDURAS");
        iso3166.put("HK", "HONG KONG");
        iso3166.put("HU", "HUNGARY");
        iso3166.put("IS", "ICELAND");
        iso3166.put("IN", "INDIA");
        iso3166.put("ID", "INDONESIA");
        iso3166.put("IR", "IRAN, ISLAMIC REPUBLIC OF");
        iso3166.put("IQ", "IRAQ");
        iso3166.put("IE", "IRELAND");
        iso3166.put("IL", "ISRAEL");
        iso3166.put("IT", "ITALY");
        iso3166.put("JM", "JAMAICA");
        iso3166.put("JP", "JAPAN");
        iso3166.put("JO", "JORDAN");
        iso3166.put("KZ", "KAZAKHSTAN");
        iso3166.put("KE", "KENYA");
        iso3166.put("KI", "KIRIBATI");
        iso3166.put("KP", "KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF");
        iso3166.put("KR", "KOREA, REPUBLIC OF");
        iso3166.put("KW", "KUWAIT");
        iso3166.put("KG", "KYRGYZSTAN");
        iso3166.put("LA", "LAO PEOPLE'S DEMOCRATIC REPUBLIC");
        iso3166.put("LV", "LATVIA");
        iso3166.put("LB", "LEBANON");
        iso3166.put("LS", "LESOTHO");
        iso3166.put("LR", "LIBERIA");
        iso3166.put("LY", "LIBYAN ARAB JAMAHIRIYA");
        iso3166.put("LI", "LIECHTENSTEIN");
        iso3166.put("LT", "LITHUANIA");
        iso3166.put("LU", "LUXEMBOURG");
        iso3166.put("MO", "MACAU");
        iso3166.put("MK", "MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF");
        iso3166.put("MG", "MADAGASCAR");
        iso3166.put("MW", "MALAWI");
        iso3166.put("MY", "MALAYSIA");
        iso3166.put("MV", "MALDIVES");
        iso3166.put("ML", "MALI");
        iso3166.put("MT", "MALTA");
        iso3166.put("MH", "MARSHALL ISLANDS");
        iso3166.put("MQ", "MARTINIQUE");
        iso3166.put("MR", "MAURITANIA");
        iso3166.put("MU", "MAURITIUS");
        iso3166.put("YT", "MAYOTTE");
        iso3166.put("MX", "MEXICO");
        iso3166.put("FM", "MICRONESIA, FEDERATED STATES OF");
        iso3166.put("MD", "MOLDOVA, REPUBLIC OF");
        iso3166.put("MC", "MONACO");
        iso3166.put("MN", "MONGOLIA");
        iso3166.put("MS", "MONTSERRAT");
        iso3166.put("MA", "MOROCCO");
        iso3166.put("MZ", "MOZAMBIQUE");
        iso3166.put("MM", "MYANMAR");
        iso3166.put("NA", "NAMIBIA");
        iso3166.put("NR", "NAURU");
        iso3166.put("NP", "NEPAL");
        iso3166.put("NL", "NETHERLANDS");
        iso3166.put("AN", "NETHERLANDS ANTILLES");
        iso3166.put("NC", "NEW CALEDONIA");
        iso3166.put("NZ", "NEW ZEALAND");
        iso3166.put("NI", "NICARAGUA");
        iso3166.put("NE", "NIGER");
        iso3166.put("NG", "NIGERIA");
        iso3166.put("NU", "NIUE");
        iso3166.put("NF", "NORFOLK ISLAND");
        iso3166.put("MP", "NORTHERN MARIANA ISLANDS");
        iso3166.put("NO", "NORWAY");
        iso3166.put("OM", "OMAN");
        iso3166.put("PK", "PAKISTAN");
        iso3166.put("PW", "PALAU");
        iso3166.put("PA", "PANAMA");
        iso3166.put("PG", "PAPUA NEW GUINEA");
        iso3166.put("PY", "PARAGUAY");
        iso3166.put("PE", "PERU");
        iso3166.put("PH", "PHILIPPINES");
        iso3166.put("PN", "PITCAIRN");
        iso3166.put("PL", "POLAND");
        iso3166.put("PT", "PORTUGAL");
        iso3166.put("PR", "PUERTO RICO");
        iso3166.put("QA", "QATAR");
        iso3166.put("RE", "REUNION");
        iso3166.put("RO", "ROMANIA");
        iso3166.put("RU", "RUSSIAN FEDERATION");
        iso3166.put("RW", "RWANDA");
        iso3166.put("KN", "SAINT KITTS AND NEVIS");
        iso3166.put("LC", "SAINT LUCIA");
        iso3166.put("VC", "SAINT VINCENT AND THE GRENADINES");
        iso3166.put("WS", "SAMOA");
        iso3166.put("SM", "SAN MARINO");
        iso3166.put("ST", "SAO TOME AND PRINCIPE");
        iso3166.put("SA", "SAUDI ARABIA");
        iso3166.put("SN", "SENEGAL");
        iso3166.put("SC", "SEYCHELLES");
        iso3166.put("SL", "SIERRA LEONE");
        iso3166.put("SG", "SINGAPORE");
        iso3166.put("SK", "SLOVAKIA (Slovak Republic)");
        iso3166.put("SI", "SLOVENIA");
        iso3166.put("SB", "SOLOMON ISLANDS");
        iso3166.put("SO", "SOMALIA");
        iso3166.put("ZA", "SOUTH AFRICA");
        iso3166.put("ES", "SPAIN");
        iso3166.put("LK", "SRI LANKA");
        iso3166.put("SH", "SAINT HELENA");
        iso3166.put("PM", "SAINT PIERRE AND MIQUELON");
        iso3166.put("SD", "SUDAN");
        iso3166.put("SR", "SURINAME");
        iso3166.put("SJ", "SVALBARD AND JAN MAYEN ISLANDS");
        iso3166.put("SZ", "SWAZILAND");
        iso3166.put("SE", "SWEDEN");
        iso3166.put("CH", "SWITZERLAND");
        iso3166.put("SY", "SYRIAN ARAB REPUBLIC");
        iso3166.put("TW", "TAIWAN, PROVINCE OF CHINA");
        iso3166.put("TJ", "TAJIKISTAN");
        iso3166.put("TZ", "TANZANIA, UNITED REPUBLIC OF");
        iso3166.put("TH", "THAILAND");
        iso3166.put("TG", "TOGO");
        iso3166.put("TK", "TOKELAU");
        iso3166.put("TO", "TONGA");
        iso3166.put("TT", "TRINIDAD AND TOBAGO");
        iso3166.put("TN", "TUNISIA");
        iso3166.put("TR", "TURKEY");
        iso3166.put("TM", "TURKMENISTAN");
        iso3166.put("TC", "TURKS AND CAICOS ISLANDS");
        iso3166.put("TV", "TUVALU");
        iso3166.put("UG", "UGANDA");
        iso3166.put("UA", "UKRAINE");
        iso3166.put("AE", "UNITED ARAB EMIRATES");
        iso3166.put("GB", "UNITED KINGDOM");
        iso3166.put("US", "UNITED STATES");
        iso3166.put("UM", "UNITED STATES MINOR OUTLYING ISLANDS");
        iso3166.put("UY", "URUGUAY");
        iso3166.put("UZ", "UZBEKISTAN");
        iso3166.put("VU", "VANUATU");
        iso3166.put("VA", "VATICAN CITY STATE (HOLY SEE)");
        iso3166.put("VE", "VENEZUELA");
        iso3166.put("VN", "VIET NAM");
        iso3166.put("VG", "VIRGIN ISLANDS (BRITISH)");
        iso3166.put("VI", "VIRGIN ISLANDS (U.S.)");
        iso3166.put("WF", "WALLIS AND FUTUNA ISLANDS");
        iso3166.put("EH", "WESTERN SAHARA");
        iso3166.put("YE", "YEMEN");
        iso3166.put("YU", "YUGOSLAVIA");
        iso3166.put("ZR", "ZAIRE");
        iso3166.put("ZM", "ZAMBIA");
        iso3166.put("ZW", "ZIMBABWE");
    }

    /**
     * parses an ISO-conform language String to a Locale
     * 
     * @param langCode
     *            language code
     * @return
     */
    public static Locale parseLocale(String langCode) {
        if (langCode == null)
            return null;
        String lang, country, variant;
        StringTokenizer st = new StringTokenizer(langCode,"_-");
        if (st.hasMoreTokens()) {
            lang = st.nextToken().toLowerCase();
        } else {
            return null;
        }
        if (!st.hasMoreTokens()) {
            return new Locale(lang);
        }else{
            country = st.nextToken().toUpperCase();
        } 
        if (!st.hasMoreTokens()) {
            return new Locale(lang,country);
        }else{
            variant = st.nextToken();
        }
        return new Locale(lang,country, variant);
//      if (langCode.length() == 2) {
//      return new Locale(langCode);
//  } else if (langCode.length() == 5) {
//      return new Locale(langCode.substring(0, 2), langCode.substring(3));
//  }
//        return null;
    }
    
}