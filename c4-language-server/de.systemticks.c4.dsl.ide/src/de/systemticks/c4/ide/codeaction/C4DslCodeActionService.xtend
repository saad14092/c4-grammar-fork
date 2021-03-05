package de.systemticks.c4.ide.codeaction

import com.google.inject.Inject
import de.systemticks.c4.c4Dsl.StyledElement
import de.systemticks.c4.validation.C4DslValidator
import org.eclipse.lsp4j.CodeAction
import org.eclipse.lsp4j.CodeActionKind
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.TextEdit
import org.eclipse.lsp4j.WorkspaceEdit
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.xtext.ide.server.DocumentExtensions
import org.eclipse.xtext.ide.server.codeActions.ICodeActionService2
import org.eclipse.xtext.nodemodel.util.NodeModelUtils

class C4DslCodeActionService implements ICodeActionService2 {

	@Inject DocumentExtensions documentExtensions

	override getCodeActions(Options options) {
		
		val document = options.document
		val params = options.codeActionParams
		val resource = options.resource
		val result = <CodeAction>newArrayList
		
		for (d : params.context.diagnostics) {
			if (d.code.get == C4DslValidator.NO_STYLED_ELEMENT_FOR_TAG) {
				val lastStyle = resource.allContents.filter(StyledElement).last	
				if(lastStyle !== null) {
					val tag = document.getSubstring(d.range)
					val node = NodeModelUtils.findActualNodeFor(lastStyle)
					val rangeLastStyle = documentExtensions.newRange(resource, node.textRegion)								
					
					result += new CodeAction => [
						kind = CodeActionKind.QuickFix	
						title = "Create a new style for tag "+tag
						diagnostics = #[d]
						val pos = new Position => [
							line = node.endLine
							character = rangeLastStyle.end.character
						]
						edit = new WorkspaceEdit => [
							val textEdit =new TextEdit => [
								range = new Range => [
									start = pos
									end = pos
								]	
								newText = 
								'''
								
								element «tag» {
									
								}
								
								'''
							]
							changes.put(resource.URI.toString, #[textEdit])
						]
					]
				}
			}
		}
		return result.map[Either.forRight(it)]
	}

}
