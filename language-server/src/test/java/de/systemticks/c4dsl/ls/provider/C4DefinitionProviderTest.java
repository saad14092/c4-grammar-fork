package de.systemticks.c4dsl.ls.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import de.systemticks.c4dsl.ls.helper.C4TestHelper;
import de.systemticks.c4dsl.ls.model.C4DocumentModel;

public class C4DefinitionProviderTest {
    
    private static C4DocumentModel model;
    private static C4DefinitionProvider definitionProvider;

    @BeforeAll
    public static void initialize() throws IOException, URISyntaxException {
        model = C4TestHelper.createDocumentFromFile(new File(C4TestHelper.PATH_VALID_MODELS + File.separator + "amazon_web_service.dsl"));
        definitionProvider = new C4DefinitionProvider();
    }

    @ParameterizedTest
    @CsvSource({
        " 7, 16,  3, 12,  3, 26", //  _webApplication_ -> database "Reads from and writes to" "JDBC/SSL"
        "17, 85,  3, 12,  3, 26", //  webApplicationInstance = containerInstance _webApplication_
        "23, 49,  4, 12,  4, 20", //  containerInstance _database_
        "30, 14, 12, 20, 12, 27", //  _route53_ -> elb "Forwards requests to" "HTTPS"
        "30, 22, 13, 20, 13, 23", //  route53 -> _elb_ "Forwards requests to" "HTTPS"
        "31, 12, 13, 20, 13, 23"  //  _elb_ -> webApplicationInstance "Forwards requests to" "HTTPS"
    })
    public void calcDefinitions(int p1, int p2, int p3, int p4, int p5, int p6) throws IOException, URISyntaxException {

        DefinitionParams params = new DefinitionParams(new TextDocumentIdentifier(model.getUri()), new Position(p1, p2));

        Either<List<? extends Location>, List<? extends LocationLink>> result = definitionProvider.calcDefinitions(model, params);

        assertEquals(1, result.getLeft().size());

        assertEquals(new Range(new Position(p3, p4), new Position(p5, p6)), result.getLeft().get(0).getRange());
    }

    @Test
    public void calcDefinitionsNotFound() throws IOException, URISyntaxException {

        DefinitionParams params = new DefinitionParams(new TextDocumentIdentifier(model.getUri()), new Position(1, 1));

        Either<List<? extends Location>, List<? extends LocationLink>> result = definitionProvider.calcDefinitions(model, params);

        assertEquals(0, result.getLeft().size());
    }

}
