package aka.jmetaagentsgenerator.velocity;

import java.util.HashMap;
import java.util.Map;

import aka.jmetaagentsgenerator.xml.query.Query;

public class RootQuery {

    private boolean existQuestion;
    private boolean existResponse;
    private String packageString;
    private String objectName;
    private Query query;
    private Map<String, Parameter> objectQuestionParam = new HashMap<>();

    /**
     * @return the packageQuestion
     */
    public String getPackage() {
        return this.packageString;
    }

    /**
     * @param packageString the packageString to set
     */
    public void setPackage(final String packageString) {
        this.packageString = packageString;
    }

    /**
     * @return the objectName
     */
    public String getObjectName() {
        return this.objectName;
    }

    /**
     * @param objectName the objectName to set
     */
    public void setObjectName(final String objectName) {
        this.objectName = objectName;
    }

    /**
     * @return the query
     */
    public Query getQuery() {
        return this.query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(final Query query) {
        this.query = query;
    }

    /**
     * @return the existQuestion
     */
    public boolean isExistQuestion() {
        return this.existQuestion;
    }

    /**
     * @param existQuestion the existQuestion to set
     */
    public void setExistQuestion(final boolean existQuestion) {
        this.existQuestion = existQuestion;
    }

    /**
     * @return the existResponse
     */
    public boolean isExistResponse() {
        return this.existResponse;
    }

    /**
     * @param existResponse the existResponse to set
     */
    public void setExistResponse(final boolean existResponse) {
        this.existResponse = existResponse;
    }

    public Map<String, Parameter> getObjectQuestionParam() {
        return this.objectQuestionParam;
    }

    public void setObjectQuestionParam(final Map<String, Parameter> objectQuestionParam) {
        this.objectQuestionParam = objectQuestionParam;
    }

}
