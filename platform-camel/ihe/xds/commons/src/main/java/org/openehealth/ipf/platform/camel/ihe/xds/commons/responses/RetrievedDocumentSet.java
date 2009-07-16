/*
 * Copyright 2009 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.xds.commons.responses;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Documents returned by the Retrieve Document Set transaction.
 * <p>
 * Lists are pre-created and can therefore never be <code>null</code>.
 * @author Jens Riemschneider
 */
public class RetrievedDocumentSet extends Response {
    private final List<RetrievedDocument> documents = new ArrayList<RetrievedDocument>();

    /**
     * Constructs the response.
     */
    public RetrievedDocumentSet() {}
    
    /**
     * Constructs the response.
     * @param status
     *          the status of the request execution.
     */
    public RetrievedDocumentSet(Status status) {        
        super(status);
    }
    
    /**
     * Constructs the response.
     * @param status
     *          the status of the request execution.
     * @param documents
     *          the documents to add to this set.
     */
    public RetrievedDocumentSet(Status status, List<RetrievedDocument> documents) {        
        super(status);
        this.documents.addAll(documents);
    }
    
    /**
     * @return the documents.
     */
    public List<RetrievedDocument> getDocuments() {
        return documents;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((documents == null) ? 0 : documents.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RetrievedDocumentSet other = (RetrievedDocumentSet) obj;
        if (documents == null) {
            if (other.documents != null)
                return false;
        } else if (!documents.equals(other.documents))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
