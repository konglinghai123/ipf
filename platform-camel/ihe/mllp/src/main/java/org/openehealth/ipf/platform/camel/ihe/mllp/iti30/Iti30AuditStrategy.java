/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.platform.camel.ihe.mllp.iti30;

import ca.uhn.hl7v2.model.Message;
import org.apache.camel.Exchange;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpAuditStrategy;
import org.openehealth.ipf.platform.camel.ihe.mllp.iti8.Iti8AuditStrategyUtils;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes.RFC3881EventOutcomeCodes;

public abstract class Iti30AuditStrategy extends MllpAuditStrategy<Iti30AuditDataset> {

    public Iti30AuditStrategy(boolean serverSide) {
        super(serverSide);
    }

    @Override
    public void enrichAuditDatasetFromRequest(Iti30AuditDataset auditDataset, Message msg, Exchange exchange) {
        Iti30AuditStrategyUtils.enrichAuditDatasetFromRequest(auditDataset, msg, exchange);
    }

    @Override
    public void doAudit(RFC3881EventOutcomeCodes eventOutcome, Iti30AuditDataset auditDataset) {
        if ("A31".equals(auditDataset.getMessageType()) ||
                "A47".equals(auditDataset.getMessageType()) ||
                "A24".equals(auditDataset.getMessageType()) ||
                "A37".equals(auditDataset.getMessageType())) {
            callUpdateAuditRoutine(eventOutcome, auditDataset, true);
        } else if ("A40".equals(auditDataset.getMessageType())) {
            callDeleteAuditRoutine(eventOutcome, auditDataset, false);
            callUpdateAuditRoutine(eventOutcome, auditDataset, true);
        } else {
            // A28
            callCreateAuditRoutine(eventOutcome, auditDataset, true);
        }
    }


    protected abstract void callCreateAuditRoutine(
            RFC3881EventOutcomeCodes eventOutcome,
            Iti30AuditDataset auditDataset,
            boolean newPatientId);

    protected abstract void callUpdateAuditRoutine(
            RFC3881EventOutcomeCodes eventOutcome,
            Iti30AuditDataset auditDataset,
            boolean newPatientId);

    protected abstract void callDeleteAuditRoutine(
            RFC3881EventOutcomeCodes eventOutcome,
            Iti30AuditDataset auditDataset,
            boolean newPatientId);

    @Override
    public Iti30AuditDataset createAuditDataset() {
        return new Iti30AuditDataset(isServerSide());
    }

}