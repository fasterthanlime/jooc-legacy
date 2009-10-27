package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.backend.TabbedWriter;

public class AwesomeWriter extends TabbedWriter {

	public AwesomeWriter(Appendable appendable) {
		super(appendable);
	}

	protected AwesomeWriter closeBlock() throws IOException {
		tabLevel--;
		appendable.append('\n');
		appendable.append(tab, 0, tabLevel);
		appendable.append('}');
		return this;
	}

	protected AwesomeWriter openBlock() throws IOException {
		//appendable.append('\n');
		//appendable.append(tab, 0, tabLevel);
		appendable.append('{');
		tabLevel++;
		return this;
	}
	
	protected AwesomeWriter openSpacedBlock() throws IOException {
		//appendable.append('\n');
		//appendable.append(tab, 0, tabLevel);
		appendable.append("{\n");
		tabLevel++;
		appendable.append(tab, 0, tabLevel);
		return this;
	}
	
	protected AwesomeWriter closeSpacedBlock() throws IOException {
		tabLevel--;
		appendable.append('\n');
		appendable.append(tab, 0, tabLevel);
		appendable.append("}\n\n");
		appendable.append(tab, 0, tabLevel);
		return this;
	}
	
	@Override
	public AwesomeWriter tab() {
		tabLevel++;
		return this;
	}
	
	@Override
	public AwesomeWriter untab() {
		tabLevel--;
		return this;
	}
	
	@Override
	public AwesomeWriter nl() throws IOException {
		appendable.append('\n');
		appendable.append(tab, 0, tabLevel);
		return this;
	}
	
	public AwesomeWriter app(char c) throws IOException {
		appendable.append(c);
		return this;
	}
	
	public AwesomeWriter app(String s) throws IOException {
		appendable.append(s);
		return this;
	}
	
	
}
