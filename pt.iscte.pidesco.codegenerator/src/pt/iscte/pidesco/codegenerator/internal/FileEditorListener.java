package pt.iscte.pidesco.codegenerator.internal;

import java.io.File;

import pt.iscte.pidesco.javaeditor.service.JavaEditorListener;

public class FileEditorListener implements JavaEditorListener {
	private File file;
	
	@Override
	public void fileOpened(File file) {
		this.file = file;
	}

	@Override
	public void fileClosed(File file) {
		this.file = file;
	}

	@Override
	public void fileSaved(File file) {
		this.file = file;
	}
	
	public File getFile(){
		return file;
	}
}