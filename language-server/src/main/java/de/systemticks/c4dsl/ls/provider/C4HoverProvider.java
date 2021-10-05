package de.systemticks.c4dsl.ls.provider;

import java.util.Iterator;

import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.MarkupContent;

import com.structurizr.view.ElementView;
import com.structurizr.view.View;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;

public class C4HoverProvider {

	public Hover calcHover(C4DocumentModel c4, HoverParams params) {
	/*
		View view = c4.getViewAtLineNumber(params.getPosition().getLine()+1);

		if(view != null) {

			Iterator<ElementView> it = view.getElements().iterator();
			if(it.hasNext()) {				
				MarkupContent content = new MarkupContent();
				content.setKind("markdown");
				content.setValue("This is the view for model element "+it.next().getElement().getOriginId());
				return new Hover(content);
			}		
		}
	*/	
		return null;		
	}

}
