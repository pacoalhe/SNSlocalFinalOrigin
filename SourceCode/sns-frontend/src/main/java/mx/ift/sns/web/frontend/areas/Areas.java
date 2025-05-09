package mx.ift.sns.web.frontend.areas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que define las áreas del mapa general de estados.
 * @author X51461MO
 */
public class Areas implements Serializable {
    /**
     * Constructor por defecto.
     */
    public Areas() {
    }

    /** Serialización. */
    private static final long serialVersionUID = 1L;
    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Areas.class);
    /** id estado. **/
    private String idEstado;
    /** coordenadas estado. */
    private String coordenadas;
    /** nombre estado. */
    private String nombreEstado;
    /** link vinculado al area. */
    private Integer idLink;
    /** tipo de area. */
    private String shape;

    // /////////////////////////////////////MÉTODOS///////////////////////////////////
    /**
     * Genera una lista de áreas correspondiente a los estados del 1 al 10.
     * @return List<Areas>
     */
    private List<Areas> generaListaEstadosPrimerGrupo() {
        List<Areas> auxList = new ArrayList<Areas>();

        Areas aguasCalientes = new Areas();
        aguasCalientes.setIdEstado("1");
        aguasCalientes.setShape("poly");
        aguasCalientes.setNombreEstado("Aguas Calientes");
        aguasCalientes.setCoordenadas("285,218,281,213,283,207,293,199,301,209,301,211,"
                + "300,213,298,216,293,217,287,216,286,216,285,218");
        aguasCalientes.setIdLink(0);
        auxList.add(aguasCalientes);

        Areas bajaCali = new Areas();
        bajaCali.setShape("poly");
        bajaCali.setCoordenadas("0,3,49,0,48,3,41,5,39,13,47,18,43,29,47,"
                + "36,49,51,76,76,86,85,86,94,60,92,60,81,50,67,30,57,20,30,0,3");
        bajaCali.setIdEstado("2");
        bajaCali.setIdLink(1);
        bajaCali.setNombreEstado("Baja California Norte");
        auxList.add(bajaCali);

        Areas bajaCaliforniaSur = new Areas();
        bajaCaliforniaSur.setShape("poly");
        bajaCaliforniaSur.setIdEstado("3");
        bajaCaliforniaSur.setNombreEstado("Baja California Sur");
        bajaCaliforniaSur.setCoordenadas("58,93,86,93,105,119,107,115,115,134,128,151,127,"
                + "164,134,167,134,163,138,163,142,169,147,169,147,173,155,183,151,"
                + "189,140,194,138,190,136,182,134,177,128,177,119,167,108,160,98,"
                + "153,100,131,80,118,80,114,69,116,66,111,55,108,55,105,42,97,55,93,58,99,62,99,60,93,58,93");
        bajaCaliforniaSur.setIdLink(2);
        auxList.add(bajaCaliforniaSur);

        Areas campeche = new Areas();
        campeche.setShape("poly");
        campeche.setIdEstado("4");
        campeche.setNombreEstado("Campeche");
        campeche.setCoordenadas("489,278,499,278,520,266,521,256,525,250,527,236,"
                + "533,239,536,242,540,241,539,247,541,248,542,251,543,254,546,"
                + "258,551,261,551,267,550,277,551,289,552,294,516,292,504,287,"
                + "501,292,496,287,495,282,489,278");
        campeche.setIdLink(3);
        auxList.add(campeche);

        Areas coahuila = new Areas();
        coahuila.setShape("poly");
        coahuila.setIdEstado("5");
        coahuila.setNombreEstado("Coahuila");
        coahuila.setCoordenadas("264,116,260,92,270,75,282,75,285,63,292,57,309,57,"
                + "321,67,341,96,340,98,332,96,329,99,329,102,319,107,319,111,324,"
                + "112,324,118,322,118,314,125,319,133,321,138,328,145,332,145,332,"
                + "147,322,147,322,162,312,154,307,156,292,147,288,150,283,150,282,"
                + "154,277,154,270,147,274,123,264,116");
        coahuila.setIdLink(4);
        auxList.add(coahuila);

        Areas colima = new Areas();
        colima.setShape("poly");
        colima.setIdEstado("6");
        colima.setNombreEstado("Colima");
        colima.setCoordenadas("251,265,258,265,260,261,267,263,270,267,272,"
                + "271,270,271,266,271,261,275,250,271,251,265");
        colima.setIdLink(5);
        auxList.add(colima);

        Areas chiapas = new Areas();
        chiapas.setShape("poly");
        chiapas.setIdEstado("7");
        chiapas.setNombreEstado("Chiapas");
        chiapas.setCoordenadas("458,329,458,322,453,316,458,314,458,310,462,305,467,305,"
                + "468,298,472,292,474,297,481,305,499,295,501,302,507,305,509,309,513,"
                + "311,515,313,520,315,521,319,525,321,528,323,526,327,500,327,489,343,"
                + "494,348,492,358,469,335,458,329");
        chiapas.setIdLink(6);
        auxList.add(chiapas);

        Areas chihuahua = new Areas();
        chihuahua.setShape("poly");
        chihuahua.setIdEstado("8");
        chihuahua.setNombreEstado("Chihuahua");
        chihuahua.setCoordenadas("178,19,186,19,212,19,241,41,245,50,246,56,270,74,258,"
                + "92,262,113,260,117,254,114,251,117,248,123,244,121,239,121,233,121,"
                + "218,113,204,139,199,136,196,134,194,130,191,129,183,126,181,121,181,"
                + "115,169,110,169,102,159,89,161,88,169,86,169,78,165,73,169,43,163,"
                + "38,166,36,169,28,178,19");
        chihuahua.setIdLink(7);
        auxList.add(chihuahua);

        Areas distritoFederal = new Areas();
        distritoFederal.setShape("poly");
        distritoFederal.setIdEstado("9");
        distritoFederal.setNombreEstado("DistritoFederal");
        distritoFederal.setCoordenadas("355,269,353,268,352,267,351,265,350,263,351,261,352,"
                + "260,353,258,355,258,357,258,359,260,360,261,361,263,360,265,359,267,357,268,355,269");
        distritoFederal.setIdLink(8);
        auxList.add(distritoFederal);

        Areas durango = new Areas();
        durango.setShape("poly");
        durango.setIdEstado("10");
        durango.setNombreEstado("durango");
        durango.setCoordenadas("209,166,208,162,205,156,198,150,198,139,210,139,219,"
                + "113,229,123,243,123,249,125,254,117,260,118,260,116,270,123,268,148,"
                + "275,155,284,155,287,162,285,162,271,162,259,170,260,179,258,181,252,"
                + "191,255,195,251,203,249,203,243,198,239,198,239,194,233,189,229,189,220,"
                + "178,221,168,216,163,209,166");
        durango.setIdLink(9);
        auxList.add(durango);

        return auxList;

    }

    /**
     * Genera una lista de áreas correspondiente a los estados del 11 al 21.
     * @return List<Areas>
     */
    private List<Areas> generaListaEstadosSegundoGrupo() {
        List<Areas> auxList = new ArrayList<Areas>();

        Areas guanajuato = new Areas();
        guanajuato.setShape("poly");
        guanajuato.setIdEstado("11");
        guanajuato.setNombreEstado("Guanajuato");
        guanajuato.setCoordenadas("298,244,298,237,295,233,306,221,306,214,315,216,"
                + "320,219,320,223,328,224,329,220,334,220,340,223,336,227,334,227,"
                + "326,232,325,237,328,244,328,251,321,251,320,244,313,251,306,244,303,244,298,244");
        guanajuato.setIdLink(10);
        auxList.add(guanajuato);

        Areas guerrero = new Areas();
        guerrero.setShape("poly");
        guerrero.setIdEstado("12");
        guerrero.setNombreEstado("Guerrero");
        guerrero.setCoordenadas("295,290,303,285,300,279,322,282,323,274,326,275"
                + ",330,284,345,277,353,283,355,287,365,295,369,293,368,301,371,"
                + "310,375,309,375,313,371,317,367,317,369,320,365,321,341,315,338,309,327,308,305,294,295,290");
        guerrero.setIdLink(11);
        auxList.add(guerrero);

        Areas hidalgo = new Areas();
        hidalgo.setShape("poly");
        hidalgo.setIdEstado("13");
        hidalgo.setNombreEstado("Hidalgo");
        hidalgo.setCoordenadas("340,243,351,231,351,227,356,227,359,227,362,227,"
                + "366,227,373,228,372,233,371,235,369,233,367,238,364,242,367,243,"
                + "370,244,375,240,375,240,372,245,373,249,371,253,371,256,368,258,"
                + "364,257,364,253,364,252,358,252,356,248,352,252,349,248,344,248,340,243");
        hidalgo.setIdLink(12);
        auxList.add(hidalgo);

        Areas jalisco = new Areas();
        jalisco.setShape("poly");
        jalisco.setIdEstado("14");
        jalisco.setNombreEstado("Jalisco");
        jalisco.setCoordenadas("247,268,227,246,232,239,235,239,237,236,243,232,"
                + "254,242,257,228,262,224,257,215,257,211,251,203,262,199,268,209,"
                + "272,203,275,203,275,208,268,216,266,225,275,228,285,222,285,217,"
                + "292,218,296,218,299,216,301,212,305,213,306,217,304,220,295,234,"
                + "297,237,294,242,276,246,281,257,285,262,271,271,268,262,258,261,254,263,247,263,247,268");
        jalisco.setIdLink(13);
        auxList.add(jalisco);

        Areas edoMexico = new Areas();
        edoMexico.setShape("poly");
        edoMexico.setIdEstado("15");
        edoMexico.setNombreEstado("EDO Mexico");
        edoMexico.setCoordenadas("332,266,336,259,336,251,340,248,346,248,350,258,"
                + "356,248,357,255,361,258,362,254,365,263,365,264,361,269,361,261,"
                + "357,258,350,258,349,269,349,274,342,274,336,279,330,280,328,"
                + "271,328,271,332,266");
        edoMexico.setIdLink(14);
        auxList.add(edoMexico);

        Areas michoacan = new Areas();
        michoacan.setShape("poly");
        michoacan.setIdEstado("16");
        michoacan.setNombreEstado("Michoacan");
        michoacan.setCoordenadas("268,275,287,262,279,246,295,244,299,247,302,248,"
                + "306,246,309,247,309,251,313,251,318,250,320,254,329,253,334,247,"
                + "336,250,332,258,328,263,328,268,322,271,321,279,322,280,301,278,"
                + "297,280,300,284,291,286,291,290,272,282,268,275");
        michoacan.setIdLink(15);
        auxList.add(michoacan);

        Areas morelos = new Areas();
        morelos.setShape("poly");
        morelos.setIdEstado("17");
        morelos.setNombreEstado("Morelos");
        morelos.setCoordenadas("356,284,351,280,349,274,351,271,352,"
                + "269,356,269,358,271,364,274,361,280,356,284");
        morelos.setIdLink(16);
        auxList.add(morelos);

        Areas nayarit = new Areas();
        nayarit.setShape("poly");
        nayarit.setIdEstado("18");
        nayarit.setNombreEstado("Nayarit");
        nayarit.setCoordenadas("229,235,236,226,236,219,227,211,226,203,229,"
                + "203,229,191,233,191,238,195,238,203,243,203,249,203,250,211,"
                + "254,212,250,215,262,222,261,224,254,226,253,239,244,"
                + "230,231,234,229,235");
        nayarit.setIdLink(17);
        auxList.add(nayarit);

        Areas nuevoLeon = new Areas();
        nuevoLeon.setShape("poly");
        nuevoLeon.setIdEstado("19");
        nuevoLeon.setNombreEstado("NuevoLeon");
        nuevoLeon.setCoordenadas("334,147,328,143,322,136,322,133,316,125,323,"
                + "119,328,119,326,111,322,109,322,108,332,104,332,99,341,98,"
                + "341,101,344,112,341,113,355,134,365,134,365,144,355,150,355,"
                + "155,341,159,341,162,344,170,348,173,343,175,340,175,338,183"
                + ",329,187,326,162,324,162,324,148,333,148,334,147");
        nuevoLeon.setIdLink(18);
        auxList.add(nuevoLeon);

        Areas oaxaca = new Areas();
        oaxaca.setShape("poly");
        oaxaca.setIdEstado("20");
        oaxaca.setNombreEstado("Oaxaca");
        oaxaca.setCoordenadas("406,336,431,328,431,323,447,325,456,330,454,"
                + "324,450,317,451,315,456,313,453,312,453,310,458,306,437,306,"
                + "431,299,420,301,417,296,419,290,419,288,414,288,408,283,404,"
                + "280,404,284,400,287,388,291,381,291,375,291,370,296,375,306,"
                + "379,313,375,317,369,321,367,325,387,329,400,333,400,333,406,336");
        oaxaca.setIdLink(19);
        auxList.add(oaxaca);

        Areas puebla = new Areas();
        puebla.setShape("poly");
        puebla.setIdEstado("21");
        puebla.setNombreEstado("Puebla");
        puebla.setCoordenadas("363,290,356,283,360,279,366,280,363,273,363,"
                + "268,367,266,370,268,374,268,376,266,381,268,381,263,387,263,"
                + "381,259,379,256,374,257,374,251,374,246,374,241,379,238,381,"
                + "241,381,247,386,250,389,249,389,252,386,255,387,260,390,264,"
                + "394,265,389,268,387,274,391,279,394,280,396,281,399,279,399,"
                + "283,397,285,392,287,387,287,387,286,363,290");
        puebla.setIdLink(20);
        auxList.add(puebla);

        return auxList;
    }

    /**
     * Genera una lista de áreas correspondiente a los estados del 22 al 32.
     * @return List<Areas>
     */
    private List<Areas> generaListaEstadosTercerGrupo() {
        List<Areas> auxList = new ArrayList<Areas>();

        Areas queretaro = new Areas();
        queretaro.setShape("rect");
        queretaro.setIdEstado("22");
        queretaro.setNombreEstado("Queretaro");
        queretaro.setCoordenadas("330,230,345,245");
        queretaro.setIdLink(21);
        auxList.add(queretaro);

        Areas quintanaRoo = new Areas();
        quintanaRoo.setShape("poly");
        quintanaRoo.setIdEstado("23");
        quintanaRoo.setNombreEstado("Quintana Roo");
        quintanaRoo.setCoordenadas("552,290,552,277,552,265,552,260,563,247,583,"
                + "242,583,234,583,230,583,219,592,220,598,223,598,230,593,238,"
                + "583,245,586,255,579,260,583,260,577,288,575,286,575,280,567,"
                + "279,559,292,557,298,552,294,552,290");
        quintanaRoo.setIdLink(22);
        auxList.add(quintanaRoo);

        Areas sanLuisPotosi = new Areas();
        sanLuisPotosi.setShape("poly");
        sanLuisPotosi.setIdEstado("24");
        sanLuisPotosi.setNombreEstado("San Luis Potosi");
        sanLuisPotosi.setCoordenadas("294,185,310,180,320,162,326,162,328,191,333,187,"
                + "336,197,343,202,349,201,351,207,364,207,366,213,363,214,365,223,"
                + "361,227,356,223,356,218,348,222,342,218,340,220,334,216,326,216,"
                + "326,220,320,215,310,214,310,212,313,209,311,205,315,198,308,195,"
                + "300,199,293,193,294,191,294,185");
        sanLuisPotosi.setIdLink(23);
        auxList.add(sanLuisPotosi);

        Areas sinaloa = new Areas();
        sinaloa.setShape("poly");
        sinaloa.setIdEstado("25");
        sinaloa.setNombreEstado("Sinaloa");
        sinaloa.setCoordenadas("155,130,169,113,179,116,179,127,186,131,193,131,"
                + "197,139,195,145,195,150,204,159,208,167,214,166,218,170,219,179,"
                + "226,189,229,191,227,196,229,199,224,199,192,164,188,164,180,156,"
                + "181,149,169,142,155,130");
        sinaloa.setIdLink(24);
        auxList.add(sinaloa);

        Areas sonora = new Areas();
        sonora.setShape("poly");
        sonora.setIdEstado("26");
        sonora.setNombreEstado("Sonora");
        sonora.setCoordenadas("47,3,120,26,165,28,165,36,160,36,167,44,165,75,"
                + "169,79,169,85,158,86,159,92,165,100,165,110,168,113,155,125,155,"
                + "118,146,117,144,106,134,106,132,94,120,94,97,67,80,37,82,28,73,"
                + "26,73,20,62,18,60,22,56,22,50,16,46,16,41,13,42,6,47,3");
        sonora.setIdLink(25);
        auxList.add(sonora);

        Areas tabasco = new Areas();
        tabasco.setShape("poly");
        tabasco.setIdEstado("27");
        tabasco.setNombreEstado("Tabasco");
        tabasco.setCoordenadas("458,292,458,286,466,284,468,282,481,282,486,"
                + "278,489,281,492,281,492,286,495,290,501,291,504,288,507,290,"
                + "515,292,516,305,509,305,505,302,502,301,500,294,495,293,487,"
                + "298,479,302,474,297,474,290,470,290,470,292,468,296,462,300,458,296,458,292");
        tabasco.setIdLink(26);
        auxList.add(tabasco);

        Areas tamaulipas = new Areas();
        tamaulipas.setShape("poly");
        tamaulipas.setIdEstado("28");
        tamaulipas.setNombreEstado("Tamaulipas");
        tamaulipas.setCoordenadas("380,207,379,202,383,156,394,137,394,132,390,"
                + "133,390,135,384,131,376,131,365,127,355,123,349,112,348,98,"
                + "341,98,346,113,345,114,356,133,365,133,365,146,357,150,357,"
                + "156,344,160,345,169,351,173,348,176,341,177,339,183,336,187,"
                + "338,187,338,195,347,199,349,199,354,204,369,203,375,200,376,201,380,207");
        tamaulipas.setIdLink(27);
        auxList.add(tamaulipas);

        Areas tlaxcala = new Areas();
        tlaxcala.setShape("poly");
        tlaxcala.setIdEstado("29");
        tlaxcala.setNombreEstado("Tlaxcala");
        tlaxcala.setCoordenadas("374,268,371,268,368,266,366,265,365,263,366,"
                + "261,368,259,371,258,374,258,377,258,380,259,382,261,382,263,382,265,380,266,377,268,374,268");
        tlaxcala.setIdLink(28);
        auxList.add(tlaxcala);

        Areas veracruz = new Areas();
        veracruz.setShape("poly");
        veracruz.setIdEstado("30");
        veracruz.setNombreEstado("Veracruz");
        veracruz.setCoordenadas("381,207,389,219,386,228,390,234,396,243,"
                + "407,251,409,254,410,261,410,266,412,267,417,273,427,277,"
                + "436,278,439,281,442,282,444,288,452,286,452,291,455,293,"
                + "462,299,462,304,458,304,458,306,439,305,431,296,421,299,"
                + "419,295,421,292,421,288,413,286,410,280,400,277,400,279,"
                + "397,280,388,273,392,269,400,265,392,263,389,257,394,249,"
                + "393,244,384,246,384,244,388,239,381,234,375,236,375,239,"
                + "370,239,372,239,375,227,371,223,366,220,369,210,371,203,381,207");
        veracruz.setIdLink(29);
        auxList.add(veracruz);

        Areas yucatan = new Areas();
        yucatan.setShape("poly");
        yucatan.setIdEstado("31");
        yucatan.setNombreEstado("Yucatan");
        yucatan.setCoordenadas("528,232,543,224,548,224,560,222,564,221,573,219,583,221,"
                + "583,226,581,228,582,232,583,237,577,242,569,244,561,246,552,260,542,"
                + "251,543,247,541,246,541,236,536,240,530,236,529,232,528,232");
        yucatan.setIdLink(30);
        auxList.add(yucatan);

        Areas zacatecas = new Areas();
        zacatecas.setShape("poly");
        zacatecas.setIdEstado("32");
        zacatecas.setNombreEstado("Zacatecas");
        zacatecas.setCoordenadas("284,221,278,224,276,224,276,228,272,227,268,226,268,221,269,218,"
                + "272,214,275,212,278,209,278,203,275,203,270,203,270,203,268,207,268,203,268,198,"
                + "258,196,257,187,264,181,264,170,276,162,283,162,290,162,291,158,287,152,295,152,"
                + "307,158,312,158,320,162,305,180,291,184,291,195,301,202,310,197,311,199,309,207,"
                + "310,209,307,212,291,201,282,207,279,214,284,221");
        zacatecas.setIdLink(31);
        auxList.add(zacatecas);

        return auxList;
    }

    /**
     * Genera la lista para mostrar con las áreas de todos los estados.
     * @return List<Areas>
     */
    public List<Areas> getListaAreasEstados() {
        List<Areas> auxList = new ArrayList<Areas>();
        try {
            auxList.addAll(generaListaEstadosPrimerGrupo());
            auxList.addAll(generaListaEstadosSegundoGrupo());
            auxList.addAll(generaListaEstadosTercerGrupo());
        } catch (Exception e) {
            LOGGER.error("Error al crear los objetos Areas de los estaods del mapa principal");
        }
        return auxList;

    }

    // /////////////////////////////GETTERS Y SETTERS///////////////////////////////
    /**
     * id del link.
     * @return the idLink
     */
    public Integer getIdLink() {
        return idLink;
    }

    /**
     * id del link.
     * @param idLink the idLink to set
     */
    public void setIdLink(Integer idLink) {
        this.idLink = idLink;
    }

    /**
     * tipo de area.
     * @return the shape
     */
    public String getShape() {
        return shape;
    }

    /**
     * tipo de area.
     * @param shape the shape to set
     */
    public void setShape(String shape) {
        this.shape = shape;
    }

    /**
     * id del estado.
     * @return the idEstado
     */
    public String getIdEstado() {
        return idEstado;
    }

    /**
     * id del estado.
     * @param idEstado the idEstado to set
     */
    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    /**
     * coordenadas del area.
     * @return the coordenadas
     */
    public String getCoordenadas() {
        return coordenadas;
    }

    /**
     * coordenadas del area.
     * @param coordenadas the coordenadas to set
     */
    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    /**
     * nombre del estado.
     * @return the nombreEstado
     */
    public String getNombreEstado() {
        return nombreEstado;
    }

    /**
     * nombre del estado.
     * @param nombreEstado the nombreEstado to set
     */
    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

}
