package de.systemticks.c4.ide.completion

import com.google.inject.Inject
import de.systemticks.c4.c4Dsl.DeploymentEnvironment
import de.systemticks.c4.c4Dsl.StaticView
import de.systemticks.c4.c4Dsl.Workspace
import de.systemticks.c4.services.C4DslGrammarAccess
import org.eclipse.xtext.Assignment
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.RuleCall
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext
import org.eclipse.xtext.ide.editor.contentassist.IIdeContentProposalAcceptor
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider

import static de.systemticks.c4.utils.C4Utils.*

class C4ProposalProvider extends IdeContentProposalProvider {

	@Inject C4DslGrammarAccess grammarAccess

       override dispatch createProposals(RuleCall ruleCall, ContentAssistContext context,
             IIdeContentProposalAcceptor acceptor) {

             switch ruleCall.rule {
                                                           
                    case grammarAccess.systemContextViewRule: {                          
                          acceptor.accept(                       
                                 proposalCreator.createSnippet(
                                       '''
                                       systemcontext ${1:SoftwareSystemIdentifier} "${2:key}" "${3:description}"{
                                           include *
                                           autoLayout
                                       }
                                       ''', 'New systemcontext view (Template)', context
                                 ), 0)                    
                    }
                    
                    case grammarAccess.systemLandscapeRule: {                          
                          acceptor.accept(                       
                                 proposalCreator.createSnippet(
                                       '''
                                       systemlandscape "${1:key}" "${2:description}" {
                                           include *
                                           autoLayout
                                       }
                                       ''', 'New systemlandscape view (Template)', context
                                 ), 0)                    
                    }
                    case grammarAccess.containerViewRule: {                          
                          acceptor.accept(                       
                                 proposalCreator.createSnippet(
                                       '''
                                       container ${1:SoftwareSystemIdentifier} "${2:key}" "${3:description}"{
                                           include *
                                           autoLayout
                                       }
                                       ''', 'New container view (Template)', context
                                 ), 0)                    
                    }
                    case grammarAccess.componentViewRule: {                          
                          acceptor.accept(                       
                                 proposalCreator.createSnippet(
                                       '''
                                       component ${1:ContainerIdentifier} "${2:key}" "${3:description}"{
                                           include *
                                           autoLayout
                                       }
                                       ''', 'New component view (Template)', context
                                 ), 0)                    
                    }
                    case grammarAccess.personRule: {
                          acceptor.accept(                       
                                 proposalCreator.createSnippet(
                                       '''
                                       ${1:id} = person "${2:name}" "${3:description}" "${4:tags}"
                                       ''', 'New person (Template)', context
                                 ), 0)                    	
                    }
                                       
                    default:
                          super._createProposals(ruleCall, context, acceptor)
             }
       }


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
			
			case grammarAccess.deploymentViewAccess.environmentAssignment_2: {
				val rootElement = EcoreUtil2.getRootContainer(context.currentModel);
				val candidates = EcoreUtil2.getAllContentsOfType(rootElement, DeploymentEnvironment);			
				for(c: candidates) {
					addProposal('"'+c.name+'"', context, acceptor)
				}
			}
			
			case grammarAccess.filteredViewAccess.baseKeyAssignment_1: {
				val rootElement = EcoreUtil2.getRootContainer(context.currentModel);
				val candidates = EcoreUtil2.getAllContentsOfType(rootElement, StaticView);			
				for(c: candidates) {
					addProposal('"'+c.name+'"', context, acceptor)
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
