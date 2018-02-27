package aka.jmetaagentsgenerator;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(final String[] args) {
        final JMetaAgentsGenerator agentsGenerator = new JMetaAgentsGenerator("aka.jmetaagents.main", "C:\\Projects\\Own\\jmetaagents\\src\\main\\java\\aka\\jmetaagents\\main", "C:\\Projects\\Own\\jmetaagents\\src\\test\\java\\aka\\jmetaagents\\main");
        agentsGenerator.build();
    }
}
