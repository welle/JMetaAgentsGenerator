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

public class JOMDBgenerator extends AbstractGenerator {

    private @NonNull final String destinationPath;
    private @NonNull final String basePackage;
    private @NonNull final APIInformation apInformation;
    private @NonNull final List<BuildInformation> buildInformationsList;
    private final QueryList queryList;
    private @NonNull final String destinationTestPath;

    public JOMDBgenerator(@NonNull final String destinationPath, @NonNull final String destinationTestPath, @NonNull final String basePackage, @NonNull final APIInformation apInformation, @NonNull final List<BuildInformation> buildInformationsList) {
        super(basePackage);
        this.destinationPath = destinationPath;
        this.destinationTestPath = destinationTestPath;
        this.basePackage = basePackage;
        this.apInformation = apInformation;
        this.buildInformationsList = buildInformationsList;
        this.queryList = new QueryList();

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
                queries.add(rootQuery);
            }
        }
        this.queryList.setQueries(queries);
    }

    public void buildMainOMDB(@NonNull final String apiName) {
        try {
            final File file = new File(this.destinationPath + "/" + apiName);
            FileUtils.forceMkdir(file);
            final VelocityContext context = new VelocityContext();
            final Root root = new Root();
            root.setName("JOMDB");
            root.setSerialUID("1L");
            context.put("comp", root);
            context.put("queryList", this.queryList);
            context.put("package", this.basePackage);
            context.put("subpackage", this.basePackage + ".omdb");
            context.put("display", new DisplayTool());
            callVelocity(this.destinationPath + "/" + apiName + "/JOMDB.java", "tpl/omdb/omdb.vm", context);
        } catch (final IOException e) {
            LOGGER.logp(Level.SEVERE, "JMetaAgentsGenerator", "buildMainOMDB", e.getMessage(), e);
        }
    }

    public void buildUnitTestOMDB(@NonNull final String apiName) {
        try {
            final File file = new File(this.destinationTestPath + "/" + apiName);
            FileUtils.forceMkdir(file);
            final VelocityContext context = new VelocityContext();
            final Root root = new Root();
            root.setName("JOMDB");
            root.setSerialUID("1L");
            context.put("comp", root);
            context.put("queryList", this.queryList);
            context.put("package", this.basePackage);
            context.put("subpackage", this.basePackage + ".omdb");
            context.put("display", new DisplayTool());
            callVelocity(this.destinationTestPath + "/" + apiName + "/JOMDB_Test.java", "tpl/omdb/omdbunittest.vm", context);
        } catch (final IOException e) {
            LOGGER.logp(Level.SEVERE, "JMetaAgentsGenerator", "buildUnitTestOMDB", e.getMessage(), e);
        }
    }
}
