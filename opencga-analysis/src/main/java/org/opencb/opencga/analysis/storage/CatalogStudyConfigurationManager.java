/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.opencga.analysis.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opencb.datastore.core.ObjectMap;
import org.opencb.datastore.core.QueryOptions;
import org.opencb.datastore.core.QueryResult;
import org.opencb.opencga.catalog.CatalogException;
import org.opencb.opencga.catalog.CatalogManager;
import org.opencb.opencga.catalog.beans.Study;
import org.opencb.opencga.catalog.db.CatalogDBException;
import org.opencb.opencga.catalog.io.CatalogIOManagerException;
import org.opencb.opencga.core.common.Config;
import org.opencb.opencga.storage.core.StudyConfiguration;
import org.opencb.opencga.storage.core.variant.StudyConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by hpccoll1 on 23/03/15.
 */
public class CatalogStudyConfigurationManager extends StudyConfigurationManager {
    protected static Logger logger = LoggerFactory.getLogger(CatalogStudyConfigurationManager.class);

    private final CatalogManager catalogManager;
    private final String sessionId;

    public static final String STUDY_CONFIGURATION_FIELD = "studyConfiguration";
    public static final QueryOptions QUERY_OPTIONS = new QueryOptions("include", Arrays.asList("projects.studies.name", "projects.studies.attributes." + STUDY_CONFIGURATION_FIELD));

    public CatalogStudyConfigurationManager(ObjectMap objectMap) throws CatalogDBException, CatalogIOManagerException {
        super(objectMap);
        catalogManager = new CatalogManager(Config.getCatalogProperties());
        sessionId = objectMap.getString("sessionId");
    }

    @Override
    public QueryResult<StudyConfiguration> getStudyConfiguration(int studyId, QueryOptions options) {
        StudyConfiguration studyConfiguration = null;
        long start = System.currentTimeMillis();
        try {
            logger.debug("Reading StudyConfiguration from Catalog. studyId: {}", studyId);
            Study study = catalogManager.getStudy(studyId, options.getString("sessionId", sessionId), QUERY_OPTIONS).first();
            Object o = study.getAttributes().get(STUDY_CONFIGURATION_FIELD);
            if (o == null ) {
                studyConfiguration = new StudyConfiguration(studyId, study.getName());
            } else if (o instanceof StudyConfiguration) {
                studyConfiguration = (StudyConfiguration) o;
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                studyConfiguration = objectMapper.readValue(objectMapper.writeValueAsString(o), StudyConfiguration.class);
            }
            logger.trace("Read StudyConfiguration studyConfiguration {}", studyConfiguration);
        } catch (CatalogException | IOException e) {
            e.printStackTrace();
            logger.error("Unable to get StudyConfiguration from Catalog", e);
        }
        return new QueryResult<>(Integer.toString(studyId), (int) (System.currentTimeMillis() - start), 1, 1, "", "", Collections.singletonList(studyConfiguration));
    }

    @Override
    public QueryResult updateStudyConfiguration(StudyConfiguration studyConfiguration, QueryOptions options) {
        try {
            logger.info("Updating StudyConfiguration " + studyConfiguration.getStudyId());
            return catalogManager.modifyStudy(studyConfiguration.getStudyId(), new ObjectMap("attributes", new ObjectMap(STUDY_CONFIGURATION_FIELD, studyConfiguration)), options.getString("sessionId", sessionId));
        } catch (CatalogException e) {
            logger.error("Unable to update StudyConfiguration in Catalog", e);
            return new QueryResult(Integer.toString(studyConfiguration.getStudyId()), -1, 0, 0, "", e.getMessage(), Collections.emptyList());
        }
    }
}
