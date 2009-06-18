package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.operators.Assignment;
import org.ooc.nodes.operators.EqualityTest;
import org.ooc.nodes.operators.Minus;
import org.ooc.nodes.operators.Plus;
import org.ooc.nodes.operators.Star;
import org.ooc.nodes.others.RawCode;
import org.ubi.SourceReader;

class OperatorsParser implements Parser {

	@Override
	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;
		
		boolean success;
		
		if (reader.matches("*=", true)) {
			
	        context.add(new RawCode(reader.getLocation(), "*= "));
	        success = true;
	
	    } else if (reader.matches("/=", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "/= "));
	        success = true;
	
	    } else if (reader.matches("+=", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "+= "));
	        success = true;
	
	    } else if (reader.matches("-=", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "-= "));
	        success = true;
	
	    } else if (reader.matches("*", true)) {
	
	        context.add(new Star(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches("/", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "/ "));
	        success = true;
	
	    } else if (reader.matches("++", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "++"));
	        success = true;
	
	    } else if (reader.matches("+", true)) {
	
	        context.add(new Plus(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches("--", true)) {
	
	    	context.add(new RawCode(reader.getLocation(), "--"));
	        success = true;
	
	    } else if (reader.matches("-", true)) {
	
	        context.add(new Minus(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches("%", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "% "));
	        success = true;
	
	    } else if (reader.matches("<=", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "<= "));
	        success = true;
	
	    } else if (reader.matches(">=", true)) {
	
	        context.add(new RawCode(reader.getLocation(), ">= "));
	        success = true;
	
	    } else if (reader.matches("<<", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "<< "));
	        success = true;
	
	    } else if (reader.matches(">>", true)) {
	
	        context.add(new RawCode(reader.getLocation(), ">> "));
	        success = true;
	
	    } else if (reader.matches("<", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "< "));
	        success = true;
	
	    } else if (reader.matches(">", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "> "));
	        success = true;
	
	    } else if (reader.matches("!=", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "!= "));
	        success = true;
	
	    } else if (reader.matches("!", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "!"));
	        success = true;
	
	    } else if (reader.matches("==", true)) {
	
	        context.add(new EqualityTest(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches("=", true)) {
	
	        context.add(new Assignment(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches("||", true)) {

	        context.add(new RawCode(reader.getLocation(), "|| "));
	        success = true;
	
	    } else if (reader.matches("|=", true)) {

	        context.add(new RawCode(reader.getLocation(), "|= "));
	        success = true;
	
	    } else if (reader.matches("|", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "| "));
	        success = true;
	
	    } else if (reader.matches("&&", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "&& "));
	        success = true;
	
	    } else if (reader.matches("&=", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "&= "));
	        success = true;
	
	    } else if (reader.matches("&", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "& "));
	        success = true;
	
	    } else if (reader.matches("?", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "? "));
	        success = true;
	
	    } else if (reader.matches(":", true)) {
	
	        context.add(new RawCode(reader.getLocation(), ": "));
	        success = true;
	
	    } else if (reader.matches("~", true)) {
	
	        context.add(new RawCode(reader.getLocation(), "~"));
	        success = true;
	
	    } else {
	    	
	    	success = false;
	    	
	    }
		
		return success;
		
	}

}
