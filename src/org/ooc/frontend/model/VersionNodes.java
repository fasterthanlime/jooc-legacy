package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.middle.hobgoblins.Resolver;

import org.ooc.frontend.Target;

public class VersionNodes {

	public static interface VersionNodeVisitor {

		void visit(VersionName versionName) throws IOException;
		void visit(VersionNegation versionNegation) throws IOException;
		void visit(VersionAnd versionAnd) throws IOException;
		void visit(VersionOr versionOr) throws IOException;
		void visit(VersionParen versionParen) throws IOException;
		
	}
	
	/**
	 * A version string, corresponding to a #define 
	 * @author Amos Wenger
	 */
	public static abstract class VersionNode {
		
		public abstract void accept(VersionNodeVisitor visitor) throws IOException;
		public abstract void acceptChildren(VersionNodeVisitor visitor) throws IOException;
		public abstract boolean isSatisfied(Resolver res);
		
	}
	
	public static class VersionName extends VersionNode {
		
		String name;
		boolean solved = false;
		
		public VersionName(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		@Override
		public void accept(VersionNodeVisitor visitor) throws IOException {
			visitor.visit(this);
		}

		@Override
		public void acceptChildren(VersionNodeVisitor visitor) {}
		
		@Override
		public String toString() {
			return name;
		}

		@Override
		public boolean isSatisfied(Resolver res) {
			if(name.toLowerCase().contains("win") && Target.guessHost() == Target.WIN) {
				return true;
			}
			if((name.toLowerCase().contains("linux") 
					|| name.toLowerCase().contains("unix")) && Target.guessHost() == Target.LINUX) {
				return true;
			}
			if(name.toLowerCase().contains("apple") && Target.guessHost() == Target.OSX) {
				return true;
			}
			if(name.toLowerCase().contains("sun") && Target.guessHost() == Target.SOLARIS) {
				return true;
			}
			return false;
		}
		
	}
	
	public static class VersionNegation extends VersionNode {
	
		VersionNode inner;

		public VersionNegation(VersionNode inner) {
			this.inner = inner;
		}
		
		public VersionNode getInner() {
			return inner;
		}
		
		@Override
		public void accept(VersionNodeVisitor visitor) throws IOException {
			visitor.visit(this);
		}

		@Override
		public void acceptChildren(VersionNodeVisitor visitor) throws IOException {
			inner.accept(visitor);
		}
		
		@Override
		public String toString() {
			return "!" + inner;
		}

		@Override
		public boolean isSatisfied(Resolver res) {
			return !inner.isSatisfied(res);
		}
		
	}
	
	public static class VersionParen extends VersionNode {
		
		VersionNode inner;

		public VersionParen(VersionNode inner) {
			this.inner = inner;
		}
		
		public VersionNode getInner() {
			return inner;
		}
		
		@Override
		public void accept(VersionNodeVisitor visitor) throws IOException {
			visitor.visit(this);
		}

		@Override
		public void acceptChildren(VersionNodeVisitor visitor) throws IOException {
			inner.accept(visitor);
		}
		
		@Override
		public String toString() {
			return "(" + inner + ")";
		}
		
		@Override
		public boolean isSatisfied(Resolver res) {
			return inner.isSatisfied(res);
		}
		
	}
	
	public abstract static class VersionCouple extends VersionNode {
	
		VersionNode left;
		VersionNode right;
		
		public VersionCouple(VersionNode left, VersionNode right) {
			this.left = left;
			this.right = right;
		}
		
		public VersionNode getLeft() {
			return left;
		}
		
		public void setLeft(VersionNode left) {
			this.left = left;
		}
		
		public VersionNode getRight() {
			return right;
		}
		
		public void setRight(VersionNode right) {
			this.right = right;
		}
		
		@Override
		public void acceptChildren(VersionNodeVisitor visitor) throws IOException {
			left.accept(visitor);
			right.accept(visitor);
		}
		
	}
	
	public static class VersionAnd extends VersionCouple {

		public VersionAnd(VersionNode left, VersionNode right) {
			super(left, right);
		}

		@Override
		public void accept(VersionNodeVisitor visitor) throws IOException {
			visitor.visit(this);
		}

		@Override
		public String toString() {
			return left + " && " + right;
		}
		
		@Override
		public boolean isSatisfied(Resolver res) {
			return left.isSatisfied(res) && right.isSatisfied(res);
		}

	}
	
	public static class VersionOr extends VersionCouple {

		public VersionOr(VersionNode left, VersionNode right) {
			super(left, right);
		}

		@Override
		public void accept(VersionNodeVisitor visitor) throws IOException {
			visitor.visit(this);
		}
		
		@Override
		public String toString() {
			return left + " || " + right;
		}
		
		@Override
		public boolean isSatisfied(Resolver res) {
			return left.isSatisfied(res) || right.isSatisfied(res);
		}
		
	}
	
}
