package syntaxtree;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Singleton class for managing the abstract syntax trees obtained
 * from parsing source files.
 *
 * In this version, a sync lock is held
 * on the SyntaxTreeManager for the duration of every
 * call to the parser.
 */
public class SyntaxTreeManager
{

    /**
     * The actual cache of syntax trees
     */
    private final Map<String, Future<SyntaxTree>> m_cache = new ConcurrentHashMap<String,Future<SyntaxTree>>();

    /**
     * Return the syntax tree for the given source file, parsing
     * the file if necessary.
     *
     * @param filename
     * @return
     */
    public SyntaxTree getSyntaxTree(String filename) throws InterruptedException
    {
        while(true) {
            Future<SyntaxTree> holder = m_cache.get(filename);
            if (holder == null) {
                Callable<SyntaxTree> callable = new Callable<SyntaxTree>() {
                    @Override
                    public SyntaxTree call() throws InterruptedException {
                        Parser parser = new Parser(filename);
                        return parser.parse();
                    }
                };
                FutureTask<SyntaxTree> ft = new FutureTask<SyntaxTree>(callable);
                holder = m_cache.putIfAbsent(filename, ft);
                if (holder == null) {
                    holder = ft;
                    ft.run();
                }
            }
            try {
                return holder.get();
            } catch (ExecutionException e) {

            }
        }
    }

    /**
     * Helper class for storing completed syntax trees.  Doesn't
     * do much at this point, but could be useful...
     */
//    private static class SyntaxTreeHolder
//    {
//        private SyntaxTree m_ast;
//
//        public SyntaxTree get()
//        {
//            return m_ast;
//        }
//
//        public void set(SyntaxTree ast)
//        {
//            m_ast = ast;
//        }
//    }
}
