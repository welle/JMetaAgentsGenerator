package aka.jmetaagentsgenerator;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @org.junit.Test
    public void buildAll() {
        final JMetaAgentsGenerator agentsGenerator = new JMetaAgentsGenerator("aka.jmetaagents.main", "C:\\Projects\\Own\\jmetaagents\\src\\main\\java\\aka\\jmetaagents\\main", "C:\\Projects\\Own\\jmetaagents\\src\\test\\java\\aka\\jmetaagents\\main");
        agentsGenerator.build();
    }

}
