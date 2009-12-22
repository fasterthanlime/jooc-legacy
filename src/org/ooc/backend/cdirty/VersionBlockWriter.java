package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.VersionBlock;
import org.ooc.frontend.model.VersionNodes.VersionAnd;
import org.ooc.frontend.model.VersionNodes.VersionName;
import org.ooc.frontend.model.VersionNodes.VersionNegation;
import org.ooc.frontend.model.VersionNodes.VersionNodeVisitor;
import org.ooc.frontend.model.VersionNodes.VersionOr;
import org.ooc.frontend.model.VersionNodes.VersionParen;

public class VersionBlockWriter {

	public static void writeVersionBlockStart(VersionBlock versionBlock, final CGenerator cgen) throws IOException {
		
		cgen.current.app("\n\n#if ");
		
		versionBlock.getVersion().accept(new VersionNodeVisitor() {
			
			public void visit(VersionOr versionOr) throws IOException {
				versionOr.getLeft().accept(this);
				cgen.current.app(" || ");
				versionOr.getRight().accept(this);
			}
			
			public void visit(VersionAnd versionAnd) throws IOException {
				versionAnd.getLeft().accept(this);
				cgen.current.app(" && ");
				versionAnd.getRight().accept(this);
			}
			
			public void visit(VersionNegation versionNegation) throws IOException {
				cgen.current.app('!');
				versionNegation.getInner().accept(this);
			}
			
			public void visit(VersionName versionName) throws IOException {
				cgen.current.app("defined(").app(versionName.getName()).app(")");
			}
		
			public void visit(VersionParen versionParen) throws IOException {
				cgen.current.app('(');
				versionParen.getInner().accept(this);
				cgen.current.app(')');
			}
		});
		//cgen.current.nl();
		
	}
	
	public static void writeVersionBlockEnd(CGenerator cgen) throws IOException {
		cgen.current.app("\n#endif");
	}
	
}
