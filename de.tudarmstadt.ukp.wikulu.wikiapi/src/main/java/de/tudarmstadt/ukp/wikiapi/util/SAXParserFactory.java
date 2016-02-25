/*******************************************************************************
 * Copyright 2010-2016 Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *******************************************************************************/
package de.tudarmstadt.ukp.wikiapi.util;


import java.net.URI;
import java.net.URISyntaxException;

import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.parsers.XIncludeAwareParserConfiguration;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.jdom.input.SAXBuilder;

@Deprecated
public
class SAXParserFactory
{
    private static final String PROP_GRAMMAR_POOL =
        "http://apache.org/xml/properties/internal/grammar-pool";

    private final XMLGrammarPool _grammarPool;
    private final CatalogManager _catalogManager;

    private final boolean _validate = false;

    public
    SAXParserFactory()
    {
        _grammarPool = new XMLGrammarPoolImpl();
        _catalogManager = new CatalogManager() {
            {
                // Do not issue a warning on System.err.
                setIgnoreMissingProperties(true);
            }
        };
        _catalogManager.setPreferPublic(true);
        _catalogManager.setUseStaticCatalog(false);
        try {
			setCatalog(new URI("/META-INF/resources/catalog.xml"));
			//System.out.println(t.getPath());
			
			//File test = new File(new URI("http://localhost:8085/META-INF/resources/catalog.xml"));
			//boolean test2 =test.exists();
			//System.out.println(test2);
		} catch (URISyntaxException e) {
			// the xml is present in the project tree, should never happen
			System.err.println("the xml is present in the project tree, should never happen");
			e.printStackTrace();
		}
    }

    protected
    CatalogResolver getCatalogResolver()
    {
        final CatalogResolver catalogResolver = new CatalogResolver(_catalogManager);
        catalogResolver.namespaceAware = true;
        catalogResolver.validating = _validate;
        return catalogResolver;
    }

    public
    void setCatalog(final URI catalogURI)
    {
        if (catalogURI == null) {
            _catalogManager.setCatalogFiles(null);
        } else {
            _catalogManager.setCatalogFiles(catalogURI.toString());
        }
    }

    public
    SAXParser getParser()
    {
        final XMLParserConfiguration config = new XIncludeAwareParserConfiguration();
        config.setProperty(PROP_GRAMMAR_POOL, _grammarPool);
        final SAXParser parser = new SAXParser(config);
        parser.setEntityResolver(getCatalogResolver());
        return parser;
    }
    
    public
    SAXBuilder getBuilder()
    {
        final SAXBuilder builder = new SAXBuilder();
        builder.setEntityResolver(getCatalogResolver());
        return builder;
    }
}