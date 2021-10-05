package de.systemticks.c4dsl.ls.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.Color;
import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.ColorPresentation;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.systemticks.c4dsl.ls.model.C4DocumentModel;

public class C4ColorProvider {

    private static final Logger logger = LoggerFactory.getLogger(C4ColorProvider.class);
    private static final int COLOR_STR_LENGTH = 7; // e.g. #00FF00
    private static final String COLOR_START_TOKEN = "#";

	public List<ColorInformation> calcDocumentColors(C4DocumentModel model) {

		logger.debug("calcDocumentColors {}", model.getUri());
		logger.debug("                   {}", model.getColors().toString());
		
		try  {
			List<ColorInformation> result = model.getColors().stream().map( lineNmbr -> {
				String rawline = model.getLineAt(lineNmbr-1);
				int startPos = rawline.indexOf(COLOR_START_TOKEN);
				int endPos = startPos + COLOR_STR_LENGTH;
				Range range = new Range(new Position(lineNmbr-1, startPos), new Position(lineNmbr-1, endPos));
				return new ColorInformation(range, hexToColor(rawline.substring(startPos, endPos)));
			}).collect(Collectors.toList());
							
			return result;	
		}
		catch( Exception e) {
			logger.error("Cannot provider color information: {}, {}", e.getClass().getSimpleName(), e.getMessage());
			return new ArrayList<>();
		}

	}

	public List<ColorPresentation> calcColorPresentations(Color color) {		
		return Collections.singletonList(new ColorPresentation(colorToHex(color)));
	}

	private Color hexToColor(String hexColor) {
		java.awt.Color c = java.awt.Color.decode(hexColor);		
		return new Color( (double)c.getRed()/255, (double)c.getGreen()/255, (double)c.getBlue()/255, 1.0 );		
	}
	
	private String colorToHex(Color color) {
		return String.format("#%02X%02X%02X", (int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255));
	}

}
