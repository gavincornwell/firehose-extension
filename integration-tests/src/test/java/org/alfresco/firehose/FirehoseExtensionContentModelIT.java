/**
 * Copyright (C) 2017 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK project.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.firehose;

import org.alfresco.model.ContentModel;
import org.alfresco.rad.test.AbstractAlfrescoIT;
import org.alfresco.rad.test.AlfrescoTestRunner;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Firehose extension custom model test
 */
@RunWith(value = AlfrescoTestRunner.class)
public class FirehoseExtensionContentModelIT extends AbstractAlfrescoIT {
    private static final String ACME_MODEL_NS = "{http://www.acme.org/model/content/1.0}";
    private static final String ACME_MODEL_LOCALNAME = "contentModel";
    private static final String ACME_TYPE_CLAIM_IMAGE = "insuranceClaimImage";
    private static final String ACME_TYPE_CLAIM_REPORT = "insuranceClaimReport";

    @Test
    public void testCustomContentModelPresence() {
        DictionaryService dictionaryService = getServiceRegistry().getDictionaryService();

        System.out.println("checking model...");
        // check model is present
        Collection<QName> allContentModels = dictionaryService.getAllModels();
        QName customContentModelQName = createQName(ACME_MODEL_LOCALNAME);
        assertTrue("Custom content model " + customContentModelQName.toString() +
                " is not present", allContentModels.contains(customContentModelQName));

        System.out.println("checking image type...");
        // check image type is present
        QName claimImageType = createQName(ACME_TYPE_CLAIM_IMAGE);
        assertNotNull("'claimImageType' type is not present",
                dictionaryService.getType(claimImageType));

        System.out.println("checking report type...");
        // check report type is present
        QName claimReportType = createQName(ACME_TYPE_CLAIM_REPORT);
        assertNotNull("'claimReportType' type is not present",
                dictionaryService.getType(claimReportType));
    }

    /**
     * ==================== Helper Methods ============================================================================
     */

    /**
     * Create a QName for the ACME content model
     *
     * @param localname the local content model name without namespace specified
     * @return the full ACME QName including namespace
     */
    private QName createQName(String localname) {
        return QName.createQName(ACME_MODEL_NS + localname);
    }
}
