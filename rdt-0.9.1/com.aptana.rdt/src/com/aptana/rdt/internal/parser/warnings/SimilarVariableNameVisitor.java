package com.aptana.rdt.internal.parser.warnings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jruby.ast.ArgsNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.Node;
import org.jruby.ast.VCallNode;
import org.jruby.ast.types.INameNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class SimilarVariableNameVisitor extends RubyLintVisitor {

	private List<Map<String, Node>> stack;

	public SimilarVariableNameVisitor(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
		stack = new ArrayList<Map<String, Node>>();
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_SIMILAR_VARIABLE_NAMES;
	}

	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		enterMethod();
		return super.visitDefnNode(iVisited);
	}
	
	@Override
	public Instruction visitDefsNode(DefsNode iVisited) {
		enterMethod();
		return super.visitDefsNode(iVisited);
	}

	private void enterMethod() {
		// TODO Auto-generated method stub
		enterScope();
	}

	@Override
	public Instruction visitArgsNode(ArgsNode iVisited) {
		ListNode list = iVisited.getArgs();
		if (list != null && list.childNodes() != null) {
			for (Object arg : list.childNodes()) {
				Node argNode = (Node) arg;
				addVar((INameNode) argNode);
			}
		}
		list = iVisited.getOptArgs();
		if (list != null && list.childNodes() != null) {
			for (Object arg : list.childNodes()) {
				Node argNode = (Node) arg;
				addVar((INameNode) argNode);
			}
		}
		return super.visitArgsNode(iVisited);
	}

	@Override
	public void exitDefnNode(DefnNode iVisited) {
		exitMethod();
		super.exitDefnNode(iVisited);
	}

	@Override
	public Instruction visitBlockNode(BlockNode iVisited) {
		enterScope();
		return super.visitBlockNode(iVisited);
	}

	private void enterScope() {
		stack.add(new HashMap<String, Node>());
	}

	@Override
	public void exitBlockNode(BlockNode iVisited) {
		exitScope();
		super.exitBlockNode(iVisited);
	}

	private void exitScope() {
		// FIXME Only create warnings on references to variables that have no declaration
		Map<String, Node> map = stack.remove(stack.size() - 1); // pop
		List<String> names = new ArrayList<String>(map.keySet());
		while (!names.isEmpty()) {
			String name = names.remove(0);
			boolean isInstanceVar = isInstanceVar(name);
			boolean isClassVar = isClassVar(name);
			for (String string : names) {
				// Strip off @s?
				String modName = name;
				if (isInstanceVar) {
					if (!isInstanceVar(string))
						continue;
					modName = name.substring(1);
					string = string.substring(1);
				} else if (isClassVar) {
					if (!isClassVar(string))
						continue;
					modName = name.substring(2);
					string = string.substring(2);
				} else { // name is local var
					if (isInstanceVar(string) || isClassVar(string))
						continue;
				}				
				if (isPlural(modName, string) || isPlural(string, modName)) {
					// check for one being plural of other, if so skip them
					// FIXME Make this option configurable!
					continue;
				}
				if (damerauLevenshteinDistance(modName, string) <= levenshteinThreshold(modName)) {
					createProblem(map.get(name).getPosition(),
							"Variable has similar name to another in scope: Possible misspelling."); // FIXME Shouldn't create a problem if there is a method with this name!
				}
			}
		}
	}

	private boolean isPlural(String singular, String plural) {
		return (singular.length() == plural.length() - 1) && (singular.equals(plural.substring(0, plural.length() - 1))) &&
			plural.charAt(plural.length() - 1) == 's';
	}

	private boolean isInstanceVar(String name) {
		return !isClassVar(name) && name.startsWith("@");
	}

	private boolean isClassVar(String name) {
		return name.startsWith("@@");
	}

	/**
	 * Determine how many changes should trigger the problem
	 */
	private int levenshteinThreshold(String name) {
		int length = name.length();
		if (length < 3)
			return 0;
		return (int) Math.ceil(length / 5.0f);
	}

	/**
	 * This is an implementation created from translating the pseudocode from
	 * Wikipedia: http://en.wikipedia.org/wiki/Damerau-Levenshtein_distance
	 * 
	 * @param s
	 * @param t
	 * @return
	 */
	private int damerauLevenshteinDistance(String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}
		int m = s.length();
		int n = t.length();
		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}
		// d is a table with m+1 rows and n+1 columns
		int[][] d = new int[m + 1][n + 1];

		for (int i = 0; i <= m; i++)
			d[i][0] = i;
		for (int j = 1; j <= n; j++)
			d[0][j] = j;

		for (int i = 1; i <= m; i++) {
			for (int j = 1; j <= n; j++) {
				int cost;
				if (s.charAt(i - 1) == t.charAt(j - 1)) {
					cost = 0;
				} else {
					cost = 1;
				}
				d[i][j] = minimum(d[i - 1][j] + 1, // deletion
						d[i][j - 1] + 1, // insertion
						d[i - 1][j - 1] + cost // substitution
				);
				if (i > 1 && j > 1 && s.charAt(i - 1) == t.charAt(j - 2)
						&& s.charAt(i - 2) == t.charAt(j - 1)) {
					d[i][j] = minimum(d[i][j], d[i - 2][j - 2] + cost // transposition
					);
				}
			}
		}
		return d[m][n];
	}

	private int minimum(int i, int j, int k) {
		int min = minimum(i, j);
		return minimum(min, k);
	}

	private int minimum(int x, int y) {
		if (x < y)
			return x;
		return y;
	}

	@Override
	public Instruction visitVCallNode(VCallNode iVisited) {
		addVar(iVisited);
		return super.visitVCallNode(iVisited);
	}

	@Override
	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {
		addVar(iVisited);
		return super.visitLocalAsgnNode(iVisited);
	}

	@Override
	public Instruction visitLocalVarNode(LocalVarNode iVisited) {
		addVar(iVisited);
		return super.visitLocalVarNode(iVisited);
	}

	private void addVar(INameNode iVisited) {
		Map<String, Node> map;
		if (stack.isEmpty()) {
			map = new HashMap<String, Node>();
			stack.add(map);
		} else {
			map = stack.get(stack.size() - 1);
		}
		map.put(iVisited.getName(), (Node) iVisited);
	}

	@Override
	public void exitClassNode(ClassNode iVisited) {
		// TODO Check for references to class and instance variables that have no declaration/assignment
		super.exitClassNode(iVisited);
	}

	@Override
	public void exitDefsNode(DefsNode iVisited) {
		exitMethod();
		super.exitDefsNode(iVisited);
	}
	
	private void exitMethod() {
		// TODO Check for references to local variables that have no declaration/assignment
		exitScope();
	}

	@Override
	public Instruction visitInstAsgnNode(InstAsgnNode iVisited) {
		addVar(iVisited);
		return super.visitInstAsgnNode(iVisited);
	}

	@Override
	public Instruction visitInstVarNode(InstVarNode iVisited) {
		addVar(iVisited);
		return super.visitInstVarNode(iVisited);
	}

	@Override
	public Instruction visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
		addVar(iVisited);
		return super.visitClassVarAsgnNode(iVisited);
	}

	@Override
	public Instruction visitClassVarNode(ClassVarNode iVisited) {
		addVar(iVisited);
		return super.visitClassVarNode(iVisited);
	}

	@Override
	public Instruction visitClassVarDeclNode(ClassVarDeclNode iVisited) {
		addVar(iVisited);
		return super.visitClassVarDeclNode(iVisited);
	}

}
