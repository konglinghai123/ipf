/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openehealth.ipf.platform.camel.ihe.fhir.iti65;

import ca.uhn.fhir.rest.api.MethodOutcome;
import org.hl7.fhir.instance.model.Bundle;
import org.hl7.fhir.instance.model.Conformance;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openehealth.ipf.commons.audit.codes.*;
import org.openehealth.ipf.commons.audit.model.ActiveParticipantType;
import org.openehealth.ipf.commons.audit.model.ParticipantObjectIdentificationType;
import org.openehealth.ipf.commons.audit.types.ParticipantObjectIdType;
import org.openehealth.ipf.commons.audit.utils.AuditUtils;
import org.openehealth.ipf.commons.audit.queue.AbstractMockedAuditMessageQueue;
import org.openehealth.ipf.commons.ihe.fhir.audit.codes.FhirEventTypeCode;

import javax.servlet.ServletException;

import static org.junit.Assert.*;

/**
 *
 */
public class TestIti65Success extends AbstractTestIti65 {

    private static final String CONTEXT_DESCRIPTOR = "iti-65.xml";

    @BeforeClass
    public static void setUpClass() throws ServletException {
        startServer(CONTEXT_DESCRIPTOR);
    }

    @Test
    public void testGetConformance() {
        Conformance conf = client.capabilities().ofType(Conformance.class).execute();
        Conformance.ConformanceRestComponent component = conf.getRest().iterator().next();
        assertEquals(Conformance.SystemRestfulInteraction.TRANSACTION, component.getInteraction().get(0).getCode());
    }

    @Test
    public void testSendManualMhd() throws Exception {

        Bundle result = sendManually(provideAndRegister());
        printAsXML(result);

        // Check ATNA Audit
        AbstractMockedAuditMessageQueue sender = getAuditSender();
        assertEquals(1, sender.getMessages().size());
        org.openehealth.ipf.commons.audit.model.AuditMessage event = sender.getMessages().get(0);

        // Event
        assertEquals(
                EventOutcomeIndicator.Success,
                event.getEventIdentification().getEventOutcomeIndicator());
        assertEquals(
                EventActionCode.Create,
                event.getEventIdentification().getEventActionCode());

        assertEquals(EventIdCode.Import, event.getEventIdentification().getEventID());
        assertEquals(FhirEventTypeCode.ProvideDocumentBundle, event.getEventIdentification().getEventTypeCode().get(0));

        // ActiveParticipant Source
        ActiveParticipantType source = event.getActiveParticipants().get(0);
        assertTrue(source.isUserIsRequestor());
        assertEquals("127.0.0.1", source.getNetworkAccessPointID());
        assertEquals(NetworkAccessPointTypeCode.IPAddress, source.getNetworkAccessPointTypeCode());

        // ActiveParticipant Destination
        ActiveParticipantType destination = event.getActiveParticipants().get(1);
        assertFalse(destination.isUserIsRequestor());
        assertEquals("http://localhost:" + DEMO_APP_PORT + "/", destination.getUserID());
        assertEquals(AuditUtils.getLocalIPAddress(), destination.getNetworkAccessPointID());

        // Patient
        ParticipantObjectIdentificationType patient = event.getParticipantObjectIdentifications().get(0);
        assertEquals(ParticipantObjectTypeCode.Person, patient.getParticipantObjectTypeCode());
        assertEquals(ParticipantObjectTypeCodeRole.Patient, patient.getParticipantObjectTypeCodeRole());
        assertEquals(ParticipantObjectIdTypeCode.PatientNumber, patient.getParticipantObjectIDTypeCode());
        assertEquals("Patient/a2", patient.getParticipantObjectID());

        // SubmissionSet
        ParticipantObjectIdentificationType poit = event.getParticipantObjectIdentifications().get(1);
        assertEquals(ParticipantObjectTypeCode.System, poit.getParticipantObjectTypeCode());
        assertEquals(ParticipantObjectTypeCodeRole.Job, poit.getParticipantObjectTypeCodeRole());

        // No real instructions how this should look like, so fpr now we take the XDS stuff
        ParticipantObjectIdType poitTypeCode = poit.getParticipantObjectIDTypeCode();
        assertEquals("urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd", poitTypeCode.getCode());
        assertEquals("IHE XDS Metadata", poitTypeCode.getCodeSystemName());
    }

    @Ignore
    public void testSendEndpointMhd() throws Exception {
        MethodOutcome result = getProducerTemplate().requestBody("direct:input", provideAndRegister(), MethodOutcome.class);
        // printAsXML(result);

        // Check ATNA Audit
        AbstractMockedAuditMessageQueue sender = getAuditSender();
        assertEquals(2, sender.getMessages().size());
    }




}
