package org.rubypeople.rdt.internal.core.ast;

import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;

import org.jruby.ast.Node;
import org.jruby.ast.visitor.NodeVisitor;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class DumpAst {

    private static final String RUBY_CODE = "class Alpha::Beta::Gamma::Delta; end";

    private static class DumpingInvocationHandler implements InvocationHandler {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Node node = (Node) args[0];
            System.out.println(node);
            
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        new DumpAst().dump(RUBY_CODE);
    }

    private void dump(String rubyCode) throws Exception {
        ShamFile file = new ShamFile("-");
        file.setContents(rubyCode);
        InputStreamReader reader = new InputStreamReader(file.getContents());
        Node rootNode = new RubyParser().parse(file, reader);
        
        NodeVisitor dumpVisitor = (NodeVisitor) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {NodeVisitor.class}, new DumpingInvocationHandler());
        dumpNode(rootNode, dumpVisitor, 0);
    }

    private void dumpNode(Node node, NodeVisitor visitor, int depth) {
        System.out.print("                ".substring(0,depth*2));
        node.accept(visitor);
        for (Iterator iter = node.childNodes().iterator(); iter.hasNext();) {
            Node childNode = (Node) iter.next();
            dumpNode(childNode, visitor, depth + 1);
        }
        
    }

}
