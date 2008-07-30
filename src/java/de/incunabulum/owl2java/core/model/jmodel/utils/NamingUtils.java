package de.incunabulum.owl2java.core.model.jmodel.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.Property;

import de.incunabulum.owl2java.core.model.jmodel.JClass;
import de.incunabulum.owl2java.core.model.jmodel.JModel;
import de.incunabulum.owl2java.core.model.jmodel.JPackage;
import de.incunabulum.owl2java.core.model.ns.NamespaceUtils;
import de.incunabulum.owl2java.core.utils.JavaUtils;
import de.incunabulum.owl2java.core.utils.StringUtils;

public class NamingUtils {

	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(NamingUtils.class);

	public static int anonCounter = 0;
	public static String anonPrefix = "Anon";

	public static String unionClassPrefix = "Union";
	public static String intersectionClassPrefix = "Intersection";

	public static String classNameAddOn = "";
	// %c = AddOn, %n = Name, %p = Prefix
	public static String classNamingSchema = "%c%n%p";

	public static String interfaceNameAddOn = "I";
	// %i = AddOn, %n = Name, %p = Prefix
	public static String interfaceNamingSchema = "%i%n%p";

	// %n = Name, $p = Prefix
	public static String propertyNamingSchema = "%n%p";
	public static boolean propertyStripPrefix = true;

	public static List<String> propertyIgnoredPrefixes;

	static {
		propertyIgnoredPrefixes = new ArrayList<String>();
		propertyIgnoredPrefixes.add("has");
		propertyIgnoredPrefixes.add("is");
	}

	public static String getJavaClassName(OntClass ontClass) {
		String nsUri = ontClass.getNameSpace();
		String prefix = ontClass.getModel().getNsURIPrefix(nsUri);

		// ignore base prefixes and namespaces
		if (NamespaceUtils.defaultNs2UriMapping.containsKey(nsUri))
			prefix = JModel.BASEPREFIX;

		String localName = ontClass.getLocalName();
		if (prefix != null) {
			prefix = StringUtils.toFirstUpperCase(prefix);
		} else {
			prefix = JModel.BASEPREFIX;
		}

		// to naming schema
		String name = classNamingSchema;
		name = name.replace("%n", localName);
		name = name.replace("%p", prefix);
		name = name.replace("%c", classNameAddOn);
		return StringUtils.toFirstUpperCase(name);
	}

	public static String getJavaClassName(String localName, String prefix) {
		String name = classNamingSchema;
		name = name.replace("%n", localName);
		name = name.replace("%p", prefix);
		name = name.replace("%c", classNameAddOn);
		return StringUtils.toFirstUpperCase(name);
	}

	public static String getJavaFullName(JPackage pkg, String className) {
		String name = pkg.getJavaName() + "." + className;
		return name;
	}

	public static String getJavaInterfaceName(OntClass ontClass) {
		String nsUri = ontClass.getNameSpace();
		String prefix = ontClass.getModel().getNsURIPrefix(nsUri);

		// ignore base prefixes and namespaces
		if (NamespaceUtils.defaultNs2UriMapping.containsKey(nsUri))
			prefix = JModel.BASEPREFIX;

		String localName = ontClass.getLocalName();
		if (prefix != null) {
			prefix = StringUtils.toFirstUpperCase(prefix);
		} else {
			prefix = JModel.BASEPREFIX;
		}

		// to naming schema
		String name = interfaceNamingSchema;
		name = name.replace("%n", localName);
		name = name.replace("%p", prefix);
		name = name.replace("%i", interfaceNameAddOn);
		return StringUtils.toFirstUpperCase(name);
	}

	public static String getJavaInterfaceName(String localName, String prefix) {
		String name = interfaceNamingSchema;
		name = name.replace("%n", localName);
		name = name.replace("%p", prefix);
		name = name.replace("%i", interfaceNameAddOn);
		return StringUtils.toFirstUpperCase(name);
	}

	public static String getJavaPackageName(String basePackage, String prefix) {
		if (prefix != JModel.BASEPREFIX)
			return basePackage + "." + prefix;
		return basePackage;
	}

	public static String getPropertyName(OntProperty ontProperty) {
		String nsUri = ontProperty.getNameSpace();
		String prefix = ontProperty.getModel().getNsURIPrefix(nsUri);
		String localName = ontProperty.getLocalName();
		if (prefix != null) {
			prefix = StringUtils.toFirstUpperCase(prefix);
		} else {
			prefix = JModel.BASEPREFIX;
		}

		if (propertyStripPrefix) {
			// prefixes are only stripped if no other property with a
			// name without the prefix exists
			String newLocalName = stripPropertyPrefixes(localName);
			// we have a different name
			if (!newLocalName.equals(localName)) {
				Property p1 = ontProperty.getOntModel().getOntProperty(nsUri +  newLocalName);
				Property p2 = ontProperty.getOntModel().getOntProperty(
						nsUri + StringUtils.toFirstLowerCase(newLocalName));
				// no other property of stripped name exists
				if (p1 == null && p2 == null) {
					localName = newLocalName;
				} else {
					log.warn(LogUtils.toLogName(ontProperty) + ": Can not strip property prefix "
							+ "as another property of such name exists");
				}
			}
		}

		// to naming schema
		String name = propertyNamingSchema;
		name = name.replace("%n", localName);
		name = name.replace("%p", prefix);
		return StringUtils.toFirstLowerCase(name);
	}

	public static String getValidJavaName(String aName) {
		return JavaUtils.toValidJavaName(aName);
	}

	public static String stripPropertyPrefixes(String string) {
		for (String prefix : propertyIgnoredPrefixes)
			string = string.replace(prefix, JModel.BASEPREFIX);
		return string;
	}

	@SuppressWarnings("unchecked")
	public static String createUnionClassName(UnionClass cls) {
		String name = unionClassPrefix;
		Iterator operandIt = cls.listOperands();
		while (operandIt.hasNext()) {
			OntClass c = (OntClass) operandIt.next();
			name += StringUtils.toFirstUpperCase(c.getLocalName());
		}
		name = NamingUtils.getValidJavaName(name);
		return name;
	}

	public static String createUnionClassName(JClass cls, List<JClass> operandClasses) {
		String name = unionClassPrefix;
		for (JClass c : operandClasses) {
			name += c.getJavaClassName();
		}
		name = NamingUtils.getValidJavaName(name);
		return name;
	}

	@SuppressWarnings("unchecked")
	public static String createIntersectionClassName(IntersectionClass cls) {
		String name = intersectionClassPrefix;
		Iterator operandIt = cls.listOperands();
		while (operandIt.hasNext()) {
			OntClass c = (OntClass) operandIt.next();
			// avoid problems with anonymous classes in intersections
			if (c.isAnon()) {
				name += anonPrefix + anonCounter;
				anonCounter++;
			} else {
				name += StringUtils.toFirstUpperCase(c.getLocalName());
			}
		}
		return name;
	}

	public static String createIntersectionClassName(JClass cls, List<JClass> operandClasses) {
		String name = intersectionClassPrefix;
		for (JClass c : operandClasses) {
			name += c.getJavaClassName();
		}
		return name;
	}

}