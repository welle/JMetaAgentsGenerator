package aka.jmetaagentsgenerator;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

/**
 * JTVDBGenerator.
 *
 * @author charlottew
 */
public class JTVDBGenerator {

    private @NonNull final String basePackage;

    public JTVDBGenerator(@NonNull final String basePackage) {
        this.basePackage = basePackage;
    }

    public void buildException() {
        final Map<String, Object> context = new HashMap<>();
        final Root root = new Root();
        root.setName("JTVDB");
        root.setSerialUID("1L");
        context.put("comp", root);

    }

}
