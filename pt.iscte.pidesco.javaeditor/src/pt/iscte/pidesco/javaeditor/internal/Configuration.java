/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package pt.iscte.pidesco.javaeditor.internal;

import java.util.Iterator;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import pt.iscte.pidesco.javaeditor.internal.scanner.JavaCodePartitionScanner;
import pt.iscte.pidesco.javaeditor.internal.scanner.JavaCodeScanner;
import pt.iscte.pidesco.javaeditor.internal.scanner.JavaCommentsPartitionScanner;


public class Configuration extends SourceViewerConfiguration {
//	private JavaCodeScanner tagScanner;

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
				IDocument.DEFAULT_CONTENT_TYPE,
				JavaCodePartitionScanner.JAVA_COMMENT};
//				JavaCodePartitionScanner.JAVA_CODE };
	}

//	protected JavaCodeScanner getTagScanner() {
//		if (tagScanner == null) {
//			tagScanner = new JavaCodeScanner();
//			tagScanner.setDefaultReturnToken(Common.createToken(TokenColor.BLACK, false));
//		}
//		return tagScanner;
//	}

	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {		
		return new IAnnotationHover() {
			@Override
			public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
				IDocument doc = sourceViewer.getDocument();
				StringBuilder ret = new StringBuilder();
				for (Iterator<Annotation> it = sourceViewer.getAnnotationModel().getAnnotationIterator(); it.hasNext(); ) {
					Annotation ann = it.next();
					int offset = sourceViewer.getAnnotationModel().getPosition(ann).offset;
					try {
//						ret += offset + " .. " + doc.getLineOffset(lineNumber) + " .. " + doc.getLineOffset(lineNumber+1);
						if(offset >= doc.getLineOffset(lineNumber) &&
								(lineNumber == doc.getNumberOfLines()-1 || offset < doc.getLineOffset(lineNumber+1))) {
							ret.append(ann.getText());
//							if(it.hasNext())
//								ret += "\n";
							break;
						}
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
				return ret.toString();
			}
		};
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new JavaCommentsPartitionScanner());
		reconciler.setDamager(dr, JavaCodePartitionScanner.JAVA_COMMENT);
		reconciler.setRepairer(dr, JavaCodePartitionScanner.JAVA_COMMENT);

		DefaultDamagerRepairer dr2 = new DefaultDamagerRepairer(new JavaCodeScanner());
		reconciler.setDamager(dr2, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr2, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(TokenColor.COMMENT.color));
		reconciler.setDamager(ndr, JavaCodePartitionScanner.JAVA_COMMENT);
		reconciler.setRepairer(ndr, JavaCodePartitionScanner.JAVA_COMMENT);

		return reconciler;
	}

}
