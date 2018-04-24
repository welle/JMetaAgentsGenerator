package aka.jmetaagentsgenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.jdt.annotation.NonNull;

import aka.convertor.json.JsonConvertor;
import aka.convertor.json.data.FieldMetaData;
import aka.convertor.json.data.ObjectMetaData;
import aka.jmetaagentsgenerator.JMetaAgentsGenerator.BuildInformation;
import aka.jmetaagentsgenerator.velocity.Parameter;
import aka.jmetaagentsgenerator.velocity.RootQuery;

/**
 * @author charlottew
 */
public abstract class AbstractGenerator {

    /**
     * Logger.
     */
    @NonNull
    public static final Logger LOGGER = Logger.getLogger(AbstractGenerator.class.getName());

    private @NonNull final VelocityEngine velocityEngine = new VelocityEngine();
    private @NonNull final String basePackage;

    /**
     * Constructor.
     *
     * @param basePackage root package.
     */
    public AbstractGenerator(@NonNull final String basePackage) {
        this.basePackage = basePackage;
        this.velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        this.velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        this.velocityEngine.init();
    }

    /**
     * Add JSONQuestion parameter to the root query.
     *
     * @param rootQuery
     * @param buildInformation
     * @return The root query
     */
    @NonNull
    public RootQuery addJSONQuestionParam(@NonNull final RootQuery rootQuery, @NonNull final BuildInformation buildInformation) {
        try {
            if (buildInformation.questionJSON != null) {
                final String jsonToParse = new String(Files.readAllBytes(buildInformation.questionJSON.toPath()));
                final JsonConvertor jsonConvertor = new JsonConvertor(this.basePackage, buildInformation.baseJavaClassName + "Question", jsonToParse, "", "");
                final ArrayList<ObjectMetaData> objects = jsonConvertor.jsonMetaData.getObjects();
                for (final ObjectMetaData object : objects) {
                    for (final FieldMetaData entry : object.getFields()) {
                        final Parameter parameter = new Parameter();
                        parameter.setParamName(entry.getParamName());
                        parameter.setParamType(entry.getJavaType());
                        rootQuery.getObjectQuestionParam().put(entry.getSerName(), parameter);
                    }
                }
            }
        } catch (final IOException e) {
            LOGGER.logp(Level.SEVERE, "JTVDBgenerator", "addJSONQuestionParam", e.getMessage(), e);
        }

        return rootQuery;
    }

    /**
     * Call the Velocity engine with the given parameters.
     *
     * @param fileNameFullPath full path to file.
     * @param templateName template name to use.
     * @param velocityContext context for the template.
     */
    public void callVelocity(final String fileNameFullPath, final String templateName, @NonNull final VelocityContext velocityContext) {
        OutputStream os = null;
        OutputStreamWriter osw = null;
        try {
            os = new FileOutputStream(fileNameFullPath);
            osw = new OutputStreamWriter(os);

            this.velocityEngine.mergeTemplate(templateName, "ISO-8859-1", velocityContext, osw);
            osw.flush();
            osw.close();
            os.flush();
            os.close();
        } catch (final IOException e) {
            LOGGER.logp(Level.SEVERE, "JMetaAgentsGenerator", "callVelocity", e.getMessage(), e);
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
                if (osw != null) {
                    osw.flush();
                    osw.close();
                }
            } catch (final IOException e) {
                LOGGER.logp(Level.SEVERE, "JMetaAgentsGenerator", "callVelocity", e.getMessage(), e);
            }
        }
    }

}
