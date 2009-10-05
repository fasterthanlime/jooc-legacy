package org.ooc.frontend.model;


public class VersionNodes {

	public static interface VersionNodeVisitor {

		void visit(VersionName versionName);
		void visit(VersionNegation versionNegation);
		void visit(VersionAnd versionAnd);
		void visit(VersionOr versionOr);
		
	}
	
	/**
	 * A version string, corresponding to a #define 
	 * @author Amos Wenger
	 */
	public static abstract class VersionNode {
		
		public abstract void accept(VersionNodeVisitor visitor);
		public abstract void acceptChildren(VersionNodeVisitor visitor);
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
		public void accept(VersionNodeVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public void acceptChildren(VersionNodeVisitor visitor) {}
		
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
		public void accept(VersionNodeVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public void acceptChildren(VersionNodeVisitor visitor) {
			inner.accept(visitor);
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
		public void acceptChildren(VersionNodeVisitor visitor) {
			left.accept(visitor);
			right.accept(visitor);
		}
		
	}
	
	public static class VersionAnd extends VersionCouple {

		public VersionAnd(VersionNode left, VersionNode right) {
			super(left, right);
		}

		@Override
		public void accept(VersionNodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	public static class VersionOr extends VersionCouple {

		public VersionOr(VersionNode left, VersionNode right) {
			super(left, right);
		}

		@Override
		public void accept(VersionNodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
}
