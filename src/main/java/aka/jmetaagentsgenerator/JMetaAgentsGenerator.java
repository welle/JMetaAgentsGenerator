package aka.jmetaagentsgenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.jdt.annotation.NonNull;

import aka.convertor.json.JsonConvertor;
import aka.convertor.json.constants.AnnotationType;
import aka.convertor.json.constants.Generator;
import aka.convertor.json.helpers.StringUtilities;
import aka.jmetaagentsgenerator.velocity.Root;

/**
 * @author charlottew
 */
public class JMetaAgentsGenerator extends AbstractGenerator {

    private @NonNull final String destinationPath;
    private @NonNull final String destinationTestPath;
    private @NonNull final String basePackage;
    private @NonNull final VelocityEngine velocityEngine = new VelocityEngine();

    /**
     * Constructor.
     *
     * @param basePackage base package (ie: aka.jmetaagents).
     * @param destinationPath where to write files.
     */
    public JMetaAgentsGenerator(@NonNull final String basePackage, @NonNull final String destinationPath, @NonNull final String destinationTestPath) {
        super(basePackage);
        this.destinationPath = destinationPath;
        this.destinationTestPath = destinationTestPath;
        this.basePackage = basePackage;

        this.velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        this.velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        this.velocityEngine.init();
    }

    public void build() {
        // Get all file path in resources
        final Map<@NonNull APIInformation, List<BuildInformation>> buildInformationByDirectoryMap = getSubPackageByDirectoryMap();
        for (final Entry<@NonNull APIInformation, List<BuildInformation>> entry : buildInformationByDirectoryMap.entrySet()) {
            generateForAPI(entry.getKey(), entry.getValue());
        }

        for (final Entry<@NonNull APIInformation, List<BuildInformation>> entry : buildInformationByDirectoryMap.entrySet()) {
            final APIInformation apiInformation = entry.getKey();
            final List<BuildInformation> buildInformationsList = entry.getValue();
            if ("tvdb".equals(apiInformation.apiName)) {
                final JTVDBgenerator bgenerator = new JTVDBgenerator(this.destinationPath, this.destinationTestPath, this.basePackage, apiInformation, buildInformationsList);
                bgenerator.buildMainTVDB(apiInformation.apiName);
                bgenerator.buildUnitTestTVDB(apiInformation.apiName);
            } else if ("tmdb".equals(apiInformation.apiName)) {
                final JTMDBBgenerator bgenerator = new JTMDBBgenerator(this.destinationPath, this.destinationTestPath, this.basePackage, apiInformation, buildInformationsList);
                bgenerator.buildMainTMDB(apiInformation.apiName);
                bgenerator.buildUnitTestTMDB(apiInformation.apiName);
            }
        }

        // generate constant file for tests
        generateTestConstants();
    }

    private void generateForAPI(@NonNull final APIInformation apiInformation, final List<BuildInformation> buildInformationsList) {
        // generate pojo from json OK
        for (final BuildInformation buildInformation : buildInformationsList) {
            try {
                generatePojoFromJson(buildInformation);
            } catch (final IOException e) {
                LOGGER.logp(Level.SEVERE, "JMetaAgentsGenerator", "build", e.getMessage(), e);
            }
        }
        // generate exception file OK
        generateExceptionFile(apiInformation);
        // generate abstract OK
        generateAbstract();
    }

    private void generateTestConstants() {
        final VelocityContext context = new VelocityContext();
        final Root root = new Root();
        root.setSerialUID("1L");
        context.put("package", this.basePackage);
        callVelocity(this.destinationTestPath + "/MetaagentConstants.java", "tpl/constants.vm", context);
    }

    private void generateAbstract() {
        final VelocityContext context = new VelocityContext();
        final Root root = new Root();
        root.setSerialUID("1L");
        context.put("package", this.basePackage);
        callVelocity(this.destinationPath + "/AbstractAgent.java", "tpl/abstractagent.vm", context);
    }

    private void generateExceptionFile(@NonNull final APIInformation apiInformation) {
        try {
            final File file = new File(apiInformation.getExceptionPath());
            FileUtils.forceMkdir(file);
            final VelocityContext context = new VelocityContext();
            final Root root = new Root();
            root.setName(apiInformation.getExceptionClassName());
            root.setSerialUID("1L");
            context.put("comp", root);
            context.put("package", apiInformation.getExceptionPackage());
            callVelocity(apiInformation.getExceptionJavaFile(), "tpl/exception.vm", context);
        } catch (final IOException e) {
            LOGGER.logp(Level.SEVERE, "JMetaAgentsGenerator", "generateExceptionFile", e.getMessage(), e);
        }
    }

    private void generateConstantsFile(@NonNull final APIInformation apiInformation) {
        try {
            final File file = new File(apiInformation.getConstantsPath());
            FileUtils.forceMkdir(file);
            final VelocityContext context = new VelocityContext();
            final Root root = new Root();
            root.setName(apiInformation.getConstantsClassName());
            root.setSerialUID("1L");
            context.put("comp", root);
            context.put("package", apiInformation.getConstantsPackage());
            callVelocity(apiInformation.getConstantsJavaFile(), "tpl/constants.vm", context);
        } catch (final IOException e) {
            LOGGER.logp(Level.SEVERE, "JMetaAgentsGenerator", "generateConstantsFile", e.getMessage(), e);
        }
    }

    private void generatePojoFromJson(@NonNull final BuildInformation buildInformation) throws IOException {
        if (buildInformation.questionJSON != null || buildInformation.responseJSON != null) {
            final String fullPackage = this.basePackage + "." + buildInformation.packageName;
            final String fullDirectoryPath = this.destinationPath + "/" + buildInformation.subDirectoryPath;
            final String fullDirectoryTestPath = this.destinationTestPath + "/" + buildInformation.subDirectoryPath;
            final File directory = new File(fullDirectoryPath);
            final File directoryTest = new File(fullDirectoryTestPath);
            FileUtils.forceMkdir(directory);
            FileUtils.forceMkdir(directoryTest);
            if (buildInformation.questionJSON != null) {
                final String jsonToParse = new String(Files.readAllBytes(buildInformation.questionJSON.toPath()));
                final JsonConvertor jsonConvertor = new JsonConvertor(fullPackage, buildInformation.baseJavaClassName + "Question", jsonToParse, fullDirectoryPath, fullDirectoryTestPath);
                jsonConvertor.generateAll(Generator.JACKSON, "deserializers", "Welle", AnnotationType.ECLIPSE);
            }
            if (buildInformation.responseJSON != null) {
                final String jsonToParse = new String(Files.readAllBytes(buildInformation.responseJSON.toPath()));
                final JsonConvertor jsonConvertor = new JsonConvertor(fullPackage, buildInformation.baseJavaClassName + "Response", jsonToParse, fullDirectoryPath, fullDirectoryTestPath);
                jsonConvertor.generateAll(Generator.JACKSON, "deserializers", "Welle", AnnotationType.ECLIPSE);
            }
        }
    }

    @NonNull
    private Map<@NonNull APIInformation, List<BuildInformation>> getSubPackageByDirectoryMap() {
        final Map<@NonNull APIInformation, List<BuildInformation>> result = new HashMap<>();
        final ClassLoader classLoader = JMetaAgentsGenerator.class.getClassLoader();
        final File directory = new File(classLoader.getResource("data/").getFile());
        final Path directoryPath = directory.toPath();
        try {
            if (directory.isDirectory()) {
                final Map<String, APIInformation> mapInformation = new HashMap<>();
                final List<@NonNull File> files = (List<@NonNull File>) FileUtils.listFilesAndDirs(directory, DirectoryFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
                for (final @NonNull File currentDirectory : files) {
                    final String[] extensions = { "json", "xml" };
                    final List<File> filesInDirectory = (List<File>) FileUtils.listFiles(currentDirectory, extensions, false);
                    if (!filesInDirectory.isEmpty()) {
                        final Path currentDirectoryPath = currentDirectory.toPath();
                        final Path relativize = directoryPath.relativize(currentDirectoryPath);
                        final String relative = relativize.toString().toLowerCase();

                        final BuildInformation buildInformation = new BuildInformation();
                        buildInformation.subDirectoryPath = relative;
                        final String packageName = relative.replaceAll("\\\\", "\\.").replaceAll("/", "\\.");
                        buildInformation.packageName = packageName;
                        final String lastName = packageName.substring(packageName.lastIndexOf(".") + 1);
                        final String baseJavaClassName = "J" + StringUtilities.firstLetterUpperCase(lastName);
                        final String apiName = packageName.substring(0, packageName.indexOf("."));
                        buildInformation.baseJavaClassName = baseJavaClassName;
                        for (final File filePresent : filesInDirectory) {
                            final String fileName = filePresent.getName();
                            if ("query.xml".equals(fileName)) {
                                buildInformation.queryXML = filePresent;
                            } else if ("question.json".equals(fileName)) {
                                buildInformation.questionJSON = filePresent;
                            } else if ("response.json".equals(fileName)) {
                                buildInformation.responseJSON = filePresent;
                            }
                        }

                        APIInformation information = mapInformation.get(apiName);
                        if (information == null) {
                            information = new APIInformation();
                            information.apiName = apiName;
                            mapInformation.put(apiName, information);
                        }
                        List<BuildInformation> list = result.get(information);
                        if (list == null) {
                            list = new ArrayList<>();
                            result.put(information, list);
                        }
                        list.add(buildInformation);
                    }
                }
            }
        } catch (final IllegalArgumentException e) {
            LOGGER.logp(Level.SEVERE, "JMetaAgentsGenerator", "getSubPackageByDirectoryMaps", e.getMessage(), e);
        }

        return result;
    }

    public class APIInformation {
        public String apiName;
        public List<BuildInformation> listOf;

        public String getConstantsPath() {
            return JMetaAgentsGenerator.this.destinationPath + "/" + this.apiName + "/constants";

        }

        public String getConstantsJavaFile() {
            return getExceptionPath() + "/" + getExceptionClassName() + "Constants.java";

        }

        public String getConstantsClassName() {
            return "J" + this.apiName;

        }

        public String getConstantsPackage() {
            return JMetaAgentsGenerator.this.basePackage + "." + this.apiName + ".constants";

        }

        public String getExceptionPath() {
            return JMetaAgentsGenerator.this.destinationPath + "/" + this.apiName + "/exceptions";

        }

        public String getExceptionPackage() {
            return JMetaAgentsGenerator.this.basePackage + "." + this.apiName + ".exceptions";

        }

        public String getExceptionJavaFile() {
            return getExceptionPath() + "/" + getExceptionClassName() + "Exception.java";

        }

        public String getExceptionClassName() {
            return "J" + this.apiName;

        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            if (this.apiName != null) {
                return this.apiName.hashCode();
            }
            return super.hashCode();
        }
    }

    public class BuildInformation {
        public String packageName;
        public String subDirectoryPath;
        public String baseJavaClassName;
        public File questionJSON;
        public File responseJSON;
        public File queryXML;

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "packageName = " + this.packageName + " :: subDirectoryPath = " + this.subDirectoryPath + " :: " + this.baseJavaClassName;
        }
    }
}
