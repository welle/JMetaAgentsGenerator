package aka.jmetaagentsgenerator.constants;

/**
 * @author charlottew
 */
public enum PARAMETER_TYPE {

    /**
     * The parameter must be in the URL path of the query.
     * ie: http://api.server.com/<b>parameter</b>/
     */
    PATH,

    /**
     * The parameter must be in the URL of the query.
     * ie: http://api.server.com/<b>parameter=value</b>
     */
    QUERY,

    /**
     * The parameter must in the Header request.
     */
    HEADER,

    /**
     * The parameter must be sent by JSON.
     */
    JSON
}
