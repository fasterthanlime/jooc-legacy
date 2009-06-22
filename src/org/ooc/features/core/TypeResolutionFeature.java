package org.ooc.features.core;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.SourceContext;
import org.ooc.features.SingleFeature;
import org.ooc.nodes.RootNode;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.nodes.types.Type;
import org.ooc.parsers.TypeParser;

/**
 * Resolve the real type of a typeUsage, e.g. does it correspond to a class ?
 * is it a base type ? (e.g. "int")
 * 
 * @author Amos Wenger
 */
public class TypeResolutionFeature extends SingleFeature<Type> {

	/**
	 * Default constructor
	 */
	public TypeResolutionFeature() {
		super(Type.class);
	}

	@Override
	protected void applyImpl(AssemblyManager manager, Type type) {
		
		/* First place to look: the current source */
		ClassDef classDef = manager.getContext().getClassDef(type.name);
    	if(classDef != null) {
    		
    		type.clazz = classDef.clazz;
    		type.isResolved = true;
    		type.isCover = type.clazz.isCover;
    		
    	} else {
    		
    		SyntaxNodeList parentContext = type.getParent();
			if(parentContext != null) {
			
				/* Second place to look: the type's parent context */
				tryContext(type, parentContext);
				
			} else if(type.getContext() != null) {
				
				/* Third place to look: the type's context (if it's being used off-tree) */
				tryContext(type, type.getContext());
				
			}
			
		}

    	if(!type.isResolved) {
    		
    		/* Fourth place to look: standard type, e.g. int, etc. */
    		if(TypeParser.isValidType(type.name)) {
    			type.isResolved = true;
    			type.isCover = true;
    		}
    		
    	}
    	
    	if(!type.isResolved) {
    		
    		SourceContext sourceContext = type.getSourceContext();
    		
    		/* Fifth place to look: the includes */
    		if(manager.getContext().getTypeDef(type.name) != null) {
    			type.isResolved = true;
    			type.isCover = true;
    		} else if(sourceContext != null && sourceContext.getTypeDef(type.name) != null) {
    			type.isResolved = true;
    			type.isCover = true;
    		}
    		
    	}
    	
    	if(type.isResolved) {
    		type.lock();
    		manager.clean(type);
    	}
		
	}

	private void tryContext(Type type, SyntaxNode contextNode) {
		
		RootNode root = contextNode.getRoot();
		if(root == null) {
			//System.out.println("Null root for a "+contextNode.getClass().getSimpleName()+", no luck :/");
			return;
		}
		
		ClassDef def = root.context.getClassDef(type.name);
		if(def != null) {
		
			//System.err.println("slamadunk: Resolved '"+type.name+"' in context "+context.getDescription());
			type.clazz = def.clazz;
			type.isResolved = true;
			type.isCover = type.clazz.isCover;
			
		}
		
	}
	
}
