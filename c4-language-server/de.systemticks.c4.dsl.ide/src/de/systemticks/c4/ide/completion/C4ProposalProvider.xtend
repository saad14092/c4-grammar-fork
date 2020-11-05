package de.systemticks.c4.ide.completion

import org.eclipse.xtext.Assignment
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext
import org.eclipse.xtext.ide.editor.contentassist.IIdeContentProposalAcceptor
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider
import de.systemticks.c4.services.C4DslGrammarAccess
import com.google.inject.Inject

import static extension de.systemticks.c4.utils.C4Utils.*
import de.systemticks.c4.c4Dsl.Workspace

class C4ProposalProvider extends IdeContentProposalProvider {

	@Inject C4DslGrammarAccess grammarAccess

	override dispatch void createProposals(Assignment assignment, ContentAssistContext context,
		IIdeContentProposalAcceptor acceptor) {

		switch (assignment) {
						
			case grammarAccess.styledElementAccess.tagAssignment_1: {
				for(element: allTags(context.rootModel as Workspace)) {
					addProposal('"'+element+'"', context, acceptor)					
				}
			}
			
			case grammarAccess.styledElementAccess.shapeAssignment_3_0_1: {
				for(s: shapes) {
					addProposal(s, context, acceptor)										
				}
			}
			
			default: {
				super._createProposals(assignment, context, acceptor);
			}
		}

	}

	private def addProposal(String element, ContentAssistContext context, IIdeContentProposalAcceptor acceptor) {
		val entry = proposalCreator.createProposal(element, context)
		val prio = proposalPriorities.getDefaultPriority(entry)
		acceptor.accept(entry, prio)		
	}

}
