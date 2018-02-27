package aka.jmetaagentsgenerator.xml.query;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Cha
 */
public class QueryReaderHelper {

    private static final Logger LOGGER = Logger.getLogger(QueryReaderHelper.class.getName());

    /**
     * Constructor.
     */
    private QueryReaderHelper() {
        // Singleton
    }

    /**
     * Get Query from XML Query file.
     *
     * @param filePath
     * @return Query
     */
    @Nullable
    public static Query getQuery(final String filePath) {
        Query result = null;
        try {
            final ClassLoader classLoader = QueryReaderHelper.class.getClassLoader();
            final File file = new File(classLoader.getResource(filePath).getFile());

            final JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            final Unmarshaller unmarshaller = jc.createUnmarshaller();

            result = (Query) unmarshaller.unmarshal(file);
        } catch (final JAXBException e) {
            LOGGER.logp(Level.SEVERE, "QueryReaderHelper", "getQuery", e.getMessage(), e);
        }

        return result;
    }
}
