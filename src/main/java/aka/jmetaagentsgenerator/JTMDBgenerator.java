package aka.jmetaagentsgenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DisplayTool;
import org.eclipse.jdt.annotation.NonNull;

import aka.jmetaagentsgenerator.JMetaAgentsGenerator.APIInformation;
import aka.jmetaagentsgenerator.JMetaAgentsGenerator.BuildInformation;
import aka.jmetaagentsgenerator.velocity.QueryList;
import aka.jmetaagentsgenerator.velocity.Root;
import aka.jmetaagentsgenerator.velocity.RootQuery;
import aka.jmetaagentsgenerator.xml.query.QueryReaderHelper;

/**
 * @author charlottew
 */
public class JTMDBgenerator extends AbstractGenerator {

    private @NonNull final String destinationPath;
    private @NonNull final String basePackage;
    private @NonNull final APIInformation apInformation;
    private @NonNull final List<BuildInformation> buildInformationsList;
    private final QueryList queryList;
    private RootQuery loginQuery;
    private @NonNull final String destinationTestPath;

    /**
     * Constructor.
     *
     * @param destinationPath
     * @param destinationTestPath
     * @param basePackage
     * @param apInformation
     * @param buildInformationsList
     */
    public JTMDBgenerator(@NonNull final String destinationPath, @NonNull final String destinationTestPath, @NonNull final String basePackage, @NonNull final APIInformation apInformation, @NonNull final List<BuildInformation> buildInformationsList) {
        super(basePackage);
        this.destinationPath = destinationPath;
        this.destinationTestPath = destinationTestPath;
        this.basePackage = basePackage;
        this.apInformation = apInformation;
        this.buildInformationsList = buildInformationsList;
        this.queryList = new QueryList();
        this.loginQuery = new RootQuery();

        final List<RootQuery> queries = new ArrayList<>();
        for (final BuildInformation buildInformation : this.buildInformationsList) {
            if (buildInformation.queryXML != null) {
                RootQuery rootQuery = new RootQuery();
                rootQuery.setQuery(QueryReaderHelper.getQuery(buildInformation.queryXML));
                rootQuery.setPackage(buildInformation.packageName);
                rootQuery.setObjectName(buildInformation.baseJavaClassName);
                rootQuery.setExistQuestion(buildInformation.questionJSON != null);
                rootQuery.setExistResponse(buildInformation.responseJSON != null);
                rootQuery = addJSONQuestionParam(rootQuery, buildInformation);
                if (buildInformation.baseJavaClassName.toLowerCase().contains("guest")) {
                    this.loginQuery = rootQuery;
                } else {
                    queries.add(rootQuery);
                }
            }
        }
        this.queryList.setQueries(queries);
    }

    /**
     * Build classes.
     *
     * @param apiName
     */
    public void buildMainTMDB(@NonNull final String apiName) {
        try {
            final File file = new File(this.destinationPath + "/" + apiName);
            FileUtils.forceMkdir(file);
            final VelocityContext context = new VelocityContext();
            final Root root = new Root();
            root.setName("JTMDB");
            root.setSerialUID("1L");
            context.put("comp", root);
            if (this.loginQuery != null) {
                context.put("loginQuery", this.loginQuery);
            }
            context.put("queryList", this.queryList);
            context.put("package", this.basePackage);
            context.put("subpackage", this.basePackage + ".tmdb");
            context.put("display", new DisplayTool());
            callVelocity(this.destinationPath + "/" + apiName + "/JTMDB.java", "tpl/tmdb/tmdb.vm", context);
        } catch (final IOException e) {
            LOGGER.logp(Level.SEVERE, "JMetaAgentsGenerator", "buildMainTMDB", e.getMessage(), e);
        }
    }

    /**
     * Build test classes.
     *
     * @param apiName
     */
    public void buildUnitTestTMDB(@NonNull final String apiName) {
        try {
            final File file = new File(this.destinationTestPath + "/" + apiName);
            FileUtils.forceMkdir(file);
            final VelocityContext context = new VelocityContext();
            final Root root = new Root();
            root.setName("JTMDB");
            root.setSerialUID("1L");
            context.put("comp", root);
            if (this.loginQuery != null) {
                context.put("loginQuery", this.loginQuery);
            }
            context.put("queryList", this.queryList);
            context.put("package", this.basePackage);
            context.put("subpackage", this.basePackage + ".tmdb");
            context.put("display", new DisplayTool());
            callVelocity(this.destinationTestPath + "/" + apiName + "/JTMDB_Test.java", "tpl/tmdb/tmdbunittest.vm", context);
        } catch (final IOException e) {
            LOGGER.logp(Level.SEVERE, "JMetaAgentsGenerator", "buildUnitTestTMDB", e.getMessage(), e);
        }
    }
}
