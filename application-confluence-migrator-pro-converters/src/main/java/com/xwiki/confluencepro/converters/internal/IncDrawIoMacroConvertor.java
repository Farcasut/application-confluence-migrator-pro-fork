/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xwiki.confluencepro.converters.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.confluence.filter.internal.input.ConfluenceConverter;
import org.xwiki.contrib.confluence.filter.internal.macros.AbstractMacroConverter;
import org.xwiki.model.reference.EntityReference;

/**
 * Converts the inc-drawio macros.
 *
 * @version $Id$
 * @since 1.20.2
 */
@Component
@Singleton
@Named("inc-drawio")
public class IncDrawIoMacroConvertor extends AbstractMacroConverter
{
    @Inject
    private Logger logger;

    @Inject
    private ConfluenceConverter converter;

    @Override
    public String toXWikiId(String confluenceId, Map<String, String> confluenceParameters, String confluenceContent,
        boolean inline)
    {
        return "confluence_drawio";
    }

    @Override
    protected Map<String, String> toXWikiParameters(String confluenceId, Map<String, String> confluenceParameters,
        String content)
    {

        // First we make sure that we log the entries that embed diagrams from other sources.
        if (confluenceParameters.containsKey("service") || confluenceParameters.containsKey("diagramUrl")) {
            logger.warn("The inc-drawio was used with unsupported parameters.");
            return Collections.emptyMap();
        }
        long pageId = Long.parseLong(confluenceParameters.get("pageId"));
        // The reference will always start with "Document " and we want to remove that and keep only the actual
        // reference to the page
        EntityReference reference = converter.convertDocumentReference(pageId, false);
        Map<String, String> xwikiParameters = new HashMap<>();
        if (reference != null) {
            String ref = reference.toString().substring(reference.toString().indexOf(" ") + 1);
            xwikiParameters.put("originalDocumentRef", ref);
        }

        xwikiParameters.put("diagramName", confluenceParameters.get("diagramDisplayName"));
        return xwikiParameters;
    }
}
