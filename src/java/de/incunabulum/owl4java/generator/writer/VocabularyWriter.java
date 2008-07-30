package de.incunabulum.owl4java.generator.writer;

import java.io.FileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import de.incunabulum.owl4java.jmodel.JModel;
import de.incunabulum.owl4java.jmodel.utils.NamingUtils;
import de.incunabulum.owl4java.utils.JavaUtils;

public class VocabularyWriter {
	private static Log log = LogFactory.getLog(VocabularyWriter.class);
	private static final String TEMPLATE_NAME = "vocabulary.vm";

	private VelocityEngine vEngine;
	private VelocityContext vContext;
	private String vocabularyName;
	private String toolsPackage;

	public VocabularyWriter(VelocityEngine vEngine, VelocityContext vContext) {
		this.vEngine = vEngine;
		this.vContext = vContext;
	}

	public void writeVocabulary(JModel jmodel, String baseDir,
			String basePackage) {
		String pkgName = NamingUtils.getJavaPackageName(basePackage, toolsPackage);
		String outDir = JavaUtils.toDirectoryFromPackage(pkgName, baseDir);
		String outName = vocabularyName;
		String outPath = outDir + "/" + outName + ".java";
		log.info("Creating vocabulary " + outPath);

		Template template;
		try {
			template = vEngine.getTemplate(TEMPLATE_NAME);
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException();
		} catch (ParseErrorException e) {
			throw new RuntimeException();
		} catch (Exception e) {
			throw new RuntimeException();
		}

		vContext.put("vName", vocabularyName);
		vContext.put("vPkg", NamingUtils.getJavaPackageName(basePackage, toolsPackage));
		vContext.put("model", jmodel);

		try {
			FileWriter fWriter = new FileWriter(outPath);
			template.merge(vContext, fWriter);
			fWriter.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setVocabularyName(String vocabularyName) {
		this.vocabularyName = vocabularyName;
	}

	public void setToolsPackage(String toolsPackage) {
		this.toolsPackage = toolsPackage;
	}

}