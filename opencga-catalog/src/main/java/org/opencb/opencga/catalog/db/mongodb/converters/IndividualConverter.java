/*
 * Copyright 2015-2017 OpenCB
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

package org.opencb.opencga.catalog.db.mongodb.converters;

import org.bson.Document;
import org.opencb.commons.datastore.mongodb.GenericDocumentComplexConverter;
import org.opencb.opencga.core.models.Individual;

/**
 * Created by pfurio on 19/01/16.
 */
public class IndividualConverter extends GenericDocumentComplexConverter<Individual> {

    public IndividualConverter() {
        super(Individual.class);
    }

    @Override
    public Document convertToStorageType(Individual object) {
        Document document = super.convertToStorageType(object);
        document.put("id", document.getInteger("id").longValue());
        validateDocumentToUpdate(document);
        return document;
    }

    public void validateDocumentToUpdate(Document document) {
        Document father = (Document) document.get("father");
        long fatherId = father != null ? (father.getInteger("id") == 0 ? -1L : father.getInteger("id").longValue()) : -1L;
        document.put("father", fatherId > 0 ? new Document("id", fatherId) : new Document());

        Document mother = (Document) document.get("mother");
        long motherId = mother != null ? (mother.getInteger("id") == 0 ? -1L : mother.getInteger("id").longValue()) : -1L;
        document.put("mother", motherId > 0 ? new Document("id", motherId) : new Document());
    }

}
