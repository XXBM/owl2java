package de.incunabulum.owl4java.jmodel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.incunabulum.owl4java.utils.IName;
import de.incunabulum.owl4java.utils.IReporting;
import de.incunabulum.owl4java.utils.StringUtils;

public class JMapped implements IReporting, IName {

	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(JMapped.class);

	private String name;
	private String comment;
	private String mapUri;

	public JMapped(String name, String mappedTo) {
		assert mappedTo != null;
		this.name = name;
		this.mapUri = mappedTo;
	}

	@Override
	public String getReport() {
		log.warn("JMapped.toReport not implemented");
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getMapUri() {
		return mapUri;
	}

	
	public String getJavaFullNameCaps() {
		return StringUtils.toFirstUpperCase(getName());	
	}

}