package pt.iscte.pidesco.codegenerator.service;

import java.io.File;

public interface CodeGeneratorListener {
	default void fileCreatedOrUpdated(File file) { }
}
